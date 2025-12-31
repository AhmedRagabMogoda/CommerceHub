package com.commercehub.service.impl;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.commercehub.dto.request.CreateProductRequest;
import com.commercehub.dto.response.PageResponse;
import com.commercehub.dto.response.ProductResponse;
import com.commercehub.entity.Product;
import com.commercehub.exception.BadRequestException;
import com.commercehub.exception.DuplicateResourceException;
import com.commercehub.exception.ResourceNotFoundException;
import com.commercehub.mapper.PageMapper;
import com.commercehub.mapper.ProductMapper;
import com.commercehub.repository.ProductRepository;
import com.commercehub.service.ProductService;
import com.commercehub.util.CacheNames;
import com.commercehub.util.Inventory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of ProductService
 * Handles product management business logic
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService{
	
	private final ProductRepository productRepository;
	
	private final ProductMapper productMapper;
	
	private final PageMapper pageMapper;
	
	

	@CacheEvict(value = {CacheNames.PRODUCTS, CacheNames.PRODUCT_BY_SKU}, allEntries = true)
	@Transactional
	@Override
	public ProductResponse createProduct(CreateProductRequest request) 
	{
		log.info("Creating new product with SKU: {}", request.getSku());
		
		// Check if SKU already exists
		if(productRepository.existsBySku(request.getSku()))
		{
			throw new DuplicateResourceException("Product", "Sku",request.getSku());
		}
		
		Product product = productMapper.toEntity(request);
		
		Product savedProduct = productRepository.save(product);
		
		log.info("Product created successfully with ID: {}", savedProduct.getId());
		
		return productMapper.toResponse(savedProduct);
	}
	
	@Cacheable(value = CacheNames.PRODUCTS, key = "#productId")
	@Transactional(readOnly = true)
	@Override
	public ProductResponse getProductById(Long productId) 
	{
		log.debug("Fetching product by ID: {}", productId);
		
		Product product = productRepository.findById(productId).orElseThrow( () -> new ResourceNotFoundException("Product", "id", productId) );
	
		return productMapper.toResponse(product);
	}

	@Cacheable(value = CacheNames.PRODUCT_BY_SKU, key = "#sku")
	@Transactional(readOnly = true)
	@Override
	public ProductResponse getProductBySku(String sku) 
	{
		log.debug("Fetching product by sku: {}", sku);
		
		Product product = productRepository.findBySku(sku).orElseThrow( () -> new ResourceNotFoundException("Product", "sku", sku) );
	
		return productMapper.toResponse(product);	}

	@Transactional(readOnly = true)
	@Override
	public PageResponse<ProductResponse> getAllProducts(Pageable pageable) 
	{
		log.debug("Fetching all products with pagination");
		
		Page<Product> productPage = productRepository.findAll(pageable);
		
		return pageMapper.toPageResponse(productPage, productMapper::toResponse);
	}

	@Transactional(readOnly = true)
	@Override
	public PageResponse<ProductResponse> getActiveProducts(Pageable pageable) 
	{
		log.debug("Fetching active products with pagination");
		
		Page<Product> productPage = productRepository.findByIsActive(true, pageable);
		
		return pageMapper.toPageResponse(productPage, productMapper::toResponse);
	}

	@Transactional(readOnly = true)
	@Override
	public PageResponse<ProductResponse> getProductsByCategory(String category, Pageable pageable) 
	{
		log.debug("Fetching products by category: {}", category);
		
		Page<Product> productPage = productRepository.findByCategoryAndIsActive(category, true, pageable);
		
		return pageMapper.toPageResponse(productPage, productMapper::toResponse);
	}

	@Transactional(readOnly = true)
	@Override
	public PageResponse<ProductResponse> getProductsByBrand(String brand, Pageable pageable) 
	{
		log.debug("Fetching products by brand: {}", brand);
		
		Page<Product> productPage = productRepository.findByBrand(brand, pageable);
		
		return pageMapper.toPageResponse(productPage, productMapper::toResponse);
	}

	@Transactional(readOnly = true)
	@Override
	public PageResponse<ProductResponse> searchProducts(String keyword, Pageable pageable) 
	{
		log.debug("Searching products with keyword: {}", keyword);

		Page<Product> productPage = productRepository.searchProducts(keyword, pageable);
		
		return pageMapper.toPageResponse(productPage, productMapper::toResponse);
	}

	@Transactional(readOnly = true)
	@Override
	public PageResponse<ProductResponse> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) 
	{
		log.debug("Fetching products with price range: {} - {}", minPrice, maxPrice);
		
		Page<Product> productPage = productRepository.findByPriceRange(minPrice, maxPrice, pageable);
		
		return pageMapper.toPageResponse(productPage, productMapper::toResponse);
	}

	@Transactional(readOnly = true)
	@Override
	public PageResponse<ProductResponse> getInStockProducts(Pageable pageable) 
	{
		log.debug("Fetching in-stock products");
		
		Page<Product> productPage = productRepository.findInStockProducts(pageable);
		
		return pageMapper.toPageResponse(productPage, productMapper::toResponse);
	}

	@Transactional(readOnly = true)
	@Override
	public PageResponse<ProductResponse> getOutOfStockProducts(Pageable pageable) 
	{
		log.debug("Fetching out-of-stock products");
		
		Page<Product> productPage = productRepository.findOutStockProducts(pageable);
		
		return pageMapper.toPageResponse(productPage, productMapper::toResponse);
	}

	@Transactional(readOnly = true)
	@Override
	public PageResponse<ProductResponse> getLowStockProducts(Pageable pageable) 
	{
		log.debug("Fetching low-stock products");
		
		Page<Product> productPage = productRepository.findLowStockproducts(Inventory.LOW_STOCK_THRESHOLD,pageable);
		
		return pageMapper.toPageResponse(productPage, productMapper::toResponse);
	}

	@Transactional(readOnly = true)
	@Override
	public List<String> getAllCategories() 
	{
		log.debug("Fetching all product categories");
		
		return productRepository.findAllCategories();
	}

	@Transactional(readOnly = true)
	@Override
	public List<String> getAllBrands() 
	{
		log.debug("Fetching all product brands");
		
		return productRepository.findAllBrands();
	}

	@CacheEvict(value = {CacheNames.PRODUCTS, CacheNames.PRODUCT_BY_SKU}, allEntries = true)
	@Transactional
	@Override
	public ProductResponse updateProduct(Long productId, CreateProductRequest request) 
	{
		log.info("Updating product with ID: {}", productId);
		
		Product product = productRepository.findById(productId).orElseThrow( () -> new ResourceNotFoundException("product", "productId", productId) );
		
		productMapper.updateEntityFromRequest(request, product);
		
		Product updatedProduct = productRepository.save(product);
		
        log.info("Product updated successfully with ID: {}", productId);

		return productMapper.toResponse(updatedProduct);
	}

	@CacheEvict(value = {CacheNames.PRODUCTS, CacheNames.PRODUCT_BY_SKU}, allEntries = true)
	@Transactional
	@Override
	public ProductResponse updateProductStock(Long productId, Integer quantity) 
	{
        log.info("Updating stock for product ID {} to quantity: {}", productId, quantity);

        if (quantity < 0) 
        {
            throw new BadRequestException("Stock quantity cannot be negative");
        }
		
		Product product = productRepository.findById(productId).orElseThrow( () -> new ResourceNotFoundException("product", "productId", productId) );
		
		product.setQuantityInStock(quantity);
		
		Product updatedProduct = productRepository.save(product);
		
		log.info("Product stock updated successfully for ID: {}", productId);

		return productMapper.toResponse(updatedProduct);
	}

	@CacheEvict(value = {CacheNames.PRODUCTS, CacheNames.PRODUCT_BY_SKU}, allEntries = true)
	@Transactional
	@Override
	public void deleteProduct(Long productId) 
	{
		log.info("Deleting product with ID: {}", productId);
		
		Product product = productRepository.findById(productId).orElseThrow( () -> new ResourceNotFoundException("product", "productId", productId) );
		
		productRepository.delete(product);
		
		log.info("Product deleted successfully with ID: {}", productId);
	}

}
