import java.util.ArrayList;
import java.util.List;

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
