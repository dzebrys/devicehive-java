package com.devicehive.dao;

import com.devicehive.configuration.Constants;
import com.devicehive.model.Configuration;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;
import java.util.List;

import static com.devicehive.model.Configuration.Queries.Names.*;
import static com.devicehive.model.Configuration.Queries.Parameters.NAME;

@Repository
@Transactional
public class ConfigurationDAO {

    @PersistenceContext(unitName = Constants.PERSISTENCE_UNIT)
    private EntityManager em;

    public Configuration findByName(@NotNull String name) {
        TypedQuery<Configuration> query = em.createNamedQuery(GET_BY_NAME, Configuration.class);
        query.setParameter(NAME, name);
        CacheHelper.cacheable(query);
        List<Configuration> list = query.getResultList();
        return list.isEmpty() ? null : list.get(0);
    }

    public List<Configuration> findAll() {
        TypedQuery<Configuration> query = em.createNamedQuery(GET_ALL, Configuration.class);
        CacheHelper.cacheable(query);
        return query.getResultList();
    }

    public void save(@NotNull String name, String value) {
        Configuration existing = findByName(name);
        if (existing != null) {
            existing.setValue(value);
        } else {
            Configuration configuration = new Configuration();
            configuration.setName(name);
            configuration.setValue(value);
            em.persist(configuration);
        }
    }

    public void delete(@NotNull String name) {
        Query query = em.createNamedQuery(DELETE);
        query.setParameter(NAME, name);
        query.executeUpdate();
    }
}
