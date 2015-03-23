package com.devicehive.controller;


import com.devicehive.auth.HiveRoles;
import com.devicehive.auth.WwwAuthenticateRequired;
import com.devicehive.configuration.ConfigurationService;
import com.devicehive.configuration.Constants;
import com.google.common.net.HttpHeaders;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.net.URI;

import static com.devicehive.configuration.Constants.NAME;
import static com.devicehive.configuration.Constants.VALUE;

/**
 * Provide API information
 */
@Path("/configuration")
@Component
public class ConfigurationController {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationController.class);
    @Autowired
    private ConfigurationService configurationService;


    @GET
    @RolesAllowed(HiveRoles.ADMIN)
    @Path("/{" + NAME + "}")
    @WwwAuthenticateRequired
    public Response get(@PathParam(NAME) String name) {
        return Response.ok().entity(configurationService.get(name)).build();
    }

    @PUT
    @RolesAllowed(HiveRoles.ADMIN)
    @Path("/{" + NAME + "}")
    @WwwAuthenticateRequired
    public Response setProperty(@PathParam(NAME) String name, String value) {
        configurationService.save(name, value);
        return Response.ok().build();
    }

    @GET
    @RolesAllowed(HiveRoles.ADMIN)
    @Path("/{" + NAME + "}/set")
    @WwwAuthenticateRequired
    public Response setPropertyGet(@PathParam(NAME) String name, @QueryParam(VALUE) String value) {
        configurationService.save(name, value);
        return Response.ok().build();
    }

    @DELETE
    @RolesAllowed(HiveRoles.ADMIN)
    @Path("/{" + NAME + "}")
    @WwwAuthenticateRequired
    public Response deleteProperty(@PathParam(NAME) String name) {
        configurationService.delete(name);
        return Response.noContent().build();
    }


    @POST
    @RolesAllowed(HiveRoles.ADMIN)
    @Path("/auto")
    @WwwAuthenticateRequired
    public Response auto(@HeaderParam(HttpHeaders.REFERER) String referrer) {
        try {
            URI ref = checkURI(referrer);
            String refString = ref.toString();
            String restUri = StringUtils.removeEnd(refString, "/") + "/rest";
            String
                wesocketUri =
                StringUtils.removeEnd("ws" + StringUtils.removeStart(refString, "http"), "/") + "/websocket";
            configurationService.save(Constants.REST_SERVER_URL, restUri);
            configurationService.save(Constants.WEBSOCKET_SERVER_URL, wesocketUri);
            return Response.seeOther(ref).build();
        } catch (Exception ex) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }


    private URI checkURI(String referrer) {
        URI uri = URI.create(referrer);
        if (!("http".equals(uri.getScheme()) || "https".equals(uri.getScheme()))) {
            throw new IllegalArgumentException();
        }
        return uri;
    }


}
