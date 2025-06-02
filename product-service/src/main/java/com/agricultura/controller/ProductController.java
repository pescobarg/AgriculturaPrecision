package com.agricultura.controller;

import com.agricultura.dto.*;
import com.agricultura.service.ProductService;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;
import java.util.List;

@Path("/api/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class ProductController {
    
    @Inject
    ProductService productService;
    
    @Inject
    JsonWebToken jwt;
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMINISTRADOR", "PROVEEDOR", "AGRICULTOR"})
    public Response createProduct(@Valid ProductCreateDTO productDto) {
        String username = jwt.getClaim("preferred_username");
        ProductResponseDTO created = productService.createProduct(productDto, username);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }
    
    @GET
    @RolesAllowed({"ADMINISTRADOR", "PROVEEDOR", "AGRICULTOR"})
    public List<ProductResponseDTO> getAllProducts() {
        return productService.getAllProducts();
    }
    
    @GET
    @Path("/my-products")
    @RolesAllowed({"ADMINISTRADOR", "PROVEEDOR", "AGRICULTOR"})
    public List<ProductResponseDTO> getMyProducts() {
        String username = jwt.getClaim("preferred_username");
        System.out.println("Username from JWT: " + username);
        return productService.getMyProducts(username);
    }
    
    @GET
    @Path("/{id}")
    @RolesAllowed({"ADMINISTRADOR", "PROVEEDOR", "AGRICULTOR"})
    public ProductResponseDTO getProductById(@PathParam("id") Long id) {
        return productService.getProductById(id);
    }
    
    @PUT
    @Path("/{id}")
    @RolesAllowed({"ADMINISTRADOR", "PROVEEDOR", "AGRICULTOR"})
    public ProductResponseDTO updateProduct(@PathParam("id") Long id, @Valid ProductUpdateDTO productDto) {
        String username = jwt.getClaim("preferred_username");
        boolean isAdmin = jwt.getGroups().contains("ADMINISTRADOR");
        return productService.updateProduct(id, productDto, username, isAdmin);
    }
    
    @DELETE
    @Path("/{id}")
    @RolesAllowed({"ADMINISTRADOR", "PROVEEDOR", "AGRICULTOR"})
    public Response deleteProduct(@PathParam("id") Long id) {
        String username = jwt.getClaim("preferred_username");
        boolean isAdmin = jwt.getGroups().contains("ADMINISTRADOR");
        productService.deleteProduct(id, username, isAdmin);
        return Response.noContent().build();
    }
}