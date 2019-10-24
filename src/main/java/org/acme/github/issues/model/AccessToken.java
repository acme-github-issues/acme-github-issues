/*
 * Copyright 2019 CloudBees, Inc.
 * All rights reserved.
 */

package org.acme.github.issues.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import java.util.Date;
import java.util.Objects;
import javax.json.bind.annotation.JsonbProperty;

/**
 * A POJO for a GitHub Access Token.
 */
@RegisterForReflection
public class AccessToken {
    /**
     * The access token used to make requests for a specific installation.
     */
    private String token;

    /**
     * The expiry time for the access token.
     */
    @JsonbProperty("expires_at")
    private Date expiresAt;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getExpiresAt() {
        return new Date(expiresAt.getTime());
    }

    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = new Date(expiresAt.getTime());
    }

    @Override
    public int hashCode() {

        return Objects.hash(token, expiresAt);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AccessToken that = (AccessToken) o;
        return Objects.equals(token, that.token)
                && Objects.equals(expiresAt, that.expiresAt);
    }

    @Override
    public String toString() {
        return "AccessToken{"
                + "token='" + token + '\''
                + ", expiresAt=" + expiresAt
                + '}';
    }
}
