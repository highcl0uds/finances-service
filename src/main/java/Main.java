import UI.MenuService.MenuService;
import services.WalletService;
import services.UsersService;

public class Main {
    public static void main(String[] args) {
        UsersService usersService = new UsersService();
        WalletService walletService = new WalletService(usersService);
        MenuService menuService = new MenuService(usersService, walletService);

        menuService.startMenu();
    }
}
