package com.devicehive.service;

import com.devicehive.dao.DeviceEquipmentDAO;
import com.devicehive.model.Device;
import com.devicehive.model.DeviceEquipment;
import com.devicehive.model.DeviceNotification;
import com.devicehive.model.SpecialNotifications;
import com.devicehive.util.ServerResponsesFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;

@Service
public class DeviceEquipmentService {

    @Autowired
    private DeviceEquipmentDAO deviceEquipmentDAO;
    @Autowired
    private TimestampService timestampService;


    /**
     * find Device equipment by device
     *
     * @param device Equipment will be fetched for this device
     * @return List of DeviceEquipment for specified device
     */

    public List<DeviceEquipment> findByFK(@NotNull Device device) {
        return deviceEquipmentDAO.findByFK(device);
    }


    public DeviceEquipment findByCodeAndDevice(@NotNull String code, @NotNull Device device) {
        return deviceEquipmentDAO.findByCodeAndDevice(code, device);
    }

    public void createDeviceEquipment(DeviceEquipment deviceEquipment) {
        if (deviceEquipment != null && !deviceEquipmentDAO.update(deviceEquipment)) {
            deviceEquipment.setTimestamp(timestampService.getTimestamp());
            deviceEquipmentDAO.createDeviceEquipment(deviceEquipment);
        }
    }


    public DeviceNotification refreshDeviceEquipment(DeviceNotification notification, Device device) {
        DeviceEquipment deviceEquipment = null;
        if (notification.getNotification().equals(SpecialNotifications.EQUIPMENT)) {
            deviceEquipment = ServerResponsesFactory.parseDeviceEquipmentNotification(notification, device);
            if (deviceEquipment.getTimestamp() == null) {
                deviceEquipment.setTimestamp(timestampService.getTimestamp());
            }
        }
        createDeviceEquipment(deviceEquipment);
        return notification;
    }
}
