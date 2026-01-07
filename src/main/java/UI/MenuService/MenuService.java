package UI.MenuService;

import export.DataExporter;
import models.Category;
import models.Wallet;
import models.User;
import services.WalletService;
import services.UsersService;
import storage.WalletStorage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MenuService {
    private final Scanner scanner;
    private final UsersService usersService;
    private final WalletService walletService;

    public MenuService(UsersService usersService, WalletService walletService) {
        this.scanner = new Scanner(System.in);
        this.usersService = usersService;
        this.walletService = walletService;
    }

    public void startMenu() {
        System.out.println("\nВы используете сервис для контроля личных финансов\n");
        chooseUser();

        boolean isRunning = true;
        while (isRunning) {
            menu();
            String option = scanNextLine();

            switch (option) {
                case "1":
                    createCategory();
                    break;
                case "2":
                    changeBudgetForCategory();
                    break;
                case "3":
                    changeNameForCategory();
                    break;
                case "4":
                    printWalletSummary();
                    break;
                case "5":
                    printCategoriesSummary();
                    break;
                case "6":
                    addCategoryOperation();
                    break;
                case "7":
                    makeTransactionToUser();
                    break;
                case "8":
                    exportData();
                    break;
                case "9":
                    chooseUser();
                    break;
                case "0":
                    isRunning = false;
                    WalletStorage.save(walletService.getCurrentWallet());
                    System.out.println("До свидания!");
                    break;
                case "help":
                    showHelp();
                    break;
                default:
                    System.out.println("Неизвестная команда. Введите 'help' для справки.");
            }
        }
    }

    private String scanNextLine() {
        if (usersService.getCurrentUser() == null) {
            return scanner.nextLine().trim();
        }

        System.out.print(usersService.getCurrentUser().username() + " > ");
        return scanner.nextLine().trim();
    }

    private void menu() {
        System.out.println("\n");
        System.out.println("Выберите действие:");
        System.out.println("1. Создать категорию планирования бюджета");
        System.out.println("2. Установить бюджет для категории");
        System.out.println("3. Поменять название категории");
        System.out.println("4. Получить всю информацию по кошельку");
        System.out.println("5. Получить информацию по категории/категориям");
        System.out.println("6. Добавить операцию по категории для учета");
        System.out.println("7. Совершить перевод другому пользователю");
        System.out.println("8. Экспорт данных (CSV/JSON/TXT)");
        System.out.println("9. Сменить пользователя");
        System.out.println("0. Завершить программу");
        System.out.println("help - Показать справку");
        System.out.println("\n");
    }

    private void createCategory() {
        Wallet wallet = this.walletService.getCurrentWallet();

        if (wallet == null) {
            System.out.println("Нет текущего кошелька, попробуйте позже");

            return;
        }

        System.out.println("Введите название категории: ");
        String categoryName = scanNextLine();

        if (categoryName.isEmpty()) {
            System.out.println("Название категории не может быть пустым");

            return;
        }

        String categoryType;

        while (true) {
            System.out.println("Введите '+', если это категория дохода, либо '-', если это категория расхода: ");
            categoryType = scanNextLine();

            if (categoryType.equals("+") || categoryType.equals("-")) {
                break;
            }

            System.out.println("Ошибка ввода. Пожалуйста, введите только '+' или '-'");
        }

        boolean isIncome = categoryType.equals("+");

        boolean isCategoryCreated = wallet.addCategory(categoryName, isIncome) != null;
        WalletStorage.save(wallet);

        if (!isCategoryCreated) {
            System.out.println("Категория '" + categoryName + "' уже существует. Попробуйте еще раз позже");

            return;
        }

        System.out.println("Добавлена категория " + (isIncome ? "дохода" : "расхода") + ": " + categoryName);
    }

    private void changeBudgetForCategory() {
        Wallet wallet = this.walletService.getCurrentWallet();

        if (wallet == null) {
            System.out.println("Нет текущего кошелька, попробуйте позже");

            return;
        }

        System.out.println("Введите название категории: ");
        String categoryName = scanNextLine();

        if (categoryName.isEmpty()) {
            System.out.println("Название категории не может быть пустым");

            return;
        }

        String budgetStr;
        int budget;

        while (true) {
            System.out.println("Введите бюджет для категории (целочисленное значение): ");
            budgetStr = scanNextLine();
            try {
                budget = Integer.parseInt(budgetStr);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Ошибка ввода. Введите целое число");
            }
        }

        Boolean isCategoryChanged = wallet.changeCategoryBudget(categoryName, budget);
        WalletStorage.save(wallet);

        if (isCategoryChanged == null || !isCategoryChanged) {
            System.out.println("Категория '" + categoryName + "' не найдена. Попробуйте еще раз позже");

            return;
        };

        System.out.println("Изменен бюджет для категории '" + categoryName + "': " + budgetStr);
    }

    private void changeNameForCategory() {
        Wallet wallet = this.walletService.getCurrentWallet();

        if (wallet == null) {
            System.out.println("Нет текущего кошелька, попробуйте позже");

            return;
        }

        System.out.println("Введите название категории, которое хотите изменить: ");
        String categoryName = scanNextLine();

        if (categoryName.isEmpty()) {
            System.out.println("Название категории не может быть пустым");

            return;
        }

        String newName;

        while (true) {
            System.out.println("Введите новое название для категории: ");
            newName = scanNextLine();

            if (newName.isEmpty()) System.out.println("Название категории не может быть пустым");
            else break;
        }

        Boolean isCategoryChanged = wallet.changeCategoryName(categoryName, newName);
        WalletStorage.save(wallet);

        if (isCategoryChanged == null) {
            System.out.println("Вы не можете изменять названия системных категорий");

            return;
        }

        if (!isCategoryChanged) {
            System.out.println("Категория '" + categoryName + "' не найдена. Попробуйте еще раз позже");

            return;
        }

        System.out.println("Изменено название для категории: '" + categoryName + "' -> '" + newName + "'");
    }

    private void printWalletSummary() {
        Wallet wallet = this.walletService.getCurrentWallet();

        if (wallet == null) {
            System.out.println("Нет текущего кошелька, попробуйте позже");

            return;
        }

        Wallet.WalletSummary walletSummary = wallet.getSummary();

        System.out.println("\n" + "=".repeat(150));
        System.out.println("Отчет по кошельку");
        System.out.println("=".repeat(150));

        System.out.println("\nДоходы:");
        System.out.println("-".repeat(150));
        walletSummary.incomeCategories().forEach((key, value) ->
            System.out.printf("  %-40s %10d%n", key, value)
        );
        System.out.println("-".repeat(150));
        System.out.printf("  %-40s %10d%n", "Итого доходов:", walletSummary.summaryIncome());

        System.out.println("\nРасходы:");
        System.out.println("-".repeat(150));
        walletSummary.expenseCategories().forEach((key, value) -> {
            System.out.printf("  %-30s %10d (Остаток: %10d)",
                key, value.summaryByCategory(), value.remainingBudget());
            if (value.remainingBudget() < 0) {
                System.out.print(" Бюджет превышен");
            }
            System.out.println();
        });
        System.out.println("-".repeat(150));
        System.out.printf("  %-40s %10d%n", "Итого расходов:", walletSummary.summaryExpense());

        System.out.println("\n" + "=".repeat(150));
        System.out.printf("  %-40s %10d%n", "Текущий баланс:", wallet.getBalance());
        System.out.println("=".repeat(150));

        if (wallet.getBalance() == 0) System.out.println("Обращаем внимание, что ваш баланс равен нулю");
        if (walletSummary.summaryIncome() < walletSummary.summaryExpense()) System.out.println("Обращаем внимание, что расходы превышают доходы на " + (walletSummary.summaryExpense() - walletSummary.summaryIncome()));
        else System.out.println("Баланс: " + wallet.getBalance());
    }

    private void printCategoriesSummary() {
        Wallet wallet = this.walletService.getCurrentWallet();

        if (wallet == null) {
            System.out.println("Нет текущего кошелька, попробуйте позже");

            return;
        }

        String categoryNamesStr;

        while (true) {
            System.out.println("Введите через запятую названия категорий, по которым хотите получить информацию (пример: 'Еда,Такси,Техника'): ");
            categoryNamesStr = scanNextLine();

            if (categoryNamesStr.isEmpty()) System.out.println("Список категорий не может быть пустым");
            else break;
        }

        String[] categoryNames = categoryNamesStr.split(",");
        List<Category> categories = new ArrayList<>();

        for (String categoryName : categoryNames) {
            Category cat = wallet.getExistingCategoryByName(categoryName.trim());
            if (cat == null) {
                System.out.println("Категория: '" + categoryName.trim() + "' не найдена");
                continue;
            }

            categories.add(cat);
        }

        if (categories.isEmpty()) {
            System.out.println("Не найдено категорий, по которым можно вывести информацию");

            return;
        }

        for (Category category : categories) {
            boolean isIncome = category.getIsIncome();
            int summaryByCategory = 0;

            System.out.println((isIncome ? "Доходы" : "Расходы") + " по категории '" + category.getName() + "' (Бюджет: " + category.getBudget() + "): ");

            for (int operation : category.getOperations()) {
                System.out.println("  * " + operation);

                summaryByCategory += operation;
            }

            System.out.println("Итого (" + category.getName() + "): " + summaryByCategory + (isIncome ? "" : (" (Оставшийся бюджет: " + (category.getBudget() - Math.abs(summaryByCategory)) + ")")) );
            if ((category.getBudget() - Math.abs(summaryByCategory)) < 0) System.out.println("Обращаем внимание, что у вас превышен лимит по категории '" + category.getName() + "': " + (Math.abs(summaryByCategory) - category.getBudget()));
            System.out.println();
        }

        if (wallet.getBalance() == 0) System.out.println("Обращаем внимание, что у вас закончился баланс: '" + wallet.getBalance());
    }

    private void addCategoryOperation() {
        Wallet wallet = this.walletService.getCurrentWallet();

        if (wallet == null) {
            System.out.println("Нет текущего кошелька, попробуйте позже");

            return;
        }

        System.out.println("Введите название категории, по которой хотите добавить операцию: ");
        String categoryName = scanNextLine();

        if (categoryName.isEmpty()) {
            System.out.println("Название категории не может быть пустым");

            return;
        }

        Category cat = wallet.getExistingCategoryByName(categoryName);

        if (cat == null) {
            System.out.println("Категория: '" + categoryName.trim() + "' не найдена");

            return;
        }

        String valStr;
        int val;

        while (true) {
            System.out.println("Введите сумму операции по категории (целочисленное значение): ");
            valStr = scanNextLine();
            try {
                val = Integer.parseInt(valStr);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Ошибка ввода. Введите целое число");
            }
        }


        Boolean isMadeTransaction = wallet.makeTransactionByCategory(categoryName, val);
        WalletStorage.save(wallet);

        if (isMadeTransaction == null) {
            System.out.println("Категория: '" + categoryName.trim() + "' не найдена");

            return;
        }

        if (!isMadeTransaction) {
            System.out.println("Недостаточный баланс для добавления операции");

            return;
        }

        System.out.println("Добавлена операция по категории: '" + categoryName + "' -> " + val);

        int summaryByCategory = 0;

        for (int operation : cat.getOperations()) {
            summaryByCategory += operation;
        }

        if ((cat.getBudget() - Math.abs(summaryByCategory)) < 0) System.out.println("Обращаем внимание, что у вас превышен лимит по категории '" + categoryName + "': " + (Math.abs(summaryByCategory) - cat.getBudget()));
        if (wallet.getBalance() == 0) System.out.println("Обращаем внимание, что у вас закончился баланс: '" + wallet.getBalance());
    }

    private void makeTransactionToUser() {
        Wallet wallet = this.walletService.getCurrentWallet();

        if (wallet == null) {
            System.out.println("Нет текущего кошелька, попробуйте позже");

            return;
        }

        String username;

        while (true) {
            System.out.println("Введите имя пользователя для отправки денег: ");
            username = scanNextLine();

            if (username.isEmpty()) System.out.println("Имя пользователя для отправки не может быть пустым");
            else break;
        }

        String valStr;
        int val;

        while (true) {
            System.out.println("Введите сумму для отправки (целочисленное значение): ");
            valStr = scanNextLine();
            try {
                val = Integer.parseInt(valStr);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Ошибка ввода. Введите целое число");
            }
        }

        Boolean isTransactionMade = this.walletService.makeTransactionToUser(username, val);
        WalletStorage.save(wallet);

        if (isTransactionMade == null) {
            System.out.println("Ошибка перевода. Запрашиваемый пользователь не найден'");

            return;
        }

        if (!isTransactionMade) {
            System.out.println("Ошибка перевода. У вас недостаточно денег на балансе'");

            return;
        }

        System.out.println("Совершен перевод денег пользователю '" + username + "' -> " + val);
    }

    private void chooseUser() {
        this.usersService.logoutUser();
        User chosenUser = null;

        while (chosenUser == null) {
            System.out.println("Введите пару логин-пароль для авторизации пользователя (например: admin qwerty12345) или нажмите Enter оставив ввод пустым для создания нового пользователя: ");
            String userLoginPair = scanNextLine();

            if (userLoginPair.isEmpty()) {
                System.out.println("Введите пару логин-пароль для создания пользователя (например: admin qwerty12345; пробел используется как разделитель - это запрещенный символ в логине или пароле): ");
                String userRegisterPair = scanNextLine();

                String[] registerParts = userRegisterPair.split(" ");

                if (registerParts.length < 2) {
                    System.out.println("Пара логин-пароль введена некорректно, попробуйте снова позже");
                    continue;
                } else System.out.println("Ваша пара логин-пароль: " + registerParts[0] + " " + registerParts[1]);

                chosenUser = this.usersService.createUser(registerParts[0], registerParts[1]);
                this.walletService.setCurrentWallet();
            } else {
                String[] loginParts = userLoginPair.split(" ");

                if (loginParts.length < 2) {
                    System.out.println("Пара логин-пароль введена некорректно, попробуйте снова позже");
                    continue;
                }

                chosenUser = this.usersService.loginUser(loginParts[0], loginParts[1]);
                this.walletService.setCurrentWallet();

                if (chosenUser == null) System.out.println("Неверные данные авторизации");
            }
        }

        System.out.println("Вы используете пользователя: " + usersService.getCurrentUser().username());
    }

    private void showHelp() {
        System.out.println("\n" + "=".repeat(150));
        System.out.println("СПРАВКА ПО КОМАНДАМ");
        System.out.println("=".repeat(150));
        System.out.println("\n1. Создать категорию планирования бюджета");
        System.out.println("   Создание новой категории доходов или расходов");
        System.out.println("   Пример: создайте категорию 'Зарплата' как доход (+)");

        System.out.println("\n2. Установить бюджет для категории");
        System.out.println("   Установка лимита расходов для категории");
        System.out.println("   Пример: установите бюджет 5000 для категории 'Еда'");

        System.out.println("\n3. Поменять название категории");
        System.out.println("   Изменение названия существующей категории");

        System.out.println("\n4. Получить всю информацию по кошельку");
        System.out.println("   Вывод полной статистики: доходы, расходы, баланс");

        System.out.println("\n5. Получить информацию по категории/категориям");
        System.out.println("   Детальная информация по выбранным категориям");
        System.out.println("   Пример ввода: Еда,Транспорт,Развлечения");

        System.out.println("\n6. Добавить операцию по категории для учета");
        System.out.println("   Добавление дохода или расхода в выбранную категорию");
        System.out.println("   Пример: добавьте 500 в категорию 'Еда'");

        System.out.println("\n7. Совершить перевод другому пользователю");
        System.out.println("   Отправка денег другому пользователю системы");

        System.out.println("\n8. Экспорт данных");
        System.out.println("   Сохранение отчета в файл (CSV, JSON или TXT формат)");

        System.out.println("\n9. Сменить пользователя");
        System.out.println("   Выход из текущего аккаунта и вход в другой");

        System.out.println("\n0. Завершить программу");
        System.out.println("   Сохранение данных и выход из приложения");

        System.out.println("\n" + "=".repeat(150));
        System.out.println("Для работы с приложением выберите номер команды или введите 'help'");
        System.out.println("=".repeat(150) + "\n");
    }

    private void exportData() {
        Wallet wallet = this.walletService.getCurrentWallet();

        if (wallet == null) {
            System.out.println("Нет текущего кошелька, попробуйте позже");
            return;
        }

        System.out.println("\nВыберите формат экспорта:");
        System.out.println("1. CSV (для Excel)");
        System.out.println("2. JSON (для программной обработки)");
        System.out.println("3. TXT (текстовый отчет)");

        String format = scanNextLine();
        String filename = "export_" + wallet.getOwner() + "_" + System.currentTimeMillis();

        try {
            switch (format) {
                case "1":
                    filename += ".csv";
                    DataExporter.exportToCSV(wallet, filename);
                    System.out.println("✓ Данные экспортированы в файл: " + filename);
                    break;
                case "2":
                    filename += ".json";
                    DataExporter.exportToJSON(wallet, filename);
                    System.out.println("✓ Данные экспортированы в файл: " + filename);
                    break;
                case "3":
                    filename += ".txt";
                    DataExporter.exportSummaryToFile(wallet, filename);
                    System.out.println("✓ Отчет сохранен в файл: " + filename);
                    break;
                default:
                    System.out.println("Неверный формат");
            }
        } catch (IOException e) {
            System.out.println("Ошибка при экспорте данных: " + e.getMessage());
        }
    }
}
