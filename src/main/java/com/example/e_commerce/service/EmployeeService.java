package com.example.e_commerce.service;

import com.example.e_commerce.dto.request.employee.EmployeeCreateRequest;
import com.example.e_commerce.dto.request.employee.EmployeeUpdateRequest;
import com.example.e_commerce.dto.response.employee.EmployeeDetailResponse;
import com.example.e_commerce.dto.response.employee.EmployeeSummaryResponse;
import com.example.e_commerce.dto.response.province.ProvinceResponse;
import com.example.e_commerce.dto.response.role.RoleSummaryResponse;
import com.example.e_commerce.entity.Employee;
import com.example.e_commerce.entity.EmployeeProvince;
import com.example.e_commerce.entity.Province;
import com.example.e_commerce.entity.Role;
import com.example.e_commerce.enums.ErrorCode;
import com.example.e_commerce.enums.UserRole;
import com.example.e_commerce.exception.AppException;
import com.example.e_commerce.mapper.EmployeeMapper;
import com.example.e_commerce.repository.EmployeeRepository;
import com.example.e_commerce.repository.ProvinceRepository;
import com.example.e_commerce.repository.RoleRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmployeeService {
    private final RoleRepository roleRepository;
    EmployeeRepository employeeRepository;
    ProvinceService provinceService;
    PasswordEncoder passwordEncoder;
    EmployeeMapper employeeMapper;
    ProvinceRepository provinceRepository;

    public Employee findEmployeeById(String employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_EXISTED));
    }

    public Employee findByUsername(String username) {
        return employeeRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_EXISTED));
    }

    public java.util.Optional<Employee> findOptionalByUsername(String username) {
        return employeeRepository.findByUsername(username);
    }

    @Transactional(readOnly = true)
    public Page<EmployeeSummaryResponse> getEmployeeSummaries(Pageable pageable) {
        Page<Employee> employees = employeeRepository.getEmployees(pageable);

        return employees.map(employee ->
                new EmployeeSummaryResponse(
                        employee.getEmployeeId(),
                        employee.getFullname(),
                        employee.getIdCardNumber(),
                        employee.getRoles()
                                .stream()
                                .map(Role::getRoleName)
                                .collect(Collectors.toSet())
                )
        );
    }


    @Transactional
    public EmployeeDetailResponse createEmployee(EmployeeCreateRequest request) {
        if (employeeRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        Set<Role> roleSet = roleRepository.findByRoleNameIn(request.getRoles());
        if (roleSet.size() != request.getRoles().size()) {
            throw new AppException(ErrorCode.ROLE_NOT_EXISTED);
        }

        Employee employee = Employee.builder()
                .fullname(request.getFullname())
                .idCardNumber(request.getIdCardNumber())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(roleSet)
                .employeeProvinces(new HashSet<>())
                .build();

        if (request.getProvinceIds() != null && !request.getProvinceIds().isEmpty()) {
            List<Province> newProvinces = provinceRepository.findAllById(request.getProvinceIds());
            if (newProvinces.size() != request.getProvinceIds().size()) {
                throw new AppException(ErrorCode.PROVINCE_NOT_EXISTED);
            }

            for (Province province : newProvinces) {
                EmployeeProvince ep = EmployeeProvince.builder()
                        .province(province)
                        .employee(employee)
                        .build();
                employee.getEmployeeProvinces().add(ep);
            }
        }


        Employee savedEmployee = employeeRepository.saveAndFlush(employee);

        Set<UserRole> roles = savedEmployee.getRoles().stream()
                .map(role -> role.getRoleName())
                .collect(Collectors.toSet());

        Set<ProvinceResponse> provinces = savedEmployee.getEmployeeProvinces().stream()
                .map(ep -> ProvinceResponse.builder()
                        .provinceId(ep.getProvince().getProvinceId())
                        .provinceName(ep.getProvince().getProvinceName())
                        .build())
                .collect(Collectors.toSet());

        return new EmployeeDetailResponse(
                savedEmployee.getEmployeeId(),
                savedEmployee.getFullname(),
                savedEmployee.getIdCardNumber(),
                savedEmployee.getUsername(),
                roles,
                provinces
        );
    }


    public EmployeeDetailResponse getEmployeeDetail(String employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_EXISTED));

        Set<UserRole> roleNames = employee.getRoles().stream()
                .map(Role::getRoleName)
                .collect(Collectors.toSet());

        Set<ProvinceResponse> provinces = employee.getEmployeeProvinces().stream()
                .map(ep -> ProvinceResponse.builder()
                        .provinceId(ep.getProvince().getProvinceId())
                        .provinceName(ep.getProvince().getProvinceName())
                        .build())
                .collect(Collectors.toSet());

        return EmployeeDetailResponse.builder()
                .employeeId(employee.getEmployeeId())
                .fullname(employee.getFullname())
                .idCardNumber(employee.getIdCardNumber())
                .username(employee.getUsername())
                .roles(roleNames)
                .assignedProvinces(provinces)
                .build();
    }


    public EmployeeDetailResponse updateEmployee(String employeeId, EmployeeUpdateRequest request) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_EXISTED));

        employeeMapper.updateEmployeeFromRequest(employee, request);

        if (request.getRoles() != null && !request.getRoles().isEmpty()) {

            Set<Role> newRoles = roleRepository.findByRoleNameIn(request.getRoles());

            if (newRoles.size() != request.getRoles().size()) {
                throw new AppException(ErrorCode.HAVE_ROLE_INVALID);
            }

            employee.getRoles().clear();

            employee.getRoles().addAll(newRoles);
        }


        if (request.getProvinceIds() != null && !request.getProvinceIds().isEmpty()) {

            List<Province> newProvinces = provinceRepository.findAllById(request.getProvinceIds());
            if (newProvinces.size() != request.getProvinceIds().size()) {
                throw new RuntimeException("Có Tỉnh/Thành phố không tồn tại trong hệ thống!");
            }

            employee.getEmployeeProvinces().clear();

            for (Province province : newProvinces) {
                EmployeeProvince ep = new EmployeeProvince();
                ep.setEmployee(employee);
                ep.setProvince(province);
                ep.setDescription("Làm việc tại khu vực");

                employee.getEmployeeProvinces().add(ep);
            }
        }

        employeeRepository.save(employee);

        return this.getEmployeeDetail(employeeId);

    }





}


