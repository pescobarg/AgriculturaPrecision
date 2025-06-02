package com.agricultura.service;

import com.agricultura.dto.*;
import com.agricultura.entity.ApplicationUser;
import com.agricultura.entity.Product;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.ForbiddenException;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ProductService {
    
    @Transactional
    public ProductResponseDTO createProduct(ProductCreateDTO dto, String username) {
        ApplicationUser user = ApplicationUser.findByUsername(username);
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        
        Product product = new Product();
        product.name = dto.name;
        product.description = dto.description;
        product.price = dto.price;
        product.stock = dto.stock;
        product.ownerId = user.id.intValue();
        product.ownerUsername = user.username;
        
        product.persist();
        
        return mapToResponseDTO(product);
    }
    
    public List<ProductResponseDTO> getAllProducts() {
        return Product.<Product>listAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }
    
    public List<ProductResponseDTO> getMyProducts(String username) {
        return Product.<Product>find("ownerUsername", username)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }
    
    public ProductResponseDTO getProductById(Long id) {
        Product product = Product.findById(id);
        if (product == null) {
            throw new NotFoundException("Product not found");
        }
        return mapToResponseDTO(product);
    }
    
    @Transactional
    public ProductResponseDTO updateProduct(Long id, ProductUpdateDTO dto, String username, boolean isAdmin) {
        Product product = Product.findById(id);
        if (product == null) {
            throw new NotFoundException("Product not found");
        }
        
        // Solo el dueño o un administrador pueden actualizar el producto
        if (!isAdmin && !product.ownerUsername.equals(username)) {
            throw new ForbiddenException("You can only update your own products");
        }
        
        if (dto.name != null) product.name = dto.name;
        if (dto.description != null) product.description = dto.description;
        if (dto.price != null) product.price = dto.price;
        if (dto.stock != null) product.stock = dto.stock;
        
        return mapToResponseDTO(product);
    }
    
    @Transactional
    public void deleteProduct(Long id, String username, boolean isAdmin) {
        Product product = Product.findById(id);
        if (product == null) {
            throw new NotFoundException("Product not found");
        }
        
        // Solo el dueño o un administrador pueden eliminar el producto
        if (!isAdmin && !product.ownerUsername.equals(username)) {
            throw new ForbiddenException("You can only delete your own products");
        }
        
        product.delete();
    }
    
    private ProductResponseDTO mapToResponseDTO(Product product) {
        return new ProductResponseDTO(
            product.id,
            product.name,
            product.description,
            product.price,
            product.stock,
            product.ownerId,
            product.ownerUsername,
            product.createdAt,
            product.updatedAt
        );
    }
}