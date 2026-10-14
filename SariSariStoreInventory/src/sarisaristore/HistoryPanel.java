package sarisaristore;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/** Sales report: every sale, and the items inside the selected sale. */
public class HistoryPanel extends JPanel implements Refreshable {
    private final DefaultTableModel salesModel = new DefaultTableModel(
            new String[]{"Sale #", "Date", "Customer", "Cashier", "Payment", "Total"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final DefaultTableModel itemsModel = new DefaultTableModel(
            new String[]{"Product", "Qty", "Unit price", "Subtotal"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable salesTable = new JTable(salesModel);
    private final JTable itemsTable = new JTable(itemsModel);

    private final JLabel lblTotal = Theme.label("0", Font.BOLD, 26, Theme.TEXT);
    private final JLabel lblCount = Theme.label("0", Font.BOLD, 26, Theme.TEXT);
    private final JLabel lblAvg = Theme.label("0", Font.BOLD, 26, Theme.TEXT);

    public HistoryPanel() {
        setBackground(Theme.BG);
        setLayout(new BorderLayout(0, 16));
        setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel metrics = new JPanel(new GridLayout(1, 3, 16, 0));
        metrics.setOpaque(false);
        metrics.add(metric("Total sales", lblTotal));
        metrics.add(metric("Transactions", lblCount));
        metrics.add(metric("Average sale", lblAvg));

        Theme.simpleTable(salesTable, 5);
        Theme.simpleTable(itemsTable, 1);

        Theme.Card salesCard = new Theme.Card(new BorderLayout(0, 10));
        salesCard.add(Theme.label("All sales (click one to see its items)", Font.BOLD, 15, Theme.TEXT),
                BorderLayout.NORTH);
        salesCard.add(scroll(salesTable), BorderLayout.CENTER);

        Theme.Card itemsCard = new Theme.Card(new BorderLayout(0, 10));
        itemsCard.add(Theme.label("Items in selected sale", Font.BOLD, 15, Theme.TEXT),
                BorderLayout.NORTH);
        itemsCard.add(scroll(itemsTable), BorderLayout.CENTER);

        JPanel center = new JPanel(new GridLayout(2, 1, 0, 16));
        center.setOpaque(false);
        center.add(salesCard);
        center.add(itemsCard);

        add(metrics, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);

        salesTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            itemsModel.setRowCount(0);
            int row = salesTable.getSelectedRow();
            if (row < 0) return;
            int saleId = Integer.parseInt(salesTable.getValueAt(row, 0).toString());
            for (Object[] r : SaleDAO.items(saleId)) {
                itemsModel.addRow(new Object[]{r[0], r[1],
                        String.format("%.2f", (Double) r[2]), String.format("%.2f", (Double) r[3])});
            }
        });
    }

    private JScrollPane scroll(JTable t) {
        JScrollPane sp = new JScrollPane(t);
        sp.setBorder(BorderFactory.createLineBorder(Theme.LINE));
        sp.getViewport().setBackground(Color.WHITE);
        return sp;
    }

    private JPanel metric(String caption, JLabel value) {
        Theme.Card c = new Theme.Card(new BorderLayout(0, 4));
        c.add(Theme.label(caption, Font.PLAIN, 12, Theme.MUTED), BorderLayout.NORTH);
        c.add(value, BorderLayout.CENTER);
        return c;
    }

    @Override public void reload() {
        salesModel.setRowCount(0);
        itemsModel.setRowCount(0);
        double total = 0;
        int count = 0;
        for (Object[] r : SaleDAO.history()) {
            double amount = (Double) r[4];
            salesModel.addRow(new Object[]{r[0], r[1], r[2], r[3], r[5], String.format("%.2f", amount)});
            total += amount;
            count++;
        }
        lblTotal.setText(String.format(Theme.PESO + "%,.2f", total));
        lblCount.setText(String.valueOf(count));
        lblAvg.setText(String.format(Theme.PESO + "%,.2f", count == 0 ? 0 : total / count));
    }
}
