# E-CommerceHub Backend

**Comprehensive e-commerce backend built with Spring Boot**, supporting JWT authentication, Redis caching and complete RESTful APIs for a professional e-commerce platform.

---

## 🚀 Project Overview

This backend application is designed to support a modern e-commerce platform with a modular, scalable, and secure architecture. The system provides:

- **User Authentication & Authorization** with JWT and role-based access control  
- **Product Management** with search, filtering, and pagination  
- **Order Management** supporting full lifecycle operations  
- **Caching** for optimized performance  
- **Database Versioning** using Flyway  
- **Secure Password Storage** using BCrypt  
- **Monitoring & Logging** with Spring Boot Actuator and Logback  

The architecture follows **RESTful principles** and is production-ready with environment-based configurations.

---


## 🛠 Tech Stack

### Backend Framework
- **Spring Boot** - Core framework
- **Spring Security** - Authentication & authorization
- **Spring Data JPA** - Data access layer

### Database & Persistence
- **Microsoft SQL Server** - Primary database
- **Flyway** - Database migration tool
- **HikariCP** - JDBC connection pool

### Security & Authentication
- **JWT** - Token-based authentication
- **BCrypt** - Password encryption
- **Spring Security** - Security framework

### Tools & Libraries
- **MapStruct** - Object mapping
- **Lombok** - Boilerplate code reduction
- **Swagger/OpenAPI** - API documentation
- **SLF4J + Logback** - Logging framework

---

## 🏗 Architecture

### Project Structure

```
CommerceHub-backend/
│
│
├── pom.xml
├── README.md
│
└── src
    ├── main
    │   ├── java
    │   │   └── com.commercehub
    │   │       │
    │   │       ├── CommerceHubApplication
    │   │       │
    │   │       ├── config
    │   │       │   ├── AppConfig
    │   │       │   └── SwaggerConfig
    │   │       │
    │   │       ├── security
    │   │       │   ├── SecurityConfig
    │   │       │   ├── JwtAuthenticationFilter
    │   │       │   ├── JwtService
    │   │       │   ├── CustomUserDetailsService
    │   │       │   ├── UserPrincipal
    │   │       │   └── SecurityUtils
    │   │       │
    │   │       ├── controller
    │   │       │   ├── AuthController
    │   │       │   ├── UserController
    │   │       │   ├── ProductController
    │   │       │   └── OrderController
    │   │       │
    │   │       ├── service
    │   │       │   ├── AuthService
    │   │       │   ├── UserService
    │   │       │   ├── ProductService
    │   │       │   ├── OrderService
    │   │       │   │
    │   │       │   └── impl
    │   │       │       ├── AuthServiceImpl
    │   │       │       ├── UserServiceImpl
    │   │       │       ├── ProductServiceImpl
    │   │       │       └── OrderServiceImpl
    │   │       │
    │   │       ├── repository
    │   │       │   ├── UserRepository
    │   │       │   ├── RoleRepository
    │   │       │   ├── ProductRepository
    │   │       │   ├── OrderRepository
    │   │       │   └── OrderItemRepository
    │   │       │
    │   │       ├── entity
    │   │       │   ├── User
    │   │       │   ├── Role
    │   │       │   ├── Product
    │   │       │   ├── Order
    │   │       │   └── OrderItem
    │   │       │
    │   │       ├── dto
    │   │       │   ├── request
    │   │       │   │   ├── LoginRequest
    │   │       │   │   ├── RegisterRequest
    │   │       │   │   ├── CreateProductRequest
    │   │       │   │   └── CreateOrderRequest
    │   │       │   │
    │   │       │   └── response
    │   │       │       ├── AuthResponse
    │   │       │       ├── UserResponse
    │   │       │       ├── ProductResponse
    │   │       │       ├── OrderResponse
    │   │       │       ├── ApiResponse
    │   │       │       └── PageResponse
    │   │       │
    │   │       ├── mapper
    │   │       │   ├── UserMapper
    │   │       │   ├── ProductMapper
    │   │       │   ├── OrderMapper
    │   │       │   └── PageMapper
    │   │       │
    │   │       ├── exception
    │   │       │   ├── GlobalExceptionHandler
    │   │       │   ├── BaseException
    │   │       │   ├── DuplicateResourceException
    │   │       │   ├── ForbiddenException
    │   │       │   ├── InsufficientStockException
    │   │       │   ├── ResourceNotFoundException
    │   │       │   ├── BadRequestException
    │   │       │   └── UnauthorizedException
    │   │       │
    │   │       └── util
    │   │           ├── Messages
    │   │           ├── JwtConstants
    │   │           ├── Inventory
    │   │           ├── CacheNames
    │   │           ├── Security
    │   │           ├── RoleName
    │   │           ├── PaymentStatus
    │   │           └── OrderStatus
    │   │
    │   └── resources
    │       ├── application.properties
    │       │
    │       └── db
                └── migration
                    ├── V1__initial_baseline.sql
                    ├── V2__insert_default_roles.sql
                    ├── V3__add_admin_account.sql   
                    ├── V4__add_admin_role.sql
                    └── V5__create_order_number_sequence.sql
```

### Backend Structure
- ✅ All **Entities** with audit fields (`createdAt`, `updatedAt`, `createdBy`, `updatedBy`)  
- ✅ All **Repositories** with custom queries using Spring Data JPA  
- ✅ All **DTOs and Mappers** using MapStruct  
- ✅ **Custom Exceptions and Global Exception Handler** for unified error handling  
- ✅ Comprehensive **Utilities and Constants**  
- ✅ Complete **Security System** with JWT Authentication & Authorization  
- ✅ All **Services** with clear business logic  
- ✅ All **REST Controllers** following RESTful principles and documented with Swagger



### API & Documentation
- **Swagger/OpenAPI Annotations** for automatic API documentation  
- **ApiResponse wrapper** for consistent responses  
- **Automatic validation** of request bodies  
- **Pagination and sorting** support for list endpoints  
- Detailed **logging** for each request  
- Correct use of **HTTP status codes** for responses  


