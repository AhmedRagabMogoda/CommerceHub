package com.commercehub.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.commercehub.dto.request.CreateProductRequest;
import com.commercehub.dto.response.ApiResponse;
import com.commercehub.dto.response.PageResponse;
import com.commercehub.dto.response.ProductResponse;
import com.commercehub.service.ProductService;
import com.commercehub.util.Messages;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/products")
@Slf4j
@Tag(name = "Products", description = "Product management APIs")
public class ProductController {
	
	@Autowired
	private ProductService productService;
	
	@PostMapping
	@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Create product", description = "Create a new product (Admin/Manager only)")
	public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@Valid @RequestBody CreateProductRequest request)
	{
		log.info("Request to create product with SKU: {}", request.getSku());
		
		ProductResponse response = productService.createProduct(request);
		
		return ResponseEntity.status(HttpStatus.CREATED).body( ApiResponse.success(Messages.PRODUCT_CREATED_SUCCESSFULLY, response) );
	}
	
    @GetMapping("/{productId}")
    @Operation(summary = "Get product by ID", description = "Get product details by ID")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable Long productId) 
    {
        log.debug("Request to get product by ID: {}", productId);
        
        ProductResponse response = productService.getProductById(productId);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/sku/{sku}")
    @Operation(summary = "Get product by SKU", description = "Get product details by SKU")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductBySku(@PathVariable String sku)
    {
        log.debug("Request to get product by sku: {}", sku);
        
        ProductResponse response = productService.getProductBySku(sku);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping
    @Operation(summary = "Get all products", description = "Get all products with pagination")
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> getAllProducts(@RequestParam(defaultValue = "0") int page,
																			    	 @RequestParam(defaultValue = "20") int size, 
																			    	 @RequestParam(defaultValue = "createdAt") String sortBy,
																			         @RequestParam(defaultValue = "DESC") String sortDir )
    {
    	log.debug("Request to get all products - page: {}, size: {}", page, size);
    	
    	Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
    	
    	Pageable pageable = PageRequest.of(page, size, sort);
    	
    	PageResponse<ProductResponse> pageResponse = productService.getAllProducts(pageable);
    	
    	return ResponseEntity.ok( ApiResponse.success(pageResponse));
    }
    
    @GetMapping("/active")
    @Operation(summary = "Get active products", description = "Get all active products")
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> getActiveProducts(@RequestParam(defaultValue = "0") int page,
    																					@RequestParam(defaultValue = "20") int size)
    {
        log.debug("Request to get active products");
        
        Pageable pageable = PageRequest.of(page, size);
        
        PageResponse<ProductResponse> response = productService.getActiveProducts(pageable);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Get products by category", description = "Get products filtered by category")
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> getProductsByCategory(@PathVariable String category,
																				            @RequestParam(defaultValue = "0") int page,
																				            @RequestParam(defaultValue = "20") int size)
    {
        log.debug("Request to get products by category: {}", category);
        
        Pageable pageable = PageRequest.of(page, size);
        
        PageResponse<ProductResponse> response = productService.getProductsByCategory(category, pageable);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/brand/{brand}")
    @Operation(summary = "Get products by brand", description = "Get products filtered by brand")
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> getProductsByBrand(@PathVariable String brand,
																				         @RequestParam(defaultValue = "0") int page,
																				         @RequestParam(defaultValue = "20") int size)
    {
        log.debug("Request to get products by brand: {}", brand);
        
        Pageable pageable = PageRequest.of(page, size);
        
        PageResponse<ProductResponse> response = productService.getProductsByBrand(brand, pageable);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/search")
    @Operation(summary = "Search products", description = "Search products by keyword")
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> searchProducts(@RequestParam String keyword,
																		             @RequestParam(defaultValue = "0") int page,
																		             @RequestParam(defaultValue = "20") int size) 
    {
        log.debug("Request to search products with keyword: {}", keyword);
        
        Pageable pageable = PageRequest.of(page, size);
        
        PageResponse<ProductResponse> response = productService.searchProducts(keyword, pageable);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/price-range")
    @Operation(summary = "Get products by price range", description = "Get products within price range")
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> getProductsByPriceRange(@RequestParam BigDecimal minPrice,
																					          @RequestParam BigDecimal maxPrice,
																					          @RequestParam(defaultValue = "0") int page,
																					          @RequestParam(defaultValue = "20") int size)
    {
        log.debug("Request to get products with price range: {} - {}", minPrice, maxPrice);
        
        Pageable pageable = PageRequest.of(page, size);
        
        PageResponse<ProductResponse> response = productService.getProductsByPriceRange(minPrice, maxPrice, pageable);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/in-stock")
    @Operation(summary = "Get in-stock products", description = "Get products that are in stock")
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> getInStockProducts(@RequestParam(defaultValue = "0") int page,
    																					 @RequestParam(defaultValue = "20") int size )
    {
        log.debug("Request to get in-stock products");
        
        Pageable pageable = PageRequest.of(page, size);
        
        PageResponse<ProductResponse> response = productService.getInStockProducts(pageable);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/categories")
    @Operation(summary = "Get all categories", description = "Get list of all product categories")
    public ResponseEntity<ApiResponse<List<String>>> getAllCategories() 
    {
        log.debug("Request to get all categories");
        
        List<String> categories = productService.getAllCategories();
        
        return ResponseEntity.ok(ApiResponse.success(categories));
    }

    @GetMapping("/brands")
    @Operation(summary = "Get all brands", description = "Get list of all product brands")
    public ResponseEntity<ApiResponse<List<String>>> getAllBrands() 
    {
        log.debug("Request to get all brands");
        
        List<String> brands = productService.getAllBrands();
        
        return ResponseEntity.ok(ApiResponse.success(brands));
    }
    
    @PutMapping("/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Update product", description = "Update product details (Admin/Manager only)")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(@PathVariable Long productId, @Valid @RequestBody CreateProductRequest request)
    {
        log.info("Request to update product with ID: {}", productId);
        
        ProductResponse response = productService.updateProduct(productId, request);
        
        return ResponseEntity.ok(ApiResponse.success(Messages.PRODUCT_UPDATED_SUCCESSFULLY, response));
    }
    
    @PatchMapping("/{productId}/stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Update product stock", description = "Update product stock quantity (Admin/Manager only)")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProductStock(@PathVariable Long productId, @RequestParam Integer quantity)
    {
    	log.info("Request to update stock for product ID: {}", productId);
    	
    	ProductResponse response = productService.updateProductStock(productId, quantity);
    	
    	return ResponseEntity.ok(ApiResponse.success(Messages.PRODUCT_UPDATED_SUCCESSFULLY, response));
    }
    
    @DeleteMapping("/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Delete product", description = "Delete product (Admin only)")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long productId)
    {
    	log.info("Request to delete for product with ID: {}", productId);
    	
    	productService.deleteProduct(productId);
    	
    	return ResponseEntity.ok(ApiResponse.success(Messages.PRODUCT_DELETED_SUCCESSFULLY));
    }

}
