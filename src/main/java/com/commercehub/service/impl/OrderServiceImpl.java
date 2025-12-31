package com.commercehub.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.commercehub.dto.request.CreateOrderRequest;
import com.commercehub.dto.response.OrderResponse;
import com.commercehub.dto.response.PageResponse;
import com.commercehub.entity.Order;
import com.commercehub.entity.OrderItem;
import com.commercehub.entity.Product;
import com.commercehub.entity.User;
import com.commercehub.exception.BadRequestException;
import com.commercehub.exception.ForbiddenException;
import com.commercehub.exception.InsufficientStockException;
import com.commercehub.exception.ResourceNotFoundException;
import com.commercehub.mapper.OrderMapper;
import com.commercehub.mapper.PageMapper;
import com.commercehub.repository.OrderNumberRepository;
import com.commercehub.repository.OrderRepository;
import com.commercehub.repository.ProductRepository;
import com.commercehub.repository.UserRepository;
import com.commercehub.security.SecurityUtils;
import com.commercehub.service.OrderService;
import com.commercehub.util.OrderStatus;
import com.commercehub.util.PaymentStatus;
import com.commercehub.util.RoleName;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService{
	
	private final OrderRepository orderRepository;
	
	private final UserRepository userRepository;
	
	private final ProductRepository productRepository;
	
	private final OrderNumberRepository orderNumberRepository;
	
	private final OrderMapper orderMapper;
	
	private final PageMapper pageMapper;
	
	
    /**
     * Check if current user can access the order
     */
    private void checkOrderAccess(Order order) 
    {
        if (!SecurityUtils.isOwnerOrHasAuthority(order.getUser().getId(), RoleName.ROLE_ADMIN.name())) 
        {
            throw new ForbiddenException("You don't have permission to access this order");
        }
    }
    
    /**
     * Generates a unique order number in the format:
     * ORD-YYYY-XXXXXX
     *
     * @return generated order number as String
     */
    @Transactional
    private String orderNumberGenerator() 
    {
        int year = LocalDateTime.now().getYear();
        Long seq = orderNumberRepository.getNextSequence();

        /** pad sequence to 6 digits */
        return "ORD-" + year + "-" + String.format("%06d", seq);
    }
        
	@Transactional
	@Override
	public OrderResponse createOrder(CreateOrderRequest request) 
	{
		log.info("Creating new order for user");
		
		Long userId = SecurityUtils.getCurrentUserIdOrThrow();
		User user = userRepository.findById(userId).orElseThrow( () -> new ResourceNotFoundException("User", "id", userId) );
		
		// Create order
		Order order = Order.builder()
				.orderNumber(orderNumberGenerator())
				.user(user)
				.orderStatus(OrderStatus.PENDING)
				.paymentStatus(PaymentStatus.UNPAID)
				.shippingAddress(request.getShippingAddress())
				.billingAddress(request.getBillingAddress())
				.paymentMethod(request.getPaymentMethod())
				.notes(request.getNotes())
				.orderedAt(LocalDateTime.now())
				.items(new ArrayList<>())
				.build();
		
		// Process order items
		List<OrderItem> orderItems = new ArrayList<>();
		BigDecimal totalAmount = BigDecimal.ZERO;
		
		for(CreateOrderRequest.OrderItemRequest itemRequest : request.getItems())
		{
			Product product = productRepository.findById(itemRequest.getProductId()).orElseThrow( () -> new ResourceNotFoundException("Product", "id", itemRequest.getProductId()) );
			
			// Check stock availability
			if(!product.isQuantityAvailable(itemRequest.getQuantity()))
			{
				throw new InsufficientStockException( product.getName(), itemRequest.getQuantity(), product.getQuantityInStock() );
			}
			
			// Create order item
			OrderItem orderItem = OrderItem.builder()
					.order(order)
					.product(product)
					.quantity(itemRequest.getQuantity())
					.unitPrice(product.getPrice())
					.build();
			
			orderItem.calculateTotalPrice();
			
			orderItems.add(orderItem);
			totalAmount = totalAmount.add(orderItem.getTotalPrice());
			
			// Decrease product stock
			product.decreaseStock(itemRequest.getQuantity());
			productRepository.save(product);
		}
		
		order.setItems(orderItems);
		order.setTotalAmount(totalAmount);
		
		Order savedOrder = orderRepository.save(order);
		
		log.info("Order created successfully with number: {}", savedOrder.getOrderNumber());

		return orderMapper.toResponse(savedOrder);
	}

	@Transactional(readOnly = true)
	@Override
	public OrderResponse getOrderById(Long orderId) 
	{
		log.debug("Fetching order by ID: {}", orderId);
		
		Order order = orderRepository.findById(orderId).orElseThrow( ( ) -> new ResourceNotFoundException("Order", "orderId", orderId) );
		
		checkOrderAccess(order);
		
		return orderMapper.toResponse(order);
	}

	@Transactional(readOnly = true)
	@Override
	public OrderResponse getOrderByNumber(String orderNumber) 
	{
		log.debug("Fetching order by Number : {}", orderNumber);
		
		Order order = orderRepository.findByOrderNumber(orderNumber).orElseThrow( ( ) -> new ResourceNotFoundException("Order", "number", orderNumber) );
		
		checkOrderAccess(order);
		
		return orderMapper.toResponse(order);
	}

	@Transactional(readOnly = true)
	@Override
	public PageResponse<OrderResponse> getCurrentUserOrders(Pageable pageable) 
	{
		log.debug("Fetching orders for current user");

		Long userId = SecurityUtils.getCurrentUserIdOrThrow();
		
		Page<Order> pageOrder = orderRepository.findByUserId(userId, pageable);
		
		return pageMapper.toPageResponse(pageOrder, orderMapper::toResponse);
	}

	@Transactional(readOnly = true)
	@Override
	public PageResponse<OrderResponse> getUserOrders(Long userId, Pageable pageable) 
	{
		log.debug("Fetching orders for user id: {}", userId);
		
		// Only admins or the user themselves can view their orders
		if(!SecurityUtils.isOwnerOrHasAuthority(userId, RoleName.ROLE_ADMIN.name()))
		{
			throw new ForbiddenException("You don't have permission to view these orders"); 
		}
		
		Page<Order> pageOrder = orderRepository.findByUserId(userId, pageable);
		
		return pageMapper.toPageResponse(pageOrder, orderMapper::toResponse);
	}

	@Transactional(readOnly = true)
	@Override
	public PageResponse<OrderResponse> getAllOrders(Pageable pageable) 
	{
		log.debug("Fetching All orders");
		
		// Only admins can view their orders
		if(!SecurityUtils.hasAuthority(RoleName.ROLE_ADMIN.name()))
		{
			throw new ForbiddenException("You don't have permission to view these orders"); 
		}
		
		Page<Order> pageOrder = orderRepository.findAll(pageable);
		
		return pageMapper.toPageResponse(pageOrder, orderMapper::toResponse);
	}

	@Transactional(readOnly = true)
	@Override
	public PageResponse<OrderResponse> getOrdersByStatus(String status, Pageable pageable) 
	{
		log.debug("Fetching All orders by Status : {}",status);
		
		// Only admins can view their orders
		if(!SecurityUtils.hasAuthority(RoleName.ROLE_ADMIN.name()))
		{
			throw new ForbiddenException("You don't have permission to view these orders"); 
		}
		
		OrderStatus orderStatus;
		try 
		{
			orderStatus = OrderStatus.valueOf( status.toUpperCase() );
			
		} catch(IllegalArgumentException ex) 
		{
			throw new BadRequestException("Invalid order status: " + status);
		}
		
		Page<Order> pageOrder = orderRepository.findByOrderStatus(orderStatus, pageable);
		
		return pageMapper.toPageResponse(pageOrder, orderMapper::toResponse);
	}

	@Transactional(readOnly = true)
	@Override
	public PageResponse<OrderResponse> getOrdersBetweenDates(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) 
	{
		log.debug("Fetching orders between dates: {} and {}", startDate, endDate);
		
		// Only admins can view their orders
		if(!SecurityUtils.hasAuthority(RoleName.ROLE_ADMIN.name()))
		{
			throw new ForbiddenException("You don't have permission to view these orders"); 
		}
		
		Page<Order> pageOrder = orderRepository.findOrdersBetweenDates(startDate, endDate, pageable);
		
		return pageMapper.toPageResponse(pageOrder, orderMapper::toResponse);
	}

	@Transactional
	@Override
	public OrderResponse updateOrderStatus(Long orderId, String status)
	{
		log.info("Updating order status to {} for order ID: {}", status, orderId);
		
		// Only admins can update order status
		if(!SecurityUtils.hasAuthority(RoleName.ROLE_ADMIN.name()))
		{
			throw new ForbiddenException("You don't have permission to view these orders"); 
		}
		
		OrderStatus orderStatus;
		try 
		{
			orderStatus = OrderStatus.valueOf( status.toUpperCase() );
			
		} catch(IllegalArgumentException ex) 
		{
			throw new BadRequestException("Invalid order status: " + status);
		}
		
		Order order = orderRepository.findById(orderId).orElseThrow( ( ) -> new ResourceNotFoundException("Order", "orderId", orderId) );
		
		order.setOrderStatus(orderStatus);
		
		Order updatedOrder = orderRepository.save(order);
		
		log.info("Order status updated successfully for ID: {}", orderId);
		
		return orderMapper.toResponse(updatedOrder);
	}

	@Transactional
	@Override
	public OrderResponse updatePaymentStatus(Long orderId, String status) 
	{
		log.info("Updating order Payment status to {} for order ID: {}", status, orderId);
		
		// Only admins can update order payment status
		if(!SecurityUtils.hasAuthority(RoleName.ROLE_ADMIN.name()))
		{
			throw new ForbiddenException("You don't have permission to view these orders"); 
		}
		
		PaymentStatus paymentStatus;
		try 
		{
			paymentStatus = PaymentStatus.valueOf( status.toUpperCase() );
			
		} catch(IllegalArgumentException ex) 
		{
			throw new BadRequestException("Invalid order status: " + status);
		}
		
		Order order = orderRepository.findById(orderId).orElseThrow( ( ) -> new ResourceNotFoundException("Order", "orderId", orderId) );
		
		order.setPaymentStatus(paymentStatus);
		
		Order updatedOrder = orderRepository.save(order);
		
		log.info("Order payment status updated successfully for ID: {}", orderId);
		
		return orderMapper.toResponse(updatedOrder);
	}
	
	@Transactional
	@Override
	public OrderResponse cancelOrder(Long orderId) 
	{
		log.info("Cancelling order with ID: {}", orderId);
		
		Order order = orderRepository.findById(orderId).orElseThrow( ( ) -> new ResourceNotFoundException("Order", "orderId", orderId) );
		
		// Check access
		checkOrderAccess(order);
		
        // Check if order can be cancelled
		if(!order.canBeCancelled())
		{
			throw new BadRequestException("Order cannot be cancelled in current status: " + order.getOrderStatus().name());
		}
		
		// Restore product stock
		for(OrderItem item : order.getItems())
		{
			Product product = item.getProduct();
			product.increaseStock(item.getQuantity());
			productRepository.save(product);
		}
		
		order.setOrderStatus(OrderStatus.CANCELLED);
		order.setCancelledAt(LocalDateTime.now());
		
		Order updatedOrder = orderRepository.save(order);
		
		log.info("Order cancelled successfully with ID: {}", orderId);
		
		return orderMapper.toResponse(updatedOrder);
	}
	
	@Transactional
	@Override
	public OrderResponse shipOrder(Long orderId) 
	{
		log.info("Marking order as shipped with ID: {}", orderId);
		
		Order order = orderRepository.findById(orderId).orElseThrow( ( ) -> new ResourceNotFoundException("Order", "orderId", orderId) );
		
		// Check access
		checkOrderAccess(order);
		
		if(order.getOrderStatus() != OrderStatus.PROCESSING)
		{
			throw new BadRequestException("Order must be in PROCESSING status to be shipped. Current status: " + order.getOrderStatus());
		}
		
		order.setOrderStatus(OrderStatus.SHIPPED);
		order.setShippedAt(LocalDateTime.now());
		
		Order updatedOrder = orderRepository.save(order);
		
		log.info("Order marked as shipped successfully with ID: {}", orderId);
		
		return orderMapper.toResponse(updatedOrder);
	}

	@Transactional
	@Override
	public OrderResponse deliverOrder(Long orderId) 
	{
		log.info("Marking order as delivered with ID: {}", orderId);
		
		Order order = orderRepository.findById(orderId).orElseThrow( ( ) -> new ResourceNotFoundException("Order", "orderId", orderId) );
		
		// Check access
		checkOrderAccess(order);
		
		if(order.getOrderStatus() != OrderStatus.SHIPPED)
		{
			throw new BadRequestException("Order must be in shipped status to be delivered. Current status: " + order.getOrderStatus());
		}
		
		order.setOrderStatus(OrderStatus.DELIVERED);
		order.setDeliveredAt(LocalDateTime.now());
		
		Order updatedOrder = orderRepository.save(order);
		
		log.info("Order marked as delivered successfully with ID: {}", orderId);
		
		return orderMapper.toResponse(updatedOrder);
	}

}
