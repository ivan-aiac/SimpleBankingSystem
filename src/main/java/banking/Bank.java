package banking;

import java.util.Optional;
import java.util.Random;

public class Bank {

    private final DataBase db;

    public Bank(String dbName){
        db = new DataBase("jdbc:sqlite:" + dbName);
    }

    public Account createAccount() {
        while (true) {
            StringBuilder sb = new StringBuilder("400000");
            Random random = new Random();
            int checksum = 8;
            for (int i = 7; i <= 15; i++) {
                int num = random.nextInt(10);
                sb.append(num);
                if (i % 2 != 0) {
                    num *= 2;
                    if (num > 9) {
                        num -= 9;
                    }
                }
                checksum += num;
            }
            checksum = checksum % 10 == 0 ? 10 : checksum % 10;
            sb.append(10 - checksum);
            if (!isCardNumberRegistered(sb.toString())) {
                Account account = new Account(sb.toString());
                sb = new StringBuilder();
                for (int i = 0; i < 4; i++) {
                    sb.append(random.nextInt(10));
                }
                account.setPin(sb.toString());
                if (db.saveAccount(account)) {
                    return account;
                }
            }
        }
    }

    public boolean isCardNumberValid(String cardNumber) {
        int luhn = 0;
        int num;
        for (int i = 1; i <= cardNumber.length(); i++) {
            num = cardNumber.charAt(i - 1) - '0';
            if (i % 2 != 0) {
                num *= 2;
                if (num > 9) {
                    num -= 9;
                }
            }
            luhn += num;
        }
        return luhn % 10 == 0;
    }

    public boolean updateAccountBalance(Account account) {
        return db.updateAccountBalance(account);
    }

    public boolean transferMoney(Account fromAccount, String toCardNumber, int transferAmount) {
        return db.transferMoney(fromAccount, toCardNumber, transferAmount);
    }

    public Optional<Account> login(String cardNumber, String pin) {
        return db.findAccount(cardNumber, pin);
    }

    public boolean closeAccount(Account account) {
        return db.closeAccount(account);
    }

    public boolean isCardNumberRegistered(String cardNumber) {
        return db.isCardNumberRegistered(cardNumber);
    }
}
