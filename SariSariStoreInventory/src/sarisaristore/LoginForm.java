package sarisaristore;

import java.awt.*;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class LoginForm extends JFrame {
    private final JTextField txtUser = Theme.field(new JTextField(20));
    private final JPasswordField txtPass = Theme.field(new JPasswordField(20));
    private final JCheckBox chkShow = new JCheckBox("Show password");
    private final JLabel lblError = Theme.label(" ", Font.PLAIN, 12, Theme.DANGER);

    public LoginForm() {
        setTitle("Sari-Sari Store Inventory - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(780, 450);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new GridLayout(1, 2));

        // ---- Left: brand panel ----
        JPanel brand = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.PRIMARY);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(255, 255, 255, 20));
                g2.fillOval(-70, getHeight() - 170, 260, 260);
                g2.fillOval(getWidth() - 110, -90, 230, 230);
                g2.dispose();
            }
        };
        JPanel brandText = new JPanel();
        brandText.setOpaque(false);
        brandText.setLayout(new BoxLayout(brandText, BoxLayout.Y_AXIS));
        Color soft = new Color(0xCCFBF1);
        JLabel t1 = Theme.label("Sari-Sari Store", Font.BOLD, 30, Color.WHITE);
        JLabel t2 = Theme.label("Inventory System", Font.PLAIN, 16, soft);
        JLabel t3 = Theme.label("Simple stock tracking for your tindahan.", Font.PLAIN, 12, soft);
        for (JLabel l : new JLabel[]{t1, t2, t3}) l.setAlignmentX(Component.LEFT_ALIGNMENT);
        brandText.add(t1);
        brandText.add(Box.createVerticalStrut(4));
        brandText.add(t2);
        brandText.add(Box.createVerticalStrut(28));
        brandText.add(t3);
        brand.add(brandText);

        // ---- Right: form ----
        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(Color.WHITE);
        JPanel box = new JPanel();
        box.setOpaque(false);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));

        JLabel h = Theme.label("Welcome back", Font.BOLD, 24, Theme.TEXT);
        JLabel sub = Theme.label("Sign in to manage your store", Font.PLAIN, 13, Theme.MUTED);
        JLabel lu = Theme.label("Username", Font.BOLD, 12, Theme.TEXT);
        JLabel lp = Theme.label("Password", Font.BOLD, 12, Theme.TEXT);
        chkShow.setOpaque(false);
        chkShow.setFocusPainted(false);
        chkShow.setFont(Theme.font(Font.PLAIN, 12));
        chkShow.setForeground(Theme.MUTED);
        JButton btnLogin = Theme.button("Sign in", true);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        JLabel hint = Theme.label("Demo login: admin / admin123", Font.PLAIN, 11, Theme.MUTED);

        JComponent[] items = {h, sub, lu, txtUser, lp, txtPass, chkShow, btnLogin, lblError, hint};
        for (JComponent c : items) c.setAlignmentX(Component.LEFT_ALIGNMENT);
        box.add(h);
        box.add(Box.createVerticalStrut(2));
        box.add(sub);
        box.add(Box.createVerticalStrut(24));
        box.add(lu);
        box.add(Box.createVerticalStrut(4));
        box.add(txtUser);
        box.add(Box.createVerticalStrut(12));
        box.add(lp);
        box.add(Box.createVerticalStrut(4));
        box.add(txtPass);
        box.add(Box.createVerticalStrut(6));
        box.add(chkShow);
        box.add(Box.createVerticalStrut(14));
        box.add(btnLogin);
        box.add(Box.createVerticalStrut(8));
        box.add(lblError);
        box.add(Box.createVerticalStrut(10));
        box.add(hint);
        right.add(box);

        add(brand);
        add(right);

        char defaultEcho = txtPass.getEchoChar();
        chkShow.addActionListener(e ->
                txtPass.setEchoChar(chkShow.isSelected() ? (char) 0 : defaultEcho));
        btnLogin.addActionListener(e -> login());
        txtPass.addActionListener(e -> login());
        txtUser.addActionListener(e -> txtPass.requestFocus());
        getRootPane().setDefaultButton(btnLogin);
    }

    private void login() {
        String u = txtUser.getText().trim();
        String p = new String(txtPass.getPassword());
        if (u.isEmpty() || p.isEmpty()) {
            lblError.setText("Please enter username and password.");
            return;
        }
        try {
            if (DataStore.checkLogin(u, p)) {
                new MainFrame(u).setVisible(true);
                dispose();
            } else {
                lblError.setText("Invalid username or password.");
                txtPass.setText("");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            lblError.setText("Can't connect to the database.");
            JOptionPane.showMessageDialog(this,
                    "Could not connect to MySQL.\n\n1) Is MySQL started in XAMPP?\n"
                    + "2) Did you add the MySQL JDBC library?\n\nDetails: " + ex.getMessage(),
                    "Database", JOptionPane.ERROR_MESSAGE);
        }
    }
}
