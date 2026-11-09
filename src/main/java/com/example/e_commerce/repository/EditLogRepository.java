package com.example.e_commerce.repository;

import com.example.e_commerce.entity.EditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EditLogRepository extends JpaRepository<EditLog, Long> {
    List<EditLog> findByProduct_ProductId(int productId);
    List<EditLog> findByEmployee_EmployeeId(String employeeId);
}