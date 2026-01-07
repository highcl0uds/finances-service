package services;

import models.User;
import models.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.UsersService;
import services.WalletService;

import static org.junit.jupiter.api.Assertions.*;

class WalletServiceTest {

    private UsersService usersService;
    private WalletService walletService;

    @BeforeEach
    void setUp() {
        usersService = new UsersService();
        walletService = new WalletService(usersService);
    }

    @Test
    void testSetCurrentWalletWithUser() {
        usersService.createUser("walletuser", "pass");
        usersService.loginUser("walletuser", "pass");
        walletService.setCurrentWallet();

        Wallet wallet = walletService.getCurrentWallet();
        assertNotNull(wallet);
        assertEquals("walletuser", wallet.getOwner());
    }

    @Test
    void testSetCurrentWalletWithoutUser() {
        usersService.logoutUser();
        walletService.setCurrentWallet();

        assertNull(walletService.getCurrentWallet());
    }

    @Test
    void testMakeTransactionToUserSuccess() {
        User sender = usersService.createUser("sender1", "pass1");
        User receiver = usersService.createUser("receiver1", "pass2");

        usersService.loginUser("sender1", "pass1");
        walletService.setCurrentWallet();

        Wallet senderWallet = walletService.getCurrentWallet();
        senderWallet.setBalance(5000);
        walletService.saveWallet(senderWallet);

        Boolean result = walletService.makeTransactionToUser("receiver1", 1000);

        assertTrue(result);
        assertEquals(4000, senderWallet.getBalance());
    }

    @Test
    void testMakeTransactionToUserInsufficientBalance() {
        usersService.createUser("sender2", "pass1");
        usersService.createUser("receiver2", "pass2");

        usersService.loginUser("sender2", "pass1");
        walletService.setCurrentWallet();

        Wallet senderWallet = walletService.getCurrentWallet();
        senderWallet.setBalance(100);

        Boolean result = walletService.makeTransactionToUser("receiver2", 500);

        assertFalse(result);
        assertEquals(100, senderWallet.getBalance());
    }

    @Test
    void testMakeTransactionToNonExistentUser() {
        usersService.createUser("sender3", "pass");
        usersService.loginUser("sender3", "pass");
        walletService.setCurrentWallet();

        Wallet senderWallet = walletService.getCurrentWallet();
        senderWallet.setBalance(1000);

        Boolean result = walletService.makeTransactionToUser("nonexistent", 500);

        assertNull(result);
    }
}
