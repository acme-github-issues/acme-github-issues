package org.acme.github.issues;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import org.acme.github.issues.client.SDMApiClient;
import org.acme.github.issues.model.AppConfig;
import org.acme.github.issues.model.SDMAuth;
import org.apache.commons.io.IOUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class ConfigManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigManager.class);

    @Inject
    @ConfigProperty(name = "sdm.app.id", defaultValue = "24")
    String appId;

    @Inject
    @RestClient
    SDMApiClient sdmApiClient;

    @Inject
    ApiManager apiManager;

    private JsonObject getConfigQuery;
    private String createConfigQueryStr;
    private Map<String, List<AppConfig>> installations;

    public Map<String, List<AppConfig>> getInstallations() {
        if (installations == null) {
            SDMAuth auth = new SDMAuth(apiManager.createToken("acme-issues"));
            JsonObject response = sdmApiClient.executeQuery("acme-issues", auth, getGetConfigQuery());

            if (response.getJsonArray("errors").size() > 0) {
                LOGGER.error("Error fetching config: {}", response.getJsonArray("errors"));
                return Collections.emptyMap();
            }

            installations = new HashMap();
            response.getJsonObject("data").getJsonObject("configs").getJsonArray("nodes")
                    .stream()
                    .map(JsonValue::asJsonObject)
                    .forEach( e-> {
                        String accountName = e.getString("account");
                        List<AppConfig> ghAccounts = new ArrayList<>();
                        e.getJsonObject("config").getJsonArray("githubAccounts").stream()
                         .map(JsonValue::asJsonObject)
                         .forEach( en -> {
                                       ghAccounts.add(new AppConfig(en));
                                   });
                        installations.put(accountName, ghAccounts);
                    });

        }
        return installations;
    }

    public void writeAccountInstallation(String account, String githubAccount, String installationId) {
        Map<String, List<AppConfig>> installations = getInstallations();
        if (installations.containsKey(account)) {
            LOGGER.info("Updating existing app config for account {} for githubAccount {} and installationId {}", account, githubAccount, installationId);
        } else {
            LOGGER.info("Creating new app config for account {} for githubAccount {} and installationId {}", account, githubAccount, installationId);
            SDMAuth auth = new SDMAuth(apiManager.createToken("acme-issues"));
            JsonObject query = getCreateConfigQuery(appId, account, githubAccount, installationId);
            JsonObject response = sdmApiClient.executeQuery("acme-issues", auth, query);

            if (response.getJsonArray("errors").size() > 0) {
                LOGGER.error("Error creating config ({}): {}", query, response.getJsonArray("errors"));
            }
        }
    }

    private JsonObject getGetConfigQuery() {
        if (getConfigQuery == null) {
            String query = null;
            try (InputStream is = WebhookResource.class.getResourceAsStream("config-query.graphql")) {
                query = IOUtils.toString(is, Charset.defaultCharset());
            } catch (IOException e) {
                LOGGER.error("Error parsing data mutation query file", e);
            }

            getConfigQuery = Json.createObjectBuilder()
                                 .add("query", query)
                                 .add("variables", Json.createObjectBuilder()
                        .add("appId", appId)
                        .build())
                                 .add("operationName", "getConfigs")
                                 .build();
        }

        return getConfigQuery;
    }

    private JsonObject getCreateConfigQuery(String appId, String account, String githubAccount, String installationId) {
        if (createConfigQueryStr == null) {
            String queryStr = null;
            try (InputStream is = WebhookResource.class.getResourceAsStream("add-config.graphql")) {
                queryStr = IOUtils.toString(is, Charset.defaultCharset());
            } catch (IOException e) {
                LOGGER.error("Error parsing config creation query file", e);
            }
            createConfigQueryStr = queryStr;
        }

        JsonObject config = Json.createObjectBuilder()
                .add("appId", appId)
                .add("account", account)
                .add("config", Json.createObjectBuilder()
                     .add("githubAccounts", Json.createArrayBuilder()
                        .add(Json.createObjectBuilder()
                                 .add("account", githubAccount)
                                 .add("installationId", installationId)
                                 .build())
                        ).build()

                ).build();

        System.out.println("config=" + config);

        JsonObject createConfigQuery = Json.createObjectBuilder()
                             .add("query", createConfigQueryStr)
                             .add("variables", Json.createObjectBuilder()
                                                   .add("config", config).build())
                             .add("operationName", "addConfig")
                             .build();
        return createConfigQuery;
    }
}
