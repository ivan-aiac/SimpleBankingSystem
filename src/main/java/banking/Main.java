package banking;

import java.util.Optional;
import java.util.Scanner;

public class Main {

    private static Scanner scanner;
    private static Bank bank;

    public static void main(String[] args) {
        String dbName = "db";
        for (int i = 0; i < args.length; i += 2) {
            if ("-fileName".equals(args[i])) {
                dbName = args[i + 1];
            }
        }
        scanner = new Scanner(System.in);
        bank = new Bank(dbName);

        int option = -1;
        while (option != 0) {
            System.out.println("1. Create an account\n2. Log into account\n0. Exit");
            option = Integer.parseInt(scanner.nextLine());
            switch (option) {
                case 1:
                    createAccount();
                    break;
                case 2:
                    loginAccount();
                    break;
                case 0:
                    exit();
                default:
                    break;
            }
        }
    }

    private static void createAccount() {
        Account account = bank.createAccount();
        System.out.printf("Your card has been created\n" +
                "Your card number:\n%s\n" +
                "Your card PIN:\n%s\n", account.getCardNumber(), account.getPin());
    }

    private static void loginAccount() {
        System.out.println("Enter your card number:");
        String cardNumber = scanner.nextLine();
        System.out.println("Enter your PIN:");
        String pin = scanner.nextLine();
        Optional<Account> account = bank.login(cardNumber, pin);
        if (account.isPresent()) {
            System.out.println("You have successfully logged in!");
            int loginOption = -1;
            while (loginOption != 5) {
                System.out.println("1. Balance\n2. Add income\n3. Do Transfer\n4. Close Account\n5. Log out\n0. Exit");
                loginOption = Integer.parseInt(scanner.nextLine());
                switch (loginOption) {
                    case 1:
                        System.out.printf("Balance: %d\n", account.get().getBalance());
                        break;
                    case 2:
                        addIncome(account.get());
                        break;
                    case 3:
                        transferMoney(account.get());
                        break;
                    case 4:
                        closeAccount(account.get());
                        break;
                    case 5:
                        System.out.println("You have successfully logged out!");
                        break;
                    case 0:
                        exit();
                    default:
                        break;
                }
            }
        } else {
            System.out.println("Wrong card number or PIN!");
        }
    }

    private static void addIncome(Account account) {
        System.out.println("Enter income:");
        int income = Integer.parseInt(scanner.nextLine());
        account.addBalance(income);
        if (income == 0 || bank.updateAccountBalance(account)) {
            System.out.println("Income was added!");
        } else {
            System.out.println("Couldn't add income to the account.");
        }
    }

    private static void closeAccount(Account account) {
        if (bank.closeAccount(account)) {
            System.out.println("The account has been closed!");
        } else {
            System.out.println("Couldn't close account.");
        }
    }

    private static void transferMoney(Account account) {
        System.out.println("Enter card number:");
        String cardNumber = scanner.nextLine();
        if (account.getCardNumber().equals(cardNumber)) {
            System.out.println("You can't transfer money to the same account!");
        } else if (!bank.isCardNumberValid(cardNumber)) {
            System.out.println("Probably you made a mistake in the card number. Please try again!");
        } else if (!bank.isCardNumberRegistered(cardNumber)) {
            System.out.println("Such a card does not exist.");
        } else {
            System.out.println("Enter how much money you want to transfer:");
            int transferAmount = Integer.parseInt(scanner.nextLine());
            if (!account.subtractBalance(transferAmount)) {
                System.out.println("Not enough money!");
            } else {
                if (bank.transferMoney(account, cardNumber, transferAmount)) {
                    System.out.println("Success!");
                } else {
                    System.out.println("Couldn't transfer the money.");
                }
            }
        }
    }

    private static void exit() {
        System.out.println("Bye!");
        System.exit(0);
    }
}