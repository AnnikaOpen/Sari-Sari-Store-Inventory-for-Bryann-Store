package sarisaristore;

/** ABSTRACTION: a general person in the store. Staff and Customer extend it. */
public abstract class Person {
    private int id;
    private String name;
    private String contact;

    protected Person(int id, String name, String contact) {
        this.id = id;
        this.name = name;
        this.contact = contact == null ? "" : contact;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getContact() { return contact; }
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setContact(String contact) { this.contact = contact; }

    /** Each subclass answers differently (POLYMORPHISM). */
    public abstract String getRole();

    @Override public String toString() { return name; }
}
