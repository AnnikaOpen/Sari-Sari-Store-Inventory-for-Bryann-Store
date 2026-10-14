package sarisaristore;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Reads and writes the STAFF table. */
public class StaffDAO extends BaseDAO<Person> {

    @Override public List<Person> findAll() {
        List<Person> list = new ArrayList<>();
        String sql = "SELECT Staff_ID, Name, Position, Contact_Number FROM STAFF ORDER BY Staff_ID";
        try (Connection c = conn(); Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Staff(rs.getInt(1), rs.getString(2), rs.getString(4), rs.getString(3)));
            }
        } catch (SQLException e) {
            showError(e);
        }
        return list;
    }

    @Override public boolean add(Person p) {
        Staff s = (Staff) p;
        String sql = "INSERT INTO STAFF (Name, Position, Contact_Number) VALUES (?,?,?)";
        try (Connection c = conn();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, s.getName());
            ps.setString(2, s.getPosition());
            ps.setString(3, nullIfEmpty(s.getContact()));
            ps.executeUpdate();
            try (ResultSet k = ps.getGeneratedKeys()) {
                if (k.next()) s.setId(k.getInt(1));
            }
            return true;
        } catch (SQLException e) {
            showError(e);
            return false;
        }
    }

    @Override public boolean update(Person p) {
        Staff s = (Staff) p;
        String sql = "UPDATE STAFF SET Name=?, Position=?, Contact_Number=? WHERE Staff_ID=?";
        try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, s.getName());
            ps.setString(2, s.getPosition());
            ps.setString(3, nullIfEmpty(s.getContact()));
            ps.setInt(4, s.getId());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            showError(e);
            return false;
        }
    }

    @Override public boolean delete(int id) {
        return deleteById("STAFF", "Staff_ID", id);
    }
}
