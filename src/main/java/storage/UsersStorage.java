package storage;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class UsersStorage {

    private static final String FILE = "data/users";

    public static Map<String, String> loadUsers() {
        File file = new File(FILE);
        if (!file.exists()) {
            return new HashMap<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<String, String>) ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveUsers(Map<String, String> users) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE))) {
            oos.writeObject(users);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
