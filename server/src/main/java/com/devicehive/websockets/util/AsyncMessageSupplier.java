package com.devicehive.websockets.util;

import com.devicehive.json.GsonFactory;
import com.devicehive.websockets.HiveWebsocketSessionState;
import com.google.gson.JsonElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;


@Component
public class AsyncMessageSupplier implements ApplicationListener<FlushQueueEvent> {

    private static final Logger logger = LoggerFactory.getLogger(AsyncMessageSupplier.class);


    @Async
    public void onApplicationEvent(FlushQueueEvent event) {
        Session session = event.getSession();
        ConcurrentLinkedQueue<JsonElement> queue = HiveWebsocketSessionState.get(session).getQueue();
        boolean acquired = false;
        try {
            acquired = HiveWebsocketSessionState.get(session).getQueueLock().tryLock();
            if (acquired) {
                while (!queue.isEmpty()) {
                    JsonElement jsonElement = queue.peek();
                    if (jsonElement == null) {
                        queue.poll();
                        continue;
                    }
                    if (session.isOpen()) {
                        String data = GsonFactory.createGson().toJson(jsonElement);
                        session.getBasicRemote().sendText(data);
                        queue.poll();
                    } else {
                        logger.error("Session is closed. Unable to deliver message");
                        queue.clear();
                        return;
                    }
                    logger.debug("Session {}: {} messages left", session.getId(), queue.size());
                }
            }
        } catch (IOException ex) {
            logger.error("Error messages delivery to session {}",session.getId(), ex);
        } finally {
            if (acquired) {
                HiveWebsocketSessionState.get(session).getQueueLock().unlock();
            }
        }
    }

}



