package com.devicehive.service;


import com.devicehive.configuration.Constants;
import com.devicehive.model.ServerTimestamp;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.sql.Timestamp;

import static com.devicehive.model.ServerTimestamp.Queries.Names.GET;

@Service
public class TimestampService {

    @PersistenceContext(unitName = Constants.PERSISTENCE_UNIT)
    private EntityManager em;

    public Timestamp getTimestamp() {
        TypedQuery<ServerTimestamp> query = em.createNamedQuery(GET, ServerTimestamp.class);
        return query.getSingleResult().getTimestamp();
    }

}
