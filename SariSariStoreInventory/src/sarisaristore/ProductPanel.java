package sarisaristore;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;

public class ProductPanel extends JPanel implements Refreshable {
    private static final int LOW_STOCK = 5;

    private final List<Product> products = DataStore.load();
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Product name", "Category", "Price", "Stock"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);
    private final TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);

    private final JTextField txtId = Theme.field(new JTextField());
    private final JTextField txtName = Theme.field(new JTextField());
    private final JComboBox<String> cboCategory = Theme.combo(new JComboBox<String>(new String[]{
            "Drinks", "Food", "Snacks", "Biscuits", "Canned Goods", "Milk", "Toiletries", "Others"}));
    private final JTextField txtPrice = Theme.field(new JTextField());
    private final JTextField txtStock = Theme.field(new JTextField());
    private final JTextField txtSearch = Theme.field(new JTextField(16));

    private final JLabel lblTotal = Theme.label("0", Font.BOLD, 26, Theme.TEXT);
    private final JLabel lblLow = Theme.label("0", Font.BOLD, 26, Theme.TEXT);
    private final JLabel lblValue = Theme.label("0", Font.BOLD, 26, Theme.TEXT);

    public ProductPanel() {
        setBackground(Theme.BG);
        setLayout(new BorderLayout());

        // ---- Stat cards ----
        JPanel metrics = new JPanel(new GridLayout(1, 3, 16, 0));
        metrics.setOpaque(false);
        metrics.add(metric("Total products", lblTotal));
        metrics.add(metric("Low-stock items", lblLow));
        metrics.add(metric("Inventory value", lblValue));

        // ---- Form card ----
        JButton btnAdd = Theme.button("Add", true);
        JButton btnUpdate = Theme.button("Update", false);
        JButton btnDelete = Theme.button("Delete", false);
        btnDelete.setForeground(Theme.DANGER);
        JButton btnClear = Theme.button("Clear", false);
        txtId.setEditable(false);
        txtId.setBackground(new Color(0xF3F4F6));

        JPanel fields = new JPanel(new GridLayout(0, 1, 0, 4));
        fields.setOpaque(false);
        addField(fields, "Product ID (auto)", txtId);
        addField(fields, "Product name", txtName);
        addField(fields, "Category", cboCategory);
        addField(fields, "Price (" + Theme.PESO + ")", txtPrice);
        addField(fields, "Stock quantity", txtStock);

        JPanel buttons = new JPanel(new GridLayout(2, 2, 8, 8));
        buttons.setOpaque(false);
        buttons.add(btnAdd); buttons.add(btnUpdate);
        buttons.add(btnDelete); buttons.add(btnClear);

        Theme.Card formCard = new Theme.Card(new BorderLayout(0, 12));
        formCard.setPreferredSize(new Dimension(290, 0));
        formCard.add(Theme.label("Product details", Font.BOLD, 15, Theme.TEXT), BorderLayout.NORTH);
        JPanel fieldsTop = new JPanel(new BorderLayout());
        fieldsTop.setOpaque(false);
        fieldsTop.add(fields, BorderLayout.NORTH);
        formCard.add(fieldsTop, BorderLayout.CENTER);
        formCard.add(buttons, BorderLayout.SOUTH);

        // ---- Table card ----
        styleTable();
        JPanel tableTop = new JPanel(new BorderLayout());
        tableTop.setOpaque(false);
        tableTop.add(Theme.label("Products", Font.BOLD, 15, Theme.TEXT), BorderLayout.WEST);
        JPanel searchBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchBox.setOpaque(false);
        searchBox.add(Theme.label("Search", Font.PLAIN, 12, Theme.MUTED));
        searchBox.add(txtSearch);
        tableTop.add(searchBox, BorderLayout.EAST);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.LINE));
        scroll.getViewport().setBackground(Color.WHITE);

        Theme.Card tableCard = new Theme.Card(new BorderLayout(0, 12));
        tableCard.add(tableTop, BorderLayout.NORTH);
        tableCard.add(scroll, BorderLayout.CENTER);
        tableCard.add(Theme.label("Red stock = " + LOW_STOCK + " or fewer left", Font.PLAIN, 11, Theme.MUTED),
                BorderLayout.SOUTH);

        // ---- Assemble ----
        JPanel main = new JPanel(new BorderLayout(16, 0));
        main.setOpaque(false);
        main.add(formCard, BorderLayout.WEST);
        main.add(tableCard, BorderLayout.CENTER);

        JPanel body = new JPanel(new BorderLayout(0, 16));
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(20, 24, 20, 24));
        body.add(metrics, BorderLayout.NORTH);
        body.add(main, BorderLayout.CENTER);

        add(body, BorderLayout.CENTER);

        // ---- Events ----
        btnAdd.addActionListener(e -> addProduct());
        btnUpdate.addActionListener(e -> updateProduct());
        btnDelete.addActionListener(e -> deleteProduct());
        btnClear.addActionListener(e -> clearForm());
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) fillFormFromSelection();
        });
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }
        });

        refreshTable();
        clearForm();
    }

    // ---------- UI helpers ----------
    private JPanel metric(String caption, JLabel value) {
        Theme.Card c = new Theme.Card(new BorderLayout(0, 4));
        c.add(Theme.label(caption, Font.PLAIN, 12, Theme.MUTED), BorderLayout.NORTH);
        c.add(value, BorderLayout.CENTER);
        return c;
    }

    private void addField(JPanel p, String caption, JComponent field) {
        JPanel row = new JPanel(new BorderLayout(0, 3));
        row.setOpaque(false);
        row.add(Theme.label(caption, Font.BOLD, 11, Theme.MUTED), BorderLayout.NORTH);
        row.add(field, BorderLayout.CENTER);
        p.add(row);
    }

    private void styleTable() {
        table.setRowSorter(sorter);
        table.setRowHeight(34);
        table.setShowGrid(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(Theme.LINE);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setFont(Theme.font(Font.PLAIN, 13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        int[] widths = {60, 230, 110, 80, 70};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        JTableHeader h = table.getTableHeader();
        h.setReorderingAllowed(false);
        h.setPreferredSize(new Dimension(0, 34));
        h.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, false, false, row, col);
                setBackground(Theme.BG);
                setForeground(Theme.MUTED);
                setFont(Theme.font(Font.BOLD, 12));
                setHorizontalAlignment(col >= 3 ? RIGHT : LEFT);
                setBorder(new CompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.LINE),
                        new EmptyBorder(0, 10, 0, 10)));
                return this;
            }
        });

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, false, row, col);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                setHorizontalAlignment(col >= 3 ? RIGHT : LEFT);
                setFont(Theme.font(Font.PLAIN, 13));
                setForeground(Theme.TEXT);
                setBackground(sel ? Theme.PRIMARY_SOFT
                        : (row % 2 == 0 ? Color.WHITE : new Color(0xF9FAFB)));
                int stock = Integer.parseInt(t.getValueAt(row, 4).toString());
                if (col == 4 && stock <= LOW_STOCK) {
                    setForeground(Theme.DANGER);
                    setBackground(Theme.DANGER_BG);
                    setFont(Theme.font(Font.BOLD, 13));
                }
                return this;
            }
        });
    }

    // ---------- Actions ----------
    private void addProduct() {
        Product p = readForm(0);
        if (p == null) return;
        if (DataStore.insert(p)) {
            products.add(p);
            refreshTable();
            clearForm();
        }
    }

    private void updateProduct() {
        Product p = selectedProduct();
        if (p == null) { msg("Select a product from the table first."); return; }
        Product in = readForm(p.getId());
        if (in == null) return;
        if (DataStore.update(in)) {
            p.setName(in.getName());
            p.setCategory(in.getCategory());
            p.setPrice(in.getPrice());
            p.setStock(in.getStock());
            refreshTable();
            clearForm();
        }
    }

    private void deleteProduct() {
        Product p = selectedProduct();
        if (p == null) { msg("Select a product from the table first."); return; }
        if (JOptionPane.showConfirmDialog(this, "Delete \"" + p.getName() + "\"?",
                "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (DataStore.delete(p.getId())) {
                products.remove(p);
                refreshTable();
                clearForm();
            }
        }
    }

    // ---------- Helpers ----------
    private Product readForm(int id) {
        String name = txtName.getText().trim();
        if (name.isEmpty()) { msg("Product name is required."); return null; }
        try {
            double price = Double.parseDouble(txtPrice.getText().trim());
            int stock = Integer.parseInt(txtStock.getText().trim());
            if (price < 0 || stock < 0) throw new NumberFormatException();
            return new Product(id, name, cboCategory.getSelectedItem().toString(), price, stock);
        } catch (NumberFormatException ex) {
            msg("Price must be a number and Stock a whole number (both 0 or more).");
            return null;
        }
    }

    private Product selectedProduct() {
        int row = table.getSelectedRow();
        if (row < 0) return null;
        String id = table.getValueAt(row, 0).toString();
        for (Product p : products) if (String.valueOf(p.getId()).equals(id)) return p;
        return null;
    }

    private void fillFormFromSelection() {
        Product p = selectedProduct();
        if (p == null) return;
        txtId.setText(String.valueOf(p.getId()));
        txtName.setText(p.getName());
        boolean known = false;
        for (int i = 0; i < cboCategory.getItemCount(); i++)
            if (cboCategory.getItemAt(i).equals(p.getCategory())) known = true;
        if (!known) cboCategory.addItem(p.getCategory());
        cboCategory.setSelectedItem(p.getCategory());
        txtPrice.setText(String.valueOf(p.getPrice()));
        txtStock.setText(String.valueOf(p.getStock()));
    }

    private void clearForm() {
        table.clearSelection();
        txtId.setText("(auto)");
        txtName.setText(""); txtPrice.setText(""); txtStock.setText("");
        cboCategory.setSelectedIndex(0);
        txtName.requestFocus();
    }

    private void filter() {
        String t = txtSearch.getText().trim();
        sorter.setRowFilter(t.isEmpty() ? null
                : RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(t), 0, 1, 2));
    }

    private void refreshTable() {
        model.setRowCount(0);
        double total = 0; int low = 0;
        for (Product p : products) {
            model.addRow(new Object[]{p.getId(), p.getName(), p.getCategory(),
                    String.format("%.2f", p.getPrice()), p.getStock()});
            total += p.getPrice() * p.getStock();
            if (p.getStock() <= LOW_STOCK) low++;
        }
        lblTotal.setText(String.valueOf(products.size()));
        lblLow.setText(String.valueOf(low));
        lblLow.setForeground(low > 0 ? Theme.DANGER : Theme.TEXT);
        lblValue.setText(String.format(Theme.PESO + "%,.2f", total));
    }

    @Override public void reload() {
        products.clear();
        products.addAll(DataStore.load());
        refreshTable();
        clearForm();
    }

    private void msg(String m) {
        JOptionPane.showMessageDialog(this, m, "Notice", JOptionPane.WARNING_MESSAGE);
    }
}
