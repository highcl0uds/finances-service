package models;

import models.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CategoryTest {

    private Category incomeCategory;
    private Category expenseCategory;
    private UUID uuid;

    @BeforeEach
    void setUp() {
        uuid = UUID.randomUUID();
        incomeCategory = new Category(uuid, "Salary", true);
        expenseCategory = new Category(UUID.randomUUID(), "Food", false);
    }

    @Test
    void testCategoryCreation() {
        assertEquals(uuid, incomeCategory.getUUID());
        assertEquals("Salary", incomeCategory.getName());
        assertTrue(incomeCategory.getIsIncome());
        assertEquals(0, incomeCategory.getBudget());
    }

    @Test
    void testSetBudget() {
        expenseCategory.setBudget(5000);
        assertEquals(5000, expenseCategory.getBudget());
    }

    @Test
    void testChangeName() {
        incomeCategory.setName("Bonus");
        assertEquals("Bonus", incomeCategory.getName());
    }

    @Test
    void testAddOperationIncome() {
        incomeCategory.addOperation(1000);
        assertEquals(1, incomeCategory.getOperations().size());
        assertEquals(1000, incomeCategory.getOperations().get(0));
    }

    @Test
    void testAddOperationExpense() {
        expenseCategory.addOperation(500);
        assertEquals(1, expenseCategory.getOperations().size());
        assertEquals(-500, expenseCategory.getOperations().get(0));
    }

    @Test
    void testMultipleOperations() {
        incomeCategory.addOperation(1000);
        incomeCategory.addOperation(2000);
        incomeCategory.addOperation(500);

        assertEquals(3, incomeCategory.getOperations().size());
        assertEquals(3500, incomeCategory.getOperations().stream()
            .mapToInt(Integer::intValue).sum());
    }

    @Test
    void testOperationsListIsUnmodifiable() {
        incomeCategory.addOperation(100);
        assertThrows(UnsupportedOperationException.class,
            () -> incomeCategory.getOperations().add(200));
    }
}
