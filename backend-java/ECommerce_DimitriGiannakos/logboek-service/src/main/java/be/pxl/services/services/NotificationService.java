package be.pxl.services.services;

import be.pxl.services.domain.DTO.ProductDTO;
import be.pxl.services.domain.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService implements INotificationService{

    @Override
    public void sendMessage(Notification notification) {
        log.info("Receiving notification...");
        log.info("sending... {}", notification.getMessage());
        log.info("TO {}", notification.getTo());
    }

    @RabbitListener(queues = "myQueue")
    public void handleProductMessage(ProductDTO product) {
        System.out.println("Product met Id: " + product.getId() + "en naam:" + product.getName());
    }
}
