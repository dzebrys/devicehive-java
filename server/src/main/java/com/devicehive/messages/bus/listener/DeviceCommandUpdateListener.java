package com.devicehive.messages.bus.listener;

import com.devicehive.messages.bus.LocalMessage;
import com.devicehive.messages.bus.LocalMessageBus;
import com.devicehive.messages.bus.Update;
import com.devicehive.model.DeviceCommand;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class DeviceCommandUpdateListener implements MessageListener<DeviceCommand> {

    private static final Logger logger = LoggerFactory.getLogger(DeviceCommandUpdateListener.class);

    @Autowired
    LocalMessageBus localMessageBus;

    @Async
    @Override
    public void onMessage(Message<DeviceCommand> message) {
        if (!message.getPublishingMember().localMember()) {
            final DeviceCommand deviceCommand = message.getMessageObject();
            try {
                logger.debug("Received device command create {}", deviceCommand.getId());
                localMessageBus.submitDeviceCommandUpdate(deviceCommand);
                logger.debug("Event for command create {} is fired", deviceCommand.getId());
            } catch (Throwable ex) {
                logger.error("Error", ex);
            }
        }

    }
}
