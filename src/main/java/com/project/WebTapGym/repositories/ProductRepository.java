package com.project.WebTapGym.repositories;

import com.project.WebTapGym.models.Product;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByName(String name);

    Page<Product> findAll(Pageable pageable);

    @Query("SELECT p FROM Product p where " +
                "(:categoryId is null or :categoryId = 0 or p.category.id = :categoryId)" +
                "and (:keyword is null or :keyword = '' or p.name like %:keyword% or p.description like %:keyword%)")
    Page<Product> searchProducts(
            @Param("categoryId") Long categoryId,
            @Param("keyword") String keyword,
            Pageable pageable);

    // Phương thức mới để tải Product cùng với ProductImages của nó
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.productImages WHERE p.id = :productId")
    Optional<Product> findByIdWithImages(@Param("productId") Long productId);
}
