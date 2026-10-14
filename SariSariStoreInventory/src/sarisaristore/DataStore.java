package sarisaristore;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/** All database reading/writing lives here (table PRODUCT of sari_sari_store). */
public class DataStore {

    public static boolean checkLogin(String user, String pass) throws SQLException {
        String sql = "SELECT 1 FROM LOGIN_ACCOUNT WHERE Username = ? AND Password = SHA2(?, 256)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, user);
            ps.setString(2, pass);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public static List<Product> load() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT Product_ID, Product_Name, Category, Price, Quantity_In_Stock "
                   + "FROM PRODUCT ORDER BY Product_ID";
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Product(rs.getInt("Product_ID"), rs.getString("Product_Name"),
                        rs.getString("Category"), rs.getDouble("Price"),
                        rs.getInt("Quantity_In_Stock")));
            }
        } catch (SQLException e) {
            showError(e);
        }
        return list;
    }

    /** Adds a product. MySQL creates the Product_ID, which is put back into p. */
    public static boolean insert(Product p) {
        String sql = "INSERT INTO PRODUCT (Product_Name, Category, Price, Quantity_In_Stock) "
                   + "VALUES (?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getName());
            ps.setString(2, p.getCategory());
            ps.setDouble(3, p.getPrice());
            ps.setInt(4, p.getStock());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) p.setId(keys.getInt(1));
            }
            return true;
        } catch (SQLException e) {
            showError(e);
            return false;
        }
    }

    public static boolean update(Product p) {
        String sql = "UPDATE PRODUCT SET Product_Name=?, Category=?, Price=?, Quantity_In_Stock=? "
                   + "WHERE Product_ID=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setString(2, p.getCategory());
            ps.setDouble(3, p.getPrice());
            ps.setInt(4, p.getStock());
            ps.setInt(5, p.getId());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            showError(e);
            return false;
        }
    }

    public static boolean delete(int id) {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM PRODUCT WHERE Product_ID=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(null,
                    "This product already has sales records, so it can't be deleted.",
                    "Cannot delete", JOptionPane.WARNING_MESSAGE);
            return false;
        } catch (SQLException e) {
            showError(e);
            return false;
        }
    }

    private static void showError(SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Database error:\n" + e.getMessage(),
                "Database", JOptionPane.ERROR_MESSAGE);
    }
}
