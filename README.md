# Warehouse Management System

A Java-based Warehouse Management System using Spring Boot and MySQL.

## Prerequisites

- Java 17
- MySQL 8.0 or higher
- Maven 3.6 or higher

## Setup

1. Clone the repository
2. Create a MySQL database named `warehouse_db`
3. Update the database credentials in `src/main/resources/application.properties` if needed
4. Run the application using Maven:

```bash
mvn spring-boot:run
```

## Default Configuration

- Database: warehouse_db
- Username: root
- Password: root
- Port: 8080

## API Endpoints

### Inventory Management
- GET /api/inventory - Get all inventory items
- GET /api/inventory/{id} - Get inventory item by ID
- POST /api/inventory - Create new inventory item
- PUT /api/inventory/{id} - Update inventory item
- DELETE /api/inventory/{id} - Delete inventory item
- GET /api/inventory/low-stock - Get low stock items

### Order Management
- GET /api/orders - Get all orders
- GET /api/orders/{id} - Get order by ID
- POST /api/orders - Create new order
- PUT /api/orders/{id} - Update order
- DELETE /api/orders/{id} - Delete order

### Supplier Management
- GET /api/suppliers - Get all suppliers
- GET /api/suppliers/{id} - Get supplier by ID
- POST /api/suppliers - Create new supplier
- PUT /api/suppliers/{id} - Update supplier
- DELETE /api/suppliers/{id} - Delete supplier

### Warehouse Management
- GET /api/warehouse/sections - Get all warehouse sections
- GET /api/warehouse/sections/{id} - Get section by ID
- POST /api/warehouse/sections - Create new section
- PUT /api/warehouse/sections/{id} - Update section
- DELETE /api/warehouse/sections/{id} - Delete section

## Security

The application uses Spring Security with basic authentication. Default roles:
- ADMIN: Full access to all features
- USER: Access to order management only 