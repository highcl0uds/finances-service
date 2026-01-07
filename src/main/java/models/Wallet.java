package models;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Wallet implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final String remittanceIncomeName = "Переводы от пользователей";
    private final String remittanceExpenseName = "Переводы пользователям";

    private final String owner;
    private final Map<UUID, Category> categories = new ConcurrentHashMap<>();

    private int balance;

    public Wallet(String owner) {
        this.owner = owner;
        this.balance = 0;
    }

    public String getOwner() {
        return this.owner;
    }

    public int getBalance() {
        return this.balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public Category addCategory(String name, Boolean isIncome) {
        Category cat = this.getExistingCategoryByName(name);
        if (cat != null) return null;

        UUID uuid;
        // Создаем с проверкой на коллизию
        do {
            uuid = UUID.randomUUID();
        } while (this.categories.containsKey(uuid));

        cat = new Category(uuid, name, isIncome);

        this.categories.put(uuid, cat);

        return cat;
    }

    public Boolean changeCategoryName(String name, String newName) {
        if (name.equals(this.remittanceIncomeName) || name.equals(this.remittanceExpenseName) || newName.equals(this.remittanceIncomeName) || newName.equals(this.remittanceExpenseName)) {
            System.out.println("Попытка изменения названия системной категории: '" + this.remittanceIncomeName + "' или '" + this.remittanceExpenseName + "'");

            return null;
        };

        Category cat = this.getExistingCategoryByName(name);
        if (cat == null) return false;

        cat.setName(newName);

        return true;
    }

    public Boolean changeCategoryBudget(String name, Integer budget) {
        Category cat = this.getExistingCategoryByName(name);
        if (cat == null) return false;

        cat.setBudget(budget);

        return true;
    }

    public Boolean makeTransactionByCategory(String name, int value) {
        Category cat = this.getExistingCategoryByName(name);
        if (cat == null) return null;

        boolean isIncome = cat.getIsIncome();

        if (isIncome) {
            balance += value;
            cat.addOperation(value);
        }
        else if (balance < value) return false;
        else {
            balance -= value;
            cat.addOperation(value);
        }

        return true;
    }

    public Integer getMoneyToSend(int value) {
        if (this.balance < value) return null;

        Category catRemittanceExpense = this.getExistingCategoryByName(this.remittanceExpenseName);
        if (catRemittanceExpense == null) {
            catRemittanceExpense = this.addCategory(this.remittanceExpenseName, false);
        }

        balance -= value;
        catRemittanceExpense.addOperation(value);

        return value;
    }

    public Category receiveMoney(int value) {
        Category catRemittanceIncome = this.getExistingCategoryByName(this.remittanceIncomeName);

        if (catRemittanceIncome == null) {
            catRemittanceIncome = this.addCategory(this.remittanceIncomeName, true);
        }

        balance += value;
        catRemittanceIncome.addOperation(value);

        return catRemittanceIncome;
    }

    public WalletSummary getSummary() {
        int summaryIncome = 0;
        int summaryExpense = 0;

        Map<String, Integer> incomeCategories = new ConcurrentHashMap<>();
        Map<String, ExpenseCategory> expenseCategories = new ConcurrentHashMap<>();

        for (Category category : categories.values()) {
            int summaryByCategory = 0;

            for (int operation : category.getOperations()) {
                summaryByCategory += operation;
            }

            if (category.getIsIncome()) {
                summaryIncome += summaryByCategory;
                incomeCategories.put(category.getName(), summaryByCategory);
            } else {
                summaryExpense += summaryByCategory;
                expenseCategories.put(category.getName(), new ExpenseCategory(summaryByCategory, category.getBudget() + summaryByCategory));
            }
        }

        return new WalletSummary(
            summaryIncome,
            summaryExpense,
            incomeCategories,
            expenseCategories
        );
    }

    public Category getExistingCategoryByName(String name) {
        for (Category category : categories.values()) {
            if (category.getName().equals(name)) return category;
        }

        return null;
    }

    public record ExpenseCategory(int summaryByCategory, int remainingBudget) {
    }

    public record WalletSummary(
        int summaryIncome,
        int summaryExpense,
        Map<String, Integer> incomeCategories,
        Map<String, ExpenseCategory> expenseCategories
    ) {}

    public record CategorySummary(
        int summaryByCategory,
        Category category
    ) {}
}
