package com.devicehive.messages.bus.listener;

import com.devicehive.messages.bus.LocalMessageBus;
import com.devicehive.model.DeviceNotification;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;


@Component
public class DeviceNotificationCreateListener implements MessageListener<DeviceNotification> {

    private static final Logger logger = LoggerFactory.getLogger(DeviceNotificationCreateListener.class);


    @Autowired
    LocalMessageBus localMessageBus;


    @Async
    @Override
    public void onMessage(Message<DeviceNotification> message) {
        if (!message.getPublishingMember().localMember()) {
            final DeviceNotification deviceNotification = message.getMessageObject();
            try {
                logger.debug("Received device command create {}", deviceNotification.getId());
                localMessageBus.submitDeviceNotification(deviceNotification);
                logger.debug("Event for command create {} is fired", deviceNotification.getId());
            } catch (Throwable ex) {
                logger.error("Error", ex);
            }
        }

    }
}
