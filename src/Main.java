import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Product cheese = new ShippedProduct("Cheese 400g", 200, 5, 0.4, false);
        Product biscuits = new ShippedProduct("Biscuits 700g", 150, 5, 0.7, true);
        Product mobileCard = new Product("Mobile Card", 50, 10); //I supposed that mobile card doesn't need any money for shipping

        Customer customer = new Customer(1000);

        customer.addToCart(cheese, 2);
        customer.addToCart(biscuits, 1);
        customer.addToCart(mobileCard, 3);

        customer.checkout();
    }
}

class Product {
    String name;
    double price;
    int quantity;

    public Product(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    boolean isOutOfStock(int requestedQuantity) {
        return requestedQuantity > this.quantity;
    }
}

class ExpirableProduct extends Product {
    boolean isExpired;

    ExpirableProduct(String name, double price, int quantity, boolean isExpired) {
        super(name, price, quantity);
        this.isExpired = isExpired;
    }

    boolean checkExpiredOrNot() {
        return isExpired;
    }
}

class ShippedProduct extends ExpirableProduct implements Shippable {
    double weight;

    ShippedProduct(String name, double price, int quantity, double weight, boolean isExpired) {
        super(name, price, quantity, isExpired);
        this.weight = weight;
    }

    @Override
    public double getWeight() {
        return weight;
    }

    @Override
    public String getName() {
        return name;
    }
}


class CartItem {
    Product product;
    int quantity;

    CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }
}

interface Shippable {
    String getName();
    double getWeight();
}

class ShippingService {
    public static void processShipment(List<Shippable> items) {
        System.out.println("Items being shipped:");
        for (Shippable item : items) {
            System.out.println("- " + item.getName() + " (Weight: " + item.getWeight() + " kg)");
        }
        System.out.println("Shipment sent successfully");
    }
}
