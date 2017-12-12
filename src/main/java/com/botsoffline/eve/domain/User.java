/*
 * User.java
 *
 * Created on 2017-12-10
 *
 */

package com.botsoffline.eve.domain;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.validation.constraints.NotNull;

import com.botsoffline.eve.domain.enums.TrackingStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * A user.
 */

@Document(collection = "jhi_user")
public class User extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Indexed
    private String login;

    @NotNull
    private Long characterId;
    private Long corporationId;
    private Long allianceId;

    private boolean activated = true;

    @JsonIgnore
    private Set<Authority> authorities = new HashSet<>();

    @NotNull
    private String characterOwnerHash;

    @NotNull
    private String refreshToken;
    private Instant accessTokenExpiry;
    private String accessToken;
    private TrackingStatus trackingStatus;
    private Boolean hideFromLeaderboard;

    public Long getCorporationId() {
        return corporationId;
    }

    public void setCorporationId(final Long corporationId) {
        this.corporationId = corporationId;
    }

    public Long getAllianceId() {
        return allianceId;
    }

    public void setAllianceId(final Long allianceId) {
        this.allianceId = allianceId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Long getCharacterId() {
        return characterId;
    }

    public void setCharacterId(final Long characterId) {
        this.characterId = characterId;
    }

    public boolean getActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public Set<Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<Authority> authorities) {
        this.authorities = authorities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        User user = (User) o;
        return !(user.getId() == null || getId() == null) && Objects.equals(getId(), user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "User{" +
            "login='" + login + '\'' +
            ", activated='" + activated + '\'' +
            "}";
    }

    public void setCharacterOwnerHash(final String characterOwnerHash) {
        this.characterOwnerHash = characterOwnerHash;
    }

    public String getCharacterOwnerHash() {
        return characterOwnerHash;
    }

    public void setRefreshToken(final String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public Instant getAccessTokenExpiry() {
        return accessTokenExpiry;
    }

    public void setAccessTokenExpiry(final Instant accessTokenExpiry) {
        this.accessTokenExpiry = accessTokenExpiry;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(final String accessToken) {
        this.accessToken = accessToken;
    }

    public void setTrackingStatus(final TrackingStatus trackingStatus) {
        this.trackingStatus = trackingStatus;
    }

    public TrackingStatus getTrackingStatus() {
        return trackingStatus;
    }

    public Boolean getHideFromLeaderboard() {
        return hideFromLeaderboard;
    }

    public void setHideFromLeaderboard(final Boolean hideFromLeaderboard) {
        this.hideFromLeaderboard = hideFromLeaderboard;
    }
}
