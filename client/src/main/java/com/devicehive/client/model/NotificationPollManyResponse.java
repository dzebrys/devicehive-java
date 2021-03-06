package com.devicehive.client.model;


import com.google.gson.annotations.SerializedName;

import com.devicehive.client.impl.json.strategies.JsonPolicyDef;

import static com.devicehive.client.impl.json.strategies.JsonPolicyDef.Policy.NOTIFICATION_TO_CLIENT;

public class NotificationPollManyResponse implements HiveEntity {

    private static final long serialVersionUID = -4390548037685312874L;
    @SerializedName("notification")
    @JsonPolicyDef(NOTIFICATION_TO_CLIENT)
    private DeviceNotification notification;

    @SerializedName("deviceGuid")
    @JsonPolicyDef(NOTIFICATION_TO_CLIENT)
    private String guid;

    public NotificationPollManyResponse(DeviceNotification notification, String guid) {
        this.notification = notification;
        this.guid = guid;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public DeviceNotification getNotification() {
        return notification;
    }

    public void setNotification(DeviceNotification notification) {
        this.notification = notification;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("NotificationPollManyResponse{");
        sb.append("notification=").append(notification);
        sb.append(", guid='").append(guid).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
