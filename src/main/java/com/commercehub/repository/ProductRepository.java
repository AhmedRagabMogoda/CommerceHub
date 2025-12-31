package com.commercehub.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.commercehub.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product>{
	
    /**
     * Find a product by SKU
     * 
     * @param sku the product SKU to search for
     * @return Optional containing the product if found
     */
	Optional<Product> findBySku(String sku);
	
    /**
     * Check if a product exists by SKU
     * 
     * @param sku the SKU to check
     * @return true if product exists, false otherwise
     */
	boolean existsBySku(String sku);
	
    /**
     * Find all active products with pagination
     * 
     * @param isActive the active status to filter by
     * @param pageable pagination information
     * @return page of products matching the criteria
     */
	Page<Product> findByIsActive(Boolean isActive, Pageable pageable);
	
    /**
     * Find products by category
     * 
     * @param category the product category
     * @param pageable pagination information
     * @return page of products in the specified category
     */
	Page<Product> findByCategory(String category, Pageable pageable);
	
    /**
     * Find products by brand
     * 
     * @param brand the product brand
     * @param pageable pagination information
     * @return page of products from the specified brand
     */
	Page<Product> findByBrand(String brand, Pageable pageable);
	
    /**
     * Find active products by category
     * 
     * @param category the product category
     * @param isActive the active status
     * @param pageable pagination information
     * @return page of active products in the specified category
     */
	Page<Product> findByCategoryAndIsActive(String category,Boolean isActive, Pageable pageable);
	
    /**
     * Count products by category
     * 
     * @param category the product category
     * @return count of products in the category
     */
	Long countByCategory(String category);
	
    /**
     * Count active products
     * 
     * @param isActive the active status
     * @return count of active products
     */
	Long countByIsActive(Boolean isActive);
	
    /**
     * Find products with price within a range
     * 
     * @param minPrice the minimum price
     * @param maxPrice the maximum price
     * @param pageable pagination information
     * @return page of products within the price range
     */
	@Query("Select p From Product p Where p.price Between :minPrice And :maxPrice And p.isActive = True")
	Page<Product> findByPriceRange(@Param("minPrice") BigDecimal minprice, @Param("maxPrice") BigDecimal maxPrice, Pageable pageable);

	/**
     * Search products by keyword in name or description
     * 
     * @param keyword the search keyword
     * @param pageable pagination information
     * @return page of products matching the search criteria
     */
	@Query("Select p From Product p Where p.isActive=True And "
			+ "Lower(p.name) Like Lower(Concat('%',:keyword,'%')) OR "
			+ "Lower(CAST(p.description AS string)) Like Lower(Concat('%',:keyword,'%'))")
	Page<Product> searchProducts(@Param("keyword") String keyword, Pageable pageable);

    /**
     * Find products that are in stock
     * 
     * @param pageable pagination information
     * @return page of products with quantity greater than zero
     */
	@Query("Select p From Product p Where p.isActive=True And p.quantityInStock > 0")
	Page<Product> findInStockProducts(Pageable pageable);

    /**
     * Find products that are out of stock
     * 
     * @param pageable pagination information
     * @return page of products with quantity equal to zero
     */
	@Query("Select p From Product p Where p.isActive=True And p.quantityInStock = 0")
	Page<Product> findOutStockProducts(Pageable pageable);
	
    /**
     * Find products with low stock (below threshold)
     * 
     * @param threshold the stock threshold
     * @param pageable pagination information
     * @return page of products with low stock
     */
	@Query("Select p From Product p Where p.isActive=True And p.quantityInStock > 0 And p.quantityInStock <= :threshold")
	Page<Product> findLowStockproducts(@Param("threshold") Integer threshold, Pageable pageable);
	
    /**
     * Get all distinct categories
     * 
     * @return list of distinct product categories
     */
	@Query("Select Distinct p.category From Product p Where p.isActive=True And p.category Is Not Null Order By p.category")
	List<String> findAllCategories();
	
    /**
     * Get all distinct brands
     * 
     * @return list of distinct product brands
     */
	@Query("Select Distinct p.brand From Product p Where p.isActive=True And p.brand Is Not Null Order By p.brand")
	List<String> findAllBrands();
	
    /**
     * Update product stock quantity
     * 
     * @param productId the product identifier
     * @param quantity the new quantity
     */
	@Modifying
	@Query("Update Product p Set p.quantityInStock = :quantity Where p.id = :id")
	void updateStock(@Param("quantity") Integer quantity, @Param("id") Long id);
	
    /**
     * Decrease product stock quantity
     * 
     * @param productId the product identifier
     * @param quantity the quantity to decrease
     */
	@Modifying
	@Query("Update Product p Set p.quantityInStock = p.quantityInStock - :quantity Where p.id = :id And p.quantityInStock >= :quantity")
	void decreaseStock(@Param("quantity") Integer quantity, @Param("id") Long id);
	
}
