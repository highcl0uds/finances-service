package services;

import models.User;
import models.Wallet;
import security.PasswordHasher;
import storage.UsersStorage;
import storage.WalletStorage;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UsersService {
    private final Map<String, User> users = new ConcurrentHashMap<>();
    private User currentUser;

    public UsersService() { this.loadUsers(); }


    public User createUser(String username, String password) {
        if (this.users.containsKey(username)) return null;

        String hash = PasswordHasher.hash(password);

        Wallet wallet = new Wallet(username);

        WalletStorage.save(wallet);

        User user = new User(username, hash);

        this.users.put(username, user);
        this.saveUsers();

        this.currentUser = user;

        return user;
    }

    public Map<String, User> getUsers() {
        return users;
    }

    public User getCurrentUser() { return this.currentUser; }

    public User getUserByUsername(String username) { return users.get(username); }

    public User loginUser(String username, String password) {
        User user = this.users.get(username);
        if (user == null) return null;

        String hash = PasswordHasher.hash(password);
        if (!user.passwordHash().equals(hash)) return null;

        this.currentUser = user;

        return user;
    }

    public void logoutUser() { this.currentUser = null; }

    private void loadUsers() {
        Map<String, String> storedUsers = UsersStorage.loadUsers();
        storedUsers.forEach((username, hash) ->
            users.put(username, new User(username, hash))
        );
    }

    private void saveUsers() {
        Map<String, String> toSave = new ConcurrentHashMap<>();
        users.forEach((username, user) ->
            toSave.put(username, user.passwordHash())
        );
        UsersStorage.saveUsers(toSave);
    }
}
