package com.project.WebTapGym.controllers;

import ch.qos.logback.core.util.StringUtil;
import com.project.WebTapGym.dtos.ProductDTO;
import jakarta.validation.Path;
import jakarta.validation.Valid;
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

@RestController
@RequestMapping("${api.prefix}/products")
public class ProductController {

    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProduct(
            @Valid @RequestBody ProductDTO productDTO,
            BindingResult result){

        try{
            if (result.hasErrors()) {
                List<String> errorMessages =  result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }

            List<MultipartFile> files = productDTO.getFiles();
            files = files == null ? new ArrayList<MultipartFile>() : files;
            for (MultipartFile file : files){
                if (file.getSize() == 0)
                {
                    continue;
                }

                // Kiem tra kich thuoc file va dinh dang
                if(file.getSize() > 10 * 1024 * 1024)
                {
                    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body("File size must be less than 10MB");
                }

                String contentType = file.getContentType();
                if(contentType == null || !contentType.startsWith("image/"))
                {
                    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("File must be an image");
                }
                // luw file va cap nhat thumbnail trong dto
                String fileName = storeFile(file);
                // luu vao doi tuong product trong db
            }


            return ResponseEntity.ok("Them moi san pham thanh cong");
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private String storeFile(MultipartFile file) throws IOException {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        // them uuid
        String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;
        // duong dan thu muc den file muon luu
        java.nio.file.Path uploadDir = Paths.get("uploads");
        // kiem tra va tao thu muc neu khong ton tai
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        // duong dan day du den file
        java.nio.file.Path destination = Paths.get(uploadDir.toString(), uniqueFileName);
        // sao chep file vao thu muc dich
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

        return uniqueFileName;

    }



    @GetMapping("")
    public ResponseEntity<?> getAllProducts(
            @RequestParam("page") int page,
            @RequestParam("limit") int limit
    ){
        return ResponseEntity.ok("asdasd");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable("id") Long productId){
        return ResponseEntity.ok("Product with id: " + productId + " found");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable("id") Long productId,
            @Valid @RequestBody ProductDTO productDTO){
        return ResponseEntity.ok("asdasd");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable("id") Long productId){
        return ResponseEntity.status(HttpStatus.OK).body("Deleted product with id: " + productId);
    }
}
