package com.project.WebTapGym.controllers;

import ch.qos.logback.core.util.StringUtil;
import com.project.WebTapGym.dtos.ProductDTO;
import com.project.WebTapGym.dtos.ProductImageDTO;
import com.project.WebTapGym.exceptions.DataNotFoundException;
import com.project.WebTapGym.models.Product;
import com.project.WebTapGym.models.ProductImage;
import com.project.WebTapGym.responses.ProductListResponse;
import com.project.WebTapGym.responses.ProductResponse;
import com.project.WebTapGym.services.IProductService;
import com.project.WebTapGym.services.ProductService;
import jakarta.validation.Path;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.prefix}/products")
@RequiredArgsConstructor
public class ProductController {

    private final IProductService productService;


    @PostMapping(value = "", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> createProduct(
            @Valid @RequestPart("product") ProductDTO productDTO,
            BindingResult bindingResult,
            @RequestPart(value = "thumbnailFile", required = false) MultipartFile thumbnailFile, // <-- Thêm thumbnailFile
            @RequestPart(value = "files", required = false) List<MultipartFile> files // <-- Thêm danh sách ảnh phụ
    ) {
        try {
            if (bindingResult.hasErrors()) {
                List<String> errorMessages = bindingResult.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .collect(Collectors.toList());
                return ResponseEntity.badRequest().body(errorMessages);
            }
            // Gọi service với thumbnailFile và files
            Product newProduct = productService.createProduct(productDTO, thumbnailFile, files);
            return ResponseEntity.ok(ProductResponse.from(newProduct));
        } catch (DataNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

//    @PostMapping(value = "uploads/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<?> uploadImages(
//            @PathVariable("id") Long productId,
//            @RequestParam("files") List<MultipartFile> files)
//    {
//        try {
//            Product existingProduct = productService.getProductById(productId);
//            files = files == null ? new ArrayList<MultipartFile>() : files;
//            if(files.size() > ProductImage.MAXIMUM_IMAGE_PER_PRODUCT )
//            {
//                return ResponseEntity.badRequest().body("Không được uploads quá 5 aảnh");
//            }
//            List<ProductImage> productImages = new ArrayList<>();
//            // Sử dụng một biến để theo dõi ảnh đầu tiên
//            String firstImageFileName = null;
//
//            for (MultipartFile file : files){
//                if (file.getSize() == 0)
//                {
//                    continue;
//                }
//                // Kiem tra kich thuoc file va dinh dang
//                if(file.getSize() > 10 * 1024 * 1024)
//                {
//                    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
//                            .body("File size must be less than 10MB");
//                }
//                String contentType = file.getContentType();
//                if(contentType == null || !contentType.startsWith("image/"))
//                {
//                    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
//                            .body("File must be an image");
//                }
//                // luw file va cap nhat thumbnail trong dto
//                String fileName = storeFile(file);
//
//                // Nếu đây là ảnh đầu tiên và chưa có thumbnail, đặt nó làm thumbnail
//                if (firstImageFileName == null) {
//                    firstImageFileName = fileName; // Lưu tên file ảnh đầu tiên
//                }
//
//                // luu vao doi tuong product trong db
//                ProductImage productImage = productService.createProductImage(
//                        existingProduct.getId(),
//                        ProductImageDTO.builder()
//                                .imageUrl(fileName)
//                                .build());
//                productImages.add(productImage);
//            }
//
//            // Sau khi tất cả ảnh đã được xử lý, cập nhật thumbnail cho Product
//            if (firstImageFileName != null && (existingProduct.getThumbnail() == null || existingProduct.getThumbnail().isEmpty())) {
//                ProductDTO productUpdateDTO = ProductDTO.builder()
//                        .name(existingProduct.getName()) // Giữ nguyên tên
//                        .price(existingProduct.getPrice()) // Giữ nguyên giá
//                        .description(existingProduct.getDescription()) // Giữ nguyên mô tả
//                        .categoryId(existingProduct.getCategory().getId()) // Giữ nguyên category
//                        .thumbnail(firstImageFileName) // Đặt thumbnail mới
//                        .build();
//                // Gọi service để cập nhật sản phẩm với thumbnail mới
//                productService.updateProduct(productId, productUpdateDTO);
//            }
//            return ResponseEntity.ok().body(productImages);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }

    @GetMapping("/images/{imageName}")
    public ResponseEntity<?> viewImage(@PathVariable String imageName){
        try{
            java.nio.file.Path imagePath = Paths.get("uploads/"+imageName);
            UrlResource resource = new UrlResource(imagePath.toUri());

            if(resource.exists()){
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            }else {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(new UrlResource(Paths.get("uploads/notfound.jpg").toUri()));
            }
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

//    private String storeFile(MultipartFile file) throws IOException {
//        if(!isImageFile(file) || file.getOriginalFilename() == null){
//            throw new IOException("Invalid image file");
//        }
//        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
//        // them uuid
//        String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;
//        // duong dan thu muc den file muon luu
//        java.nio.file.Path uploadDir = Paths.get("uploads");
//        // kiem tra va tao thu muc neu khong ton tai
//        if (!Files.exists(uploadDir)) {
//            Files.createDirectories(uploadDir);
//        }
//        // duong dan day du den file
//        java.nio.file.Path destination = Paths.get(uploadDir.toString(), uniqueFileName);
//        // sao chep file vao thu muc dich
//        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
//
//        return uniqueFileName;
//
//    }
//
//    private boolean isImageFile(MultipartFile file) {
//        String contentType = file.getContentType();
//        return contentType != null && contentType.startsWith("image/");
//    }

    @GetMapping("")
    public ResponseEntity<ProductListResponse> getAllProducts(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0", name = "category_id") Long categoryId,
            @RequestParam("page") int page,
            @RequestParam("limit") int limit
    ){
        PageRequest pageRequest = PageRequest.of(page, limit,
                Sort.by("createdAt").descending());

        Page<ProductResponse> productPage = productService.getAllProducts(keyword, categoryId, pageRequest);

        int totalsPages = productPage.getTotalPages();
        List<ProductResponse> products = productPage.getContent();
        return ResponseEntity.ok(ProductListResponse.builder()
                        .products(products)
                        .totalPages(totalsPages)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable("id") Long productId) throws DataNotFoundException {
        Product existingProduct = productService.getProductById(productId);
        return ResponseEntity.ok(ProductResponse.from(existingProduct));
    }

    @PutMapping(value = "/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> updateProduct(
            @PathVariable("id") Long productId,
            @Valid @RequestPart("product") ProductDTO productDTO,
            BindingResult result,
            @RequestPart(value = "thumbnailFile", required = false) MultipartFile thumbnailFile,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        try {
            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            Product updatedProduct = productService.updateProductWithImages(productId, productDTO, thumbnailFile, files);
            return ResponseEntity.ok(ProductResponse.from(updatedProduct));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

        @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable("id") Long productId){
        try{
            productService.deleteProduct(productId);
            return ResponseEntity.ok("Xóa thành công product với id: " + productId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
