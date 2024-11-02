package be.pxl.services.services;

import be.pxl.services.domain.Notification;

public interface INotificationService {
    void sendMessage(Notification notification);
}
