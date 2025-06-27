package com.hexaware.ordermanagement.dao;

import java.sql.*;
import java.util.*;

import com.hexaware.ordermanagement.beans.*;
import com.hexaware.ordermanagement.exception.*;
import com.hexaware.ordermanagement.util.DBConnUtil;

public class OrderProcessor implements IOrderManagementRepository {

    private Connection conn;

    public OrderProcessor() {
        conn = DBConnUtil.getDBConn();
    }

    @Override
    public boolean createUser(User user) {
        try {
            String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getRole());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    user.setUserId(rs.getInt(1));
                    System.out.println("Generated User ID: " + user.getUserId());
                }
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Error in createUser: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean createProduct(User adminUser, Product product) throws UserNotFoundException {
        if (!isAdmin(adminUser.getUserId())) {
            throw new UserNotFoundException("Provided user ID is not an admin.");
        }

        String sql = "INSERT INTO products (productName, description, price, quantityInStock, type, brand, warrantyPeriod, size, color) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, product.getProductName());
            ps.setString(2, product.getDescription());
            ps.setDouble(3, product.getPrice());
            ps.setInt(4, product.getQuantityInStock());
            ps.setString(5, product.getType());

            if (product instanceof Electronics) {
                Electronics e = (Electronics) product;
                ps.setString(6, e.getBrand());
                ps.setInt(7, e.getWarrantyPeriod());
                ps.setNull(8, Types.VARCHAR);
                ps.setNull(9, Types.VARCHAR);
            } else if (product instanceof Clothing) {
                Clothing c = (Clothing) product;
                ps.setNull(6, Types.VARCHAR);
                ps.setNull(7, Types.INTEGER);
                ps.setString(8, c.getSize());
                ps.setString(9, c.getColor());
            } else {
                ps.setNull(6, Types.VARCHAR);
                ps.setNull(7, Types.INTEGER);
                ps.setNull(8, Types.VARCHAR);
                ps.setNull(9, Types.VARCHAR);
            }

            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    product.setProductId(rs.getInt(1));
                    System.out.println("Generated Product ID: " + product.getProductId());
                }
                return true;
            }

        } catch (SQLException e) {
            System.out.println("Error in createProduct: " + e.getMessage());
        }

        return false;
    }

    @Override
    public boolean createOrder(User user, List<Product> products) throws UserNotFoundException {
        if (!isValidUser(user.getUserId())) {
            throw new UserNotFoundException("User not found or invalid role.");
        }

        String sql = "INSERT INTO orders (userId, productId) VALUES (?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Product p : products) {
                ps.setInt(1, user.getUserId());
                ps.setInt(2, p.getProductId());
                ps.addBatch();
            }

            int[] result = ps.executeBatch();
            return result.length == products.size();
        } catch (SQLException e) {
            System.out.println("Error placing order: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean cancelOrder(int userId, int orderId) throws OrderNotFoundException {
        if (!isValidOrder(userId, orderId)) {
            throw new OrderNotFoundException("Order ID " + orderId + " not found for user ID " + userId);
        }

        String sql = "DELETE FROM orders WHERE orderId = ? AND userId = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setInt(2, userId);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Error cancelling order: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Product> getAllProducts() {
        List<Product> productList = new ArrayList<>();
        try {
            String sql = "SELECT * FROM products";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Product p = new Product(
                    rs.getInt("productId"),
                    rs.getString("productName"),
                    rs.getString("description"),
                    rs.getDouble("price"),
                    rs.getInt("quantityInStock"),
                    rs.getString("type")
                );
                productList.add(p);
            }
        } catch (SQLException e) {
            System.out.println("Error in getAllProducts: " + e.getMessage());
        }
        return productList;
    }

    @Override
    public List<Map<String, Object>> getOrdersByUser(int userId) throws UserNotFoundException {
        if (!isValidUser(userId)) {
            throw new UserNotFoundException("User ID " + userId + " is not a valid user.");
        }

        List<Map<String, Object>> orders = new ArrayList<>();
        String sql = "SELECT o.orderId, o.productId, p.productName FROM orders o JOIN products p ON o.productId = p.productId WHERE o.userId = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> order = new HashMap<>();
                order.put("orderId", rs.getInt("orderId"));
                order.put("productId", rs.getInt("productId"));
                order.put("productName", rs.getString("productName"));
                orders.add(order);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching orders: " + e.getMessage());
        }

        return orders;
    }

    @Override
    public boolean isAdmin(int userId) {
        try {
            String sql = "SELECT * FROM users WHERE userId = ? AND role = 'Admin'";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Error checking admin role: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean isValidUser(int userId) {
        try {
            String sql = "SELECT * FROM users WHERE userId = ? AND role = 'User'";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Error validating user: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean isValidProduct(int productId) {
        try {
            String sql = "SELECT productId FROM products WHERE productId = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Error checking product ID: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean isValidOrder(int userId, int orderId) {
        String sql = "SELECT * FROM orders WHERE orderId = ? AND userId = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Error validating order: " + e.getMessage());
            return false;
        }
    }
}
