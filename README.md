# Spring Boot Microservices: Top Shoes Brands 
This project demonstrates a Microservices Ecosystem where different small services work together like a team. Instead of one giant application, we have a Catalog Service that holds brand data and an Edge Service that talks to it. To keep track of everyone, we use Netflix Eureka, which acts like a phonebook; when the Edge Service needs data, it looks up the Catalog Service's "name" instead of a fixed address. Communication is handled by OpenFeign, a tool that makes requesting data as easy as calling a local function. This setup ensures that the system is modular, making it much easier to update or scale individual parts without breaking the whole application.

To make the system "smart" and reliable, we implemented custom logic and safety guards. The Edge Service doesn't just pass data through; it uses Java Streams to filter the list, showing only specific "Top Brands" to the user. More importantly, we added a Circuit Breaker using Resilience4j. This acts like a safety fuse in your home; if the Catalog Service crashes or slows down, the Circuit Breaker "trips" and instantly sends a friendly "Service Down" message to the user. This prevents the entire website from freezing and ensures a smooth experience even when things go wrong behind the scenes.

# Key Project Features:
Dynamic Discovery: Uses Eureka so services find each other automatically on any network.

Smart Filtering: Uses Java logic to clean and customize data before it reaches the user.

Fault Tolerance: Uses a Circuit Breaker to provide "Fallback" data if a service fails.

Reactive Bridge: Includes a custom configuration to make modern WebFlux and Feign tools work together perfectly.

## Tech Stack

- **Java 17**
- **Spring Boot 3.x**
- **Spring Cloud Netflix Eureka** (Service Discovery)
- **Spring Cloud OpenFeign** (Declarative REST Client)
- **Resilience4j** (Circuit Breaker)
- **Lombok** & **JPA/H2**
