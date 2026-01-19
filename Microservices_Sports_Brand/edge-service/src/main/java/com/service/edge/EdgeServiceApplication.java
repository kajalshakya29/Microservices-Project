package com.service.edge;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class EdgeServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EdgeServiceApplication.class, args);
	}
}

class Item {
	private Long id;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}

@FeignClient("ITEMCATALOG")
interface ItemClient {
	@GetMapping("/all-items")
	Collection<Item> readItems();
}

@RestController
class GoodItemApiAdapterRestController {

	private final ItemClient itemClient;

	public GoodItemApiAdapterRestController(ItemClient itemClient) {
		this.itemClient = itemClient;
	}

	// Fallback method (Jab service down ho)
	public Collection<Item> fallback(Exception e) {
		Item errorItem = new Item();
		errorItem.setName("Service Down - Testing Fallback");
		return java.util.Collections.singletonList(errorItem);
	}

	//@CircuitBreaker(name = "items", fallbackMethod = "fallback")
	@GetMapping("/top-brands")
	@CrossOrigin(origins = "*")
	public Collection<Item> goodItem() {
		System.out.println("Fetching items from Catalog...");
		return itemClient.readItems()
				.stream()
				.filter(this::isGreat)
				.collect(Collectors.toList());
	}
	private boolean isGreat(Item item) {
		if (item == null || item.getName() == null) return false; // Null check
		return !item.getName().equals("Nike") &&
				!item.getName().equals("Addidas") &&
				!item.getName().equals("Reebook");
	}
}

@Configuration
class FeignConfig {
	@Bean
	@ConditionalOnMissingBean
	public HttpMessageConverters messageConverters(ObjectProvider<HttpMessageConverter<?>> converters) {
		return new HttpMessageConverters(converters.orderedStream().collect(Collectors.toList()));
	}
}