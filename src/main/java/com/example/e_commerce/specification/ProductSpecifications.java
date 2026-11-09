package com.example.e_commerce.specification;

import com.example.e_commerce.dto.request.product.ProductFilterRequest;
import com.example.e_commerce.entity.Product;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;


public class ProductSpecifications {
    public static Specification<Product> buildSpec(Long typeId,Long categoryId, ProductFilterRequest filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (categoryId != null) {
                predicates.add(cb.equal(root.get("category").get("categoryId"), categoryId));
            }

            if (typeId != null) {
                predicates.add(cb.equal(root.get("categoryType").get("typeId"), typeId));
            }

            if (filter.getName() != null && !filter.getName().trim().isEmpty()) {
                predicates.add(cb.like(root.get("name"), "%" + filter.getName().trim() + "%"));
            }

            if (filter.getMinPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("unitPrice"), filter.getMinPrice()));
            }

            if (Long.class != query.getResultType()) {
                query.distinct(true);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}


