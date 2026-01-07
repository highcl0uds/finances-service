package services;

import models.User;
import models.Wallet;
import storage.WalletStorage;

public class WalletService {
    private Wallet currentWallet;
    private final UsersService usersService;

    public WalletService(UsersService usersService) { this.usersService = usersService; }

    public Wallet getCurrentWallet() { return this.currentWallet; }

    public void setCurrentWallet() {
        User currentUser = this.usersService.getCurrentUser();

        if (currentUser == null) this.currentWallet = null;
        else this.currentWallet = loadWallet(currentUser);
    }

    public Wallet loadWallet(User user) { return WalletStorage.load(user.username()); }

    public void saveWallet(Wallet wallet) { WalletStorage.save(wallet); }

    public Boolean makeTransactionToUser(String username, int value) {
        if (value > this.currentWallet.getBalance()) return false;

        User userTo = this.usersService.getUserByUsername(username);
        if (userTo == null) return null;

        Wallet walletTo = loadWallet(userTo);

        walletTo.receiveMoney(this.currentWallet.getMoneyToSend(value));

        saveWallet(this.currentWallet);
        saveWallet(walletTo);

        return true;
    }
}
