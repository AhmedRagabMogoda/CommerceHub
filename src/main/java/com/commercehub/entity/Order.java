package com.commercehub.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.commercehub.util.OrderStatus;
import com.commercehub.util.PaymentStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "orders")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order extends BaseEntity{
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, unique = true)
	private String orderNumber;
	
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	@Builder.Default
	private OrderStatus orderStatus=OrderStatus.PENDING;
	
	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal totalAmount;

	
	@Column(nullable = false, columnDefinition = "TEXT")
	private String shippingAddress;
	
	@Column(nullable = false, columnDefinition = "TEXT")
	private String billingAddress;
	
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	@Builder.Default
	private PaymentStatus paymentStatus = PaymentStatus.UNPAID;
	
	@Column(columnDefinition = "TEXT")
	private String notes;
	
	private String paymentMethod;
	
	@Column(nullable = false)
	@Builder.Default
	private LocalDateTime orderedAt = LocalDateTime.now();
	
	private LocalDateTime shippedAt;
	
	private LocalDateTime deliveredAt;
	
	private LocalDateTime cancelledAt;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;
	
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<OrderItem> items = new ArrayList<>();
	
	public void addOrderItem(OrderItem item)
	{
		this.items.add(item);
		item.setOrder(this);
	}
	
	public void removeOrderItem(OrderItem item)
	{
		this.items.remove(item);
		item.setOrder(null);
	}
	
	/* Helper method to calculate total amount from order items */
	public void calculateTotalAmount()
	{
	    BigDecimal total = BigDecimal.ZERO;

	    for (OrderItem item : items) {
	        total = total.add(item.getTotalPrice());
	    }

	    this.totalAmount = total;
	}
	
	/* Helper method to check if order can be cancelled*/
	public boolean canBeCancelled()
	{ 
		return orderStatus == OrderStatus.CONFIRMED || 
			   orderStatus == OrderStatus.PENDING   || 
			   orderStatus == OrderStatus.PROCESSING;
	}
	
	/* Helper method to check if order is completed */
	public boolean isCompleted()
	{
		return orderStatus == OrderStatus.DELIVERED;
	}
	
	/* Helper method to check if order is in progress */
	public boolean isInProcess()
	{
		return orderStatus == OrderStatus.SHIPPED || 
			   orderStatus == OrderStatus.PROCESSING;
	}
	
    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", orderNumber='" + orderNumber + '\'' +
                ", status=" + orderStatus +
                ", totalAmount=" + totalAmount +
                ", paymentStatus=" + paymentStatus +
                ", orderedAt=" + orderedAt +
                '}';
    }
}
