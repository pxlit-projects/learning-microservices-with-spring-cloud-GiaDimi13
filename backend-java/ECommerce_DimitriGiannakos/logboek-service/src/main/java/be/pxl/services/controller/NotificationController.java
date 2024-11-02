package be.pxl.services.controller;

import be.pxl.services.domain.Notification;
import be.pxl.services.services.INotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/logboek")
@RequiredArgsConstructor
public class NotificationController {
    private final INotificationService notificationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void sendMessage(@RequestBody Notification notification) {
        notificationService.sendMessage(notification);
    }
}
