package com.example.e_commerce.repository;

import com.example.e_commerce.entity.InvalidatedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface InvalidatedTokenRepository  extends JpaRepository<InvalidatedToken, String> {
    boolean existsById(String id);

    @Modifying
    @Query("DELETE FROM InvalidatedToken t WHERE t.expiryTime <= :now")
    int deleteAllExpiredTokens(@Param("now") Date now);
}
