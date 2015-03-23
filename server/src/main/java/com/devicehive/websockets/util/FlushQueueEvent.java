package com.devicehive.websockets.util;

import org.springframework.context.ApplicationEvent;
import org.springframework.web.socket.WebSocketSession;

import javax.inject.Qualifier;
import javax.websocket.Session;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


public class FlushQueueEvent extends ApplicationEvent {

    public FlushQueueEvent(Session source) {
        super(source);
    }

    public Session getSession() {
        return (Session) this.getSource();
    }
}
