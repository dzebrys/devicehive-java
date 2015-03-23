package com.devicehive.configuration;

import com.devicehive.dao.ConfigurationDAO;
import com.devicehive.model.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

@Service
public class ConfigurationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationService.class);

    @Autowired
    private ConfigurationDAO configurationDAO;

    public <T> void save(@NotNull String name, T value) {
        String str = value != null ? value.toString() : null;
        configurationDAO.save(name, str);
    }

    public String get(@NotNull String name) {
        Configuration configuration = configurationDAO.findByName(name);
        if (configuration == null) {
            LOGGER.warn(String.format(Messages.CONFIG_NOT_FOUND, name));
            return null;
        }
        return configuration.getValue();
    }

    public long getLong(@NotNull String name, long defaultValue) {
        String val = get(name);
        return val != null ? Long.parseLong(val) : defaultValue;
    }

    public int getInt(@NotNull String name, int defaultValue) {
        String val = get(name);
        return val != null ? Integer.parseInt(val) : defaultValue;
    }

    public boolean getBoolean(@NotNull String name, boolean defaultValue) {
        String val = get(name);
        return val != null ? Boolean.parseBoolean(val) : defaultValue;
    }

    public <T> void delete(@NotNull String name) {
        configurationDAO.delete(name);
    }

}
