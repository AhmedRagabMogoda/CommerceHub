package com.commercehub.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {

    private Long id;

    private String name;

    private String description;

    private String sku;

    private BigDecimal price;

    private Integer quantityInStock;

    private String category;

    private String brand;

    private String imageUrl;

    private Boolean isActive;

    private BigDecimal weight;

    private Boolean inStock;
}
