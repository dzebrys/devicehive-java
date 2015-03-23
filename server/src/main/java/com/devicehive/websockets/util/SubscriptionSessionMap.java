package com.devicehive.websockets.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by stas on 06.05.14.
 */
@Component
public class SubscriptionSessionMap {

    @Autowired
    private SessionMonitor sessionMonitor;

    private ConcurrentMap<UUID, String> map = new ConcurrentHashMap<>();


    public void put(UUID subId, Session session) {
        map.put(subId, session.getId());
    }

    public Session get(UUID subId) {
        String sessionId = map.get(subId);
        if (sessionId != null) {
            return sessionMonitor.getSession(sessionId);
        }
        return null;
    }

    public void removeAll(Collection<UUID> uuids) {
        if (uuids != null) {
            for (UUID uuid : uuids) {
                map.remove(uuid);
            }
        }
    }

    public void remove(UUID uuid) {
        map.remove(uuid);
    }
}
