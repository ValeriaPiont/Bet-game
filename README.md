## Bet game

### About

This game allows users to place a bet on a number. If the user's number is greater than the randomly generated number, the user wins. The winning amount is calculated based on the difference between the user's number and the maximum possible number, multiplied by the user's bet.

### How to run
To run this project locally, follow these steps:

```mvn spring-boot:run```

Once the service is running, you can access and try it out via API Documentation (Swagger): http://localhost:9090/swagger-ui.html.

Game API URL: http://localhost:9090/api/v1/game

## Technologies and Dependencies
The project uses the following technologies and dependencies:

- **Java Version**: The project is developed using Java version 17.

- **Spring Boot version 2.7.16**: A framework for building Java applications, particularly web applications.
    - `spring-boot-starter-web`: For building web applications with Spring Boot.
    - `spring-boot-starter-test`: For testing Spring Boot applications.
    - `spring-boot-starter-validation`: For Spring Boot's built-in validation support.

- **Project Lombok**: A library for reducing boilerplate code in Java, such as getters, setters, constructors, etc.
    - `lombok`: Dependency for Project Lombok.

- **Mockito**: A testing framework for mocking objects and behavior during unit testing.
    - `mockito-core`: Dependency for Mockito testing framework.

- **Springdoc OpenAPI UI**: A library for generating OpenAPI documentation for your Spring Boot RESTful APIs and providing an interactive UI for exploring the API documentation.
    - `springdoc-openapi-ui`: Dependency for Springdoc OpenAPI UI.
