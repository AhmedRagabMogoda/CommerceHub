package com.commercehub.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.commercehub.dto.request.CreateOrderRequest;
import com.commercehub.dto.response.ApiResponse;
import com.commercehub.dto.response.OrderResponse;
import com.commercehub.dto.response.PageResponse;
import com.commercehub.service.OrderService;
import com.commercehub.util.Messages;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/orders")
@Slf4j
@Tag(name = "Orders", description = "Order management APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class OrderController {

	@Autowired
	private OrderService orderService;
	
	@PostMapping
	@Operation(summary = "Create order", description = "Create a new order for current user")
	public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@Valid @RequestBody CreateOrderRequest request)
	{
		log.info("Request to create new order");
		
		OrderResponse response = orderService.createOrder(request);
		
		return ResponseEntity.status(HttpStatus.CREATED)
				             .body(ApiResponse.success(Messages.ORDER_CREATED_SUCCESSFULLY, response));
	}
	
	@GetMapping("/{orderId}")
	@Operation(summary = "Get order by ID", description = "Get order details by ID")
	public ResponseEntity<ApiResponse<OrderResponse>> getOrderId(@PathVariable Long orderId)
	{
		log.debug("Request to get order by ID: {}", orderId);
		
		OrderResponse response = orderService.getOrderById(orderId);
		
		return ResponseEntity.ok(ApiResponse.success(response));
	}
	
    @GetMapping("/number/{orderNumber}")
    @Operation(summary = "Get order by number", description = "Get order details by order number")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderByNumber(@PathVariable String orderNumber) 
    {
        log.debug("Request to get order by number: {}", orderNumber);
        
        OrderResponse response = orderService.getOrderByNumber(orderNumber);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/my-orders")
    @Operation(summary = "Get my orders", description = "Get all orders for current user")
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getMyOrders(@RequestParam(defaultValue = "0") int page,
																	            @RequestParam(defaultValue = "20") int size,
																	            @RequestParam(defaultValue = "orderedAt") String sortBy,
																	            @RequestParam(defaultValue = "DESC") String sortDir)
    {
        log.debug("Request to get orders for current user");
        
        Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        PageResponse<OrderResponse> response = orderService.getCurrentUserOrders(pageable);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user orders", description = "Get all orders for specific user")
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getUserOrders(@PathVariable Long userId,
																	              @RequestParam(defaultValue = "0") int page,
																	              @RequestParam(defaultValue = "20") int size)
    {
        log.debug("Request to get orders for user ID: {}", userId);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("orderedAt").descending());
        
        PageResponse<OrderResponse> response = orderService.getUserOrders(userId, pageable);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all orders", description = "Get all orders (Admin only)")
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getAllOrders(@RequestParam(defaultValue = "0") int page,
																	             @RequestParam(defaultValue = "20") int size,
																	             @RequestParam(defaultValue = "orderedAt") String sortBy,
																	             @RequestParam(defaultValue = "DESC") String sortDir)
    {
        log.debug("Request to get all orders");
        
        Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        PageResponse<OrderResponse> response = orderService.getAllOrders(pageable);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get orders by status", description = "Get orders filtered by status (Admin only)")
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getOrdersByStatus(@PathVariable String status,
																		              @RequestParam(defaultValue = "0") int page,
																		              @RequestParam(defaultValue = "20") int size)
    {
        log.debug("Request to get orders by status: {}", status);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("orderedAt").descending());
        
        PageResponse<OrderResponse> response = orderService.getOrdersByStatus(status, pageable);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get orders by date range", description = "Get orders within date range (Admin only)")
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getOrdersByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size)
    {
        log.debug("Request to get orders between {} and {}", startDate, endDate);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("orderedAt").descending());
        
        PageResponse<OrderResponse> response = orderService.getOrdersBetweenDates(startDate, endDate, pageable);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PatchMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update order status", description = "Update order status (Admin only)")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(@PathVariable Long orderId, @RequestParam String status)
    {
    	log.info("Request to update order status to {} for order ID: {}", status, orderId);
        
    	OrderResponse response = orderService.updateOrderStatus(orderId, status);
    	
    	return ResponseEntity.ok(ApiResponse.success(Messages.ORDER_UPDATED_SUCCESSFULLY, response));
    }

    @PatchMapping("/{orderId}/payment-status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update payment status", description = "Update payment status (Admin only)")
    public ResponseEntity<ApiResponse<OrderResponse>> updatePaymentStatus(@PathVariable Long orderId, @RequestParam String status)
    {
        log.info("Request to update payment status to {} for order ID: {}", status, orderId);
        
        OrderResponse response = orderService.updatePaymentStatus(orderId, status);
        
        return ResponseEntity.ok(ApiResponse.success(Messages.ORDER_UPDATED_SUCCESSFULLY, response));
    }
    
    @PostMapping("/{orderId}/cancel")
    @Operation(summary = "Cancel order", description = "Cancel an order")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(@PathVariable Long orderId)
    {
    	log.info("Request to cancel order with ID: {}", orderId);
    	
        OrderResponse response = orderService.cancelOrder(orderId);
        
        return ResponseEntity.ok(ApiResponse.success(Messages.ORDER_CANCELLED_SUCCESSFULLY, response));
    }
    
    @PostMapping("/{orderId}/ship")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Ship order", description = "Mark order as shipped (Admin only)")
    public ResponseEntity<ApiResponse<OrderResponse>> shipOrder(@PathVariable Long orderId) 
    {
        log.info("Request to ship order with ID: {}", orderId);
        
        OrderResponse response = orderService.shipOrder(orderId);
        
        return ResponseEntity.ok(ApiResponse.success(Messages.ORDER_SHIPPED_SUCCESSFULLY, response));
    }

    @PostMapping("/{orderId}/deliver")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deliver order", description = "Mark order as delivered (Admin only)")
    public ResponseEntity<ApiResponse<OrderResponse>> deliverOrder(@PathVariable Long orderId) 
    {
        log.info("Request to deliver order with ID: {}", orderId);
        
        OrderResponse response = orderService.deliverOrder(orderId);
        
        return ResponseEntity.ok(ApiResponse.success(Messages.ORDER_DELIVERED_SUCCESSFULLY, response));
    }
}
