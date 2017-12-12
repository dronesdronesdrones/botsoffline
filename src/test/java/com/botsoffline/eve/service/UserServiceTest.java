package com.botsoffline.eve.service;

import java.util.Optional;

import static java.util.Collections.emptyList;

import com.botsoffline.eve.domain.User;
import com.botsoffline.eve.repository.AuthorityRepository;
import com.botsoffline.eve.repository.CharacterLocationRepository;
import com.botsoffline.eve.repository.UserRepository;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    private AuthorityRepository authRepo = mock(AuthorityRepository.class);
    private UserRepository userRepo = mock(UserRepository.class);
    private CharacterLocationRepository locationRepository = mock(CharacterLocationRepository.class);
    private JsonRequestService requestService = new JsonRequestService();
    private UserService sut = new UserService(userRepo, locationRepository, authRepo, requestService);
    private String characterOwnerHash = "1";

    @Test
    public void createUser() throws Exception {
        when(userRepo.save(any(User.class))).thenReturn(null);

        User user = sut.createUser("login", 1L, characterOwnerHash, "1");

        assertEquals("login", user.getLogin());
        assertEquals(1L, user.getCharacterId().longValue());
        assertTrue(user.getActivated());
        assertFalse(user.getAuthorities().isEmpty());
    }

    @Test
    public void deleteUser() throws Exception {
        User user = new User();
        when(userRepo.findOneByLogin(anyString())).thenReturn(Optional.of(user));
        when(locationRepository.findAllByCharacterId(anyLong())).thenReturn(emptyList());

        sut.deleteUser("");

        verify(userRepo).delete(user);
    }

    @Test
    public void deleteUser_withoutResult() throws Exception {
        when(userRepo.findOneByLogin(anyString())).thenReturn(Optional.empty());
        when(locationRepository.findAllByCharacterId(anyLong())).thenReturn(emptyList());

        sut.deleteUser("");

        verify(userRepo, never()).delete(any(User.class));
    }
}
