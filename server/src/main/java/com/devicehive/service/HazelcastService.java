package com.devicehive.service;


import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Set;

@Service
public class HazelcastService {

    private static final Logger logger = LoggerFactory.getLogger(HazelcastService.class);

    private HazelcastInstance hazelcast;

    private boolean wasCreated = false;

    @PostConstruct
    protected void postConstruct() {
        logger.info("Initializing Hazelcast instance...");
        Set<HazelcastInstance> set = Hazelcast.getAllHazelcastInstances();
        if (!set.isEmpty()) {
            hazelcast = set.iterator().next();
            logger.info("Existing Hazelcast instance is reused: " + hazelcast);
        } else {
            hazelcast = Hazelcast.newHazelcastInstance();
            wasCreated = true;
            logger.info("New Hazelcast instance created: " + hazelcast);
        }
    }

    @PreDestroy
    protected void preDestroy() {
        if (wasCreated) {
            hazelcast.getLifecycleService().shutdown();
        }
    }


    public HazelcastInstance getHazelcast() {
        return hazelcast;
    }
}
