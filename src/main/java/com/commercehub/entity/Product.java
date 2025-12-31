package com.commercehub.entity;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "products")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false)
	private String name;
	
	@Column(columnDefinition = "TEXT")
	private String description;
	
	@Column(unique = true ,nullable = false)
	private String sku;
	
	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal price;
	
	@Column(nullable = false)
	@Builder.Default
	private Integer quantityInStock = 0;
	
	private String category;
	
	private String brand;
	
	private String imageUrl;
	
	@Column(nullable = false)
	@Builder.Default
	private Boolean isActive = true;
	
	@Column(precision = 10, scale = 2)
	private BigDecimal weight;
	
	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private Set<OrderItem> orderItems = new HashSet<>();
	
	/* Helper method to check if product is in stock */
	public boolean isInStock()
	{
		return quantityInStock != null && quantityInStock > 0;
	}
	
	/* Helper method to check if quantity is available */
	public boolean isQuantityAvailable(int requestedQuantity)
	{
		return quantityInStock != null && quantityInStock >= requestedQuantity;
	}
	
	/* Helper method to decrease stock*/
	public void decreaseStock(int quantity)
	{
		if(!isQuantityAvailable(quantity))
		{
			throw new IllegalStateException("Insufficient stock for product : " + this.name);
		}
		this.quantityInStock -= quantity;
	}
	
	/* Helper method to increase stock*/
	public void increaseStock(int quantity)
	{
		if( quantity < 0)
		{
			throw new IllegalArgumentException("Quantity cannot be negative");
		}
		this.quantityInStock += quantity;
	}

	@Override
	public String toString() {
		return "Product [ id=" + id + ", name=" + name + ", sku=" + sku + ", price=" + price + ", quantityInStock=" + quantityInStock
				+ ", isActive=" + isActive + "]";
	}
	
	
}
