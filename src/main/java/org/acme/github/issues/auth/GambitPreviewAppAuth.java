/*
 * Copyright 2019 CloudBees, Inc.
 * All rights reserved.
 */

package org.acme.github.issues.auth;

import javax.ws.rs.HeaderParam;

/**
 * GitHub App Authentication for Gambit Preview calls.
 */
public class GambitPreviewAppAuth extends AppAuth {

    static String GAMBIT_PREVIEW_ACCEPT_HEADER = "application/vnd.github.gambit-preview+json";

    /**
     * Returns the GambitPreview auth for the provided AppAuth.
     * @param appAuth the AppAuth to wrap
     * @return the GambitPreview auth
     */
    public static GambitPreviewAppAuth of(AppAuth appAuth) {
        return new GambitPreviewAppAuth(appAuth.jwtToken);
    }

    GambitPreviewAppAuth(String jwtToken) {
        super(jwtToken);
    }

    @HeaderParam("Accept")
    public String getAccept() {
        return GAMBIT_PREVIEW_ACCEPT_HEADER;
    }
}
