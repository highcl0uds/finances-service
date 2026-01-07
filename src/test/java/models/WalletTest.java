package models;

import models.Category;
import models.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WalletTest {

    private Wallet wallet;

    @BeforeEach
    void setUp() {
        wallet = new Wallet("WalletTest1");
    }

    @Test
    void testWalletCreation() {
        assertEquals("WalletTest1", wallet.getOwner());
        assertEquals(0, wallet.getBalance());
    }

    @Test
    void testAddIncomeCategory() {
        Category category = wallet.addCategory("Salary", true);

        assertNotNull(category);
        assertEquals("Salary", category.getName());
        assertTrue(category.getIsIncome());
    }

    @Test
    void testAddExpenseCategory() {
        Category category = wallet.addCategory("Food", false);

        assertNotNull(category);
        assertEquals("Food", category.getName());
        assertFalse(category.getIsIncome());
    }

    @Test
    void testAddDuplicateCategory() {
        wallet.addCategory("Food", false);
        Category duplicate = wallet.addCategory("Food", false);

        assertNull(duplicate);
    }

    @Test
    void testChangeCategoryBudget() {
        wallet.addCategory("Food", false);
        Boolean result = wallet.changeCategoryBudget("Food", 5000);

        assertTrue(result);
        assertEquals(5000, wallet.getExistingCategoryByName("Food").getBudget());
    }

    @Test
    void testChangeCategoryBudgetNonExistent() {
        Boolean result = wallet.changeCategoryBudget("NonExistent", 1000);

        assertFalse(result);
    }

    @Test
    void testChangeCategoryName() {
        wallet.addCategory("OldName", true);
        Boolean result = wallet.changeCategoryName("OldName", "NewName");

        assertTrue(result);
        assertNotNull(wallet.getExistingCategoryByName("NewName"));
        assertNull(wallet.getExistingCategoryByName("OldName"));
    }

    @Test
    void testMakeIncomeTransaction() {
        wallet.addCategory("Salary", true);
        Boolean result = wallet.makeTransactionByCategory("Salary", 5000);

        assertTrue(result);
        assertEquals(5000, wallet.getBalance());
    }

    @Test
    void testMakeExpenseTransaction() {
        wallet.setBalance(10000);
        wallet.addCategory("Food", false);
        Boolean result = wallet.makeTransactionByCategory("Food", 2000);

        assertTrue(result);
        assertEquals(8000, wallet.getBalance());
    }

    @Test
    void testMakeExpenseTransactionInsufficientBalance() {
        wallet.setBalance(100);
        wallet.addCategory("Food", false);
        Boolean result = wallet.makeTransactionByCategory("Food", 500);

        assertFalse(result);
        assertEquals(100, wallet.getBalance());
    }

    @Test
    void testReceiveMoney() {
        Category category = wallet.receiveMoney(1000);

        assertNotNull(category);
        assertEquals(1000, wallet.getBalance());
    }

    @Test
    void testGetMoneyToSend() {
        wallet.setBalance(5000);
        Integer sent = wallet.getMoneyToSend(2000);

        assertNotNull(sent);
        assertEquals(2000, sent);
        assertEquals(3000, wallet.getBalance());
    }

    @Test
    void testGetMoneyToSendInsufficientBalance() {
        wallet.setBalance(100);
        Integer sent = wallet.getMoneyToSend(500);

        assertNull(sent);
        assertEquals(100, wallet.getBalance());
    }

    @Test
    void testGetSummary() {
        wallet.addCategory("Salary", true);
        wallet.addCategory("Food", false);
        wallet.makeTransactionByCategory("Salary", 10000);
        wallet.makeTransactionByCategory("Food", 2000);

        Wallet.WalletSummary summary = wallet.getSummary();

        assertEquals(10000, summary.summaryIncome());
        assertEquals(-2000, summary.summaryExpense());
        assertEquals(1, summary.incomeCategories().size());
        assertEquals(1, summary.expenseCategories().size());
    }
}
