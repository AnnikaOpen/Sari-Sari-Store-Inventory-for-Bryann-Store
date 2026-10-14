package sarisaristore;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/** Shared colors, fonts and small styled components for a clean, minimal look. */
public class Theme {
    public static final Color PRIMARY       = new Color(0x0F766E);
    public static final Color PRIMARY_HOVER = new Color(0x115E59);
    public static final Color PRIMARY_SOFT  = new Color(0xCCFBF1);
    public static final Color BG            = new Color(0xF4F6F8);
    public static final Color TEXT          = new Color(0x1F2937);
    public static final Color MUTED         = new Color(0x6B7280);
    public static final Color LINE          = new Color(0xE5E7EB);
    public static final Color LINE_STRONG   = new Color(0xD1D5DB);
    public static final Color DANGER        = new Color(0xB91C1C);
    public static final Color DANGER_BG     = new Color(0xFEE2E2);

    public static final String PESO = "\u20B1";

    public static Font font(int style, float size) {
        return new Font("Segoe UI", Font.PLAIN, 13).deriveFont(style, size);
    }

    public static JLabel label(String text, int style, float size, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font(style, size));
        l.setForeground(color);
        return l;
    }

    private static void smooth(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }

    /** Thin rounded outline. */
    static class RoundBorder extends AbstractBorder {
        private final Color color;
        private final int arc;
        RoundBorder(Color color, int arc) { this.color = color; this.arc = arc; }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            smooth(g2);
            g2.setColor(color);
            g2.drawRoundRect(x, y, w - 1, h - 1, arc, arc);
            g2.dispose();
        }
        @Override public Insets getBorderInsets(Component c) { return new Insets(1, 1, 1, 1); }
    }

    /** Rounded text/password field that highlights when focused. */
    public static <T extends JTextField> T field(T f) {
        Border pad = new EmptyBorder(8, 10, 8, 10);
        Border normal = new CompoundBorder(new RoundBorder(LINE_STRONG, 10), pad);
        Border focus = new CompoundBorder(new RoundBorder(PRIMARY, 10), pad);
        f.setFont(font(Font.PLAIN, 13));
        f.setForeground(TEXT);
        f.setBackground(Color.WHITE);
        f.setCaretColor(PRIMARY);
        f.setBorder(normal);
        f.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { f.setBorder(focus); }
            @Override public void focusLost(FocusEvent e) { f.setBorder(normal); }
        });
        return f;
    }

    public static <T> JComboBox<T> combo(JComboBox<T> c) {
        c.setFont(font(Font.PLAIN, 13));
        c.setBackground(Color.WHITE);
        c.setForeground(TEXT);
        c.setBorder(new RoundBorder(LINE_STRONG, 10));
        return c;
    }

    /** Flat rounded button. primary = teal fill, otherwise soft gray. */
    public static JButton button(String text, boolean primary) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                smooth(g2);
                boolean hover = getModel().isRollover();
                g2.setColor(primary ? (hover ? PRIMARY_HOVER : PRIMARY)
                                    : (hover ? new Color(0xE5E7EB) : new Color(0xF3F4F6)));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(font(Font.BOLD, 13));
        b.setForeground(primary ? Color.WHITE : TEXT);
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setRolloverEnabled(true);
        b.setBorder(new EmptyBorder(9, 16, 9, 16));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    /** White rounded card with a hairline border. */
    public static class Card extends JPanel {
        public Card(LayoutManager lm) {
            super(lm);
            setOpaque(false);
            setBorder(new EmptyBorder(16, 16, 16, 16));
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            smooth(g2);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
            g2.setColor(LINE);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Clean striped table look. Columns from rightFrom onward are right-aligned. */
    public static void simpleTable(JTable table, int rightFrom) {
        table.setRowHeight(34);
        table.setShowGrid(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(LINE);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setFont(font(Font.PLAIN, 13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JTableHeader h = table.getTableHeader();
        h.setReorderingAllowed(false);
        h.setPreferredSize(new Dimension(0, 34));
        h.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, false, false, row, col);
                setBackground(BG);
                setForeground(MUTED);
                setFont(font(Font.BOLD, 12));
                setHorizontalAlignment(col >= rightFrom ? RIGHT : LEFT);
                setBorder(new CompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, LINE),
                        new EmptyBorder(0, 10, 0, 10)));
                return this;
            }
        });
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, false, row, col);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                setHorizontalAlignment(col >= rightFrom ? RIGHT : LEFT);
                setFont(font(Font.PLAIN, 13));
                setForeground(TEXT);
                setBackground(sel ? PRIMARY_SOFT : (row % 2 == 0 ? Color.WHITE : new Color(0xF9FAFB)));
                return this;
            }
        });
    }
}
