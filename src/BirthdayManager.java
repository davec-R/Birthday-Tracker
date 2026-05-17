import java.io.IOException;
import java.util.*;

/**
 * ENCAPSULATION Module
 * Demonstrates:
 * - Data hiding (private fields)
 * - Public interface for interaction
 * - Controlled access to internal state
 * - Dependency injection through interfaces
 */

class BirthdayManager {
    // ENCAPSULATION - Private data members
    private List<Contact> contacts;
    private DataStorage storage;
    private Notifiable notifier;

    // Constructor with dependency injection (POLYMORPHISM)
    public BirthdayManager() {
        contacts = new ArrayList<>();
        storage = new FileStorage();  // Can be swapped with other implementations
        notifier = new PopupNotification();  // Can be swapped with other implementations
        loadData();
    }

    // Public methods - Controlled access to private data
    public void addContact(Contact contact) {
        contacts.add(contact);
        saveData();
    }

    public void removeContact(Contact contact) {
        contacts.remove(contact);
        saveData();
    }

    // Returns a copy to prevent external modification (ENCAPSULATION)
    public List<Contact> getContacts() {
        return new ArrayList<>(contacts);
    }

    // Business logic methods
    public List<Contact> getContactsByType(String type) {
        List<Contact> filtered = new ArrayList<>();
        for (Contact c : contacts) {
            if (c.getRelationshipType().equalsIgnoreCase(type)) {
                filtered.add(c);
            }
        }
        filtered.sort(Comparator.comparingInt(Contact::getDaysUntilBirthday));
        return filtered;
    }

    public Set<String> getAllCustomTypes() {
        Set<String> types = new HashSet<>();
        for (Contact c : contacts) {
            if (c instanceof CustomContact) {
                types.add(c.getRelationshipType());
            }
        }
        return types;
    }

    public List<Contact> getBirthdaysInMonth(int year, int month) {
        List<Contact> monthBirthdays = new ArrayList<>();
        for (Contact c : contacts) {
            if (c.getBirthdate().getMonthValue() == month) {
                monthBirthdays.add(c);
            }
        }
        return monthBirthdays;
    }

    // Private helper methods - Internal implementation hidden
    private void saveData() {
        try {
            storage.saveContacts(contacts);
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
        }
    }

    private void loadData() {
        try {
            contacts = storage.loadContacts();
        } catch (IOException e) {
            System.err.println("Error loading data: " + e.getMessage());
        }
    }

    // Method using the notifier interface (POLYMORPHISM)
    public void notifyBirthday(Contact contact) {
        notifier.sendNotification(contact);
    }
}