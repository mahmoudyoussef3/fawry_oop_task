import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Product cheese = new ShippedProduct("Cheese 400g", 200, 5, 0.4, false);
        Product biscuits = new ShippedProduct("Biscuits 700g", 150, 5, 0.7, false);
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


class Customer {
    double balance;
    List<CartItem> cart;

    Customer(double balance) {
        this.balance = balance;
        this.cart = new ArrayList<>();
    }

    void addToCart(Product product, int quantity) {
        if (product.isOutOfStock(quantity)) {
            System.out.println("Not enough stock: " + product.name);
            return;
        }

        if (product instanceof ExpirableProduct && ((ExpirableProduct) product).checkExpiredOrNot()) {
            System.out.println("This product is expired: " + product.name);
            return;
        }

        cart.add(new CartItem(product, quantity));
        product.quantity -= quantity;
        System.out.println("Added to cart: " + product.name + " (Quantity: " + quantity + ")");
    }

    void checkout() {
        if (cart.isEmpty()) {
            System.out.println("Your cart is empty.");
            return;
        }

        double subtotal = 0;
        double totalWeight = 0;

        System.out.println("** Order Summary **");
        for (CartItem item : cart) {
            Product product = item.product;
            System.out.println(product.name + " - Quantity: " + item.quantity + " - Price: $" + product.price);
            subtotal += product.price * item.quantity;

            if (product instanceof ShippedProduct) {
                totalWeight += ((ShippedProduct) product).getWeight() * item.quantity;
            }
        }

        double shippingFees = totalWeight * 10; //I supposed that he must pay 10$ for 1kg
        double totalAmount = subtotal + shippingFees;

        System.out.println("----------------------");
        System.out.println("Subtotal: " + subtotal);
        System.out.println("Shipping: " + shippingFees);
        System.out.println("Total Amount: " + totalAmount);

        if (balance < totalAmount) {
            System.out.println("U have not enough money, Your current balance is: " + balance);
        } else {
            balance -= totalAmount;
            System.out.println("Payment successful, Ur new balance: " + balance);
            sendToShippingService();
        }
    }

    private void sendToShippingService() {
        List<Shippable> shippableItems = new ArrayList<>();
        for (CartItem item : cart) {
            if (item.product instanceof ShippedProduct) {
                shippableItems.add((ShippedProduct) item.product);
            }
        }

        if (!shippableItems.isEmpty()) {
            System.out.println("** Shipping Order **");
            ShippingService.processShipment(shippableItems);
        }
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
