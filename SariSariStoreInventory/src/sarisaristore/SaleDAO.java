package sarisaristore;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/** Saves a sale into SALE + SALE_ITEM, lowers PRODUCT stock, and reads sales history. */
public class SaleDAO {

    /** Saves the whole sale in ONE transaction: all of it works, or none of it is saved. */
    public static int checkout(int staffId, int customerId, String payment, List<SaleItem> cart)
            throws SQLException {
        double total = 0;
        for (SaleItem i : cart) total += i.getSubtotal();

        try (Connection c = DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                int saleId = 0;
                String saleSql = "INSERT INTO SALE (Sale_Date, Total_Amount, Payment_Method, "
                               + "Staff_ID, Customer_ID) VALUES (NOW(), ?, ?, ?, ?)";
                try (PreparedStatement ps = c.prepareStatement(saleSql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setDouble(1, total);
                    ps.setString(2, payment);
                    ps.setInt(3, staffId);
                    if (customerId > 0) ps.setInt(4, customerId);
                    else ps.setNull(4, Types.INTEGER);
                    ps.executeUpdate();
                    try (ResultSet k = ps.getGeneratedKeys()) {
                        if (k.next()) saleId = k.getInt(1);
                    }
                }

                String stockSql = "UPDATE PRODUCT SET Quantity_In_Stock = Quantity_In_Stock - ? "
                                + "WHERE Product_ID = ? AND Quantity_In_Stock >= ?";
                String itemSql = "INSERT INTO SALE_ITEM (Sale_ID, Product_ID, Quantity, Unit_Price, Subtotal) "
                               + "VALUES (?,?,?,?,?)";
                try (PreparedStatement stock = c.prepareStatement(stockSql);
                     PreparedStatement item = c.prepareStatement(itemSql)) {
                    for (SaleItem i : cart) {
                        stock.setInt(1, i.getQuantity());
                        stock.setInt(2, i.getProduct().getId());
                        stock.setInt(3, i.getQuantity());
                        if (stock.executeUpdate() == 0) {
                            throw new SQLException("Not enough stock for "
                                    + i.getProduct().getName() + ".");
                        }
                        item.setInt(1, saleId);
                        item.setInt(2, i.getProduct().getId());
                        item.setInt(3, i.getQuantity());
                        item.setDouble(4, i.getProduct().getPrice());
                        item.setDouble(5, i.getSubtotal());
                        item.executeUpdate();
                    }
                }
                c.commit();
                return saleId;
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        }
    }

    /** Rows: Integer saleId, String date, String customer, String staff, Double total, String payment. */
    public static List<Object[]> history() {
        List<Object[]> rows = new ArrayList<>();
        String sql = "SELECT s.Sale_ID, s.Sale_Date, COALESCE(c.Name, 'Walk-in customer'), "
                   + "COALESCE(st.Name, '-'), s.Total_Amount, s.Payment_Method "
                   + "FROM SALE s LEFT JOIN CUSTOMER c ON s.Customer_ID = c.Customer_ID "
                   + "LEFT JOIN STAFF st ON s.Staff_ID = st.Staff_ID ORDER BY s.Sale_ID DESC";
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try (Connection c = DBConnection.getConnection(); Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Timestamp t = rs.getTimestamp(2);
                rows.add(new Object[]{rs.getInt(1), t == null ? "" : fmt.format(t),
                        rs.getString(3), rs.getString(4), rs.getDouble(5), rs.getString(6)});
            }
        } catch (SQLException e) {
            BaseDAO.showError(e);
        }
        return rows;
    }

    /** Rows: String product, Integer quantity, Double unitPrice, Double subtotal. */
    public static List<Object[]> items(int saleId) {
        List<Object[]> rows = new ArrayList<>();
        String sql = "SELECT p.Product_Name, si.Quantity, si.Unit_Price, si.Subtotal "
                   + "FROM SALE_ITEM si JOIN PRODUCT p ON p.Product_ID = si.Product_ID "
                   + "WHERE si.Sale_ID = ?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, saleId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(new Object[]{rs.getString(1), rs.getInt(2), rs.getDouble(3), rs.getDouble(4)});
                }
            }
        } catch (SQLException e) {
            BaseDAO.showError(e);
        }
        return rows;
    }
}
