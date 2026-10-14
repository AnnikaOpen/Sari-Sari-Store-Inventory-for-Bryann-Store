package sarisaristore;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

/** Main window: header + tabs for every part of the system. */
public class MainFrame extends JFrame {

    public MainFrame(String user) {
        setTitle("Sari-Sari Store Inventory");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 720);
        setMinimumSize(new Dimension(980, 620));
        setLocationRelativeTo(null);
        getContentPane().setBackground(Theme.BG);
        setLayout(new BorderLayout());

        // ---- Header ----
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.LINE),
                new EmptyBorder(12, 24, 12, 24)));
        header.add(Theme.label("Sari-Sari Store Inventory", Font.BOLD, 18, Theme.PRIMARY), BorderLayout.WEST);
        JPanel userBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        userBar.setOpaque(false);
        JButton btnLogout = Theme.button("Log out", false);
        userBar.add(Theme.label("Signed in as " + user, Font.PLAIN, 12, Theme.MUTED));
        userBar.add(btnLogout);
        header.add(userBar, BorderLayout.EAST);

        // ---- Tabs ----
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(Theme.font(Font.BOLD, 13));
        tabs.setBackground(Theme.BG);
        tabs.addTab("Products", new ProductPanel());
        tabs.addTab("New Sale", new SalesPanel());
        tabs.addTab("Sales History", new HistoryPanel());
        tabs.addTab("Customers", new PersonPanel(new CustomerDAO(), false));
        tabs.addTab("Staff", new PersonPanel(new StaffDAO(), true));

        // POLYMORPHISM: every tab is "Refreshable", so we reload whichever one is opened.
        tabs.addChangeListener(e -> {
            Component c = tabs.getSelectedComponent();
            if (c instanceof Refreshable) ((Refreshable) c).reload();
        });

        add(header, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);

        btnLogout.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "Log out?", "Logout",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                new LoginForm().setVisible(true);
                dispose();
            }
        });
    }
}
