package com.warehouse.config;

import com.warehouse.model.Customer;
import com.warehouse.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    CommandLineRunner initCustomers(CustomerRepository customerRepository) {
        return args -> {
            try {
                // Only add customers if the table is empty
                if (customerRepository.count() == 0) {
                    // Create sample customers
                    Customer customer1 = new Customer();
                    customer1.setName("John Doe");
                    customer1.setEmail("john.doe@example.com");
                    customer1.setPhone("123-456-7890");
                    customer1.setAddress("123 Main St, City");
                    customerRepository.save(customer1);

                    Customer customer2 = new Customer();
                    customer2.setName("Jane Smith");
                    customer2.setEmail("jane.smith@example.com");
                    customer2.setPhone("098-765-4321");
                    customer2.setAddress("456 Oak Ave, Town");
                    customerRepository.save(customer2);

                    Customer customer3 = new Customer();
                    customer3.setName("Bob Wilson");
                    customer3.setEmail("bob.wilson@example.com");
                    customer3.setPhone("555-555-5555");
                    customer3.setAddress("789 Pine Rd, Village");
                    customerRepository.save(customer3);

                    logger.info("Sample customers created successfully");
                }
            } catch (Exception e) {
                logger.error("Error initializing customers: " + e.getMessage(), e);
                throw e;
            }
        };
    }
} 