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
        User user = usersService.createUser("UsersServiceTest1", "password123");

        assertNotNull(user);
        assertEquals("UsersServiceTest1", user.username());
    }

    @Test
    void testCreateDuplicateUser() {
        usersService.createUser("UsersServiceTest2", "pass1");
        User duplicate = usersService.createUser("UsersServiceTest2", "pass2");

        assertNull(duplicate);
    }

    @Test
    void testLoginUser() {
        usersService.createUser("UsersServiceTest3", "mypassword");
        usersService.logoutUser();

        User loggedIn = usersService.loginUser("UsersServiceTest3", "mypassword");

        assertNotNull(loggedIn);
        assertEquals("UsersServiceTest3", loggedIn.username());
        assertEquals(loggedIn, usersService.getCurrentUser());
    }

    @Test
    void testLoginUserWrongPassword() {
        usersService.createUser("UsersServiceTest4", "correctpass");
        usersService.logoutUser();

        User failed = usersService.loginUser("UsersServiceTest4", "wrongpass");

        assertNull(failed);
    }

    @Test
    void testLoginNonExistentUser() {
        User failed = usersService.loginUser("UsersServiceTest5", "anypass");

        assertNull(failed);
    }

    @Test
    void testLogoutUser() {
        usersService.createUser("UsersServiceTest6", "pass");
        usersService.loginUser("UsersServiceTest6", "pass");

        assertNotNull(usersService.getCurrentUser());

        usersService.logoutUser();

        assertNull(usersService.getCurrentUser());
    }

    @Test
    void testGetUserByUsername() {
        usersService.createUser("UsersServiceTest7", "password");

        User found = usersService.getUserByUsername("UsersServiceTest7");

        assertNotNull(found);
        assertEquals("UsersServiceTest7", found.username());
    }
}
