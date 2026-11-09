package com.example.e_commerce.repository;

import com.example.e_commerce.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {
    Optional<Employee> findByUsername(String username);

    Optional<Employee> findById(String employeeId);

    boolean existsByUsername(String username);

    @EntityGraph(attributePaths = {"roles"})
    @Query("""
    SELECT DISTINCT e
    FROM Employee e
    LEFT JOIN e.roles r
""")
    Page<Employee> getEmployees(Pageable pageable);

}