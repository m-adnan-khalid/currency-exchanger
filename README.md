
# Currency Exchange and Discount Service

![Java](https://img.shields.io/badge/java-17-blue)
![Spring Boot](https://img.shields.io/badge/spring%20boot-3.1-green)
![Maven](https://img.shields.io/badge/maven-3.8%2B-orange)

## Overview

This Spring Boot application integrates with currency exchange APIs to calculate discounted bills with currency conversion capabilities.

## Key Features

  **Real-time Exchange Rates**: Integration with both [Open Exchange Rates API](https://app.exchangerate-api.com/) and [ExchangeRate-API](https://openexchangerates.org/) 
- **Smart Discount Calculation**:
  - Role-based discounts (Employee, Affiliate, Customer)
  - Bill value discounts ($5 off every $100)
  - Special rules for grocery items
- **Currency Conversion**: Convert totals to specified currencies
- **Secure Access**: JWT authentication for all endpoints
- **Comprehensive Testing**: Unit and integration test coverage
- **Caching**: Ehcache for exchange rate caching

## Project Structure

```text
📦currency-exchanger
├── .gitignore
├── pom.xml
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── app
│   │   │           └── currencyexchanger
│   │   │               ├── CurrencyExchangerApplication.java
│   │   │               ├── client
│   │   │               │   ├── RestClient.java
│   │   │               │   └── impl
│   │   │               │       └── RestClientImpl.java
│   │   │               ├── config
│   │   │               │   ├── AppConfig.java
│   │   │               │   ├── CacheConfig.java
│   │   │               │   ├── OpenAPIConfig.java
│   │   │               │   └── SecurityConfig.java
│   │   │               ├── constants
│   │   │               │   ├── ApiUrl.java
│   │   │               │   └── SecurityConstant.java
│   │   │               ├── controller
│   │   │               │   ├── AuthController.java
│   │   │               │   └── BillController.java
│   │   │               ├── enums
│   │   │               │   ├── ItemCategory.java
│   │   │               │   └── Role.java
│   │   │               ├── exception
│   │   │               │   ├── BadRequestException.java
│   │   │               │   ├── ExchangeRateException.java
│   │   │               │   ├── GlobalExceptionHandler.java
│   │   │               │   └── InternalServerException.java
│   │   │               ├── model
│   │   │               │   ├── BillItem.java
│   │   │               │   ├── BillRequest.java
│   │   │               │   ├── BillResponse.java
│   │   │               │   ├── ExchangeRateApiResponse.java
│   │   │               │   ├── ExchangeRateResponse.java
│   │   │               │   ├── JwtResponse.java
│   │   │               │   ├── User.java
│   │   │               │   └── UserLoginRequest.java
│   │   │               ├── providers
│   │   │               │   ├── CurrencyExchangeProvider.java
│   │   │               │   ├── ExchangeRateApiProvider.java
│   │   │               │   └── OpenExchangeRatesProvider.java
│   │   │               ├── security
│   │   │               │   └── JwtAuthenticationFilter.java
│   │   │               ├── service
│   │   │               │   ├── BillService.java
│   │   │               │   ├── CurrencyExchangeService.java
│   │   │               │   └── impl
│   │   │               │       ├── BillServiceImpl.java
│   │   │               │       └── CurrencyExchangeServiceImpl.java
│   │   │               └── util
│   │   │                   ├── JwtUtil.java
│   │   │                   ├── SecurityUtil.java
│   │   │                   └── ValidationUtils.java
│   │   └── resources
│   │       ├── application.properties
│   │       ├── ehcache.xml
│   │       └── openapi.yaml
│   └── test
│       └── java
│           └── com
│               └── app
│                   └── currencyexchanger
│                       ├── CurrencyExchangerApplicationTests.java
│                       ├── client
│                       │   └── impl
│                       │       └── RestClientImplTest.java
│                       ├── controller
│                       │   ├── AuthControllerTest.java
│                       │   └── BillControllerTest.java
│                       ├── providers
│                       │   ├── ExchangeRateApiProviderTest.java
│                       │   └── OpenExchangeRatesProviderTest.java
│                       └── service
│                           └── impl
│                               ├── BillServiceImplTest.java
│                               └── CurrencyExchangeServiceImplTest.java
```

## Installation and Running

### Prerequisites

- **Java 17** or higher
- **Maven 3.8+**

### Steps

1. **Clone the repository:**

    ```bash
    git clone https://github.com/your-repo-name/currency-exchanger.git
    cd currency-exchanger
    ```

2. **Build the project using Maven:**

    ```bash
    mvn clean install
    ```

3. **Run the Spring Boot application:**

    ```bash
    mvn spring-boot:run
    ```

    The application will start running on `http://localhost:9090`.

### Accessing API Endpoints

- **Swagger UI**:  
    To access API documentation, navigate to:  
    `http://localhost:9090/swagger-ui/index.html`

- **OpenAPI Specification**:  
    Access the OpenAPI YAML file at:  
    `http://localhost:9090/v3/api-docs`

## API Endpoints

```bash
- **POST /api/auth/login**  
  This endpoint generates a JWT token for the user based on the provided credentials.

- **Employee User**:
    - Username: `employee`
    - Password: `emp@pas`

- **Affiliate User**:
    - Username: `affiliate`
    - Password: `aft@pas`

- **Customer User**:
    - Username: `customer`
    - Password: `cus@pas`
  
### Request Body

The request body must include the user's username and password.

```json
{
  "username": "employee",
  "password": "emp@pas"
}
```

### Response Body

```json
{
  "token": "JWT_TOKEN_HERE"
}
```

- **POST /api/calculate**  
  Calculates the payable amount for a bill after applying discounts and currency conversion. after create the token from above api pass this as a authzation header like 

```bash
-H 'Authorization: Bearer YOUR_JWT_TOKEN'

### Request Body

```json
{
  "items": [
    {
      "name": "Laptop",
      "category": "CLOTHING",
      "price": 999.99,
      "quantity": 1
    },
    {
      "name": "Milk",
      "category": "GROCERIES",
      "price": 3.50,
      "quantity": 2
    }
  ],
  "originalCurrency": "USD",
  "targetCurrency": "EUR"
}
```

### Response Body

```json
{
  "originalAmount": 1006.99,
  "discountedAmount": 654.89,
  "finalAmount": 378230.08,
  "originalCurrency": "USD",
  "targetCurrency": "EUR"
}
```

## Testing

Run unit tests with:

```bash
mvn test
```

Generate code coverage reports with JaCoCo:

```bash
mvn clean verify
```

## Code Quality and Static Analysis

You can run static code analysis using:

```bash
mvn checkstyle:check
```

To build the project and run the code quality checks, use the following Maven command:

```bash
mvn mvn clean install checkstyle:check -Pci

```

## Caching

Exchange rates are cached using Ehcache to reduce the number of external API calls.

## Conclusion

This application implements a robust and efficient way to calculate discounted bills, handle currency conversion, and ensure secure access to endpoints.

## Repository

[Link to GitHub Repository](https://github.com/m-adnan-khalid/currency-exchanger)

## Follow-up Discussion

Be ready for a 15-minute discussion where I'll explain key design decisions and assumptions made during the implementation.
