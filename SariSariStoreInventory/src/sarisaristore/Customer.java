package sarisaristore;

/** INHERITANCE: Customer is a Person who buys products. */
public class Customer extends Person {
    public Customer(int id, String name, String contact) {
        super(id, name, contact);
    }

    @Override public String getRole() { return "Customer"; }
}
