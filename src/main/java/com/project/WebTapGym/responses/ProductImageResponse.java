package com.project.WebTapGym.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.WebTapGym.models.ProductImage;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductImageResponse {
    private Long id;

    @JsonProperty("image_url")
    private String imageUrl;

    public static ProductImageResponse fromProductImage(ProductImage productImage) {
        if (productImage == null) {
            return null;
        }
        return ProductImageResponse.builder()
                .id(productImage.getId())
                .imageUrl(productImage.getImageUrl())
                // .productId(productImage.getProduct() != null ? productImage.getProduct().getId() : null) // hiển thị productId
                .build();
    }
}