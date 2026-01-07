package models;

import models.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testUserCreation() {
        User user = new User("UserTest1", "hash123");

        assertEquals("UserTest1", user.username());
        assertEquals("hash123", user.passwordHash());
    }

    @Test
    void testUsernameImmutable() {
        User user = new User("UserTest2", "hashedpass");

        assertEquals("UserTest2", user.username());
    }

    @Test
    void testPasswordHashRetrieval() {
        String expectedHash = "abcdef123456";
        User user = new User("UserTest3", expectedHash);

        assertEquals(expectedHash, user.passwordHash());
    }
}
