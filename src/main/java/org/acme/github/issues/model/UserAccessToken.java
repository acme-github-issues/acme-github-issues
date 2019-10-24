/*
 * Copyright 2019 CloudBees, Inc.
 * All rights reserved.
 */

package org.acme.github.issues.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import java.util.Objects;
import javax.json.bind.annotation.JsonbProperty;

/**
 * A POJO for a GitHub User Access Token.
 */
@RegisterForReflection
public class UserAccessToken {
    /**
     * The access token used to make requests for a specific installation.
     */
    @JsonbProperty("access_token")
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public int hashCode() {
        return Objects.hash(token);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserAccessToken that = (UserAccessToken) o;
        return Objects.equals(token, that.token);
    }

    @Override
    public String toString() {
        return "AccessToken{"
                + "token='" + token + '\''
                + '}';
    }
}
