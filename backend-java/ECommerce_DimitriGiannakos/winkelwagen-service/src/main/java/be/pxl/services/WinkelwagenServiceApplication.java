package be.pxl.services;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * WinkelwagenServiceApplication!
 */
@SpringBootApplication
@EnableDiscoveryClient
public class WinkelwagenServiceApplication
{
    public static void main( String[] args )
    {
        SpringApplication.run(WinkelwagenServiceApplication.class, args);
    }
}
