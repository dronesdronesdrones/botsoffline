/*
 * UserDTO.java
 *
 * Created on 2017-12-10
 *
 */

package com.botsoffline.eve.service.dto;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

import com.botsoffline.eve.domain.Authority;
import com.botsoffline.eve.domain.User;
import com.botsoffline.eve.domain.enums.TrackingStatus;

import org.hibernate.validator.constraints.NotBlank;

/**
 * A DTO representing a user, with his authorities.
 */
public class UserDTO {

    private String id;

    @NotBlank
    private String login;

    private boolean activated;

    private String createdBy;

    private Instant createdDate;

    private String lastModifiedBy;

    private Instant lastModifiedDate;

    private Set<String> authorities;

    private TrackingStatus trackingStatus;

    private boolean hideFromLeaderboard;

    public UserDTO() {
        // Empty constructor needed for Jackson.
    }

    public UserDTO(User user) {
        this(user.getId(), user.getLogin(), user.getActivated(),
            user.getCreatedBy(), user.getCreatedDate(), user.getLastModifiedBy(), user.getLastModifiedDate(),
            user.getAuthorities().stream().map(Authority::getName)
                .collect(Collectors.toSet()), user.getTrackingStatus(), user.getHideFromLeaderboard());
    }

    public UserDTO(String id, String login, boolean activated,
        String createdBy, Instant createdDate, String lastModifiedBy, Instant lastModifiedDate,
        Set<String> authorities, TrackingStatus trackingStatus, boolean hideFromLeaderboard) {

        this.id = id;
        this.login = login;
        this.activated = activated;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
        this.lastModifiedBy = lastModifiedBy;
        this.lastModifiedDate = lastModifiedDate;
        this.authorities = authorities;
        this.trackingStatus = trackingStatus;
        this.hideFromLeaderboard = hideFromLeaderboard;
    }

    public boolean isHideFromLeaderboard() {
        return hideFromLeaderboard;
    }

    public void setHideFromLeaderboard(final boolean hideFromLeaderboard) {
        this.hideFromLeaderboard = hideFromLeaderboard;
    }

    public TrackingStatus getTrackingStatus() {
        return trackingStatus;
    }

    public void setTrackingStatus(final TrackingStatus trackingStatus) {
        this.trackingStatus = trackingStatus;
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

    public boolean isActivated() {
        return activated;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public Instant getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public Set<String> getAuthorities() {
        return authorities;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
            "login='" + login + '\'' +
            ", activated=" + activated +
            ", createdBy=" + createdBy +
            ", createdDate=" + createdDate +
            ", lastModifiedBy='" + lastModifiedBy + '\'' +
            ", lastModifiedDate=" + lastModifiedDate +
            ", authorities=" + authorities +
            "}";
    }
}
