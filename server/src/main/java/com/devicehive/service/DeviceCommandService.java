package com.devicehive.service;

import com.devicehive.auth.HivePrincipal;
import com.devicehive.configuration.Messages;
import com.devicehive.dao.DeviceCommandDAO;
import com.devicehive.exceptions.HiveException;
import com.devicehive.messages.bus.GlobalMessageBus;
import com.devicehive.model.Device;
import com.devicehive.model.DeviceCommand;
import com.devicehive.model.User;
import com.devicehive.model.updates.DeviceCommandUpdate;
import com.devicehive.util.HiveValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;


@Service
public class DeviceCommandService {

    @Autowired
    private DeviceCommandDAO commandDAO;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private TimestampService timestampService;
    @Autowired
    private HiveValidator hiveValidator;

    @Autowired
    private GlobalMessageBus globalMessageBus;



    public DeviceCommand getByGuidAndId(@NotNull String guid, @NotNull long id) {
        return commandDAO.getByDeviceGuidAndId(guid, id);
    }


    public DeviceCommand findById(Long id) {
        return commandDAO.findById(id);
    }

    public List<DeviceCommand> getDeviceCommandsList(Collection<String> devices, Collection<String> names,
                                                     Timestamp timestamp,
                                                     HivePrincipal principal) {
        if (devices != null) {
            return commandDAO
                .findCommands(deviceService.findByGuidWithPermissionsCheck(devices, principal), names, timestamp,
                              null);
        } else {
            return commandDAO.findCommands(null, names, timestamp, principal);
        }
    }


    public List<DeviceCommand> queryDeviceCommand(Device device, Timestamp start, Timestamp end, String command,
                                                  String status, String sortField, Boolean sortOrderAsc,
                                                  Integer take, Integer skip, Integer gridInterval) {
        return commandDAO.queryDeviceCommand(device, start, end, command, status, sortField, sortOrderAsc, take,
                                             skip, gridInterval);
    }

    public DeviceCommand getByDeviceGuidAndId(@NotNull String guid, @NotNull long id) {
        return commandDAO.getByDeviceGuidAndId(guid, id);
    }

    public void submitDeviceCommandUpdate(DeviceCommandUpdate update, Device device) {
        DeviceCommand saved = saveDeviceCommandUpdate(update, device);
        globalMessageBus.publishDeviceCommandUpdate(saved);
    }

    public void submitDeviceCommand(DeviceCommand command, Device device, User user) {
        command.setDevice(device);
        command.setUser(user);
        command.setUserId(user.getId());
        command.setTimestamp(timestampService.getTimestamp());
        commandDAO.createCommand(command);
        globalMessageBus.publishDeviceCommand(command);
    }

    private DeviceCommand saveDeviceCommandUpdate(DeviceCommandUpdate update, Device device) {

        DeviceCommand cmd = commandDAO.findById(update.getId());

        if (cmd == null) {
            throw new HiveException(String.format(Messages.COMMAND_NOT_FOUND, update.getId()),
                                    NOT_FOUND.getStatusCode());
        }

        if (!cmd.getDevice().getId().equals(device.getId())) {
            throw new HiveException(String.format(Messages.COMMAND_NOT_FOUND, update.getId()),
                                    NOT_FOUND.getStatusCode());
        }

        if (update.getCommand() != null) {
            cmd.setCommand(update.getCommand().getValue());
        }
        if (update.getFlags() != null) {
            cmd.setFlags(update.getFlags().getValue());
        }
        if (update.getLifetime() != null) {
            cmd.setLifetime(update.getLifetime().getValue());
        }
        if (update.getParameters() != null) {
            cmd.setParameters(update.getParameters().getValue());
        }
        if (update.getResult() != null) {
            cmd.setResult(update.getResult().getValue());
        }
        if (update.getStatus() != null) {
            cmd.setStatus(update.getStatus().getValue());
        }
        if (update.getTimestamp() != null) {
            cmd.setTimestamp(update.getTimestamp().getValue());
        }
        hiveValidator.validate(cmd);
        return cmd;
    }


}
