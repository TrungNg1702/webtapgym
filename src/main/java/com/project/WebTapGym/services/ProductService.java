package com.project.WebTapGym.services;

import com.project.WebTapGym.dtos.ProductDTO;
import com.project.WebTapGym.dtos.ProductImageDTO;
import com.project.WebTapGym.exceptions.DataNotFoundException;
import com.project.WebTapGym.exceptions.InvalidParamException;
import com.project.WebTapGym.models.Category;
import com.project.WebTapGym.models.Product;
import com.project.WebTapGym.models.ProductImage;
import com.project.WebTapGym.repositories.CategoryRepository;
import com.project.WebTapGym.repositories.ProductImageRepository;
import com.project.WebTapGym.repositories.ProductRepository;
import com.project.WebTapGym.responses.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;


    @Override
    @Transactional
    public Product createProduct(ProductDTO productDTO,
                                 MultipartFile thumbnailFile,
                                 List<MultipartFile> files) throws DataNotFoundException, IOException {
        Category existingCategory = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find category with id: " + productDTO.getCategoryId()));

        Product newProduct = Product.builder()
                .name(productDTO.getName())
                .price(productDTO.getPrice())
                .description(productDTO.getDescription())
                .category(existingCategory)
                .build();

        // Xử lý Thumbnail File
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            String thumbnailFileName = storeFile(thumbnailFile);
            newProduct.setThumbnail(thumbnailFileName);
        }

        // Lưu Product trước để có ID
        newProduct = productRepository.save(newProduct);

        // Xử lý các ảnh phụ (ProductImage)
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                if (file.getSize() == 0) {
                    continue;
                }
                // Kiểm tra kích thước file và định dạng
                if (file.getSize() > 10 * 1024 * 1024) {
                    throw new IOException("File is too large! Maximum size is 10MB");
                }
                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    throw new IOException("File must be an image");
                }

                String filename = storeFile(file); // Lưu file ảnh phụ
                ProductImage productImage = ProductImage.builder()
                        .product(newProduct)
                        .imageUrl(filename)
                        .build();
                productImageRepository.save(productImage);
            }
        }

        return newProduct;
    }

    @Override
    public Product getProductById(Long productId) throws DataNotFoundException {
        return productRepository.findById(productId).orElseThrow(() -> new DataNotFoundException("Khong tim thay Product"));
    }

    @Override
    public Page<ProductResponse> getAllProducts(String keyword,
                                                Long categoryId,
                                                PageRequest pageRequest) {
        Page<Product> productPage;
        productPage = productRepository.searchProducts(categoryId, keyword, pageRequest);
        return productPage.map(ProductResponse::from);
    }

    @Override
    @Transactional
    public Product updateProduct(Long productId, ProductDTO productDTO) throws DataNotFoundException {
        Product existingProduct = getProductById(productId);
        if (existingProduct != null) {
            Category existingCategory = categoryRepository
                    .findById(productDTO.getCategoryId())
                    .orElseThrow(() -> new DataNotFoundException("Category not found" + productDTO.getCategoryId()));


            existingProduct.setName(productDTO.getName());
            existingProduct.setCategory(existingCategory);
            existingProduct.setPrice(productDTO.getPrice());
            existingProduct.setDescription(productDTO.getDescription());
            existingProduct.setThumbnail(productDTO.getThumbnail());
            return productRepository.save(existingProduct);
        }
        return null;
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        optionalProduct.ifPresent(productRepository::delete);

    }

    @Override
    public boolean existsByName(String name) {
        return productRepository.existsByName(name);
    }

    @Override
    @Transactional
    public ProductImage createProductImage(
            Long productId,
            ProductImageDTO productImageDTO) throws Exception {
        Product existingProduct = productRepository
                .findById(productId)
                .orElseThrow(() -> new DataNotFoundException(
                        "Cannot find product with id: " + productImageDTO.getProductId()));

        ProductImage newProductImage = ProductImage
                .builder()
                .product(existingProduct)
                .imageUrl(productImageDTO.getImageUrl())
                .build();

        // ko cho insert qua 5 anh cho 1 san pham
        int size = productImageRepository.findByProductId(productId).size();
        if (size >= ProductImage.MAXIMUM_IMAGE_PER_PRODUCT ){
            throw new InvalidParamException("Number of image must be <= "+ ProductImage.MAXIMUM_IMAGE_PER_PRODUCT);
        }
        return productImageRepository.save(newProductImage);
    }

    @Override
    @Transactional
    public void deleteProductImage(Long imageId) throws DataNotFoundException {
        ProductImage imageDelete = productImageRepository.findById(imageId)
                .orElseThrow(() -> new DataNotFoundException("Image not found" + imageId));

        // xoa vat ly
        java.nio.file.Path filePath = Paths.get("uploads", imageDelete.getImageUrl());
        try{
            Files.delete(filePath);
        } catch (Exception e) {
            System.err.println("Error deleting image " + imageDelete.getImageUrl());
        }

        productImageRepository.delete(imageDelete);
    }

    // Phuong thuc moi de cap nhat san pham bao gom ca anh
    @Override
    @Transactional
    public Product updateProductWithImages(
            Long productId,
            ProductDTO productDTO,
            MultipartFile thumbnailFile,
            List<MultipartFile> files) throws Exception {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new DataNotFoundException("Product not found" + productId));

        Category existingCategory
                = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new DataNotFoundException("Category not found" + productDTO.getCategoryId()));

        existingProduct.setName(productDTO.getName());
        existingProduct.setCategory(existingCategory);
        existingProduct.setPrice(productDTO.getPrice());
        existingProduct.setDescription(productDTO.getDescription());

        // xu ly thumbnail file
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            // Kiểm tra kích thước và định dạng của thumbnail file
            if (thumbnailFile.getSize() > 10 * 1024 * 1024) {
                throw new InvalidParamException("Thumbnail file size must be less than 10MB");
            }
            String contentType = thumbnailFile.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new InvalidParamException("Thumbnail file must be an image");
            }

            // Lưu thumbnail file mới
            String newThumbnailFileName = storeFile(thumbnailFile);
            existingProduct.setThumbnail(newThumbnailFileName);
        } else if (productDTO.getThumbnail() != null && !productDTO.getThumbnail().isEmpty()) {
            existingProduct.setThumbnail(productDTO.getThumbnail());
        }

        // Xóa các ảnh được chỉ định
        if (productDTO.getDeleteImageIds() != null && !productDTO.getDeleteImageIds().isEmpty()) {
            for (Long imageId : productDTO.getDeleteImageIds()) {
                deleteProductImage(imageId); // Gọi phương thức xóa từng ảnh theo ID
            }
        }

        // Them anh moi
        String firstNewImageFileName = null;

        if (files != null && !files.isEmpty()) {
            // Lấy danh sách ảnh hiện có (đã được tải bởi findByIdWithImages)
            List<ProductImage> currentImages = existingProduct.getProductImages();
            int currentImageCount = (currentImages != null) ? currentImages.size() : 0;

            if (currentImageCount + files.size() > 5) { // Giả sử giới hạn 5 ảnh phụ
                throw new IOException("Number of product images must be less than 5");
            }
            for (MultipartFile file : files) {
                if (file.isEmpty()) {
                    continue;
                }
                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    throw new IOException("File must be an image");
                }
                if (file.getSize() > 10 * 1024 * 1024) { // Kích thước tối đa 10MB
                    throw new IOException("File is too large! Maximum size is 10MB");
                }

                String filename = storeFile(file); // Lưu file
                ProductImage newProductImage = ProductImage.builder()
                        .product(existingProduct)
                        .imageUrl(filename)
                        .build();
                productImageRepository.save(newProductImage); // Lưu vào DB
                // Không cần thêm vào existingProduct.getProductImages() vì @Transactional
                // và fetchType.LAZY sẽ tải lại khi cần, hoặc findByIdWithImages cuối cùng sẽ làm.
            }
        }

        Product savedProduct = productRepository.save(existingProduct);

        return productRepository.findByIdWithImages(savedProduct.getId())
                .orElse(savedProduct);
    }
    private String storeFile(MultipartFile file) throws IOException {
        if (!isImageFile(file) || file.getOriginalFilename() == null) {
            throw new IOException("Invalid image file");
        }
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;
        java.nio.file.Path uploadDir = Paths.get("uploads");
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        java.nio.file.Path destination = Paths.get(uploadDir.toString(), uniqueFileName);
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        return uniqueFileName;
    }

    private boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }

}
