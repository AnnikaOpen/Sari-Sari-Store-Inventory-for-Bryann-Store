package sarisaristore;

public class Product {
    private int id;
    private String name, category;
    private double price;
    private int stock;

    public Product(int id, String name, String category, double price, int stock) {
        this.id = id; this.name = name; this.category = category;
        this.price = price; this.stock = stock;
    }
    public int getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }
    public void setId(int i) { id = i; }
    public void setName(String n) { name = n; }
    public void setCategory(String c) { category = c; }
    public void setPrice(double p) { price = p; }
    public void setStock(int s) { stock = s; }

    @Override public String toString() {
        return name + " - \u20B1" + String.format("%.2f", price) + "  (" + stock + " left)";
    }
}
