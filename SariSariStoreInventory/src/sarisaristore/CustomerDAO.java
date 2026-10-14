package sarisaristore;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Reads and writes the CUSTOMER table. */
public class CustomerDAO extends BaseDAO<Person> {

    @Override public List<Person> findAll() {
        List<Person> list = new ArrayList<>();
        String sql = "SELECT Customer_ID, Name, Contact_Number FROM CUSTOMER ORDER BY Customer_ID";
        try (Connection c = conn(); Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Customer(rs.getInt(1), rs.getString(2), rs.getString(3)));
            }
        } catch (SQLException e) {
            showError(e);
        }
        return list;
    }

    @Override public boolean add(Person p) {
        String sql = "INSERT INTO CUSTOMER (Name, Contact_Number) VALUES (?,?)";
        try (Connection c = conn();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getName());
            ps.setString(2, nullIfEmpty(p.getContact()));
            ps.executeUpdate();
            try (ResultSet k = ps.getGeneratedKeys()) {
                if (k.next()) p.setId(k.getInt(1));
            }
            return true;
        } catch (SQLException e) {
            showError(e);
            return false;
        }
    }

    @Override public boolean update(Person p) {
        String sql = "UPDATE CUSTOMER SET Name=?, Contact_Number=? WHERE Customer_ID=?";
        try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setString(2, nullIfEmpty(p.getContact()));
            ps.setInt(3, p.getId());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            showError(e);
            return false;
        }
    }

    @Override public boolean delete(int id) {
        return deleteById("CUSTOMER", "Customer_ID", id);
    }
}
