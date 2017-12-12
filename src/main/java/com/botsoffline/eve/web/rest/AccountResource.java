/*
 * AccountResource.java
 *
 * Created on 2017-12-10
 *
 */

package com.botsoffline.eve.web.rest;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.botsoffline.eve.domain.User;
import com.botsoffline.eve.domain.enums.TrackingStatus;
import com.botsoffline.eve.security.SecurityUtils;
import com.botsoffline.eve.service.UserService;
import com.botsoffline.eve.service.dto.UserDTO;
import com.codahale.metrics.annotation.Timed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
public class AccountResource {

    private final Logger log = LoggerFactory.getLogger(AccountResource.class);

    private final UserService userService;

    public AccountResource(final UserService userService) {
        this.userService = userService;
    }

    /**
     * GET  /authenticate : check if the user is authenticated, and return its login.
     *
     * @param request the HTTP request
     * @return the login if the user is authenticated
     */
    @GetMapping("/authenticate")
    @Timed
    public String isAuthenticated(final HttpServletRequest request) {
        log.debug("REST request to check if the current user is authenticated");
        return request.getRemoteUser();
    }

    /**
     * GET  /account : get the current user.
     *
     * @return the ResponseEntity with status 200 (OK) and the current user in body, or status 500 (Internal Server Error) if the user couldn't be returned
     */
    @GetMapping("/account")
    @Timed
    public ResponseEntity<UserDTO> getAccount() {
        return Optional.ofNullable(userService.getUserWithAuthorities())
            .map(user -> new ResponseEntity<>(new UserDTO(user), HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @PutMapping("/account/tracking/{status}")
    @Timed
    public void updateTracking(@PathVariable final TrackingStatus status) {
        final User user = userService.getUserWithAuthorities();
        user.setTrackingStatus(status);
        userService.persistUser(user);
    }

    @PutMapping("/account/leaderboard/{hide}")
    @Timed
    public void updateLeaderboard(@PathVariable final Boolean hide) {
        final User user = userService.getUserWithAuthorities();
        user.setHideFromLeaderboard(hide);
        userService.persistUser(user);
    }

    @DeleteMapping("/account")
    @Timed
    public void deleteAccount() {
        userService.deleteUser(SecurityUtils.getCurrentUserLogin());
    }
}
