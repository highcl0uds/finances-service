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
        usersService.createUser("WalletServiceTest1", "pass");
        usersService.loginUser("WalletServiceTest1", "pass");
        walletService.setCurrentWallet();

        Wallet wallet = walletService.getCurrentWallet();
        assertNotNull(wallet);
        assertEquals("WalletServiceTest1", wallet.getOwner());
    }

    @Test
    void testSetCurrentWalletWithoutUser() {
        usersService.logoutUser();
        walletService.setCurrentWallet();

        assertNull(walletService.getCurrentWallet());
    }

    @Test
    void testMakeTransactionToUserSuccess() {
        User sender = usersService.createUser("WalletServiceTest2", "pass1");
        User receiver = usersService.createUser("WalletServiceTest3", "pass2");

        usersService.loginUser("WalletServiceTest2", "pass1");
        walletService.setCurrentWallet();

        Wallet senderWallet = walletService.getCurrentWallet();
        senderWallet.setBalance(5000);
        walletService.saveWallet(senderWallet);

        Boolean result = walletService.makeTransactionToUser("WalletServiceTest3", 1000);

        assertTrue(result);
        assertEquals(4000, senderWallet.getBalance());
    }

    @Test
    void testMakeTransactionToUserInsufficientBalance() {
        usersService.createUser("WalletServiceTest4", "pass1");
        usersService.createUser("WalletServiceTest5", "pass2");

        usersService.loginUser("WalletServiceTest4", "pass1");
        walletService.setCurrentWallet();

        Wallet senderWallet = walletService.getCurrentWallet();
        senderWallet.setBalance(100);

        Boolean result = walletService.makeTransactionToUser("WalletServiceTest5", 500);

        assertFalse(result);
        assertEquals(100, senderWallet.getBalance());
    }

    @Test
    void testMakeTransactionToNonExistentUser() {
        usersService.createUser("WalletServiceTest6", "pass");
        usersService.loginUser("WalletServiceTest6", "pass");
        walletService.setCurrentWallet();

        Wallet senderWallet = walletService.getCurrentWallet();
        senderWallet.setBalance(1000);

        Boolean result = walletService.makeTransactionToUser("WalletServiceTest7", 500);

        assertNull(result);
    }
}
