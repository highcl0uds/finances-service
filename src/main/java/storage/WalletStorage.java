package storage;

import models.Wallet;

import java.io.*;

public class WalletStorage {

    private static final String DATA_DIR = "data";

    static {
        new File(DATA_DIR).mkdirs();
    }

    public static void save(Wallet wallet) {
        if (wallet == null) return;

        try (ObjectOutputStream oos = new ObjectOutputStream(
            new FileOutputStream(DATA_DIR + "/" + "wallet." + wallet.getOwner()))) {

            oos.writeObject(wallet);

        } catch (IOException e) {
            throw new RuntimeException("Ошибка сохранения кошелька", e);
        }
    }

    public static Wallet load(String username) {
        File file = new File(DATA_DIR + "/" + "wallet." + username);

        Wallet wallet;

        if (!file.exists()) {
            return null;
        } else {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {

                wallet = (Wallet) ois.readObject();

            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException("Ошибка загрузки кошелька", e);
            }
        }

        return wallet;
    }
}
