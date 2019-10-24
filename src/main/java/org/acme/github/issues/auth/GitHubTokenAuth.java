/*
 * Copyright 2019 CloudBees, Inc.
 * All rights reserved.
 */

package org.acme.github.issues.auth;

import javax.ws.rs.HeaderParam;

/**
 * Used by GitHubApiClient to provide request headers/parameters for a GitHub Installation request.
 */
public class GitHubTokenAuth extends GitHubAuth {

    private String accessToken;

    public static GitHubTokenAuth of(String accessToken) {
        return new GitHubTokenAuth(accessToken);
    }

    GitHubTokenAuth(String accessToken) {
        this.accessToken = accessToken;
    }

    @HeaderParam("Authorization")
    public String getAuthorization() {
        return "token " + accessToken;
    }
}
