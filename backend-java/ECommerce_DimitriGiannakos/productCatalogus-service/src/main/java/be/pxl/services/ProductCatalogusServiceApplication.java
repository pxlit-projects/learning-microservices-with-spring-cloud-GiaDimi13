package be.pxl.services;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * ProductCatalogusServiceApplication
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class ProductCatalogusServiceApplication
{
    public static void main( String[] args )
    {
        SpringApplication.run(ProductCatalogusServiceApplication.class, args);
    }
}
