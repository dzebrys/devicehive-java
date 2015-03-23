package com.devicehive.service;

import com.devicehive.configuration.Messages;
import com.devicehive.dao.IdentityProviderDAO;
import com.devicehive.exceptions.HiveException;
import com.devicehive.model.IdentityProvider;
import com.devicehive.util.HiveValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

/**
 * Created by tmatvienko on 11/17/14.
 */
@Service
public class IdentityProviderService {

    @Autowired
    private IdentityProviderDAO identityProviderDAO;
    @Autowired
    private HiveValidator hiveValidator;

    public IdentityProvider find(@NotNull Long id) {
        return identityProviderDAO.get(id);
    }


    public IdentityProvider find(@NotNull String name) {
        return identityProviderDAO.get(name);
    }


    public boolean delete(@NotNull Long id) {
        return identityProviderDAO.delete(id);
    }

    public IdentityProvider update(@NotNull Long identityProviderId, IdentityProvider identityProvider) {
        IdentityProvider existing = find(identityProviderId);
        if (existing == null) {
            throw new HiveException(String.format(Messages.IDENTITY_PROVIDER_NOT_FOUND, identityProviderId), BAD_REQUEST.getStatusCode());
        }
        if (identityProvider.getName() != null) {
            existing.setName(identityProvider.getName());
        }
        if (identityProvider.getApiEndpoint() != null) {
            existing.setApiEndpoint(identityProvider.getApiEndpoint());
        }
        hiveValidator.validate(existing);
        return identityProviderDAO.update(existing);
    }
}
