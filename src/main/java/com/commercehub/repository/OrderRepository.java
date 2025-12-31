package com.commercehub.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.commercehub.entity.Order;
import com.commercehub.util.OrderStatus;
import com.commercehub.util.PaymentStatus;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order>{
	
    /**
     * Find an order by order number
     * 
     * @param orderNumber the order number to search for
     * @return Optional containing the order if found
     */
	Optional<Order> findByOrderNumber(String orderNumber);

    /**
     * Check if an order exists by order number
     * 
     * @param orderNumber the order number to check
     * @return true if order exists, false otherwise
     */
	boolean existsByOrderNumber(String orderNumber);
	
    /**
     * Find orders by user ID with pagination
     * 
     * @param userId the user identifier
     * @param pageable pagination information
     * @return page of orders for the specified user
     */
	Page<Order> findByUserId(Long userId, Pageable pageable);
	
    /**
     * Find orders by status
     * 
     * @param status the order status
     * @param pageable pagination information
     * @return page of orders with the specified status
     */
	Page<Order> findByOrderStatus(OrderStatus orderStatus, Pageable pageable);
	
	
    /**
     * Find orders by user ID and status
     * 
     * @param userId the user identifier
     * @param status the order status
     * @param pageable pagination information
     * @return page of orders matching the criteria
     */
	Page<Order> findByUserIdAndOrderStatus(Long userId, OrderStatus orderStatus, Pageable pageable);
	
    /**
     * Find orders by payment status
     * 
     * @param paymentStatus the payment status
     * @param pageable pagination information
     * @return page of orders with the specified payment status
     */
	Page<Order> findByPaymentStatus(PaymentStatus paymentStatus, Pageable pageable);
	
	   /**
     * Count orders by status
     * 
     * @param status the order status
     * @return count of orders with the specified status
     */
	Long countByOrderStatus(OrderStatus orderStatus);
	
    /**
     * Count orders by user ID
     * 
     * @param userId the user identifier
     * @return count of orders for the user
     */
	Long countByUserId(Long userId);
	
    /**
     * Count orders by user ID and status
     * 
     * @param userId the user identifier
     * @param status the order status
     * @return count of orders matching the criteria
     */
	Long countByUserIdAndOrderStatus(Long userId, OrderStatus orderStatus);
	
    /**
     * Find orders within a date range
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @param pageable pagination information
     * @return page of orders within the date range
     */
	@Query("Select o from Order o where o.orderedAt Between :startDate And :endDate")
	Page<Order> findOrdersBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);
	
    /**
     * Find orders by user ID within a date range
     * 
     * @param userId the user identifier
     * @param startDate the start date
     * @param endDate the end date
     * @param pageable pagination information
     * @return page of orders matching the criteria
     */
	@Query("Select o from Order o where o.user.id = :userId And o.orderedAt Between :startDate And :endDate")
	Page<Order> findUserOrdersBetweenDates(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);
	
    /**
     * Find orders with total amount greater than specified value
     * 
     * @param amount the minimum total amount
     * @param pageable pagination information
     * @return page of orders with total amount greater than specified
     */
	@Query("Select o From Order o Where o.totalAmount >= :minTotalAmount")
   	Page<Order> findOrdersWithTotalAmountGreaterThan(@Param("minTotalAmount") BigDecimal minTotalAmount, Pageable pageable);
	
	  /**
     * Get total revenue for all completed orders
     * 
     * @return total revenue
     */
	@Query("Select Coalesce(Sum(o.totalAmount),0) From Order o Where o.orderStatus = 'DELIVERED' And o.paymentStatus = 'PAID' ")
	BigDecimal getTotalRevenue();
	
    /**
     * Get total revenue for a specific user
     * 
     * @param userId the user identifier
     * @return total revenue for the user
     */
	@Query("Select Coalesce(Sum(o.totalAmount),0) From Order o Where o.user.id = :userId And o.orderStatus = 'DELIVERED' And o.paymentStatus = 'PAID' ")
	BigDecimal getTotalRevenueByUser(@Param("userId") Long userId);
	
    /**
     * Get total revenue within a date range
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @return total revenue within the date range
     */
	@Query("Select Coalesce(Sum(o.totalAmount),0) From Order o Where o.orderedAt Between :startDate And :endDate And o.orderStatus = 'DELIVERED' And o.paymentStatus = 'PAID' ")
	BigDecimal getTotalRevenueBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
	
    /**
     * Find pending orders older than specified date
     * 
     * @param date the cutoff date
     * @return list of pending orders older than the date
     */
	@Query("Select o from Order o Where o.orderStatus = 'PENDING' And o.orderedAt < :date ")
	List<Order> findPendingOrdersOlderThanSpecifid(@Param("date") LocalDateTime date);
	
    /**
     * Find latest orders by user
     * 
     * @param userId the user identifier
     * @param limit the maximum number of orders to return
     * @return list of latest orders for the user
     */
	@Query("Select o From Order o Where o.user.id = :userId Order By o.orderedAt Desc")
	List<Order> findLatestOrdersByUser(@Param("userId") Long userId, Pageable pageable);
	
	/**
     * Get order statistics by status
     * 
     * @return list of order counts grouped by status
     */
	@Query("Select o.orderStatus, Count(o) From Order o Group By o.orderStatus")
	List<Object[]> getOrderStatisticsByOrderStatus();
	
}
