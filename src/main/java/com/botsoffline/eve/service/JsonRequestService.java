package com.botsoffline.eve.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.botsoffline.eve.domain.CharacterLocation;
import com.botsoffline.eve.domain.SolarSystem;
import com.botsoffline.eve.domain.SolarSystemStats;
import com.botsoffline.eve.domain.Token;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.BaseRequest;
import com.mashape.unirest.request.GetRequest;
import com.mashape.unirest.request.body.MultipartBody;
import com.mashape.unirest.request.body.RequestBodyEntity;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JsonRequestService {

    private static final String WRONG_STATUS_CODE = "{} returned status code {}.";
    private static final String UNIREST_EXCEPTION = "Failed to get data from url={}";
    private static final String ESI_BASE_URL = "https://esi.tech.ccp.is";
    private static final String TOKEN_URL = "https://login.eveonline.com/oauth/token";
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Map<String, String> defaultHeaders;

    @Value("${CLIENT_ID}")
    private String clientId;

    @Value("${CLIENT_SECRET}")
    private String clientSecret;

    public JsonRequestService() {
        defaultHeaders = new HashMap<>();
        defaultHeaders.put("X-User-Agent", "EvE: dronesdronesdrones");
        defaultHeaders.put("Accept-Encoding", "gzip");
    }

    public String getAccessToken(final Token token) throws UnirestException {
        final HttpResponse<JsonNode> response = Unirest.post(TOKEN_URL)
                                                 .headers(defaultHeaders)
                                                 .field("grant_type","refresh_token")
                                                 .field("refresh_token", token.getRefreshToken())
                                                 .basicAuth(token.getClientId(), token.getClientSecret())
                                                 .asJson();
        final JSONObject object = response.getBody().getObject();
        return object.getString("access_token");
    }

    public Optional<JsonNode> getInitialAccessToken(final String code) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        Map<String, Object> fields = new HashMap<>();
        fields.put("grant_type", "authorization_code");
        fields.put("code", code);

        MultipartBody postRequest = post(TOKEN_URL, clientId, clientSecret, headers, fields);

        return executeRequest(postRequest);
    }

    public Optional<JsonNode> getAccessToken(final String refreshToken) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        Map<String, Object> fields = new HashMap<>();
        fields.put("grant_type", "refresh_token");
        fields.put("refresh_token", refreshToken);

        MultipartBody postRequest = post(TOKEN_URL, clientId, clientSecret, headers, fields);

        return executeRequest(postRequest);
    }

    public Optional<JsonNode> getUserDetails(final String accessToken) {
        final String url = "https://login.eveonline.com/oauth/verify";
        final Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + accessToken);

        final GetRequest getRequest = get(url, headers);

        return executeRequest(getRequest);
    }

    private Optional<JsonNode> justGet(final String url) {
        return executeRequest(get(url, null));
    }

    Optional<JsonNode> executeRequest(final BaseRequest request) {
        try {
            HttpResponse<JsonNode> response = request.asJson();
            if (response.getStatus() != 200) {
                log.warn(WRONG_STATUS_CODE, request.getHttpRequest().getUrl(), response.getStatus());
                return Optional.empty();
            }
            return Optional.of(response.getBody());
        } catch (UnirestException e) {
            log.error(UNIREST_EXCEPTION, request.getHttpRequest().getUrl(), e);
            return Optional.empty();
        }
    }

    GetRequest get(final String url, final Map<String, String> headers) {
        return Unirest.get(url).headers(defaultHeaders).headers(headers);
    }

    MultipartBody post(final String url, final String username, final String password, final Map<String, String> headers, final Map<String, Object> fields) {
        return Unirest.post(url).basicAuth(username, password).headers(defaultHeaders).headers(headers).fields(fields);
    }

    RequestBodyEntity post(final String url, final String body) {
        return Unirest.post(url).body(body);
    }

    Optional<JsonNode> getCharacterName(final long characterId) {
        return justGet(String.format("%s/v1/characters/names/?character_ids=%d", ESI_BASE_URL, characterId));
    }

    public List<Long> getAllSystemIds() {
        final Optional<JsonNode> jsonNode = justGet(ESI_BASE_URL + "/v1/universe/systems/");
        final List<Long> result = new ArrayList<>();
        if (jsonNode.isPresent()) {
            final JSONArray array = jsonNode.get().getArray();
            for (int i = 0; i < array.length(); i++) {
                result.add(array.getLong(i));
            }
        } else {
            throw new RuntimeException("Loading SystemIds failed.");
        }
        log.info("Retrieved {} systems.", result.size());
        return result;
    }

    public SolarSystem isNullsec(final Long systemId) {
        final Optional<JsonNode> jsonNode = justGet(String.format("%s/v3/universe/systems/%d/", ESI_BASE_URL, systemId));
        if (jsonNode.isPresent()) {
            final JSONObject obj = jsonNode.get().getObject();
            final double securityStatus = obj.getDouble("security_status");
            final String name = obj.getString("name");
            final long constellationId = obj.getLong("constellation_id");
            return new SolarSystem(systemId, name, securityStatus, constellationId);
        } else {
            throw new RuntimeException("Loading SystemIds failed.");
        }
    }

    public List<SolarSystemStats> getSolarSystemStats() {
        final Optional<JsonNode> jsonNode = justGet(ESI_BASE_URL + "/v2/universe/system_kills/");
        final List<SolarSystemStats> result = new ArrayList<>();
        if (jsonNode.isPresent()) {
            final JSONArray array = jsonNode.get().getArray();
            for (int i = 0; i < array.length(); i++) {
                final JSONObject obj = array.getJSONObject(i);
                result.add(new SolarSystemStats(obj.getLong("system_id"), obj.getInt("ship_kills"),
                                                obj.getInt("npc_kills"), obj.getInt("pod_kills")));
            }
        } else {
            log.warn("Failed to load solarSystemStats.");
        }
        return result;
    }

    public CharacterLocation getLocation(final long characterId, final String accessToken) {
        final String url = String.format("%s/v1/characters/%d/location/?token=%s", ESI_BASE_URL, characterId, accessToken);
        final Optional<JsonNode> jsonNode = justGet(url);
        if (jsonNode.isPresent()) {
            final JSONObject obj = jsonNode.get().getObject();
            final Long systemId = obj.getLong("solar_system_id");
            final Long structureId = obj.has("structure_id") ? obj.getLong("structure_id") : null;
            return new CharacterLocation(characterId, systemId, structureId);
        } else {
            log.warn("Failed to location for {}.", characterId);
            return null;
        }
    }

    public boolean isOnline(final long characterId, final String accessToken) {
        final String url = String.format("%s/v1/characters/%d/online/?token=%s", ESI_BASE_URL, characterId, accessToken);
        final GetRequest onlineRequest = get(url, null);
        final HttpResponse<String> onlineResponse;
        try {
            onlineResponse = onlineRequest.asString();
        } catch (final UnirestException e) {
            log.error("Failed to load online status for {}.", characterId, e);
            return false;
        }
        if (onlineResponse.getStatus() != 200) {
            log.warn(WRONG_STATUS_CODE, onlineRequest.getHttpRequest().getUrl(), onlineResponse.getStatus());
            return false;
        }
        return Boolean.parseBoolean(onlineResponse.getBody());
    }

    public void setClientId(final String clientId) {
        this.clientId = clientId;
    }

    public void setClientSecret(final String clientSecret) {
        this.clientSecret = clientSecret;
    }

    Long getCorporationId(final long characterId) {
        final String url = String.format("%s/v4/characters/%d", ESI_BASE_URL, characterId);
        return justGet(url)
                .map(response -> response.getObject().getLong("corporation_id"))
                .orElse(null);
    }

    Long getAllianceId(final long corporationId) {
        final String url = String.format("%s/v3/corporations/%d", ESI_BASE_URL, corporationId);
        return justGet(url)
                .map(response -> {
                    final JSONObject obj = response.getObject();
                    return obj.has("alliance_id") ? obj.getLong("alliance_id") : null;
                })
                .orElse(null);
    }
}
