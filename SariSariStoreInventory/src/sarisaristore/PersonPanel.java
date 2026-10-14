package sarisaristore;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/** One screen used for BOTH Staff and Customers (POLYMORPHISM: it only sees Person). */
public class PersonPanel extends JPanel implements Refreshable {
    private final Crud<Person> dao;
    private final boolean staffMode;
    private final List<Person> people = new ArrayList<>();

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Name", "Role", "Contact"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);
    private final JTextField txtId = Theme.field(new JTextField());
    private final JTextField txtName = Theme.field(new JTextField());
    private final JTextField txtPosition = Theme.field(new JTextField());
    private final JTextField txtContact = Theme.field(new JTextField());

    public PersonPanel(Crud<Person> dao, boolean staffMode) {
        this.dao = dao;
        this.staffMode = staffMode;
        setBackground(Theme.BG);
        setLayout(new BorderLayout(16, 0));
        setBorder(new EmptyBorder(20, 24, 20, 24));

        txtId.setEditable(false);
        txtId.setBackground(new Color(0xF3F4F6));

        JPanel fields = new JPanel(new GridLayout(0, 1, 0, 4));
        fields.setOpaque(false);
        addField(fields, "ID (auto)", txtId);
        addField(fields, "Full name", txtName);
        if (staffMode) addField(fields, "Position", txtPosition);
        addField(fields, "Contact number", txtContact);
        JPanel fieldsTop = new JPanel(new BorderLayout());
        fieldsTop.setOpaque(false);
        fieldsTop.add(fields, BorderLayout.NORTH);

        JButton btnAdd = Theme.button("Add", true);
        JButton btnUpdate = Theme.button("Update", false);
        JButton btnDelete = Theme.button("Delete", false);
        btnDelete.setForeground(Theme.DANGER);
        JButton btnClear = Theme.button("Clear", false);
        JPanel buttons = new JPanel(new GridLayout(2, 2, 8, 8));
        buttons.setOpaque(false);
        buttons.add(btnAdd); buttons.add(btnUpdate);
        buttons.add(btnDelete); buttons.add(btnClear);

        Theme.Card formCard = new Theme.Card(new BorderLayout(0, 12));
        formCard.setPreferredSize(new Dimension(290, 0));
        formCard.add(Theme.label(staffMode ? "Staff details" : "Customer details",
                Font.BOLD, 15, Theme.TEXT), BorderLayout.NORTH);
        formCard.add(fieldsTop, BorderLayout.CENTER);
        formCard.add(buttons, BorderLayout.SOUTH);

        Theme.simpleTable(table, 99);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.LINE));
        scroll.getViewport().setBackground(Color.WHITE);

        Theme.Card tableCard = new Theme.Card(new BorderLayout(0, 12));
        tableCard.add(Theme.label(staffMode ? "Staff" : "Customers", Font.BOLD, 15, Theme.TEXT),
                BorderLayout.NORTH);
        tableCard.add(scroll, BorderLayout.CENTER);

        add(formCard, BorderLayout.WEST);
        add(tableCard, BorderLayout.CENTER);

        btnAdd.addActionListener(e -> addPerson());
        btnUpdate.addActionListener(e -> updatePerson());
        btnDelete.addActionListener(e -> deletePerson());
        btnClear.addActionListener(e -> clearForm());
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) fillForm();
        });
    }

    private void addField(JPanel p, String caption, JComponent field) {
        JPanel row = new JPanel(new BorderLayout(0, 3));
        row.setOpaque(false);
        row.add(Theme.label(caption, Font.BOLD, 11, Theme.MUTED), BorderLayout.NORTH);
        row.add(field, BorderLayout.CENTER);
        p.add(row);
    }

    @Override public void reload() {
        people.clear();
        people.addAll(dao.findAll());
        model.setRowCount(0);
        for (Person p : people) {
            model.addRow(new Object[]{p.getId(), p.getName(), p.getRole(), p.getContact()});
        }
        clearForm();
    }

    private Person fromForm(int id) {
        String name = txtName.getText().trim();
        String contact = txtContact.getText().trim();
        if (name.isEmpty()) { msg("Name is required."); return null; }
        if (contact.length() > 20) { msg("Contact number is too long (max 20)."); return null; }
        if (staffMode) {
            String pos = txtPosition.getText().trim();
            if (pos.isEmpty()) { msg("Position is required."); return null; }
            return new Staff(id, name, contact, pos);
        }
        return new Customer(id, name, contact);
    }

    private void addPerson() {
        Person p = fromForm(0);
        if (p != null && dao.add(p)) reload();
    }

    private void updatePerson() {
        Person old = selected();
        if (old == null) { msg("Select a row from the table first."); return; }
        Person p = fromForm(old.getId());
        if (p != null && dao.update(p)) reload();
    }

    private void deletePerson() {
        Person p = selected();
        if (p == null) { msg("Select a row from the table first."); return; }
        if (JOptionPane.showConfirmDialog(this, "Delete \"" + p.getName() + "\"?",
                "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (dao.delete(p.getId())) reload();
        }
    }

    private Person selected() {
        int row = table.getSelectedRow();
        if (row < 0) return null;
        int id = Integer.parseInt(table.getValueAt(row, 0).toString());
        for (Person p : people) if (p.getId() == id) return p;
        return null;
    }

    private void fillForm() {
        Person p = selected();
        if (p == null) return;
        txtId.setText(String.valueOf(p.getId()));
        txtName.setText(p.getName());
        txtContact.setText(p.getContact());
        if (p instanceof Staff) txtPosition.setText(((Staff) p).getPosition());
    }

    private void clearForm() {
        table.clearSelection();
        txtId.setText("(auto)");
        txtName.setText("");
        txtPosition.setText("");
        txtContact.setText("");
    }

    private void msg(String m) {
        JOptionPane.showMessageDialog(this, m, "Notice", JOptionPane.WARNING_MESSAGE);
    }
}
