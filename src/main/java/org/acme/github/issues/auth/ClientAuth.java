/*
 * Copyright 2019 CloudBees, Inc.
 * All rights reserved.
 */

package org.acme.github.issues.auth;

import io.quarkus.runtime.annotations.RegisterForReflection;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.QueryParam;

/**
 * Used by GitHubApiClient to provide request headers/parameters for a GitHub Oauth request.
 */
@RegisterForReflection
public class ClientAuth {
    private String clientId;
    private String clientSecret;

    public static ClientAuth of(String clientId, String clientSecret) {
        return new ClientAuth(clientId, clientSecret);
    }

    ClientAuth() {

    }

    ClientAuth(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    @QueryParam("client_id")
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @QueryParam("client_secret")
    public String getClientSecret() {
        return clientSecret;
    }

    @HeaderParam("Accept")
    public String getAccept() {
        return "application/json";
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }
}
