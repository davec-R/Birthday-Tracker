import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;

/**
 * Main Application GUI
 * Integrates all components and provides main interface
 */

class WelcomeScreen extends JFrame {
    private final Color LIGHT_PINK = new Color(255, 240, 245);
    private final Color CORAL = new Color(255, 114, 118);

    public WelcomeScreen() {
        setTitle("Birthday Tracker - Welcome");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900);
        setLayout(new BorderLayout());
        getContentPane().setBackground(LIGHT_PINK);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        JLabel titleLabel = new JLabel("Birthday Tracker", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Edwardian Script ITC", Font.PLAIN, 100));
        titleLabel.setForeground(CORAL);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Never forget a special day again!",
                SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Palatino Linotype", Font.PLAIN, 22));
        subtitleLabel.setForeground(new Color(100, 100, 100));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        RoundButton startButton = new RoundButton("Get Started", 30);
        startButton.setFont(new Font("Arial", Font.BOLD, 28));
        startButton.setForeground(Color.WHITE);
        startButton.setBackground(CORAL);
        startButton.setFocusPainted(false);
        startButton.setBorderPainted(false);
        startButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.setMaximumSize(new Dimension(250, 60));
        startButton.addActionListener(e -> {
            this.dispose();
            SwingUtilities.invokeLater(() ->
                    new BirthdayTrackerGUI().setVisible(true));
        });

        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(titleLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        centerPanel.add(subtitleLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 60)));
        centerPanel.add(startButton);
        centerPanel.add(Box.createVerticalGlue());

        add(centerPanel, BorderLayout.CENTER);
        setLocationRelativeTo(null);
    }
}

class BirthdayTrackerGUI extends JFrame {
    private BirthdayManager manager;
    private CalendarPanel calendarPanel;
    private JPanel rightPanel;
    private Map<String, Color> customTypeColors;
    private final Color PINK_BG = new Color(255, 192, 203);
    private final Color LIGHT_PINK = new Color(255, 240, 245);
    private final Color CORAL = new Color(255, 114, 118);

    public BirthdayTrackerGUI() {
        manager = new BirthdayManager();
        customTypeColors = new HashMap<>();
        initializeUI();
        refreshCategoryButtons();
    }

    private void initializeUI() {
        setTitle("Birthday Tracker");
        setSize(1400, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(LIGHT_PINK);

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(PINK_BG);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        JLabel titleLabel = new JLabel("Birthday Tracker ",
                SwingConstants.CENTER);
        titleLabel.setFont(new Font("edwardian script itc", Font.BOLD, 100));
        titleLabel.setForeground(CORAL);

        JPanel titleTextPanel = new JPanel();
        titleTextPanel.setLayout(new BoxLayout(titleTextPanel, BoxLayout.Y_AXIS));
        titleTextPanel.setOpaque(false);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleTextPanel.add(titleLabel);
        titlePanel.add(titleTextPanel);
        add(titlePanel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout(40, 0));
        mainPanel.setBackground(LIGHT_PINK);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 50, 50));

        calendarPanel = new CalendarPanel(manager);
        mainPanel.add(calendarPanel, BorderLayout.CENTER);

        rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setOpaque(false);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(80, 0, 50, 0));
        mainPanel.add(rightPanel, BorderLayout.EAST);

        add(mainPanel, BorderLayout.CENTER);
        setLocationRelativeTo(null);
    }

    public void refreshCategoryButtons() {
        rightPanel.removeAll();

        CategoryButton friendBtn = new CategoryButton("FRIEND'S BIRTHDAY",
                new Color(239, 127, 183, 255), manager, "Friend", false, this);
        CategoryButton familyBtn = new CategoryButton("FAMILY BIRTHDAY",
                new Color(255, 232, 168), manager, "Family", false, this);
        CategoryButton colleagueBtn = new CategoryButton("COLLEAGUE'S BIRTHDAY",
                new Color(190, 203, 217), manager, "Colleague", false, this);

        rightPanel.add(friendBtn);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        rightPanel.add(familyBtn);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        rightPanel.add(colleagueBtn);

        for (String customType : manager.getAllCustomTypes()) {
            rightPanel.add(Box.createRigidArea(new Dimension(0, 30)));
            Color btnColor = customTypeColors.getOrDefault(customType,
                    new Color(150, 200, 150));
            for (Contact c : manager.getContacts()) {
                if (c instanceof CustomContact &&
                        c.getRelationshipType().equals(customType)) {
                    btnColor = c.getTypeColor();
                    customTypeColors.put(customType, btnColor);
                    break;
                }
            }
            CategoryButton customBtn = new CategoryButton(
                    customType.toUpperCase() + " BIRTHDAY",
                    btnColor, manager, customType, true, this);
            rightPanel.add(customBtn);
        }

        rightPanel.add(Box.createVerticalGlue());
        rightPanel.add(Box.createRigidArea(new Dimension(0, 50)));

        RoundButton addBtn = new RoundButton("+");
        addBtn.setPreferredSize(new Dimension(90, 90));
        addBtn.setMaximumSize(new Dimension(90, 90));
        addBtn.setBackground(CORAL);
        addBtn.setForeground(Color.WHITE);
        addBtn.setFont(new Font("Arial", Font.BOLD, 50));
        addBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        addBtn.addActionListener(e -> showAddDialog());

        rightPanel.add(addBtn);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 50)));
        rightPanel.revalidate();
        rightPanel.repaint();
    }

    public void refreshCalendar() {
        calendarPanel.updateCalendar();
    }

    private void showAddDialog() {
        JDialog dialog = new JDialog(this, "Add Birthday", true);
        dialog.setLayout(new BorderLayout(20, 20));
        dialog.getContentPane().setBackground(LIGHT_PINK);

        JPanel formPanel = new JPanel(new GridLayout(6, 1, 10, 15));
        formPanel.setBackground(LIGHT_PINK);
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 20, 30));

        JTextField nameField = new JTextField(20);
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        datePanel.setOpaque(false);

        String[] months = {"January", "February", "March", "April", "May",
                "June", "July", "August", "September", "October", "November",
                "December"};
        JComboBox<String> monthCombo = new JComboBox<>(months);
        monthCombo.setSelectedIndex(0);

        Integer[] days = new Integer[31];
        for (int i = 0; i < 31; i++) days[i] = i + 1;
        JComboBox<Integer> dayCombo = new JComboBox<>(days);

        Integer[] years = new Integer[100];
        int currentYear = LocalDate.now().getYear();
        for (int i = 0; i < 100; i++) years[i] = currentYear - i;
        JComboBox<Integer> yearCombo = new JComboBox<>(years);
        yearCombo.setSelectedIndex(25);

        monthCombo.addActionListener(e ->
                updateDaysInMonth(monthCombo, dayCombo, yearCombo));
        yearCombo.addActionListener(e ->
                updateDaysInMonth(monthCombo, dayCombo, yearCombo));

        datePanel.add(new JLabel("Month:"));
        datePanel.add(monthCombo);
        datePanel.add(new JLabel("Day:"));
        datePanel.add(dayCombo);
        datePanel.add(new JLabel("Year:"));
        datePanel.add(yearCombo);

        JTextArea notesArea = new JTextArea(2, 20);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);

        String[] types = {"Friend", "Family", "Colleague", "Custom..."};
        JComboBox<String> typeCombo = new JComboBox<>(types);

        JTextField customTypeField = new JTextField(20);
        customTypeField.setEnabled(false);

        JButton colorButton = new JButton("Choose Color");
        colorButton.setEnabled(false);
        colorButton.setBackground(new Color(150, 200, 150));
        final Color[] selectedColor = {new Color(150, 200, 150)};

        colorButton.addActionListener(e -> {
            Color color = JColorChooser.showDialog(dialog,
                    "Choose Category Color", selectedColor[0]);
            if (color != null) {
                selectedColor[0] = color;
                colorButton.setBackground(color);
            }
        });

        typeCombo.addActionListener(e -> {
            boolean isCustom = typeCombo.getSelectedItem().equals("Custom...");
            customTypeField.setEnabled(isCustom);
            colorButton.setEnabled(isCustom);
        });

        formPanel.add(createLabel("Name:", nameField));
        formPanel.add(createLabel("Birthdate:", datePanel));
        formPanel.add(createLabel("Type:", typeCombo));
        formPanel.add(createLabel("Custom Type:", customTypeField));
        formPanel.add(createLabel("Custom Color:", colorButton));
        formPanel.add(createLabel("Notes:", new JScrollPane(notesArea)));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(LIGHT_PINK);

        JButton saveBtn = createStyledButton("Save");
        JButton cancelBtn = createStyledButton("Cancel");

        saveBtn.addActionListener(e -> {
            try {
                int month = monthCombo.getSelectedIndex() + 1;
                int day = (Integer) dayCombo.getSelectedItem();
                int year = (Integer) yearCombo.getSelectedItem();

                LocalDate birthdate = LocalDate.of(year, month, day);
                String name = nameField.getText();
                String notes = notesArea.getText();
                String selectedType = (String) typeCombo.getSelectedItem();

                if (name.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog,
                            "Please enter a name!");
                    return;
                }

                Contact contact = null;

                if (selectedType.equals("Custom...")) {
                    String customType = customTypeField.getText().trim();
                    if (customType.isEmpty()) {
                        JOptionPane.showMessageDialog(dialog,
                                "Please enter a custom type!");
                        return;
                    }
                    contact = new CustomContact(name, birthdate, notes,
                            customType, selectedColor[0]);
                    customTypeColors.put(customType, selectedColor[0]);
                } else if (selectedType.equals("Family")) {
                    contact = new FamilyMember(name, birthdate, notes);
                } else if (selectedType.equals("Friend")) {
                    contact = new Friend(name, birthdate, notes);
                } else if (selectedType.equals("Colleague")) {
                    contact = new Colleague(name, birthdate, notes);
                }

                if (contact != null) {
                    manager.addContact(contact);
                    calendarPanel.updateCalendar();
                    refreshCategoryButtons();
                    dialog.dispose();
                    JOptionPane.showMessageDialog(this, "Birthday added! 🎉");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Error: Invalid date. " + ex.getMessage());
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void updateDaysInMonth(JComboBox<String> monthCombo,
                                   JComboBox<Integer> dayCombo,
                                   JComboBox<Integer> yearCombo) {
        int month = monthCombo.getSelectedIndex() + 1;
        int year = (Integer) yearCombo.getSelectedItem();
        YearMonth yearMonth = YearMonth.of(year, month);
        int daysInMonth = yearMonth.lengthOfMonth();

        int currentDay = (Integer) dayCombo.getSelectedItem();
        dayCombo.removeAllItems();
        for (int i = 1; i <= daysInMonth; i++) {
            dayCombo.addItem(i);
        }
        if (currentDay <= daysInMonth) {
            dayCombo.setSelectedItem(currentDay);
        } else {
            dayCombo.setSelectedItem(daysInMonth);
        }
    }

    private JPanel createLabel(String text, JComponent component) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(LIGHT_PINK);
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 13));
        panel.add(label, BorderLayout.NORTH);
        panel.add(component, BorderLayout.CENTER);
        return panel;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(CORAL);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 40));
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new WelcomeScreen().setVisible(true));
    }
}