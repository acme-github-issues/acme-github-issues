/*
 * Copyright 2019 CloudBees, Inc.
 * All rights reserved.
 */

package org.acme.github.issues.error;

import io.quarkus.runtime.annotations.RegisterForReflection;
import javax.json.bind.annotation.JsonbProperty;

/**
 * Represents an error that can be returned from the GitHub App API.
 */
@RegisterForReflection
public class GitHubApiError {
    private String message;

    @JsonbProperty("documentation_url")
    private String documentationUrl;

    public GitHubApiError() {

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDocumentationUrl() {
        return documentationUrl;
    }

    public void setDocumentationUrl(String documentationUrl) {
        this.documentationUrl = documentationUrl;
    }
}
