package sarisaristore;

import java.sql.*;
import javax.swing.JOptionPane;

/** ABSTRACT CLASS: shared database helpers for StaffDAO and CustomerDAO. */
public abstract class BaseDAO<T> implements Crud<T> {

    protected Connection conn() throws SQLException {
        return DBConnection.getConnection();
    }

    public static void showError(SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Database error:\n" + e.getMessage(),
                "Database", JOptionPane.ERROR_MESSAGE);
    }

    protected boolean deleteById(String table, String idColumn, int id) {
        String sql = "DELETE FROM " + table + " WHERE " + idColumn + " = ?";
        try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(null,
                    "This record is used in sales records, so it can't be deleted.",
                    "Cannot delete", JOptionPane.WARNING_MESSAGE);
            return false;
        } catch (SQLException e) {
            showError(e);
            return false;
        }
    }

    protected static String nullIfEmpty(String s) {
        return (s == null || s.trim().isEmpty()) ? null : s.trim();
    }
}
