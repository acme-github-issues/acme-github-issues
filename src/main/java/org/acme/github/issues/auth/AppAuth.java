/*
 * Copyright 2019 CloudBees, Inc.
 * All rights reserved.
 */

package org.acme.github.issues.auth;

import io.quarkus.runtime.annotations.RegisterForReflection;
import javax.ws.rs.HeaderParam;

/**
 * Used by GitHubApiClient to provide request headers/parameters for a GitHub App request.
 */
@RegisterForReflection
public class AppAuth extends GitHubAuth {

    String jwtToken;

    public static AppAuth of(String jwtToken) {
        return new AppAuth(jwtToken);
    }

    AppAuth(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    @HeaderParam("Authorization")
    public String getAuthorization() {
        return "Bearer " + jwtToken;
    }
}
