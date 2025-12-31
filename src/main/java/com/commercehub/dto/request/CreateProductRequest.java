package com.commercehub.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateProductRequest {
	
	@NotBlank(message = "Product name is required")
	@Size(max = 255, message = "Product name must not exceed 255 characters")
	private String name;
	
	@Size(max = 5000, message = "Description must not exceed 5000 characters")
	private String description;
	
	@NotBlank(message = "Sku is required")
	@Size(max = 100, message = "SKU must not exceed 100 characters")
	@Pattern(regexp = "^[A-Z0-9-]+$", message = "SKU can only contain uppercase letters, numbers, and hyphens")
	private String sku;
	
	@NotNull(message = "Price is required")
	@DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
	@Digits(integer = 2, fraction = 10, message = "Price must have maximum 8 integer digits and 2 decimal places")
	private BigDecimal price;
	
	@NotNull(message = "Quantity in stock is required")
	@Min(value = 0, message = "Quantity must be 0 or greater")
	private Integer quantityInStock;
	
	@Size(max = 100, message = "Category must not exceed 100 characters")
	private String category;
	
	@Size(max = 100, message = "Brand must not exceed 100 characters")
	private String brand;
	
	@Size(max = 500, message = "Image URL must not exceed 500 characters")
    @Pattern(regexp = "^(https?://)?[\\w\\-]+(\\.[\\w\\-]+)+[/#?]?.*$|^$", message = "Invalid URL format")
	private String imageUrl;
	
	@Builder.Default
	private boolean isActive = true;
	
	@DecimalMin(value = "0.0", message = "Weight must be 0 or greater")
    @Digits(integer = 8, fraction = 2, message = "Weight must have maximum 8 integer digits and 2 decimal places")
	private BigDecimal weight;

}
