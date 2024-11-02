package be.pxl.services.client;

import be.pxl.services.domain.NotificationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "logboek-service") // -> naam van de service
public interface LogboekClient {

    @PostMapping("/logboek/")
    void sendNotification(@RequestBody NotificationRequest notificationRequest);
}