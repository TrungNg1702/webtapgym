package com.project.WebTapGym.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.WebTapGym.models.Product;
// import com.project.WebTapGym.models.ProductImage; // XÓA DÒNG NÀY, KHÔNG TRỰC TIẾP DÙNG ENTITY NỮA
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors; // THÊM IMPORT NÀY

@AllArgsConstructor
@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
public class ProductResponse extends BaseResponse {
    private Long id;
    private String name;
    private Float price;
    private String thumbnail;
    private String description;

    @JsonProperty("product_images")
    // SỬ DỤNG ProductImageResponse thay vì ProductImage
    private List<ProductImageResponse> productImages = new ArrayList<>();

    @JsonProperty("category_id")
    private Long categoryId;

    public static ProductResponse from(Product product) {
        if (product == null) {
            return null;
        }

        // Ánh xạ danh sách ProductImage entity sang danh sách ProductImageResponse DTO
        List<ProductImageResponse> imageResponses = null;
        if (product.getProductImages() != null) {
            imageResponses = product.getProductImages().stream()
                    .map(ProductImageResponse::fromProductImage)
                    .collect(Collectors.toList());
        } else {
            imageResponses = new ArrayList<>(); // Đảm bảo trả về list rỗng nếu không có ảnh
        }

        ProductResponse productResponse = ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .thumbnail(product.getThumbnail())
                .description(product.getDescription())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null) // Kiểm tra null cho category
                .productImages(imageResponses) // Gán danh sách ProductImageResponse đã ánh xạ
                .build();
        productResponse.setCreatedAt(product.getCreatedAt());
        productResponse.setUpdatedAt(product.getUpdatedAt());

        return productResponse;
    }
}