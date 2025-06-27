package com.hexaware.ordermanagement.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.hexaware.ordermanagement.beans.Clothing;
import com.hexaware.ordermanagement.beans.Electronics;
import com.hexaware.ordermanagement.beans.Product;
import com.hexaware.ordermanagement.beans.User;
import com.hexaware.ordermanagement.dao.IOrderManagementRepository;
import com.hexaware.ordermanagement.dao.OrderProcessor;
import com.hexaware.ordermanagement.exception.OrderNotFoundException;
import com.hexaware.ordermanagement.exception.ProductNotFoundException;
import com.hexaware.ordermanagement.exception.UserNotFoundException;

public class OrderManagement {
    private static final Scanner sc = new Scanner(System.in);
    private static final IOrderManagementRepository repo = new OrderProcessor();

    public static void main(String[] args) {

            System.out.println("\n===== Order Management System =====");
            System.out.println("1. Create User");
            System.out.println("2. Create Product");
            System.out.println("3. Create Order");
            System.out.println("4. Cancel Order");
            System.out.println("5. View All Products");
            System.out.println("6. View Orders By User");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");
            String choice = sc.nextLine();

            switch (choice) {
                case "1": createUser(); break;
                case "2": createProduct(); break;
                case "3": createOrder(); break;
                case "4": cancelOrder(); break;
                case "5": viewAllProducts(); break;
                case "6": viewOrdersByUser(); break;
                case "7":
                    System.out.println("Exiting system.");break;
                default:
                    System.out.println("Invalid choice.");
            }

        sc.close();
    }

    private static void createUser() {
        try {
            System.out.print("Enter username: ");
            String username = sc.nextLine();

            System.out.print("Enter password: ");
            String password = sc.nextLine();

            System.out.print("Enter role (Admin/User): ");
            String role = sc.nextLine();

            User user = new User(0, username, password, role); 

            boolean result = repo.createUser(user);

            System.out.println(result ? "User created successfully." : "Failed to create user.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    private static void createProduct() {
        try {
            System.out.print("Enter Admin User ID: ");
            int adminId = Integer.parseInt(sc.nextLine());

            if (!((OrderProcessor) repo).isAdmin(adminId)) {
                throw new UserNotFoundException("Admin ID " + adminId + " is not a valid Admin.");
            }

            System.out.print("Enter product type (Electronics/Clothing): ");
            String type = sc.nextLine().trim();

            System.out.print("Enter product name: ");
            String name = sc.nextLine();

            System.out.print("Enter description: ");
            String description = sc.nextLine();

            System.out.print("Enter price: ");
            double price = Double.parseDouble(sc.nextLine());

            System.out.print("Enter quantity in stock: ");
            int quantity = Integer.parseInt(sc.nextLine());

            Product product;

            if (type.equalsIgnoreCase("Electronics")) {
                System.out.print("Enter brand: ");
                String brand = sc.nextLine();

                System.out.print("Enter warranty period (months): ");
                int warranty = Integer.parseInt(sc.nextLine());

                product = new Electronics(0, name, description, price, quantity, brand, warranty);

            } else if (type.equalsIgnoreCase("Clothing")) {
                System.out.print("Enter size: ");
                String size = sc.nextLine();

                System.out.print("Enter color: ");
                String color = sc.nextLine();

                product = new Clothing(0, name, description, price, quantity, size, color);

            } else {
                System.out.println("Invalid product type.");
                return;
            }

            User admin = new User();
            admin.setUserId(adminId);

            boolean result = repo.createProduct(admin, product);

            if (result) {
                System.out.println("Product created successfully.");
            } else {
                System.out.println("Failed to create product.");
            }

        } catch (UserNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        }
    }



private static void createOrder() {
    try {
        System.out.print("Enter User ID to place order: ");
        int userId = Integer.parseInt(sc.nextLine());

        if (!((OrderProcessor) repo).isValidUser(userId)) {
            throw new UserNotFoundException("User ID not found or not a valid User.");
        }

        List<Product> allProducts = repo.getAllProducts();
        if (allProducts.isEmpty()) {
            System.out.println("No products available.");
            return;
        }

        System.out.println("Available Products:");
        for (Product p : allProducts) {
            System.out.println(p.getProductId() + " - " + p.getProductName());
        }

        System.out.print("Enter number of products to order: ");
        int count = Integer.parseInt(sc.nextLine());

        List<Product> productList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            System.out.print("Enter Product ID for item " + (i + 1) + ": ");
            int pid = Integer.parseInt(sc.nextLine());

            if (!((OrderProcessor) repo).isValidProduct(pid)) {
                throw new ProductNotFoundException("Product ID " + pid + " does not exist.");
            }

            Product p = new Product();
            p.setProductId(pid);
            productList.add(p);
        }

        User user = new User();
        user.setUserId(userId);

        boolean result = repo.createOrder(user, productList);

        if (result) {
            System.out.println("Order placed successfully.");
        } else {
            System.out.println("Order placement failed.");
        }

    } catch (UserNotFoundException | ProductNotFoundException e) {
        System.out.println("Error: " + e.getMessage());
    } catch (Exception e) {
        System.out.println("Unexpected error: " + e.getMessage());
    }
}


private static void cancelOrder() {
    try {
        System.out.print("Enter User ID: ");
        int userId = Integer.parseInt(sc.nextLine());

        if (!((OrderProcessor) repo).isValidUser(userId)) {
            throw new UserNotFoundException("User ID " + userId + " is not a valid user.");
        }

        List<Map<String, Object>> orderList = ((OrderProcessor) repo).getOrdersByUser(userId);

        if (orderList.isEmpty()) {
            throw new OrderNotFoundException("No orders found for user ID " + userId);
        }

        System.out.println("Your Orders:");
        for (Map<String, Object> order : orderList) {
            System.out.println("Order ID: " + order.get("orderId") +
                               " | Product ID: " + order.get("productId") +
                               " | Product Name: " + order.get("productName"));
        }

        System.out.print("Enter Order ID to cancel: ");
        int orderId = Integer.parseInt(sc.nextLine());

        if (!((OrderProcessor) repo).isValidOrder(userId, orderId)) {
            throw new OrderNotFoundException("Order ID " + orderId + " does not belong to user ID " + userId);
        }

        boolean result = repo.cancelOrder(userId, orderId);
        System.out.println(result ? "Order cancelled." : "Failed to cancel order.");

    } catch (UserNotFoundException | OrderNotFoundException e) {
        System.out.println("Error: " + e.getMessage());
    } catch (Exception e) {
        System.out.println("Unexpected error: " + e.getMessage());
    }
}



    private static void viewAllProducts() {
        try {
            List<Product> products = repo.getAllProducts();
            if (products.isEmpty()) {
                System.out.println("No products found.");
            } else {
                for (Product p : products) {
                    System.out.println(p.getProductId() + " | " + p.getProductName() + " | " +
                            p.getPrice() + " | " + p.getType());
                }
            }
        } catch (Exception e) {
            System.out.println("Error fetching products: " + e.getMessage());
        }
    }

    private static void viewOrdersByUser() {
        try {
            System.out.print("Enter User ID to view orders: ");
            int userId = Integer.parseInt(sc.nextLine());

            if (!((OrderProcessor) repo).isValidUser(userId)) {
                throw new UserNotFoundException("User ID " + userId + " is not a valid user.");
            }

            List<Map<String, Object>> orderList = ((OrderProcessor) repo).getOrdersByUser(userId);

            if (orderList.isEmpty()) {
                System.out.println("No orders found for this user.");
            } else {
                System.out.println("Orders placed by User ID " + userId + ":");
                for (Map<String, Object> order : orderList) {
                    System.out.println("Order ID: " + order.get("orderId") +
                                       " | Product ID: " + order.get("productId") +
                                       " | Product Name: " + order.get("productName"));
                }
            }

        } catch (UserNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        }
    }

}
