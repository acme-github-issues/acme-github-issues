/*
 * Copyright 2019 CloudBees, Inc.
 * All rights reserved.
 */

package org.acme.github.issues.client;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.acme.github.issues.auth.AppAuth;
import org.acme.github.issues.auth.GambitPreviewAppAuth;
import org.acme.github.issues.auth.GitHubTokenAuth;
import org.acme.github.issues.exception.GitHubApiException;
import org.acme.github.issues.model.AccessToken;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 * A REST client used to make issue requests to GitHub on behalf of the GitHub App as well
 * as installations.
 */
@RegisterRestClient
@Consumes("application/json")
@Produces("application/json")
public interface GitHubApiClient {

    @GET
    @Path("/app")
    JsonObject getAppInfo(@BeanParam AppAuth appAuth) throws GitHubApiException;

    @GET
    @Path("/app/installations")
    JsonArray getAppInstallations(@BeanParam AppAuth appAuth) throws GitHubApiException;

    @GET
    @Path("/app/installations/{installationId}")
    JsonObject getAppInstallation(@BeanParam AppAuth appAuth,
                                  @PathParam("installationId") String installationId) throws GitHubApiException;

    @DELETE
    @Path("/app/installations/{installationId}")
    void deleteAppInstallation(@BeanParam GambitPreviewAppAuth appAuth,
                               @PathParam("installationId") String installationId) throws GitHubApiException,
            NotFoundException;

    @POST
    @Path("/app/installations/{installation}/access_tokens")
    AccessToken getAccessTokenForInstallation(@BeanParam AppAuth appAuth,
                                              @PathParam("installation") String installation) throws GitHubApiException;

    @GET
    @Path(("/user/installations"))
    JsonObject getUserInstallations(@BeanParam GitHubTokenAuth gitHubTokenAuth) throws GitHubApiException;
}
