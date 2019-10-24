/*
 * Copyright 2019 CloudBees, Inc.
 * All rights reserved.
 */

package org.acme.github.issues.client;

import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import org.acme.github.issues.auth.ClientAuth;
import org.acme.github.issues.model.UserAccessToken;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;


/**
 * A REST client used to access GitHub Oauth.
 */
@RegisterRestClient
@Produces("application/json")
@Consumes("application/json")
public interface GitHubAuthClient {

    @POST
    @Path("/access_token")
    UserAccessToken exchangeForUserAccessToken(@BeanParam ClientAuth clientAuth,
                                               @QueryParam("code") String token) throws WebApplicationException;
}
