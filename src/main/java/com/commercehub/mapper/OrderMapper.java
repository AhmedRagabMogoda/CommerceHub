package com.commercehub.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.commercehub.dto.request.CreateOrderRequest;
import com.commercehub.dto.response.OrderResponse;
import com.commercehub.entity.Order;
import com.commercehub.entity.OrderItem;

@Mapper(componentModel = "spring")
public interface OrderMapper {
	
    /**
     * Convert Order entity to OrderResponse DTO
     * Maps order status and payment status enums to strings
     * Includes nested order items with product details
     */
	@Mapping(target = "userId", source = "user.id")
	@Mapping(target = "customerName", expression = "java(order.getUser().getFirstName() + \" \" + order.getUser().getLastName())")
	@Mapping(target = "orderStatus", expression = "java(order.getOrderStatus().name())")
	@Mapping(target = "paymentStatus", expression = "java(order.getPaymentStatus().name())")
	@Mapping(target = "items", source = "items")
	OrderResponse toResponse(Order order);
	
    /**
     * Convert OrderItem entity to OrderItemResponse DTO
     */
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "productSku", source = "product.sku")
	OrderResponse.OrderItemResponse toOrderItemResponse(OrderItem orderItem);
    

    /**
     * Convert list of Order entities to list of OrderResponse DTOs
     */
    List<OrderResponse> toResponseList(List<Order> orders);
    
    /**
     * Convert CreateOrderRequest to Order entity
     * Some fields are set later in service layer
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderNumber", ignore = true)
    @Mapping(target = "orderStatus", ignore = true)
    @Mapping(target = "paymentStatus", ignore = true)
    @Mapping(target = "totalAmount", ignore = true)
    @Mapping(target = "orderedAt", ignore = true)
    @Mapping(target = "shippedAt", ignore = true)
    @Mapping(target = "deliveredAt", ignore = true)
    @Mapping(target = "cancelledAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "items", source = "items")
    Order toEntity(CreateOrderRequest request);
    
    /**
     * Convert OrderItemRequest to OrderItem entity
     * Product is assigned later in service layer
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "unitPrice", ignore = true)
    @Mapping(target = "totalPrice", ignore = true)
    OrderItem toOrderItemEntity(CreateOrderRequest.OrderItemRequest orderItemRequest);

}
