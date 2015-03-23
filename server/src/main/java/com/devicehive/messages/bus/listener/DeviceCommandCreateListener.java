package com.devicehive.messages.bus.listener;

import com.devicehive.messages.bus.Create;
import com.devicehive.messages.bus.LocalMessage;
import com.devicehive.messages.bus.LocalMessageBus;
import com.devicehive.model.DeviceCommand;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;



@Component
public class DeviceCommandCreateListener implements MessageListener<DeviceCommand> {

    private static final Logger logger = LoggerFactory.getLogger(DeviceCommandCreateListener.class);


    @Autowired
    LocalMessageBus localMessageBus;

    @Override
    @Async
    public void onMessage(final Message<DeviceCommand> message) {
        if (!message.getPublishingMember().localMember()) {
            final DeviceCommand deviceCommand = message.getMessageObject();
            try {
                logger.debug("Received device command create {}", deviceCommand.getId());
                localMessageBus.submitDeviceCommand(deviceCommand);
                logger.debug("Event for command create {} is fired", deviceCommand.getId());
            } catch (Throwable ex) {
                logger.error("Error", ex);
            }
        }

    }
}
