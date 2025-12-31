package com.commercehub.mapper;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.commercehub.dto.request.CreateProductRequest;
import com.commercehub.dto.response.ProductResponse;
import com.commercehub.entity.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {


    /**
     * Convert Product entity to ProductResponse DTO
     * Includes computed field for stock availability
     */
	@Mapping(target = "inStock", expression = "java(product.isInStock())")
	ProductResponse toResponse(Product product);

    /**
     * Convert list of Product entities to list of ProductResponse DTOs
     */
	List<ProductResponse> toResponseList(List<Product> products);
	
	/**
     * Convert CreateProductRequest DTO to Product entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    @Mapping(target = "isActive", ignore = true)
	Product toEntity(CreateProductRequest request);
    
    /**
     * Update existing Product entity from CreateProductRequest
     * Ignores SKU as it should not be changed after creation
     */
    @Mapping(target = "sku", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastModifiedAt", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "statusCode", ignore = true)
    void updateEntityFromRequest(CreateProductRequest request, @MappingTarget Product product);
    
    /**
     * Partial update of Product entity
     * Only updates non-null fields from request
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "sku", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastModifiedAt", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "statusCode", ignore = true)
    void partialUpdateEntityFromRequest(CreateProductRequest request, @MappingTarget Product product);
	
}
