package com.agricultura.service;

import com.agricultura.dto.CreateOrderRequest;
import com.agricultura.dto.ProductInfo;
import com.agricultura.entity.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class OrderService {
    
    @Inject
    ProductServiceClient productServiceClient;
    
    @Inject
    CartService cartService;
    
    @Transactional
    public Order createOrderFromCart(Integer userId, String username, 
                                   CreateOrderRequest request, String authToken) {
        Cart cart = cartService.getCartByUserId(userId);
        if (cart == null || cart.items.isEmpty()) {
            throw new BadRequestException("El carrito está vacío");
        }
        
        // Agrupar items por proveedor (una orden por proveedor)
        Map<Integer, List<CartItem>> itemsByProvider = cart.items.stream()
            .collect(Collectors.groupingBy(item -> item.providerId));
        
        if (itemsByProvider.size() > 1) {
            throw new BadRequestException("No se pueden crear órdenes con productos de múltiples proveedores. " +
                "Por favor, realice órdenes separadas para cada proveedor.");
        }
        
        // Crear orden para el único proveedor
        Map.Entry<Integer, List<CartItem>> providerEntry = itemsByProvider.entrySet().iterator().next();
        Integer providerId = providerEntry.getKey();
        List<CartItem> items = providerEntry.getValue();
        
        // Verificar stock y crear orden
        return createOrderForProvider(userId, username, providerId, items, request, authToken);
    }
    
    private Order createOrderForProvider(Integer userId, String username, Integer providerId, 
                                       List<CartItem> items, CreateOrderRequest request, String authToken) {
        // Verificar stock disponible para todos los productos
        for (CartItem item : items) {
            ProductInfo product = productServiceClient.getProductById(item.productId, authToken);
            if (product.stock < item.quantity) {
                throw new BadRequestException("Stock insuficiente para " + item.productName + 
                    ". Disponible: " + product.stock + ", Solicitado: " + item.quantity);
            }
        }
        
        // Crear la orden
        Order order = new Order();
        order.buyerId = userId;
        order.buyerUsername = username;
        order.sellerId = providerId;
        order.sellerUsername = items.get(0).providerUsername;
        order.shippingAddress = request.shippingAddress;
        order.paymentMethod = request.paymentMethod;
        order.status = Order.OrderStatus.PENDING;
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        // Crear items de la orden
        for (CartItem cartItem : items) {
            OrderItem orderItem = new OrderItem();
            orderItem.order = order;
            orderItem.productId = cartItem.productId;
            orderItem.productName = cartItem.productName;
            orderItem.quantity = cartItem.quantity;
            orderItem.unitPrice = cartItem.price;
            orderItem.calculateSubtotal();
            
            totalAmount = totalAmount.add(orderItem.subtotal);
        }
        
        order.totalAmount = totalAmount;
        order.persist();
        
        // Persistir items de la orden
        for (CartItem cartItem : items) {
            OrderItem orderItem = new OrderItem();
            orderItem.order = order;
            orderItem.productId = cartItem.productId;
            orderItem.productName = cartItem.productName;
            orderItem.quantity = cartItem.quantity;
            orderItem.unitPrice = cartItem.price;
            orderItem.persist();
        }
        
        // Actualizar stock de productos
        for (CartItem item : items) {
            ProductInfo product = productServiceClient.getProductById(item.productId, authToken);
            int newStock = product.stock - item.quantity;
            productServiceClient.updateProductStock(item.productId, newStock, authToken);
        }
        
        // Limpiar carrito
        cartService.clearCart(userId);
        
        return order;
    }
    
    @Transactional
    public Order updateOrderStatus(Long orderId, Order.OrderStatus newStatus, Integer userId, String userRole) {
        Order order = Order.findById(orderId);
        if (order == null) {
            throw new NotFoundException("Orden no encontrada");
        }
        
        // Verificar permisos
        if (!userRole.equals("ADMINISTRADOR") && 
            !order.buyerId.equals(userId) && 
            !order.sellerId.equals(userId)) {
            throw new BadRequestException("No tiene permisos para actualizar esta orden");
        }
        
        order.status = newStatus;
        
        // Actualizar timestamps según el estado
        switch (newStatus) {
            case SHIPPED:
                order.shippedAt = LocalDateTime.now();
                break;
            case DELIVERED:
                order.deliveredAt = LocalDateTime.now();
                break;
        }
        
        order.persist();
        return order;
    }
    
    public List<Order> getOrdersByBuyer(Integer buyerId) {
        return Order.findByBuyerId(buyerId);
    }
    
    public List<Order> getOrdersBySeller(Integer sellerId) {
        return Order.findBySellerId(sellerId);
    }
    
    public Order getOrderById(Long orderId, Integer userId, String userRole) {
        Order order = Order.findById(orderId);
        if (order == null) {
            throw new NotFoundException("Orden no encontrada");
        }
        
        // Verificar permisos
        if (!userRole.equals("ADMINISTRADOR") && 
            !order.buyerId.equals(userId) && 
            !order.sellerId.equals(userId)) {
            throw new BadRequestException("No tiene permisos para ver esta orden");
        }
        
        return order;
    }
}