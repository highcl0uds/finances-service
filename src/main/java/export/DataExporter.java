package export;

import models.Category;
import models.Wallet;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class DataExporter {

    public static void exportToCSV(Wallet wallet, String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("Owner," + wallet.getOwner());
            writer.println("Balance," + wallet.getBalance());
            writer.println("Export Date," + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            writer.println();

            writer.println("Category Type,Category Name,Budget,Operations");
            Wallet.WalletSummary summary = wallet.getSummary();

            for (Map.Entry<String, Integer> entry : summary.incomeCategories().entrySet()) {
                String categoryName = entry.getKey();
                Category cat = wallet.getExistingCategoryByName(categoryName);
                if (cat != null) {
                    writer.print("Income," + categoryName + ",");
                    writer.print(cat.getBudget() + ",\"");
                    writer.print(String.join(";", cat.getOperations().stream()
                        .map(String::valueOf).toArray(String[]::new)));
                    writer.println("\"");
                }
            }

            for (Map.Entry<String, Wallet.ExpenseCategory> entry : summary.expenseCategories().entrySet()) {
                String categoryName = entry.getKey();
                Category cat = wallet.getExistingCategoryByName(categoryName);
                if (cat != null) {
                    writer.print("Expense," + categoryName + ",");
                    writer.print(cat.getBudget() + ",\"");
                    writer.print(String.join(";", cat.getOperations().stream()
                        .map(String::valueOf).toArray(String[]::new)));
                    writer.println("\"");
                }
            }
        }
    }

    public static void exportToJSON(Wallet wallet, String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("{");
            writer.println("  \"owner\": \"" + wallet.getOwner() + "\",");
            writer.println("  \"balance\": " + wallet.getBalance() + ",");
            writer.println("  \"exportDate\": \"" + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "\",");

            Wallet.WalletSummary summary = wallet.getSummary();
            writer.println("  \"incomeCategories\": [");

            boolean firstIncome = true;
            for (Map.Entry<String, Integer> entry : summary.incomeCategories().entrySet()) {
                String categoryName = entry.getKey();
                Category cat = wallet.getExistingCategoryByName(categoryName);
                if (cat != null) {
                    if (!firstIncome) writer.println(",");
                    writer.println("    {");
                    writer.println("      \"name\": \"" + categoryName + "\",");
                    writer.println("      \"budget\": " + cat.getBudget() + ",");
                    writer.print("      \"operations\": [");
                    writer.print(String.join(", ", cat.getOperations().stream()
                        .map(String::valueOf).toArray(String[]::new)));
                    writer.println("]");
                    writer.print("    }");
                    firstIncome = false;
                }
            }
            writer.println();
            writer.println("  ],");

            writer.println("  \"expenseCategories\": [");
            boolean firstExpense = true;
            for (Map.Entry<String, Wallet.ExpenseCategory> entry : summary.expenseCategories().entrySet()) {
                String categoryName = entry.getKey();
                Category cat = wallet.getExistingCategoryByName(categoryName);
                if (cat != null) {
                    if (!firstExpense) writer.println(",");
                    writer.println("    {");
                    writer.println("      \"name\": \"" + categoryName + "\",");
                    writer.println("      \"budget\": " + cat.getBudget() + ",");
                    writer.println("      \"remainingBudget\": " + entry.getValue().remainingBudget() + ",");
                    writer.print("      \"operations\": [");
                    writer.print(String.join(", ", cat.getOperations().stream()
                        .map(String::valueOf).toArray(String[]::new)));
                    writer.println("]");
                    writer.print("    }");
                    firstExpense = false;
                }
            }
            writer.println();
            writer.println("  ]");
            writer.println("}");
        }
    }

    public static void exportSummaryToFile(Wallet wallet, String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("=".repeat(150));
            writer.println("Отчет");
            writer.println("Пользователь: " + wallet.getOwner());
            writer.println("Дата: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
            writer.println("=".repeat(150));
            writer.println();

            Wallet.WalletSummary summary = wallet.getSummary();

            writer.println("Доходы:");
            writer.println("-".repeat(150));
            for (Map.Entry<String, Integer> entry : summary.incomeCategories().entrySet()) {
                writer.printf("  %-40s %10d%n", entry.getKey(), entry.getValue());
            }
            writer.println("-".repeat(150));
            writer.printf("  %-40s %10d%n", "Итого доходов:", summary.summaryIncome());
            writer.println();

            writer.println("Расходы:");
            writer.println("-".repeat(150));
            for (Map.Entry<String, Wallet.ExpenseCategory> entry : summary.expenseCategories().entrySet()) {
                writer.printf("  %-30s %10d (Остаток: %10d)%n",
                    entry.getKey(),
                    entry.getValue().summaryByCategory(),
                    entry.getValue().remainingBudget());
            }
            writer.println("-".repeat(150));
            writer.printf("  %-40s %10d%n", "Иитого расходов:", summary.summaryExpense());
            writer.println();

            writer.println("=".repeat(150));
            writer.printf("  %-40s %10d%n", "Текущий баланс:", wallet.getBalance());
            writer.println("=".repeat(150));
        }
    }
}
