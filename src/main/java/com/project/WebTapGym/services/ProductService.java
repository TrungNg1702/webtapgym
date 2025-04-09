package com.project.WebTapGym.services;

import com.project.WebTapGym.dtos.ProductDTO;
import com.project.WebTapGym.exceptions.DataNotFoundException;
import com.project.WebTapGym.models.Category;
import com.project.WebTapGym.models.Product;
import com.project.WebTapGym.repositories.CategoryRepository;
import com.project.WebTapGym.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {

    private final ProductRepository productRepository;
//    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;

    @Override
    public Product createProduct(ProductDTO productDTO) throws DataNotFoundException {
         Category existingCategory = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new DataNotFoundException("Category not found" + productDTO.getCategoryId()));

        Product newProduct = Product
                .builder()
                .name(productDTO.getName())
                .price(productDTO.getPrice())
                .thumbnail(productDTO.getThumbnail())
                .category(existingCategory)
                .build();
        return productRepository.save(newProduct);
    }

    @Override
    public Product getProductById(Long productId) throws DataNotFoundException {
        return productRepository.findById(productId).orElseThrow(() -> new DataNotFoundException("Khong tim thay Product"));
    }

    @Override
    public Page<Product> getAllProducts(PageRequest pageRequest) {
        return productRepository.findAll(pageRequest);
    }

    @Override
    public Product updateProduct(Long productId, ProductDTO productDTO) throws DataNotFoundException {
        Product existingProduct = getProductById(productId);
        if (existingProduct != null) {
            Category existingCategory = categoryRepository
                    .findById(productDTO.getCategoryId())
                    .orElseThrow(() -> new DataNotFoundException("Category not found" + productDTO.getCategoryId()));


            existingProduct.setName(productDTO.getName());
            existingProduct.setPrice(productDTO.getPrice());
            existingProduct.setThumbnail(productDTO.getThumbnail());
            existingProduct.setCategory(existingCategory);
            return productRepository.save(existingProduct);
        }
        return null;
    }

    @Override
    public void deleteProduct(Long id) {

    }

    @Override
    public boolean existsByName(String name) {
        return false;
    }
}
