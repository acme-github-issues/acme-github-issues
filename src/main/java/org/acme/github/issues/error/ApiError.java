/*
 * Copyright 2019 CloudBees, Inc.
 * All rights reserved.
 */

package org.acme.github.issues.error;

import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * A simple API Error response containing an error message.
 */
@RegisterForReflection
public class ApiError {
    private String error;

    public ApiError(String error) {
        this.error = error;
    }

    public ApiError() {

    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
