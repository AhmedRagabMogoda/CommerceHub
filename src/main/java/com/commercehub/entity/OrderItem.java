package com.commercehub.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_items")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem extends BaseEntity {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false)
	private Integer quantity;
	
	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal unitPrice;
	
	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal totalPrice;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinTable(name = "order_id")
	private Order order;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinTable(name = "product_id")
	private Product product;
	
	@PrePersist
	@PreUpdate
	private void prePersistOrUpdate()
	{
		calculateTotalPrice();
	}
	
	/* Helper method to calculate total price */
	public void calculateTotalPrice()
	{
		if(unitPrice != null && quantity != null)
			this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
	}
	
	public boolean isQuantityVaild()
	{
		return quantity != null && quantity > 0;
	}
	
	/* Helper method to set product and unit price */
	public void setProductWithPrice(Product product)
	{
		this.product = product;
		this.unitPrice = product.getPrice();
		calculateTotalPrice();
	}
	
	/* Helper method to validate prices */
	public boolean arePricesVaild()
	{
		return unitPrice != null && unitPrice.compareTo(BigDecimal.ZERO) >= 0 && 
			   totalPrice != null && totalPrice.compareTo(BigDecimal.ZERO) >= 0 ;
	}
	
	/* Helper method to check if total price matches calculation */
	public boolean isTotalPriceCorrect()
	{
		if(unitPrice == null || quantity == null || totalPrice == null)
		{
			return false;
		}
		BigDecimal calculatedTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
		return calculatedTotal.compareTo(totalPrice) == 0;
	}
	
}
