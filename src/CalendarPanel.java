import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;
import java.util.List;

/**
 * Calendar Panel Component
 * Displays monthly calendar with birthday indicators
 */

class CalendarPanel extends JPanel {
    private BirthdayManager manager;
    private YearMonth currentMonth;
    private JLabel monthYearLabel, yearLabel, monthNumberLabel;
    private JPanel daysPanel;
    private final Color CORAL = new Color(255, 114, 118);
    private final Color SUNDAY_COLOR = new Color(255, 182, 193);

    public CalendarPanel(BirthdayManager manager) {
        this.manager = manager;
        this.currentMonth = YearMonth.now();
        setLayout(new BorderLayout(0, 20));
        setOpaque(false);
        createHeader();
        createCalendar();
        updateCalendar();
    }

    private void createHeader() {
        RoundedPanel headerPanel = new RoundedPanel(CORAL);
        headerPanel.setLayout(new BorderLayout(20, 0));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        headerPanel.setPreferredSize(new Dimension(800, 120));

        RoundButton prevBtn = new RoundButton("<");
        prevBtn.setPreferredSize(new Dimension(70, 70));
        prevBtn.setBackground(CORAL);
        prevBtn.setForeground(Color.WHITE);
        prevBtn.setFont(new Font("Arial", Font.BOLD, 28));
        prevBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        RoundButton nextBtn = new RoundButton(">");
        nextBtn.setPreferredSize(new Dimension(70, 70));
        nextBtn.setBackground(CORAL);
        nextBtn.setForeground(Color.WHITE);
        nextBtn.setFont(new Font("Arial", Font.BOLD, 28));
        nextBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 100, 0));

        monthNumberLabel = new JLabel("");
        monthNumberLabel.setFont(new Font("Arial", Font.BOLD, 40));
        monthNumberLabel.setForeground(Color.WHITE);

        monthYearLabel = new JLabel("");
        monthYearLabel.setFont(new Font("Anton", Font.BOLD, 50));
        monthYearLabel.setForeground(Color.WHITE);

        yearLabel = new JLabel("");
        yearLabel.setFont(new Font("Arial", Font.BOLD, 40));
        yearLabel.setForeground(Color.WHITE);

        centerPanel.add(monthNumberLabel);
        centerPanel.add(monthYearLabel);
        centerPanel.add(yearLabel);

        headerPanel.add(prevBtn, BorderLayout.WEST);
        headerPanel.add(centerPanel, BorderLayout.CENTER);
        headerPanel.add(nextBtn, BorderLayout.EAST);

        prevBtn.addActionListener(e -> {
            currentMonth = currentMonth.minusMonths(1);
            updateCalendar();
        });
        nextBtn.addActionListener(e -> {
            currentMonth = currentMonth.plusMonths(1);
            updateCalendar();
        });

        add(headerPanel, BorderLayout.NORTH);
    }

    private void createCalendar() {
        JPanel calendarContainer = new JPanel(new BorderLayout(0, 15));
        calendarContainer.setOpaque(false);

        JPanel weekPanel = new JPanel(new GridLayout(1, 7, 15, 0));
        weekPanel.setOpaque(false);
        weekPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        String[] days = {"S", "M", "T", "W", "T", "F", "S"};
        for (int i = 0; i < days.length; i++) {
            JLabel label = new JLabel(days[i], SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 20));
            label.setForeground((i == 0 || i == 6) ? SUNDAY_COLOR : Color.BLACK);
            weekPanel.add(label);
        }

        daysPanel = new JPanel(new GridLayout(0, 7, 15, 15));
        daysPanel.setOpaque(false);
        daysPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        calendarContainer.add(weekPanel, BorderLayout.NORTH);
        calendarContainer.add(daysPanel, BorderLayout.CENTER);
        add(calendarContainer, BorderLayout.CENTER);
    }

    public void updateCalendar() {
        daysPanel.removeAll();
        monthNumberLabel.setText(String.format("%02d",
                currentMonth.getMonthValue()));
        monthYearLabel.setText(currentMonth.getMonth()
                .getDisplayName(TextStyle.FULL, Locale.ENGLISH));
        yearLabel.setText(String.valueOf(currentMonth.getYear()));

        LocalDate firstDay = currentMonth.atDay(1);
        int dayOfWeek = firstDay.getDayOfWeek().getValue() % 7;
        int daysInMonth = currentMonth.lengthOfMonth();
        List<Contact> monthBirthdays = manager.getBirthdaysInMonth(
                currentMonth.getYear(), currentMonth.getMonthValue());

        for (int i = 0; i < dayOfWeek; i++) {
            daysPanel.add(new JLabel(""));
        }

        LocalDate today = LocalDate.now();

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = currentMonth.atDay(day);

            int dow = date.getDayOfWeek().getValue() % 7;
            boolean isWeekend = (dow == 0 || dow == 6);
            boolean hasBirthday = false;
            boolean isToday = date.equals(today);

            for (Contact c : monthBirthdays) {
                if (c.getBirthdate().getDayOfMonth() == day) {
                    hasBirthday = true;
                    break;
                }
            }

            final int dayNum = day;
            final boolean finalHasBirthday = hasBirthday;
            final boolean finalIsToday = isToday;
            final boolean finalIsWeekend = isWeekend;

            JPanel dayPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

                    if (finalIsToday) {
                        g2.setColor(CORAL);
                        int size = Math.min(getWidth(), getHeight()) - 10;
                        int x = (getWidth() - size) / 2;
                        int y = (getHeight() - size) / 2;
                        g2.fillOval(x, y, size, size);
                    }

                    g2.setFont(new Font("Arial", Font.BOLD, 40));
                    g2.setColor(finalIsToday ? Color.WHITE :
                            finalHasBirthday ? CORAL :
                                    finalIsWeekend ? SUNDAY_COLOR : Color.BLACK);

                    String text = String.valueOf(dayNum);
                    FontMetrics fm = g2.getFontMetrics();
                    int textX = (getWidth() - fm.stringWidth(text)) / 2;
                    int textY = ((getHeight() - fm.getHeight()) / 2) +
                            fm.getAscent();
                    g2.drawString(text, textX, textY);

                    if (finalHasBirthday && !finalIsToday) {
                        g2.setStroke(new BasicStroke(3));
                        g2.setColor(CORAL);
                        int size = Math.min(getWidth(), getHeight()) - 10;
                        int x = (getWidth() - size) / 2;
                        int y = (getHeight() - size) / 2;
                        g2.drawOval(x, y, size, size);
                    }

                    g2.dispose();
                }
            };

            dayPanel.setOpaque(false);

            if (hasBirthday) {
                StringBuilder tooltip = new StringBuilder("<html>");
                for (Contact c : monthBirthdays) {
                    if (c.getBirthdate().getDayOfMonth() == day) {
                        tooltip.append("🎂 ").append(c.getName()).append("<br>");
                    }
                }
                tooltip.append("</html>");
                dayPanel.setToolTipText(tooltip.toString());
            }

            daysPanel.add(dayPanel);
        }

        daysPanel.revalidate();
        daysPanel.repaint();
    }
}