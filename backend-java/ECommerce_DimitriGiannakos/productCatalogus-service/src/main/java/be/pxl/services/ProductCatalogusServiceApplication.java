package be.pxl.services;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * ProductCatalogusServiceApplication
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ProductCatalogusServiceApplication
{
    public static void main( String[] args )
    {
        SpringApplication.run(ProductCatalogusServiceApplication.class, args);
    }
}
