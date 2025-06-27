package com.hexaware.ordermanagement.dao;

import java.util.List;
import java.util.Map;
import com.hexaware.ordermanagement.beans.Product;
import com.hexaware.ordermanagement.beans.User;
import com.hexaware.ordermanagement.exception.*;

public interface IOrderManagementRepository {
    boolean createUser(User user);
    boolean createProduct(User adminUser, Product product) throws UserNotFoundException;
    boolean createOrder(User user, List<Product> products) throws UserNotFoundException;
    boolean cancelOrder(int userId, int orderId) throws UserNotFoundException, OrderNotFoundException;
    List<Product> getAllProducts();
    List<Map<String, Object>> getOrdersByUser(int userId) throws UserNotFoundException;

    boolean isAdmin(int userId);
    boolean isValidUser(int userId);
    boolean isValidProduct(int productId);
    boolean isValidOrder(int userId, int orderId);
}
