package com.project.WebTapGym.services;

import com.project.WebTapGym.dtos.ProductDTO;
import com.project.WebTapGym.dtos.ProductImageDTO;
import com.project.WebTapGym.exceptions.DataNotFoundException;
import com.project.WebTapGym.models.Product;
import com.project.WebTapGym.models.ProductImage;
import org.springframework.data.domain.*;

public interface IProductService {
    Product createProduct(ProductDTO productDTO) throws Exception;

    Product getProductById(Long id) throws DataNotFoundException;

    Page<Product> getAllProducts(PageRequest pageRequest);

    Product updateProduct(Long productId, ProductDTO productDTO) throws DataNotFoundException;

    void deleteProduct(Long id);

    boolean existsByName(String name);

    ProductImage createProductImage(
            Long productId,
            ProductImageDTO productImageDTO) throws Exception;
}
