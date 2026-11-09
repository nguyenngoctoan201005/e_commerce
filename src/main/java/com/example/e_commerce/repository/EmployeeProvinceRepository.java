package com.example.e_commerce.repository;

import com.example.e_commerce.entity.EmployeeProvince;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EmployeeProvinceRepository extends JpaRepository<EmployeeProvince, Long> {
    List<EmployeeProvince> findByEmployee_EmployeeId(String employeeId);
}