package sarisaristore;

/** INHERITANCE: Staff is a Person who has a position (e.g. Cashier). */
public class Staff extends Person {
    private String position;

    public Staff(int id, String name, String contact, String position) {
        super(id, name, contact);
        this.position = position;
    }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    @Override public String getRole() { return position; }
    @Override public String toString() { return getName() + " (" + position + ")"; }
}
