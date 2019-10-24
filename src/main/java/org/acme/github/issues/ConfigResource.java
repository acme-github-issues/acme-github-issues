package org.acme.github.issues;

import java.util.Optional;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.acme.github.issues.error.ApiError;
import org.acme.github.issues.exception.GitHubApiException;
import org.acme.github.issues.model.UserAccessToken;
import org.acme.github.issues.service.GitHubService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/config")
public class ConfigResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigResource.class);

    @Inject
    GitHubService gitHubService;

    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response validateInstallation(@QueryParam("installation_id") String installationId,
                                         @QueryParam("code") String code,
                                         @QueryParam("account") String sdmAccount) throws WebApplicationException {

        UserAccessToken accessToken = gitHubService.exchangeForUserAccessToken(code);
        if (accessToken == null || StringUtils.isBlank(accessToken.getToken())) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ApiError("User not found for code " + code))
                    .build();
        }

        Optional<JsonObject> matchingInstallation;
        try {
            matchingInstallation = gitHubService.getUserInstallations(accessToken)
                    .getJsonArray("installations")
                    .stream()
                    .map(JsonValue::asJsonObject)
                    .filter(v -> installationId.equals(String.valueOf(v.asJsonObject().getInt("id"))))
                    .findFirst();

            if (!matchingInstallation.isPresent()) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new ApiError("User does not have access to installation " + installationId))
                        .build();
            }
        } catch (GitHubApiException e) {
            LOGGER.warn("Unable to lookup user installations for token {}: {}", accessToken, e.getMessage());
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ApiError("Unable to lookup installations for user with token " + accessToken))
                    .build();
        }

        try {
            JsonObject installation = gitHubService.getAppInstallation(installationId);

            if (installation == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ApiError("Unable to locate installation " + installationId + "."))
                        .build();
            }
        } catch (GitHubApiException e) {
            LOGGER.warn("Unable to lookup installation {} within GitHub", installationId, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ApiError("Unable to lookup installation " + installationId))
                    .build();
        }

        return Response.ok().entity(matchingInstallation.get()).build();
    }
}
