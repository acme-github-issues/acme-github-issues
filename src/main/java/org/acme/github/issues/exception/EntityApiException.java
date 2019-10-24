/*
 * Copyright 2019 CloudBees, Inc.
 * All rights reserved.
 */

package org.acme.github.issues.exception;

import javax.json.bind.JsonbBuilder;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * Helper ApiException that extracts the entity from the response for further deserialization.
 */
abstract class EntityApiException extends WebApplicationException {

    private String entity;

    EntityApiException() {

    }

    EntityApiException(Response response) {
        super(response);

        if (response.hasEntity()) {
            this.entity = response.readEntity(String.class);
        }
    }

    /**
     * Attempts to deserialize the entity to the provided type.
     *
     * @param entityType the type to deserialize
     * @return the deserialized entity
     */
    <T> T getEntityAs(Class<T> entityType) {
        if (entity == null) {
            return null;
        }

        return JsonbBuilder.create().fromJson(entity, entityType);
    }

}
