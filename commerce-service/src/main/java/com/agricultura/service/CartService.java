package com.agricultura.service;

import com.agricultura.dto.AddToCartRequest;
import com.agricultura.dto.ProductInfo;
import com.agricultura.entity.Cart;
import com.agricultura.entity.CartItem;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

import java.math.BigDecimal;

@ApplicationScoped
public class CartService {
    
    @Inject
    ProductServiceClient productServiceClient;
    
    @Transactional
    public Cart getOrCreateCart(Integer userId, String username) {
        Cart cart = Cart.findByUserId(userId);
        if (cart == null) {
            cart = new Cart();
            cart.userId = userId;
            cart.userUsername = username;
            cart.persist();
        }
        return cart;
    }
    
    @Transactional
    public CartItem addItemToCart(Integer userId, String username, 
                                  AddToCartRequest request, String authToken) {
        // Obtener información del producto
        ProductInfo product = productServiceClient.getProductById(request.productId, authToken);
        
        if (product.stock < request.quantity) {
            throw new BadRequestException("Stock insuficiente. Disponible: " + product.stock);
        }
        
        // Obtener o crear carrito
        Cart cart = getOrCreateCart(userId, username);
        
        // Verificar si el item ya existe en el carrito
        CartItem existingItem = CartItem.findByCartAndProduct(cart, request.productId);
        
        if (existingItem != null) {
            // Actualizar cantidad
            int newQuantity = existingItem.quantity + request.quantity;
            if (product.stock < newQuantity) {
                throw new BadRequestException("Stock insuficiente para la cantidad total. Disponible: " + product.stock);
            }
            existingItem.quantity = newQuantity;
            existingItem.calculateSubtotal();
            existingItem.persist();
        } else {
            // Crear nuevo item
            CartItem newItem = new CartItem();
            newItem.cart = cart;
            newItem.productId = request.productId;
            newItem.productName = product.name;
            newItem.providerId = product.ownerId;
            newItem.providerUsername = product.ownerUsername;
            newItem.quantity = request.quantity;
            newItem.price = product.price;
            newItem.persist();
            existingItem = newItem;
        }
        
        // Recalcular total del carrito
        cart.calculateTotal();
        cart.persist();
        
        return existingItem;
    }
    
    @Transactional
    public CartItem updateCartItem(Long itemId, Integer userId, Integer newQuantity, String authToken) {
        CartItem item = CartItem.findById(itemId);
        if (item == null || !item.cart.userId.equals(userId)) {
            throw new NotFoundException("Item no encontrado");
        }
        
        // Verificar stock disponible
        ProductInfo product = productServiceClient.getProductById(item.productId, authToken);
        if (product.stock < newQuantity) {
            throw new BadRequestException("Stock insuficiente. Disponible: " + product.stock);
        }
        
        item.quantity = newQuantity;
        item.calculateSubtotal();
        item.persist();
        
        // Recalcular total del carrito
        item.cart.calculateTotal();
        item.cart.persist();
        
        return item;
    }
    
    @Transactional
    public void removeCartItem(Long itemId, Integer userId) {
        CartItem item = CartItem.findById(itemId);
        if (item == null || !item.cart.userId.equals(userId)) {
            throw new NotFoundException("Item no encontrado");
        }
        
        Cart cart = item.cart;
        item.delete();
        
        // Recalcular total del carrito
        cart.calculateTotal();
        cart.persist();
    }
    
    @Transactional
    public void clearCart(Integer userId) {
        Cart cart = Cart.findByUserId(userId);
        if (cart != null) {
            cart.items.forEach(CartItem::delete);
            cart.totalAmount = BigDecimal.ZERO;
            cart.persist();
        }
    }
    
    public Cart getCartByUserId(Integer userId) {
        Cart cart = Cart.findByUserId(userId);
        if (cart != null) {
            cart.calculateTotal();
            cart.persist();
        }
        return cart;
    }
}