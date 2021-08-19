package banking;

import java.sql.*;
import java.util.Optional;

public class DataBase {
    private static final String CREATE_CARD_TABLE =
            "CREATE TABLE IF NOT EXISTS card (" +
            "id INTEGER PRIMARY KEY, " +
            "number TEXT, " +
            "pin TEXT, " +
            "balance INTEGER DEFAULT 0" +
            ");";
    private static final String INSERT_CARD = "INSERT INTO card(number, pin) VALUES (?, ?)";
    private static final String LOGIN = "SELECT * FROM card WHERE number = ? AND pin = ?";
    private static final String CARD_EXISTS = "SELECT id FROM card WHERE number = ?";
    private static final String UPDATE_BALANCE = "UPDATE card SET balance = ? WHERE id = ?";
    private static final String TRANSFER_MONEY = "UPDATE card SET balance = balance + ? WHERE number = ?";
    private static final String CLOSE_ACCOUNT = "DELETE FROM card WHERE id = ?";
    private final String dbUrl;

    public DataBase(String dbUrl) {
        this.dbUrl = dbUrl;
        createDataBase();
    }

    public boolean saveAccount(Account account) {
        try(Connection con = DriverManager.getConnection(dbUrl);
            PreparedStatement ps = con.prepareStatement(INSERT_CARD)) {
            ps.setString(1, account.getCardNumber());
            ps.setString(2, account.getPin());
            return ps.executeUpdate() != 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public Optional<Account> findAccount(String cardNumber, String pin) {
        try(Connection con = DriverManager.getConnection(dbUrl);
            PreparedStatement ps = con.prepareStatement(LOGIN)) {
            ps.setString(1, cardNumber);
            ps.setString(2, pin);
            try(ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()){
                    Account account = new Account(
                            resultSet.getInt("id"),
                            resultSet.getString("number"),
                            resultSet.getString("pin"),
                            resultSet.getInt("balance"));
                    return Optional.of(account);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return Optional.empty();
        }
    }

    public boolean updateAccountBalance(Account account) {
        try(Connection con = DriverManager.getConnection(dbUrl);
            PreparedStatement ps = con.prepareStatement(UPDATE_BALANCE)) {
            ps.setInt(1, account.getBalance());
            ps.setInt(2, account.getId());
            return ps.executeUpdate() != 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean isCardNumberRegistered(String cardNumber) {
        try(Connection con = DriverManager.getConnection(dbUrl);
            PreparedStatement ps = con.prepareStatement(CARD_EXISTS)) {
            ps.setString(1, cardNumber);
            try(ResultSet resultSet = ps.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return true;
        }
    }

    public boolean closeAccount(Account account) {
        try(Connection con = DriverManager.getConnection(dbUrl);
            PreparedStatement ps = con.prepareStatement(CLOSE_ACCOUNT)) {
            ps.setInt(1, account.getId());
            return ps.executeUpdate() != 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean transferMoney(Account fromAccount, String toCardNumber, int transferAmount) {
        try(Connection con = DriverManager.getConnection(dbUrl)) {
            con.setAutoCommit(false);
            try(PreparedStatement subtractBalance = con.prepareStatement(UPDATE_BALANCE);
                PreparedStatement addBalance = con.prepareStatement(TRANSFER_MONEY)) {

                subtractBalance.setInt(1, fromAccount.getBalance());
                subtractBalance.setInt(2, fromAccount.getId());
                subtractBalance.executeUpdate();

                addBalance.setInt(1, transferAmount);
                addBalance.setString(2, toCardNumber);
                addBalance.executeUpdate();

                con.commit();
                return true;
            } catch (SQLException e) {
                con.rollback();
                System.out.println(e.getMessage());
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    private void createDataBase() {
        try(Connection con = DriverManager.getConnection(dbUrl);
            Statement statement = con.createStatement()) {
            statement.execute(CREATE_CARD_TABLE);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
