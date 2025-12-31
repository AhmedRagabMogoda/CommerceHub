package com.commercehub.util;

/**
 * Cache names for Redis caching
 */

public final class CacheNames {
    private CacheNames() {}
    
    public static final String USERS = "users";
    public static final String USER_BY_USERNAME = "userByUsername";
    public static final String USER_BY_EMAIL = "userByEmail";
    public static final String PRODUCTS = "products";
    public static final String PRODUCT_BY_SKU = "productBySku";
    public static final String ORDERS = "orders";
    public static final String ORDER_BY_NUMBER = "orderByNumber";
    public static final String ROLES = "roles";
    public static final String CATEGORIES = "categories";
    public static final String BRANDS = "brands";
}