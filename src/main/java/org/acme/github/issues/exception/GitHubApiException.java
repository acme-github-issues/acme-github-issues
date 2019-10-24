/*
 * Copyright 2019 CloudBees, Inc.
 * All rights reserved.
 */

package org.acme.github.issues.exception;

import javax.ws.rs.core.Response;
import org.acme.github.issues.error.GitHubApiError;

/**
 * An exception thrown from the GitHub Rest Client that contains the formatted GitHub error
 * payload.
 */
public class GitHubApiException extends EntityApiException {

    private GitHubApiError gitHubApiError;

    public GitHubApiException(Response response) {
        super(response);
    }

    public GitHubApiException(GitHubApiError gitHubApiError) {
        this.gitHubApiError = gitHubApiError;
    }

    /**
     * Returns the formatted GitHub Error.
     * @return the GitHub error
     */
    public GitHubApiError getGitHubApiError() {
        if (gitHubApiError == null) {
            this.gitHubApiError = getEntityAs(GitHubApiError.class);
        }

        return gitHubApiError;
    }

}
