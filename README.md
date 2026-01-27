# Spring Boot Microservices: Top Shoes Brands 
This project demonstrates a robust Microservices Architecture where services operate as a decentralized team. At its core, we have an Item-Catalog Service (The Source) and an Edge Service (The Consumer). To manage this ecosystem, we use Netflix Eureka as a Service Registry (the "phonebook"), allowing services to discover each other dynamically using service names instead of hardcoded IP addresses.

To make the system "smart" and reliable, we implemented custom logic and safety guards. The Edge Service doesn't just pass data through; it uses Java Streams to filter the list, showing only specific "Top Brands" to the user. More importantly, we added a Circuit Breaker using Resilience4j. This acts like a safety fuse in your home; if the Catalog Service crashes or slows down, the Circuit Breaker "trips" and instantly sends a friendly "Service Down" message to the user. This prevents the entire website from freezing and ensures a smooth experience even when things go wrong behind the scenes.

# Key Project Features:
Multi-Client Communication: Implemented Feign, RestTemplate, and the modern RestClient within the same ecosystem.

Client-Side Load Balancing: Integrated @LoadBalanced with RestClient to automatically distribute traffic across multiple service instances.

Dynamic Discovery: Services register with Eureka, making the system "Location Transparent."

Fault Tolerance: Uses Resilience4j to provide "Fallback" responses, ensuring high availability.

Modern Java Logic: Utilizes Java 17 features and Streams for real-time data transformation and filtering.

## Tech Stack

- **Java 17**
- **Spring Boot 3.x**
- **Spring Cloud Netflix Eureka** (Service Discovery)
- **Spring Cloud LoadBalancer** (Modern replacement for Ribbon)
- **Spring RestClient & RestTemplate** (Manual REST Clients)
- **Spring Cloud OpenFeign** (Declarative REST Client)
- **Resilience4j** (Circuit Breaker)
- **Lombok** & **JPA/H2**
