import javax.swing.JOptionPane;
import java.io.*;
import java.util.*;
import java.time.LocalDate;
import java.awt.Color;

/**
 * POLYMORPHISM Module
 * Demonstrates:
 * - Interface definitions
 * - Multiple implementations of same interface
 * - Polymorphic behavior through interfaces
 */

// POLYMORPHISM - Interface for notification strategies
interface Notifiable {
    void sendNotification(Contact contact);
}

// Concrete implementation of Notifiable
class PopupNotification implements Notifiable {
    @Override
    public void sendNotification(Contact contact) {
        JOptionPane.showMessageDialog(null,
                "🎉 Birthday Reminder!\n\n" +
                        "Name: " + contact.getName() +
                        "\nDays until birthday: " + contact.getDaysUntilBirthday() +
                        "\nType: " + contact.getRelationshipType(),
                "Birthday Alert",
                JOptionPane.INFORMATION_MESSAGE);
    }
}

// POLYMORPHISM - Interface for storage strategies
interface DataStorage {
    void saveContacts(List<Contact> contacts) throws IOException;
    List<Contact> loadContacts() throws IOException;
}

// Concrete implementation of DataStorage
class FileStorage implements DataStorage {
    private String filename = "birthdays.txt";

    @Override
    public void saveContacts(List<Contact> contacts) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (Contact contact : contacts) {
                String type = "", extra = "";

                if (contact instanceof FamilyMember) {
                    type = "FAMILY";
                } else if (contact instanceof Friend) {
                    type = "FRIEND";
                } else if (contact instanceof Colleague) {
                    type = "COLLEAGUE";
                } else if (contact instanceof CustomContact) {
                    type = "CUSTOM";
                    CustomContact cc = (CustomContact) contact;
                    extra = cc.getCustomType() + "|" + cc.getCustomColor().getRGB();
                }

                writer.println(type + "|" + contact.getName() + "|" +
                        contact.getBirthdate() + "|" + contact.getNotes() +
                        (extra.isEmpty() ? "" : "|" + extra));
            }
        }
    }

    @Override
    public List<Contact> loadContacts() throws IOException {
        List<Contact> contacts = new ArrayList<>();
        File file = new File(filename);
        if (!file.exists()) return contacts;

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 4) {
                    LocalDate birthdate = LocalDate.parse(parts[2]);
                    String name = parts[1], notes = parts[3];

                    switch (parts[0]) {
                        case "FAMILY":
                            contacts.add(new FamilyMember(name, birthdate, notes));
                            break;
                        case "FRIEND":
                            contacts.add(new Friend(name, birthdate, notes));
                            break;
                        case "COLLEAGUE":
                            contacts.add(new Colleague(name, birthdate, notes));
                            break;
                        case "CUSTOM":
                            if (parts.length >= 6) {
                                try {
                                    contacts.add(new CustomContact(name, birthdate, notes,
                                            parts[4], new Color(Integer.parseInt(parts[5]))));
                                } catch (Exception e) {
                                    contacts.add(new CustomContact(name, birthdate, notes,
                                            parts[4], new Color(150, 200, 150)));
                                }
                            }
                            break;
                    }
                }
            }
        }
        return contacts;
    }
}