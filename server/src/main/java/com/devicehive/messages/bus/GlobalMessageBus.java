package com.devicehive.messages.bus;

import com.devicehive.messages.bus.listener.DeviceCommandCreateListener;
import com.devicehive.messages.bus.listener.DeviceCommandUpdateListener;
import com.devicehive.messages.bus.listener.DeviceNotificationCreateListener;
import com.devicehive.model.DeviceCommand;
import com.devicehive.model.DeviceNotification;
import com.devicehive.service.HazelcastService;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;


@Service
public class GlobalMessageBus {

    private static final Logger logger = LoggerFactory.getLogger(GlobalMessageBus.class);
    private static final String DEVICE_COMMAND = "DEVICE_COMMAND";
    private static final String DEVICE_COMMAND_UPDATE = "DEVICE_COMMAND_UPDATE";
    private static final String DEVICE_NOTIFICATION = "DEVICE_NOTIFICATION";

    @Autowired
    private HazelcastService hazelcastService;

    private HazelcastInstance hazelcast;
    private String commandListener;
    private String commandUpdateListener;
    private String notificationListener;


    @Autowired
    private DeviceCommandCreateListener deviceCommandCreateListener;

    @Autowired
    private DeviceCommandUpdateListener deviceCommandUpdateListener;

    @Autowired
    private DeviceNotificationCreateListener deviceNotificationCreateListener;

    @PostConstruct
    protected void postConstruct() {
        hazelcast = hazelcastService.getHazelcast();

        logger.debug("Initializing topic {}...", DEVICE_COMMAND);
        ITopic<DeviceCommand> deviceCommandTopic = hazelcast.getTopic(DEVICE_COMMAND);
        commandListener = deviceCommandTopic.addMessageListener(deviceCommandCreateListener);
        logger.debug("Done {}", DEVICE_COMMAND);

        logger.debug("Initializing topic {}...", DEVICE_COMMAND_UPDATE);
        ITopic<DeviceCommand> deviceCommandUpdateTopic = hazelcast.getTopic(DEVICE_COMMAND_UPDATE);
        commandUpdateListener = deviceCommandUpdateTopic.addMessageListener(deviceCommandUpdateListener);
        logger.debug("Done {}", DEVICE_COMMAND_UPDATE);

        logger.debug("Initializing topic {}...", DEVICE_NOTIFICATION);
        ITopic<DeviceNotification> deviceNotificationTopic = hazelcast.getTopic(DEVICE_NOTIFICATION);
        notificationListener = deviceNotificationTopic.addMessageListener(deviceNotificationCreateListener);
        logger.debug("Done {}", DEVICE_NOTIFICATION);
    }

    @PreDestroy
    protected void preDestroy() {
        hazelcast.getTopic(DEVICE_COMMAND).removeMessageListener(commandListener);
        hazelcast.getTopic(DEVICE_COMMAND_UPDATE).removeMessageListener(commandUpdateListener);
        hazelcast.getTopic(DEVICE_NOTIFICATION).removeMessageListener(notificationListener);
    }


    public void publishDeviceCommand(DeviceCommand deviceCommand) {
        logger.debug("Sending device command {}", deviceCommand.getId());
        hazelcast.getTopic(DEVICE_COMMAND).publish(deviceCommand);
        logger.debug("Sent");
    }


    public void publishDeviceCommandUpdate(DeviceCommand deviceCommandUpdate) {
        logger.debug("Sending device command update {}", deviceCommandUpdate.getId());
        hazelcast.getTopic(DEVICE_COMMAND_UPDATE).publish(deviceCommandUpdate);
        logger.debug("Sent");
    }


    public void publishDeviceNotification(DeviceNotification deviceNotification) {
        logger.debug("Sending device notification {}", deviceNotification.getId());
        hazelcast.getTopic(DEVICE_NOTIFICATION).publish(deviceNotification);
        logger.debug("Sent");
    }

}
