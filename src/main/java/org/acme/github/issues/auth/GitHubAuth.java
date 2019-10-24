/*
 * Copyright 2019 CloudBees, Inc.
 * All rights reserved.
 */

package org.acme.github.issues.auth;

import javax.ws.rs.HeaderParam;

/**
 * Defines the required headers for a GitHub Auth request.
 */
public abstract class GitHubAuth {

    static String GITHUB_ACCEPT_HEADER = "application/vnd.github.machine-man-preview+json";

    @HeaderParam("Accept")
    public String getAccept() {
        return GITHUB_ACCEPT_HEADER;
    }
}
