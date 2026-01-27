package com.service.item_catalog;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.stream.Stream;

@EnableDiscoveryClient
@SpringBootApplication
public class ItemCatalogApplication {

	public static void main(String[] args) {
		SpringApplication.run(ItemCatalogApplication.class, args);
	}
}
@Data
@Entity
class Item{
	public Item() {}

	// Constructor with name
	public Item(String name) {
		this.name = name;
	}

	@Id
	@GeneratedValue
	private long id;
	private String name;
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Item{" + "id=" + id + ", name='" + name + '\'' + '}';
	}
}

@RepositoryRestResource
interface ItemRepository extends JpaRepository<Item,Long>{}

@Component
class ItemInitializer implements CommandLineRunner{
	private final ItemRepository itemRepository;

	ItemInitializer(ItemRepository itemRepository){
        this.itemRepository = itemRepository;
    }
	public void run(String... args) throws Exception{
		Stream.of("Lining", "PUMA","Bad Boy", "Air Jordan", "Nike", "Addidas", "Reebook")
				.forEach(item -> itemRepository.save(new Item(item)));
		itemRepository.findAll().forEach(System.out::println);
	}
}

@RestController
class ItemController {

	private final ItemRepository itemRepository;
	private static final Logger log = LoggerFactory.getLogger(ItemController.class);
	ItemController(ItemRepository itemRepository) {
		this.itemRepository = itemRepository;
	}

	@GetMapping("/all-items")
	public Collection<Item> getAllItems() {
		log.info(">>>> Item-Catalog: Sending all items to Edge-Service");
		return itemRepository.findAll();
	}
}