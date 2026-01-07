package models;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Category implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final UUID uuid;
    private final boolean isIncome;
    private final ArrayList<Integer> transactions;
    private String name;
    private Integer budget;

    public Category(UUID uuid, String name, boolean isIncome) {
        this.uuid = uuid;
        this.name = name;
        this.isIncome = isIncome;
        this.budget = 0;
        this.transactions = new ArrayList<>();
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public String getName() {
        return this.name;
    }

    public boolean getIsIncome() {
        return this.isIncome;
    }

    public Integer getBudget() {
        return this.budget;
    }

    public List<Integer> getOperations() {
        return Collections.unmodifiableList(transactions);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBudget(Integer budget) {
        this.budget = budget;
    }

    public void addOperation(Integer value) {
        if (!this.isIncome) value = value * -1;

        this.transactions.add(value);
    }
}
