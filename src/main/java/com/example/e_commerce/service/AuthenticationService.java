package com.example.e_commerce.service;

import com.example.e_commerce.dto.request.LogoutRequest;
import com.example.e_commerce.dto.request.RefreshRequest;
import com.example.e_commerce.dto.request.authentication.AuthenticationCustomerRequest;
import com.example.e_commerce.dto.request.IntrospectRequest;
import com.example.e_commerce.dto.request.authentication.AuthenticationEmployeeRequest;
import com.example.e_commerce.dto.response.AuthenticationResponse;
import com.example.e_commerce.dto.response.IntrospectResponse;
import com.example.e_commerce.entity.AuthUser;
import com.example.e_commerce.entity.InvalidatedToken;
import com.example.e_commerce.enums.ErrorCode;
import com.example.e_commerce.exception.AppException;
import com.example.e_commerce.repository.CustomerRepository;
import com.example.e_commerce.repository.EmployeeRepository;
import com.example.e_commerce.repository.InvalidatedTokenRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Transactional
public class AuthenticationService {
    EmployeeRepository employeeRepository;
    CustomerRepository customerRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;
    PasswordEncoder passwordEncoder;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long VALID_DURATION;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected long REFRESHABLE_DURATION;

    public IntrospectResponse introspect(IntrospectRequest request) {
        var token = request.getToken();
        boolean isValid = true;

        try {
            verifyToken(token, false);
        } catch (AppException | JOSEException | ParseException e) {
            isValid = false;
        }

        return IntrospectResponse.builder().valid(isValid).build();
    }

    public AuthenticationResponse authenticateCustomer(AuthenticationCustomerRequest request) {
        var customer = customerRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.CUSTOMER_NOT_EXISTED));

        boolean authenticated = passwordEncoder.matches(request.getPassword(), customer.getPassword());

        if (!authenticated)
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        var token = generateToken(customer);

        return AuthenticationResponse.builder().token(token).authenticated(true).build();
    }

    public AuthenticationResponse authenticateEmployee(AuthenticationEmployeeRequest request) {
        var employee = employeeRepository
                .findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_EXISTED));

        boolean authenticated = passwordEncoder.matches(request.getPassword(), employee.getPassword());

        if (!authenticated)
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        var token = generateToken(employee);

        return AuthenticationResponse.builder().token(token).authenticated(true).build();
    }

    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        try {
            var signToken = verifyToken(request.getToken(), true);

            String jit = signToken.getJWTClaimsSet().getJWTID();
            Date expiryTime = new Date(signToken.getJWTClaimsSet().getIssueTime()
                    .toInstant().plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS).toEpochMilli());

            InvalidatedToken invalidatedToken = InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();

            invalidatedTokenRepository.save(invalidatedToken);
        } catch (AppException exception) {
            log.info("Token already expired or invalid");
        }
    }

    public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {

        var signedJWT = verifyToken(request.getToken(), true);

        var jit = signedJWT.getJWTClaimsSet().getJWTID();

        Date expiryTime = new Date(signedJWT.getJWTClaimsSet().getIssueTime()
                .toInstant().plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS).toEpochMilli());

        InvalidatedToken invalidatedToken = InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();

        invalidatedTokenRepository.save(invalidatedToken);

        var loginId = signedJWT.getJWTClaimsSet().getSubject();
        var userType = signedJWT.getJWTClaimsSet().getStringClaim("user_type");

        AuthUser user;

        if ("CUSTOMER".equals(userType)) {
            user = customerRepository.findByEmail(loginId)
                    .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));
        } else if ("EMPLOYEE".equals(userType)) {
            user = employeeRepository.findByUsername(loginId)
                    .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));
        } else {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        var token = generateToken(user);
        return AuthenticationResponse.builder().token(token).authenticated(true).build();
    }

    private String generateToken(AuthUser user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsernameOrEmail())
                .issuer("toan.com")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("user_type", user.getUserType())
                .claim("scope", buildScope(user))
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new RuntimeException(e);
        }
    }

    private SignedJWT verifyToken(String token, boolean isRefresh)
            throws JOSEException, ParseException {

        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
        SignedJWT signedJWT;

        try {
            signedJWT = SignedJWT.parse(token);
        } catch (ParseException e) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        Date expiryTime;

        if (isRefresh) {
            Date issueTime = signedJWT.getJWTClaimsSet().getIssueTime();

            expiryTime = new Date(
                    issueTime.toInstant()
                            .plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS)
                            .toEpochMilli());

        } else {
            expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        }

        boolean verified = signedJWT.verify(verifier);

        Date currentTime = new Date();
        if (!verified) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        if (expiryTime == null || expiryTime.before(currentTime)
                || expiryTime.equals(currentTime)) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        String jwtId = signedJWT.getJWTClaimsSet().getJWTID();
        boolean invalidated = invalidatedTokenRepository.existsById(jwtId);
        if (invalidated) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        return signedJWT;
    }

    public boolean isTokenInvalidated(String jwtId) {
        return invalidatedTokenRepository.existsById(jwtId);
    }

    @org.springframework.scheduling.annotation.Scheduled(cron = "0 0 2 * * ?")
    public void cleanUpExpiredTokens() {
        int deletedCount = invalidatedTokenRepository.deleteAllExpiredTokens(new Date());
        log.info("Đã dọn dẹp {} token hết hạn khỏi bảng invalidated_tokens.", deletedCount);
    }

    private String buildScope(AuthUser user) {
        StringJoiner stringJoiner = new StringJoiner(" ");

        stringJoiner.add("ROLE_" + user.getUserType());

        if ("EMPLOYEE".equals(user.getUserType())) {
            if (!CollectionUtils.isEmpty(user.getRoles())) {
                user.getRoles().forEach(role -> {
                    stringJoiner.add("ROLE_" + role.getRoleName());
                    if (!CollectionUtils.isEmpty(role.getPermissions())) {
                        role.getPermissions()
                                .forEach(permission -> stringJoiner.add(permission.getPermissionName().name()));
                    }
                });
            }
        }
        return stringJoiner.toString();
    }
}