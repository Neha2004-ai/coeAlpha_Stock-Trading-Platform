
import java.util.ArrayList;
import java.util.Scanner;

// Stock Class
class Stock {
    String symbol;
    String company;
    double price;

    Stock(String symbol, String company, double price) {
        this.symbol = symbol;
        this.company = company;
        this.price = price;
    }
}

// Transaction Class
class Transaction {
    String type;
    String symbol;
    int quantity;
    double amount;

    Transaction(String type, String symbol, int quantity, double amount) {
        this.type = type;
        this.symbol = symbol;
        this.quantity = quantity;
        this.amount = amount;
    }
}

// User Class
class User {
    double balance = 100000;
    ArrayList<Stock> portfolioStocks = new ArrayList<>();
    ArrayList<Integer> quantities = new ArrayList<>();
    ArrayList<Double> buyPrices = new ArrayList<>();
    ArrayList<Transaction> history = new ArrayList<>();
}

// Main Class
public class code2{

    static Scanner sc = new Scanner(System.in);

    static ArrayList<Stock> market = new ArrayList<>();
    static User user = new User();

    // Add Stocks to Market
    static void initializeMarket() {
        market.add(new Stock("TCS", "Tata Consultancy", 3500));
        market.add(new Stock("INFY", "Infosys", 1800));
        market.add(new Stock("WIPRO", "Wipro", 500));
        market.add(new Stock("HDFC", "HDFC Bank", 1600));
        market.add(new Stock("RELIANCE", "Reliance", 2900));
    }

    // Find Stock
    static Stock findStock(String symbol) {
        for (Stock s : market) {
            if (s.symbol.equalsIgnoreCase(symbol)) {
                return s;
            }
        }
        return null;
    }

    // Find Stock in Portfolio
    static int findPortfolioIndex(String symbol) {
        for (int i = 0; i < user.portfolioStocks.size(); i++) {
            if (user.portfolioStocks.get(i).symbol.equalsIgnoreCase(symbol)) {
                return i;
            }
        }
        return -1;
    }

    // Display Market
    static void displayMarket() {
        System.out.println("\n----- STOCK MARKET -----");
        System.out.printf("%-12s %-22s %s%n", "Symbol", "Company", "Price");

        for (Stock s : market) {
            System.out.printf("%-12s %-22s Rs. %.2f%n",
                    s.symbol, s.company, s.price);
        }
    }

    // Buy Stock
    static void buyStock() {
        System.out.print("Enter stock symbol: ");
        String symbol = sc.nextLine().trim();

        Stock stock = findStock(symbol);

        if (stock == null) {
            System.out.println("Stock not found!");
            return;
        }

        System.out.print("Enter quantity: ");

        if (!sc.hasNextInt()) {
            System.out.println("Please enter a valid quantity!");
            sc.nextLine();
            return;
        }

        int quantity = sc.nextInt();
        sc.nextLine();

        if (quantity <= 0) {
            System.out.println("Quantity must be greater than zero!");
            return;
        }

        double cost = stock.price * quantity;

        if (cost > user.balance) {
            System.out.println("Insufficient balance!");
            return;
        }

        int index = findPortfolioIndex(symbol);

        if (index == -1) {
            user.portfolioStocks.add(stock);
            user.quantities.add(quantity);
            user.buyPrices.add(stock.price);
        } else {
            int oldQuantity = user.quantities.get(index);
            double oldCost = user.buyPrices.get(index) * oldQuantity;

            int newQuantity = oldQuantity + quantity;
            double averagePrice = (oldCost + cost) / newQuantity;

            user.quantities.set(index, newQuantity);
            user.buyPrices.set(index, averagePrice);
        }

        user.balance -= cost;

        user.history.add(new Transaction("BUY", symbol, quantity, cost));

        System.out.println("Stock purchased successfully!");
        System.out.printf("Total Cost: Rs. %.2f%n", cost);
    }

    // Sell Stock
    static void sellStock() {
        System.out.print("Enter stock symbol: ");
        String symbol = sc.nextLine().trim();

        int index = findPortfolioIndex(symbol);

        if (index == -1) {
            System.out.println("You do not own this stock!");
            return;
        }

        System.out.print("Enter quantity to sell: ");

        if (!sc.hasNextInt()) {
            System.out.println("Please enter a valid quantity!");
            sc.nextLine();
            return;
        }

        int quantity = sc.nextInt();
        sc.nextLine();

        int owned = user.quantities.get(index);

        if (quantity <= 0 || quantity > owned) {
            System.out.println("Invalid quantity!");
            return;
        }

        Stock stock = user.portfolioStocks.get(index);

        double sellingAmount = stock.price * quantity;

        user.balance += sellingAmount;

        int remaining = owned - quantity;

        if (remaining == 0) {
            user.portfolioStocks.remove(index);
            user.quantities.remove(index);
            user.buyPrices.remove(index);
        } else {
            user.quantities.set(index, remaining);
        }

        user.history.add(
                new Transaction("SELL", symbol, quantity, sellingAmount)
        );

        System.out.println("Stock sold successfully!");
        System.out.printf("Amount Received: Rs. %.2f%n", sellingAmount);
    }

    // Display Portfolio
    static void displayPortfolio() {
        System.out.println("\n----- MY PORTFOLIO -----");

        if (user.portfolioStocks.isEmpty()) {
            System.out.println("No stocks purchased yet.");
            return;
        }

        System.out.printf("%-12s %-10s %-15s %-15s%n",
                "Symbol", "Quantity", "Current Price", "Total Value");

        for (int i = 0; i < user.portfolioStocks.size(); i++) {
            Stock s = user.portfolioStocks.get(i);
            int quantity = user.quantities.get(i);

            System.out.printf("%-12s %-10d Rs. %-12.2f Rs. %.2f%n",
                    s.symbol, quantity, s.price,
                    s.price * quantity);
        }

        System.out.printf("Available Balance: Rs. %.2f%n", user.balance);
    }

    // Portfolio Performance
    static void portfolioPerformance() {
        double investmentValue = 0;

        for (int i = 0; i < user.portfolioStocks.size(); i++) {
            Stock s = user.portfolioStocks.get(i);
            int quantity = user.quantities.get(i);

            investmentValue += s.price * quantity;
        }

        double totalValue = user.balance + investmentValue;

        double profitLoss = totalValue - 100000;

        System.out.println("\n----- PORTFOLIO PERFORMANCE -----");

        System.out.printf("Available Balance: Rs. %.2f%n", user.balance);
        System.out.printf("Stock Value: Rs. %.2f%n", investmentValue);
        System.out.printf("Total Portfolio Value: Rs. %.2f%n", totalValue);
        System.out.printf("Profit / Loss: Rs. %.2f%n", profitLoss);

        if (profitLoss > 0) {
            System.out.println("Status: PROFIT");
        } else if (profitLoss < 0) {
            System.out.println("Status: LOSS");
        } else {
            System.out.println("Status: NO PROFIT, NO LOSS");
        }
    }

    // Transaction History
    static void transactionHistory() {
        System.out.println("\n----- TRANSACTION HISTORY -----");

        if (user.history.isEmpty()) {
            System.out.println("No transactions available.");
            return;
        }

        System.out.printf("%-10s %-12s %-10s %s%n",
                "Type", "Symbol", "Quantity", "Amount");

        for (Transaction t : user.history) {
            System.out.printf("%-10s %-12s %-10d Rs. %.2f%n",
                    t.type, t.symbol, t.quantity, t.amount);
        }
    }

    // Update Stock Price (Simulation)
    static void updatePrice() {
        System.out.print("Enter stock symbol: ");
        String symbol = sc.nextLine().trim();

        Stock stock = findStock(symbol);

        if (stock == null) {
            System.out.println("Stock not found!");
            return;
        }

        System.out.print("Enter new price: ");

        if (!sc.hasNextDouble()) {
            System.out.println("Please enter a valid price!");
            sc.nextLine();
            return;
        }

        double price = sc.nextDouble();
        sc.nextLine();

        if (price <= 0) {
            System.out.println("Price must be greater than zero!");
            return;
        }

        stock.price = price;

        System.out.println("Stock price updated successfully!");
    }

    // Main Method
    public static void main(String[] args) {

        initializeMarket();

        int choice = 0;

        do {
            System.out.println("\n================================");
            System.out.println("     STOCK TRADING PLATFORM");
            System.out.println("================================");

            System.out.println("1. Display Stock Market");
            System.out.println("2. Buy Stocks");
            System.out.println("3. Sell Stocks");
            System.out.println("4. View My Portfolio");
            System.out.println("5. Portfolio Performance");
            System.out.println("6. Transaction History");
            System.out.println("7. Update Stock Price");
            System.out.println("8. Exit");

            System.out.print("\nEnter your choice: ");

            if (!sc.hasNextInt()) {
                System.out.println("Please enter a valid menu number!");
                sc.nextLine();
                continue;
            }

            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    displayMarket();
                    break;

                case 2:
                    buyStock();
                    break;

                case 3:
                    sellStock();
                    break;

                case 4:
                    displayPortfolio();
                    break;

                case 5:
                    portfolioPerformance();
                    break;

                case 6:
                    transactionHistory();
                    break;

                case 7:
                    updatePrice();
                    break;

                case 8:
                    System.out.println("Thank you for using Stock Trading Platform!");
                    break;

                default:
                    System.out.println("Invalid choice! Please try again.");
            }

        } while (choice != 8);

        sc.close();
    }
}
