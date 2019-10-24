/*
 * Copyright 2019 CloudBees, Inc.
 * All rights reserved.
 */

package org.acme.github.issues.exception;

import javax.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

/**
 * A ResponseExceptionMapper for GitHubApiException.
 */
@RegisterProvider(GitHubApiExceptionMapper.class)
public class GitHubApiExceptionMapper implements ResponseExceptionMapper<GitHubApiException> {

    @Override
    public GitHubApiException toThrowable(Response response) {
        return new GitHubApiException(response);
    }
}