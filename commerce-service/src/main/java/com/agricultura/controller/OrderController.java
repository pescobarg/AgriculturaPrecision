package com.agricultura.controller;

import com.agricultura.dto.CreateOrderRequest;
import com.agricultura.dto.UpdateOrderStatusRequest;
import com.agricultura.entity.Order;
import com.agricultura.service.OrderService;
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

import java.util.List;

@Path("/api/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"AGRICULTOR", "PROVEEDOR", "ADMINISTRADOR"})
@Tag(name = "Orders", description = "Operaciones de órdenes de compra")
public class OrderController {
    
    @Inject
    JsonWebToken jwt;
    
    @Inject
    OrderService orderService;
    
    @POST
    @Operation(summary = "Crear orden desde el carrito")
    @RolesAllowed({"AGRICULTOR", "ADMINISTRADOR"})
    public Response createOrder(@Valid CreateOrderRequest request) {
        Integer userId = getUserId();
        String username = getUsername();
        String userRole = getUserRole();
        String authToken = getAuthToken();
        
        try {
            Order order = orderService.createOrderFromCart(userId, username, request, authToken);
            return Response.status(Response.Status.CREATED).entity(order).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(e.getMessage())).build();
        }
    }
    
    @GET
    @Path("/my-purchases")
    @Operation(summary = "Obtener mis compras (como comprador)")
    @RolesAllowed({"AGRICULTOR", "ADMINISTRADOR"})
    public Response getMyPurchases() {
        Integer userId = getUserId();
        List<Order> orders = orderService.getOrdersByBuyer(userId);
        return Response.ok(orders).build();
    }
    
    @GET
    @Path("/my-sales")
    @Operation(summary = "Obtener mis ventas (como vendedor)")
    @RolesAllowed({"PROVEEDOR", "ADMINISTRADOR"})
    public Response getMySales() {
        Integer userId = getUserId();
        List<Order> orders = orderService.getOrdersBySeller(userId);
        return Response.ok(orders).build();
    }
    
    @GET
    @Path("/{orderId}")
    @Operation(summary = "Obtener orden por ID")
    public Response getOrder(@PathParam("orderId") Long orderId) {
        Integer userId = getUserId();
        String userRole = getUserRole();
        
        try {
            Order order = orderService.getOrderById(orderId, userId, userRole);
            return Response.ok(order).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorResponse(e.getMessage())).build();
        } catch (BadRequestException e) {
            return Response.status(Response.Status.FORBIDDEN)
                .entity(new ErrorResponse(e.getMessage())).build();
        }
    }
    
    @PUT
    @Path("/{orderId}/status")
    @Operation(summary = "Actualizar estado de la orden")
    public Response updateOrderStatus(@PathParam("orderId") Long orderId, 
                                    @Valid UpdateOrderStatusRequest request) {
        Integer userId = getUserId();
        String userRole = getUserRole();
        
        try {
            Order order = orderService.updateOrderStatus(orderId, request.status, userId, userRole);
            return Response.ok(order).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorResponse(e.getMessage())).build();
        } catch (BadRequestException e) {
            return Response.status(Response.Status.FORBIDDEN)
                .entity(new ErrorResponse(e.getMessage())).build();
        }
    }
    
    @GET
    @Path("/all")
    @Operation(summary = "Obtener todas las órdenes (solo administradores)")
    @RolesAllowed("ADMINISTRADOR")
    public Response getAllOrders() {
        List<Order> orders = Order.listAll();
        return Response.ok(orders).build();
    }
    
    private Integer getUserId() {
        return Integer.parseInt(jwt.getClaim("user_id").toString());
    }
    
    private String getUsername() {
        return jwt.getClaim("preferred_username").toString();
    }
    
    private String getUserRole() {
        return jwt.getClaim("role").toString();
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