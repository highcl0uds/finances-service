package models;

import models.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testUserCreation() {
        User user = new User("testuser", "hash123");

        assertEquals("testuser", user.username());
        assertEquals("hash123", user.passwordHash());
    }

    @Test
    void testUsernameImmutable() {
        User user = new User("admin", "hashedpass");

        assertEquals("admin", user.username());
    }

    @Test
    void testPasswordHashRetrieval() {
        String expectedHash = "abcdef123456";
        User user = new User("user1", expectedHash);

        assertEquals(expectedHash, user.passwordHash());
    }
}
