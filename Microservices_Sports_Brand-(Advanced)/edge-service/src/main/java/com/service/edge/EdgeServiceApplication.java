package com.service.edge;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class EdgeServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EdgeServiceApplication.class, args);
	}
	//@LoadBalanced
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
	@LoadBalanced
	@Bean
	public RestClient.Builder restClientBuilder() {
		return RestClient.builder();
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
	public Item() {
	}
}

@FeignClient("ITEMCATALOG")
interface ItemClient {
	@GetMapping("/all-items")
	Collection<Item> readItems();
}

@RestController
class GoodItemApiAdapterRestController {

	private final RestClient restClient;
	private final RestTemplate restTemplate;
	private final ItemClient itemClient;
	private static final Logger log = LoggerFactory.getLogger(GoodItemApiAdapterRestController.class);
	public GoodItemApiAdapterRestController(ItemClient itemClient, RestClient.Builder builder, RestTemplate restTemplate) {
		this.restTemplate=restTemplate;
		this.itemClient = itemClient;
		this.restClient = builder.baseUrl("http://ITEMCATALOG").build();
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
		//log.info(">>>> Edge-Service (Port 8089): Calling Item-Catalog (Port 8088)");
		//log.info(">>>> Edge-Service: Fetching data using RestClient...");
		log.info(">>>> Edge-Service: Calling ITEMCATALOG via Load Balancer");
		//String url = "http://localhost:8088/all-items";
		//Item[] itemsArray = restTemplate.getForObject(url, Item[].class);
		//Collection<Item> items = java.util.Arrays.asList(itemsArray);
		//return itemClient.readItems()
		List<Item> items = restClient.get()
				.uri("/all-items")
				.retrieve()
				.body(new ParameterizedTypeReference<List<Item>>() {});
		return items.stream()
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

