package com.project.WebTapGym.services;

import com.project.WebTapGym.dtos.ProductDTO;
import com.project.WebTapGym.dtos.ProductImageDTO;
import com.project.WebTapGym.exceptions.DataNotFoundException;
import com.project.WebTapGym.models.Product;
import com.project.WebTapGym.models.ProductImage;
import com.project.WebTapGym.responses.ProductResponse;
import org.springframework.data.domain.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IProductService {
    Product createProduct(ProductDTO productDTO, MultipartFile thumbnailFile, List<MultipartFile> files) throws DataNotFoundException, IOException;

    Product getProductById(Long id) throws DataNotFoundException;

    Page<ProductResponse> getAllProducts(String keyword, Long categoryId, PageRequest pageRequest);

    Product updateProduct(Long productId, ProductDTO productDTO) throws DataNotFoundException;

    void deleteProduct(Long id);

    boolean existsByName(String name);

    ProductImage createProductImage(
            Long productId,
            ProductImageDTO productImageDTO) throws Exception;

    void deleteProductImage(Long imageId)  throws DataNotFoundException;

    Product updateProductWithImages(Long productId,
                                    ProductDTO productDTO,
                                    MultipartFile thumbnailFile,
                                    List<MultipartFile> files) throws Exception;
}
