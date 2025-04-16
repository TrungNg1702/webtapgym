package com.project.WebTapGym.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductDTO {
    @NotNull(message = "khong duoc de trong")
    @Size(min = 3, max = 255, message = "Chiều dài tên sản phẩm phải từ 3 đến 255 kí tự")
    private String name;

    @Min(value = 0, message = "Gía sản phẩm phải lớn hơn 0")
    @Max(value = 100000000, message = "Gía sản phẩm không được cao quá 100,000,000")
    private Float price;

    private String description;

    private String thumbnail;

    @JsonProperty("category_id")
    private Long categoryId;

}
