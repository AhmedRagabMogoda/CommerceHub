package com.commercehub.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

    private Long id;

    private String orderNumber;

    private Long userId;

    private String customerName;

    private String orderStatus;

    private BigDecimal totalAmount;

    private String shippingAddress;

    private String billingAddress;

    private String paymentMethod;

    private String paymentStatus;

    private String notes;

    private List<OrderItemResponse> items;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime orderedAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime shippedAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime deliveredAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime cancelledAt;

    /**
     * Nested DTO for order item information
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemResponse {

        private Long id;

        private Long productId;

        private String productName;

        private String productSku;

        private Integer quantity;

        private BigDecimal unitPrice;

        private BigDecimal totalPrice;
    }
}
