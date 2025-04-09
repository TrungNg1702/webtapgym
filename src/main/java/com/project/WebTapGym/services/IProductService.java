package com.project.WebTapGym.services;

import com.project.WebTapGym.dtos.ProductDTO;
import com.project.WebTapGym.exceptions.DataNotFoundException;
import com.project.WebTapGym.models.Product;
import org.springframework.data.domain.*;

public interface IProductService {
    public Product createProduct(ProductDTO productDTO) throws DataNotFoundException;

    Product getProductById(Long id) throws DataNotFoundException;

    Page<Product> getAllProducts(PageRequest pageRequest);

    Product updateProduct(Long productId, ProductDTO productDTO) throws DataNotFoundException;

    void deleteProduct(Long id);

    boolean existsByName(String name);
}
