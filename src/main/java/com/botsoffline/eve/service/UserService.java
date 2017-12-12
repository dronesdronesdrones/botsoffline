/*
 * UserService.java
 *
 * Created on 2017-12-10
 *
 */

package com.botsoffline.eve.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.botsoffline.eve.config.Constants;
import com.botsoffline.eve.domain.Authority;
import com.botsoffline.eve.domain.CharacterLocation;
import com.botsoffline.eve.domain.User;
import com.botsoffline.eve.domain.enums.TrackingStatus;
import com.botsoffline.eve.repository.AuthorityRepository;
import com.botsoffline.eve.repository.CharacterLocationRepository;
import com.botsoffline.eve.repository.UserRepository;
import com.botsoffline.eve.security.AuthoritiesConstants;
import com.botsoffline.eve.security.SecurityUtils;
import com.botsoffline.eve.service.dto.UserDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service class for managing users.
 */
@Service
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final CharacterLocationRepository locationRepository;
    private final AuthorityRepository authorityRepository;
    private final JsonRequestService requestService;

    public UserService(final UserRepository userRepository,
            final CharacterLocationRepository locationRepository,
            final AuthorityRepository authorityRepository,
            final JsonRequestService requestService) {
        this.userRepository = userRepository;
        this.locationRepository = locationRepository;
        this.authorityRepository = authorityRepository;
        this.requestService = requestService;
    }

    public User createUser(final String login, final Long characterId, final String characterOwnerHash,
            final String refreshToken) {
        User newUser = new User();
        Authority authority = authorityRepository.findOne(AuthoritiesConstants.USER);
        Set<Authority> authorities = new HashSet<>();
        newUser.setLogin(login);
        newUser.setCharacterId(characterId);
        newUser.setCharacterOwnerHash(characterOwnerHash);
        newUser.setRefreshToken(refreshToken);
        newUser.setTrackingStatus(TrackingStatus.ENABLED);
        // new user gets registration key
        authorities.add(authority);
        newUser.setAuthorities(authorities);
        userRepository.save(newUser);
        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }


    /**
     * Update all information for a specific user, and return the modified user.
     *
     * @param userDTO user to update
     * @return updated user
     */
    public Optional<UserDTO> persistUser(UserDTO userDTO) {
        return Optional.of(userRepository
            .findOne(userDTO.getId()))
            .map(user -> {
                user.setLogin(userDTO.getLogin());
                user.setActivated(userDTO.isActivated());
                Set<Authority> managedAuthorities = user.getAuthorities();
                managedAuthorities.clear();
                userDTO.getAuthorities().stream()
                    .map(authorityRepository::findOne)
                    .forEach(managedAuthorities::add);
                userRepository.save(user);
                log.debug("Changed Information for User: {}", user);
                return user;
            })
            .map(UserDTO::new);
    }

    public void deleteUser(final String login) {
        userRepository.findOneByLogin(login).ifPresent(user -> {
            final List<CharacterLocation> locations = locationRepository.findAllByCharacterId(user.getCharacterId())
                    .stream().peek(location -> location.setCharacterId(0))
                    .collect(Collectors.toList());
            locationRepository.save(locations);
            userRepository.delete(user);
            log.debug("Deleted User: {}", user);
        });
    }

    public Page<UserDTO> getAllManagedUsers(Pageable pageable) {
        return userRepository.findAllByLoginNot(pageable, Constants.ANONYMOUS_USER).map(UserDTO::new);
    }

    public Optional<User> getUserWithAuthoritiesByLogin(String login) {
        return userRepository.findOneByLogin(login);
    }

    public User getUserWithAuthorities(String id) {
        return userRepository.findOne(id);
    }

    public User getUserWithAuthorities() {
        return userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).orElse(null);
    }

    public List<String> getAuthorities() {
        return authorityRepository.findAll().stream().map(Authority::getName).collect(Collectors.toList());
    }

    public void persistUser(final User user) {
        userRepository.save(user);
    }

    public void initAffiliations() {
        final List<User> collect = userRepository.findAllByCorporationIdIsNull().stream()
                .peek(this::setCorporationAndAlliance)
                .filter(user -> user.getCorporationId() != null)
                .collect(Collectors.toList());
        userRepository.save(collect);
        log.info("Initiated affiliations for {} characters.", collect.size());
    }

    public void updateAffiliations() {
        final List<User> collect = userRepository.findAll().stream()
                .peek(this::setCorporationAndAlliance)
                .filter(user -> user.getCorporationId() != null)
                .collect(Collectors.toList());
        userRepository.save(collect);
        log.info("Updated affiliations for {} characters.", collect.size());
    }

    private void setCorporationAndAlliance(final User user) {
        final Long corporationId = requestService.getCorporationId(user.getCharacterId());
        if (null != corporationId) {
            user.setCorporationId(corporationId);
            final Long allianceId = requestService.getAllianceId(corporationId);
            if (null != allianceId) {
                user.setAllianceId(allianceId);
            }
        } else {
            log.warn("Could not load the corporation of user {} {}.", user.getLogin(), user.getCharacterId());
        }
    }
}
