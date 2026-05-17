import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.*;
import java.util.List;

/**
 * Custom UI Components Module
 * Reusable visual components with custom rendering
 */

// Custom rounded button component
class RoundButton extends JButton {
    private int cornerRadius;

    public RoundButton(String text) {
        this(text, 0);
    }

    public RoundButton(String text, int cornerRadius) {
        super(text);
        this.cornerRadius = cornerRadius;
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(getModel().isPressed() ?
                getBackground().darker() : getBackground());

        if (cornerRadius > 0) {
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(),
                    getHeight(), cornerRadius, cornerRadius));
        } else {
            g2.fillOval(0, 0, getWidth(), getHeight());
        }

        g2.setColor(getForeground());
        g2.setFont(getFont());
        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(getText())) / 2;
        int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
        g2.drawString(getText(), x, y);
        g2.dispose();
    }
}

// Custom rounded panel component
class RoundedPanel extends JPanel {
    private Color backgroundColor;
    private int cornerRadius = 50;

    public RoundedPanel(Color bgColor) {
        super();
        this.backgroundColor = bgColor;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(backgroundColor);
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(),
                getHeight(), cornerRadius, cornerRadius));
    }
}

// Category button with custom rendering and interaction
class CategoryButton extends JPanel {
    private String text;
    private Color bgColor;
    private BirthdayManager manager;
    private String categoryType;
    private boolean isCustom;
    private BirthdayTrackerGUI parentGUI;

    public CategoryButton(String text, Color bgColor, BirthdayManager manager,
                          String categoryType, boolean isCustom,
                          BirthdayTrackerGUI parentGUI) {
        this.text = text;
        this.bgColor = bgColor;
        this.manager = manager;
        this.categoryType = categoryType;
        this.isCustom = isCustom;
        this.parentGUI = parentGUI;

        setOpaque(false);
        setPreferredSize(new Dimension(280, 120));
        setMaximumSize(new Dimension(280, 120));
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3 && isCustom) {
                    showDeleteCategoryMenu(e.getX(), e.getY());
                } else {
                    showCategoryBirthdays();
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(bgColor);
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(),
                getHeight(), 60, 60));

        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(text)) / 2;
        int y = (getHeight() + fm.getAscent()) / 2 - 5;
        g2.drawString(text, x, y);
    }

    private void showDeleteCategoryMenu(int x, int y) {
        JPopupMenu popup = new JPopupMenu();
        JMenuItem deleteItem = new JMenuItem("Delete Category");
        deleteItem.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Delete all " + categoryType + " birthdays and this category?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                List<Contact> toRemove = new ArrayList<>(
                        manager.getContactsByType(categoryType));
                for (Contact c : toRemove) manager.removeContact(c);
                parentGUI.refreshCategoryButtons();
                parentGUI.refreshCalendar();
                JOptionPane.showMessageDialog(this, "Category deleted!");
            }
        });
        popup.add(deleteItem);
        popup.show(this, x, y);
    }

    private void showCategoryBirthdays() {
        List<Contact> categoryContacts = manager.getContactsByType(categoryType);
        if (categoryContacts.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No " + categoryType + " birthdays found!");
            return;
        }

        Window window = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(
                window instanceof Frame ? (Frame)window : null, text, true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.getContentPane().setBackground(new Color(255, 240, 245));

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);
        listPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        for (Contact c : categoryContacts) {
            JPanel contactPanel = new JPanel(new BorderLayout(10, 5));
            contactPanel.setBackground(new Color(249, 249, 249));
            contactPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)));
            contactPanel.setMaximumSize(new Dimension(400, 80));

            int days = c.getDaysUntilBirthday();
            String daysText = days == 0 ? "TODAY! 🎉" :
                    days == 1 ? "Tomorrow" : days + " days";

            JLabel nameLabel = new JLabel("<html><b>" + c.getName() +
                    "</b><br><span style='color: #666;'>" + daysText +
                    "</span></html>");
            nameLabel.setFont(new Font("Arial", Font.PLAIN, 14));

            JButton deleteBtn = new JButton("🗑️");
            deleteBtn.setFont(new Font("Arial", Font.PLAIN, 16));
            deleteBtn.setBackground(new Color(239, 68, 68));
            deleteBtn.setForeground(Color.WHITE);
            deleteBtn.setFocusPainted(false);
            deleteBtn.setBorderPainted(false);
            deleteBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            deleteBtn.setPreferredSize(new Dimension(45, 45));
            deleteBtn.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(dialog,
                        "Delete " + c.getName() + "'s birthday?",
                        "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    manager.removeContact(c);
                    parentGUI.refreshCategoryButtons();
                    parentGUI.refreshCalendar();
                    dialog.dispose();
                    JOptionPane.showMessageDialog(this, "Birthday deleted!");
                }
            });

            contactPanel.add(nameLabel, BorderLayout.CENTER);
            contactPanel.add(deleteBtn, BorderLayout.EAST);
            listPanel.add(contactPanel);
            listPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setPreferredSize(new Dimension(450, 400));

        JButton closeBtn = new JButton("Close");
        closeBtn.setBackground(new Color(255, 114, 118));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFocusPainted(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setFont(new Font("Arial", Font.BOLD, 14));
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(255, 240, 245));
        buttonPanel.add(closeBtn);

        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}