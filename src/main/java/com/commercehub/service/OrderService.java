package com.commercehub.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Pageable;

import com.commercehub.dto.request.CreateOrderRequest;
import com.commercehub.dto.response.OrderResponse;
import com.commercehub.dto.response.PageResponse;

/**
 * Service interface for order management operations
 * Handles order creation, updates, and queries
 */

public interface OrderService {

    /**
     * Create a new order for the current user
     * 
     * @param request order creation details
     * @return created order response
     */
    OrderResponse createOrder(CreateOrderRequest request);

    /**
     * Get order by ID
     * 
     * @param orderId the order identifier
     * @return order response DTO
     */
    OrderResponse getOrderById(Long orderId);

    /**
     * Get order by order number
     * 
     * @param orderNumber the order number
     * @return order response DTO
     */
    OrderResponse getOrderByNumber(String orderNumber);

    /**
     * Get all orders for current user
     * 
     * @param pageable pagination information
     * @return paginated order responses
     */
    PageResponse<OrderResponse> getCurrentUserOrders(Pageable pageable);

    /**
     * Get all orders for a specific user
     * 
     * @param userId the user identifier
     * @param pageable pagination information
     * @return paginated order responses
     */
    PageResponse<OrderResponse> getUserOrders(Long userId, Pageable pageable);

    /**
     * Get all orders (admin only)
     * 
     * @param pageable pagination information
     * @return paginated order responses
     */
    PageResponse<OrderResponse> getAllOrders(Pageable pageable);

    /**
     * Get orders by status
     * 
     * @param status order status
     * @param pageable pagination information
     * @return paginated order responses
     */
    PageResponse<OrderResponse> getOrdersByStatus(String status, Pageable pageable);

    /**
     * Get orders within date range
     * 
     * @param startDate start date
     * @param endDate end date
     * @param pageable pagination information
     * @return paginated order responses
     */
    PageResponse<OrderResponse> getOrdersBetweenDates(
            LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * Update order status
     * 
     * @param orderId the order identifier
     * @param status new order status
     * @return updated order response
     */
    OrderResponse updateOrderStatus(Long orderId, String status);

    /**
     * Update payment status
     * 
     * @param orderId the order identifier
     * @param paymentStatus new payment status
     * @return updated order response
     */
    OrderResponse updatePaymentStatus(Long orderId, String paymentStatus);

    /**
     * Cancel order
     * 
     * @param orderId the order identifier
     * @return updated order response
     */
    OrderResponse cancelOrder(Long orderId);

    /**
     * Mark order as shipped
     * 
     * @param orderId the order identifier
     * @return updated order response
     */
    OrderResponse shipOrder(Long orderId);

    /**
     * Mark order as delivered
     * 
     * @param orderId the order identifier
     * @return updated order response
     */
    OrderResponse deliverOrder(Long orderId);
}