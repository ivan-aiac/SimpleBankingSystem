package banking;

public class Account {

    private final int id;
    private final String cardNumber;
    private String pin;
    private int balance;

    public Account(int id, String cardNumber, String pin, int balance) {
        this.id = id;
        this.cardNumber = cardNumber;
        this.pin = pin;
        this.balance = balance;
    }

    public Account(String cardNumber) {
        this(0, cardNumber, "", 0);
    }

    public int getId() {
        return id;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getPin() {
        return pin;
    }

    public int getBalance() {
        return balance;
    }

    public void addBalance(int amount) {
        balance += amount;
    }

    public boolean subtractBalance(int amount) {
        if (amount <= balance) {
            balance -= amount;
            return true;
        }
        return false;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }
}
