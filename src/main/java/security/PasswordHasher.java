package security;

public final class PasswordHasher {

    private PasswordHasher() {}

    public static String hash(String password) {
        return Integer.toHexString(password.hashCode());
    }
}
