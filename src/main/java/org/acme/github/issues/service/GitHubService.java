package org.acme.github.issues.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.JacksonSerializer;
import io.quarkus.runtime.StartupEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.security.PrivateKey;
import java.security.Security;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import org.acme.github.issues.WebhookResource;
import org.acme.github.issues.auth.AppAuth;
import org.acme.github.issues.auth.ClientAuth;
import org.acme.github.issues.auth.GitHubTokenAuth;
import org.acme.github.issues.client.GitHubApiClient;
import org.acme.github.issues.client.GitHubAuthClient;
import org.acme.github.issues.exception.GitHubApiException;
import org.acme.github.issues.model.UserAccessToken;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class GitHubService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GitHubService.class);
    private static final Duration JWT_EXPIRATION = Duration.ofMinutes(10);

    /**
     * Private key required for the GitHub App.
     */
    @ConfigProperty(name = "github.app.private.key", defaultValue = "acme-github-issues.pem")
    String privateKeyFile;

    /**
     * The GitHub App ID.
     */
    @ConfigProperty(name = "github.app.id", defaultValue = "44407")
    String applicationId;

    /**
     * The GitHub App Client ID.
     */
    @ConfigProperty(name = "github.app.client.id", defaultValue = "Iv1.ef58593da000cc84")
    String clientId;

    /**
     * The GitHub App Client Secret.
     */
    @ConfigProperty(name = "github.app.client.secret", defaultValue = "77ff8a3f8aa51876a171b10c63042391c8b2b3b6")
    String clientSecret;

    @Inject
    @RestClient
    GitHubApiClient gitHubApiClient;

    @Inject
    @RestClient
    GitHubAuthClient gitHubAuthClient;

    private AppAuth appAuth;
    private AtomicLong appAuthExpiration = new AtomicLong();
    private PrivateKey privateKey;

    @PostConstruct
    void init() {
        Security.addProvider(new BouncyCastleProvider());
        privateKey = getPrivateKey();
        appAuth = getAppAuth();
        LOGGER.info("GitHubService started.");
    }

    void startup(@Observes StartupEvent se) {
        LOGGER.info("Installations: {}", getAppInstallations());
    }

    public JsonArray getAppInstallations() {
        JsonArray installations = gitHubApiClient.getAppInstallations(getAppAuth());

        List<String> installationIds = installations
                .getValuesAs(JsonValue::asJsonObject)
                .stream()
                .map(val -> String.format(
                        "<%s> %s",
                        val.get("id").toString(),
                        val.getJsonObject("account").getString("login"))
                )
                .collect(Collectors.toList());
        LOGGER.info("Installations: {}", installationIds);

        return installations;
    }

    public JsonObject getAppInstallation(String installationId) throws GitHubApiException {
        return gitHubApiClient.getAppInstallation(getAppAuth(), installationId);
    }

    public JsonObject getUserInstallations(UserAccessToken userAccessToken) throws GitHubApiException {
        return gitHubApiClient.getUserInstallations(
                GitHubTokenAuth.of(userAccessToken.getToken())
        );
    }

    public UserAccessToken exchangeForUserAccessToken(String code) {
        return gitHubAuthClient.exchangeForUserAccessToken(
                getClientAuth(),
                code
        );
    }

    private ClientAuth getClientAuth() {
        return ClientAuth.of(clientId, clientSecret);
    }

    private AppAuth getAppAuth() {
        if (applicationId == null || privateKey == null) {
            throw new IllegalStateException("GitHub App Auth not initialized.");
        }

        if (System.currentTimeMillis() > appAuthExpiration.get()) {
            LOGGER.debug("Creating new JWT token");
            SignatureAlgorithm algorithm = SignatureAlgorithm.RS256;
            Instant start = Instant.now();
            long expiration = start.plus(JWT_EXPIRATION).toEpochMilli();

            String jwtToken = Jwts.builder()
                    .serializeToJsonWith(new JacksonSerializer<>(new ObjectMapper()))
                    .signWith(privateKey, algorithm)
                    .setIssuedAt(Date.from(start))
                    .setExpiration(new Date(expiration))
                    .setIssuer(applicationId)
                    .compact();
            LOGGER.debug("Created JWT Token: {}", jwtToken);
            appAuth = AppAuth.of(jwtToken);

            // Expire a minute prior to actual expiration to account for timing offsets between us and GitHub
            appAuthExpiration.set(expiration - Duration.ofMinutes(1).toMillis());
        }

        return appAuth;
    }

    private PrivateKey getPrivateKey() {
        String privateKeyString;
        try (InputStream is = WebhookResource.class.getResourceAsStream(privateKeyFile)) {
            privateKeyString = IOUtils.toString(is, Charset.defaultCharset());
        } catch (IOException e) {
            LOGGER.error("Error parsing data mutation query file", e);
            return null;
        }

        try {
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME);
            PEMParser parser = new PEMParser(new StringReader(privateKeyString));
            Object o = parser.readObject();

            if (o instanceof PEMKeyPair) {
                return converter.getPrivateKey(((PEMKeyPair) o).getPrivateKeyInfo());
            } else if (o instanceof PrivateKey) {
                return (PrivateKey) o;
            }
        } catch (IOException e) {
            LOGGER.error("Unable to parse private key", e);
        }

        return null;
    }
}
