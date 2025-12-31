package com.commercehub.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderRequest {
	
	@NotEmpty(message = "Order must contain at least one item")
	@Valid
	private List<OrderItemRequest> items;
	
    @NotBlank(message = "Shipping address is required")
    @Size(max = 1000, message = "Shipping address must not exceed 1000 characters")
    private String shippingAddress;

    @NotBlank(message = "Billing address is required")
    @Size(max = 1000, message = "Billing address must not exceed 1000 characters")
    private String billingAddress;

    @Size(max = 50, message = "Payment method must not exceed 50 characters")
    private String paymentMethod;

    @Size(max = 2000, message = "Notes must not exceed 2000 characters")
    private String notes;
	
	/**
     * Nested DTO for order items
     */
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class OrderItemRequest{
		
		@NotNull(message = "Product ID is required")
		private Long productId;
		
		@NotNull(message = "Quantity is required")
		@Min(value = 1, message = "Quantity must be at least 1")
		private Integer quantity;
	}

}
