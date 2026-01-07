package services;

import models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.UsersService;

import static org.junit.jupiter.api.Assertions.*;

class UsersServiceTest {

    private UsersService usersService;

    @BeforeEach
    void setUp() {
        usersService = new UsersService();
    }

    @Test
    void testCreateUser() {
        User user = usersService.createUser("newuser", "password123");

        assertNotNull(user);
        assertEquals("newuser", user.username());
        assertEquals(user, usersService.getCurrentUser());
    }

    @Test
    void testCreateDuplicateUser() {
        usersService.createUser("testuser", "pass1");
        User duplicate = usersService.createUser("testuser", "pass2");

        assertNull(duplicate);
    }

    @Test
    void testLoginUser() {
        usersService.createUser("logintest", "mypassword");
        usersService.logoutUser();

        User loggedIn = usersService.loginUser("logintest", "mypassword");

        assertNotNull(loggedIn);
        assertEquals("logintest", loggedIn.username());
        assertEquals(loggedIn, usersService.getCurrentUser());
    }

    @Test
    void testLoginUserWrongPassword() {
        usersService.createUser("user1", "correctpass");
        usersService.logoutUser();

        User failed = usersService.loginUser("user1", "wrongpass");

        assertNull(failed);
    }

    @Test
    void testLoginNonExistentUser() {
        User failed = usersService.loginUser("nonexistent", "anypass");

        assertNull(failed);
    }

    @Test
    void testLogoutUser() {
        usersService.createUser("user2", "pass");
        assertNotNull(usersService.getCurrentUser());

        usersService.logoutUser();

        assertNull(usersService.getCurrentUser());
    }

    @Test
    void testGetUserByUsername() {
        usersService.createUser("findme", "password");

        User found = usersService.getUserByUsername("findme");

        assertNotNull(found);
        assertEquals("findme", found.username());
    }
}
