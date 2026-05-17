import java.awt.Color;
import java.time.LocalDate;
import java.time.Period;

/**
 * ABSTRACTION & INHERITANCE Module
 * Demonstrates:
 * - Abstract class with common behavior
 * - Inheritance hierarchy
 * - Method overriding
 * - Polymorphism through abstract methods
 */

// Abstract base class - ABSTRACTION
abstract class Contact {
    private String name, notes;
    private LocalDate birthdate;

    public Contact(String name, LocalDate birthdate, String notes) {
        this.name = name;
        this.birthdate = birthdate;
        this.notes = notes;
    }

    // Getters and Setters - ENCAPSULATION
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public LocalDate getBirthdate() { return birthdate; }
    public void setBirthdate(LocalDate birthdate) { this.birthdate = birthdate; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    // Abstract methods - ABSTRACTION
    public abstract String getRelationshipType();
    public abstract Color getTypeColor();

    // Common behavior - INHERITANCE
    public int getAge() {
        return Period.between(birthdate, LocalDate.now()).getYears();
    }

    public int getDaysUntilBirthday() {
        LocalDate today = LocalDate.now();
        LocalDate nextBirthday = birthdate.withYear(today.getYear());
        if (nextBirthday.isBefore(today) || nextBirthday.isEqual(today)) {
            nextBirthday = nextBirthday.plusYears(1);
        }
        return (int) java.time.temporal.ChronoUnit.DAYS.between(today, nextBirthday);
    }
}

// INHERITANCE - Concrete subclasses
class FamilyMember extends Contact {
    public FamilyMember(String name, LocalDate birthdate, String notes) {
        super(name, birthdate, notes);
    }

    @Override
    public String getRelationshipType() { return "Family"; }

    @Override
    public Color getTypeColor() { return new Color(255, 232, 168); }
}

class Friend extends Contact {
    public Friend(String name, LocalDate birthdate, String notes) {
        super(name, birthdate, notes);
    }

    @Override
    public String getRelationshipType() { return "Friend"; }

    @Override
    public Color getTypeColor() { return new Color(239, 127, 183); }
}

class Colleague extends Contact {
    public Colleague(String name, LocalDate birthdate, String notes) {
        super(name, birthdate, notes);
    }

    @Override
    public String getRelationshipType() { return "Colleague"; }

    @Override
    public Color getTypeColor() { return new Color(179, 201, 255); }
}

// INHERITANCE with additional ENCAPSULATION
class CustomContact extends Contact {
    private String customType;
    private Color customColor;

    public CustomContact(String name, LocalDate birthdate, String notes,
                         String customType, Color customColor) {
        super(name, birthdate, notes);
        this.customType = customType;
        this.customColor = customColor;
    }

    @Override
    public String getRelationshipType() { return customType; }

    @Override
    public Color getTypeColor() { return customColor; }

    public String getCustomType() { return customType; }
    public Color getCustomColor() { return customColor; }
}