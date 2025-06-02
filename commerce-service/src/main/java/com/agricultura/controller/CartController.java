package com.agricultura.controller;

import com.agricultura.dto.AddToCartRequest;
import com.agricultura.dto.UpdateCartItemRequest;
import com.agricultura.entity.Cart;
import com.agricultura.entity.CartItem;
import com.agricultura.service.CartService;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/cart")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"AGRICULTOR", "PROVEEDOR", "ADMINISTRADOR"})
@Tag(name = "Cart", description = "Operaciones del carrito de compras")
public class CartController {
    
    @Inject
    JsonWebToken jwt;
    
    @Inject
    CartService cartService;
    
    @GET
    @Operation(summary = "Obtener carrito del usuario actual")
    public Response getCart() {
        Integer userId = getUserId();
        String username = getUsername();
        
        Cart cart = cartService.getCartByUserId(userId);
        if (cart == null) {
            cart = cartService.getOrCreateCart(userId, username);
        }
        
        return Response.ok(cart).build();
    }
    
    @POST
    @Path("/items")
    @Operation(summary = "Agregar producto al carrito")
    public Response addItemToCart(@Valid AddToCartRequest request) {
        Integer userId = getUserId();
        String username = getUsername();
        String authToken = getAuthToken();
        
        try {
            CartItem item = cartService.addItemToCart(userId, username, request, authToken);
            return Response.status(Response.Status.CREATED).entity(item).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(e.getMessage())).build();
        }
    }
    
    @PUT
    @Path("/items/{itemId}")
    @Operation(summary = "Actualizar cantidad de item en carrito")
    public Response updateCartItem(@PathParam("itemId") Long itemId, 
                                 @Valid UpdateCartItemRequest request) {
        Integer userId = getUserId();
        String authToken = getAuthToken();
        
        try {
            CartItem item = cartService.updateCartItem(itemId, userId, request.quantity, authToken);
            return Response.ok(item).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorResponse(e.getMessage())).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(e.getMessage())).build();
        }
    }
    
    @DELETE
    @Path("/items/{itemId}")
    @Operation(summary = "Eliminar item del carrito")
    public Response removeCartItem(@PathParam("itemId") Long itemId) {
        Integer userId = getUserId();
        
        try {
            cartService.removeCartItem(itemId, userId);
            return Response.noContent().build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorResponse(e.getMessage())).build();
        }
    }
    
    @DELETE
    @Operation(summary = "Limpiar carrito completo")
    public Response clearCart() {
        Integer userId = getUserId();
        cartService.clearCart(userId);
        return Response.noContent().build();
    }
    
    private Integer getUserId() {
        return Integer.parseInt(jwt.getClaim("user_id").toString());
    }
    
    private String getUsername() {
        return jwt.getClaim("preferred_username").toString();
    }
    
    private String getAuthToken() {
        return jwt.getRawToken();
    }
    
    public static class ErrorResponse {
        public String message;
        
        public ErrorResponse(String message) {
            this.message = message;
        }
    }
}