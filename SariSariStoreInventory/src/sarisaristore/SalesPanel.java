package sarisaristore;

import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/** Checkout screen: build a cart, pick customer + payment, save the sale to the database. */
public class SalesPanel extends JPanel implements Refreshable {
    private final StaffDAO staffDAO = new StaffDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();

    private final JComboBox<Person> cboStaff = Theme.combo(new JComboBox<Person>());
    private final JComboBox<Person> cboCustomer = Theme.combo(new JComboBox<Person>());
    private final JComboBox<String> cboPayment =
            Theme.combo(new JComboBox<String>(new String[]{"Cash", "GCash"}));
    private final JComboBox<Product> cboProduct = Theme.combo(new JComboBox<Product>());
    private final JTextField txtQty = Theme.field(new JTextField("1"));

    private final List<SaleItem> cart = new ArrayList<>();
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Product", "Qty", "Unit price", "Subtotal"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);
    private final JLabel lblTotal = Theme.label(Theme.PESO + "0.00", Font.BOLD, 28, Theme.PRIMARY);

    public SalesPanel() {
        setBackground(Theme.BG);
        setLayout(new BorderLayout(16, 0));
        setBorder(new EmptyBorder(20, 24, 20, 24));

        // ---- Left: sale details ----
        JPanel fields = new JPanel(new GridLayout(0, 1, 0, 4));
        fields.setOpaque(false);
        addField(fields, "Cashier (staff)", cboStaff);
        addField(fields, "Customer", cboCustomer);
        addField(fields, "Payment method", cboPayment);
        addField(fields, "Product", cboProduct);
        addField(fields, "Quantity", txtQty);

        JButton btnAdd = Theme.button("Add to cart", true);
        JPanel fieldsTop = new JPanel(new BorderLayout());
        fieldsTop.setOpaque(false);
        fieldsTop.add(fields, BorderLayout.NORTH);

        Theme.Card left = new Theme.Card(new BorderLayout(0, 12));
        left.setPreferredSize(new Dimension(320, 0));
        left.add(Theme.label("New sale", Font.BOLD, 15, Theme.TEXT), BorderLayout.NORTH);
        left.add(fieldsTop, BorderLayout.CENTER);
        left.add(btnAdd, BorderLayout.SOUTH);

        // ---- Right: cart ----
        Theme.simpleTable(table, 1);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.LINE));
        scroll.getViewport().setBackground(Color.WHITE);

        JPanel totalRow = new JPanel(new BorderLayout());
        totalRow.setOpaque(false);
        totalRow.add(Theme.label("Total", Font.BOLD, 15, Theme.MUTED), BorderLayout.WEST);
        totalRow.add(lblTotal, BorderLayout.EAST);

        JButton btnRemove = Theme.button("Remove selected", false);
        JButton btnClear = Theme.button("Clear cart", false);
        JButton btnCheckout = Theme.button("Checkout", true);
        JPanel buttons = new JPanel(new GridLayout(1, 3, 8, 0));
        buttons.setOpaque(false);
        buttons.add(btnRemove);
        buttons.add(btnClear);
        buttons.add(btnCheckout);

        JPanel bottom = new JPanel(new BorderLayout(0, 10));
        bottom.setOpaque(false);
        bottom.add(totalRow, BorderLayout.NORTH);
        bottom.add(buttons, BorderLayout.SOUTH);

        Theme.Card right = new Theme.Card(new BorderLayout(0, 12));
        right.add(Theme.label("Cart", Font.BOLD, 15, Theme.TEXT), BorderLayout.NORTH);
        right.add(scroll, BorderLayout.CENTER);
        right.add(bottom, BorderLayout.SOUTH);

        add(left, BorderLayout.WEST);
        add(right, BorderLayout.CENTER);

        btnAdd.addActionListener(e -> addToCart());
        btnRemove.addActionListener(e -> removeSelected());
        btnClear.addActionListener(e -> { cart.clear(); refreshCart(); });
        btnCheckout.addActionListener(e -> checkout());
    }

    private void addField(JPanel p, String caption, JComponent field) {
        JPanel row = new JPanel(new BorderLayout(0, 3));
        row.setOpaque(false);
        row.add(Theme.label(caption, Font.BOLD, 11, Theme.MUTED), BorderLayout.NORTH);
        row.add(field, BorderLayout.CENTER);
        p.add(row);
    }

    @Override public void reload() {
        cboStaff.removeAllItems();
        for (Person s : staffDAO.findAll()) cboStaff.addItem(s);

        cboCustomer.removeAllItems();
        cboCustomer.addItem(new Customer(0, "Walk-in customer", ""));
        for (Person c : customerDAO.findAll()) cboCustomer.addItem(c);

        cboProduct.removeAllItems();
        for (Product p : DataStore.load()) if (p.getStock() > 0) cboProduct.addItem(p);
    }

    private void addToCart() {
        Product p = (Product) cboProduct.getSelectedItem();
        if (p == null) { msg("No product available. Add stock in the Products tab."); return; }
        int qty;
        try {
            qty = Integer.parseInt(txtQty.getText().trim());
            if (qty <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            msg("Quantity must be a whole number above 0.");
            return;
        }
        SaleItem existing = null;
        for (SaleItem i : cart) if (i.getProduct().getId() == p.getId()) existing = i;
        int already = existing == null ? 0 : existing.getQuantity();
        if (already + qty > p.getStock()) {
            msg("Only " + p.getStock() + " in stock (you already have " + already + " in the cart).");
            return;
        }
        if (existing == null) cart.add(new SaleItem(p, qty));
        else existing.setQuantity(already + qty);
        txtQty.setText("1");
        refreshCart();
    }

    private void removeSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { msg("Select a cart row first."); return; }
        cart.remove(row);
        refreshCart();
    }

    private void refreshCart() {
        model.setRowCount(0);
        double total = 0;
        for (SaleItem i : cart) {
            model.addRow(new Object[]{i.getProduct().getName(), i.getQuantity(),
                    String.format("%.2f", i.getProduct().getPrice()),
                    String.format("%.2f", i.getSubtotal())});
            total += i.getSubtotal();
        }
        lblTotal.setText(String.format(Theme.PESO + "%,.2f", total));
    }

    private void checkout() {
        if (cart.isEmpty()) { msg("The cart is empty."); return; }
        Person staff = (Person) cboStaff.getSelectedItem();
        if (staff == null) { msg("Add a staff member first (Staff tab)."); return; }
        Person customer = (Person) cboCustomer.getSelectedItem();
        int customerId = customer == null ? 0 : customer.getId();

        try {
            int saleId = SaleDAO.checkout(staff.getId(), customerId,
                    cboPayment.getSelectedItem().toString(), cart);
            JOptionPane.showMessageDialog(this, "Sale #" + saleId + " saved.\nTotal: "
                    + lblTotal.getText(), "Sale complete", JOptionPane.INFORMATION_MESSAGE);
            cart.clear();
            refreshCart();
            reload();
        } catch (SQLException ex) {
            msg(ex.getMessage());
        }
    }

    private void msg(String m) {
        JOptionPane.showMessageDialog(this, m, "Notice", JOptionPane.WARNING_MESSAGE);
    }
}
