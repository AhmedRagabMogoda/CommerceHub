package com.commercehub.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Pageable;

import com.commercehub.dto.request.CreateProductRequest;
import com.commercehub.dto.response.PageResponse;
import com.commercehub.dto.response.ProductResponse;

/**
* Service interface for product management operations
* Handles product CRUD operations and inventory management
*/

public interface ProductService {

   /**
    * Create a new product
    * 
    * @param request product creation details
    * @return created product response
    */
   ProductResponse createProduct(CreateProductRequest request);

   /**
    * Get product by ID
    * 
    * @param productId the product identifier
    * @return product response DTO
    */
   ProductResponse getProductById(Long productId);

   /**
    * Get product by SKU
    * 
    * @param sku the product SKU
    * @return product response DTO
    */
   ProductResponse getProductBySku(String sku);

   /**
    * Get all products with pagination
    * 
    * @param pageable pagination information
    * @return paginated product responses
    */
   PageResponse<ProductResponse> getAllProducts(Pageable pageable);

   /**
    * Get active products with pagination
    * 
    * @param pageable pagination information
    * @return paginated product responses
    */
   PageResponse<ProductResponse> getActiveProducts(Pageable pageable);

   /**
    * Get products by category
    * 
    * @param category product category
    * @param pageable pagination information
    * @return paginated product responses
    */
   PageResponse<ProductResponse> getProductsByCategory(String category, Pageable pageable);

   /**
    * Get products by brand
    * 
    * @param brand product brand
    * @param pageable pagination information
    * @return paginated product responses
    */
   PageResponse<ProductResponse> getProductsByBrand(String brand, Pageable pageable);

   /**
    * Search products by keyword
    * 
    * @param keyword search keyword
    * @param pageable pagination information
    * @return paginated product responses
    */
   PageResponse<ProductResponse> searchProducts(String keyword, Pageable pageable);

   /**
    * Get products within price range
    * 
    * @param minPrice minimum price
    * @param maxPrice maximum price
    * @param pageable pagination information
    * @return paginated product responses
    */
   PageResponse<ProductResponse> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

   /**
    * Get products that are in stock
    * 
    * @param pageable pagination information
    * @return paginated product responses
    */
   PageResponse<ProductResponse> getInStockProducts(Pageable pageable);

   /**
    * Get products that are out of stock
    * 
    * @param pageable pagination information
    * @return paginated product responses
    */
   PageResponse<ProductResponse> getOutOfStockProducts(Pageable pageable);

   /**
    * Get products with low stock
    * 
    * @param pageable pagination information
    * @return paginated product responses
    */
   PageResponse<ProductResponse> getLowStockProducts(Pageable pageable);

   /**
    * Get all product categories
    * 
    * @return list of categories
    */
   List<String> getAllCategories();

   /**
    * Get all product brands
    * 
    * @return list of brands
    */
   List<String> getAllBrands();

   /**
    * Update product
    * 
    * @param productId the product identifier
    * @param request updated product details
    * @return updated product response
    */
   ProductResponse updateProduct(Long productId, CreateProductRequest request);

   /**
    * Update product stock quantity
    * 
    * @param productId the product identifier
    * @param quantity new stock quantity
    * @return updated product response
    */
   ProductResponse updateProductStock(Long productId, Integer quantity);

   /**
    * Delete product
    * 
    * @param productId the product identifier
    */
   void deleteProduct(Long productId);
}