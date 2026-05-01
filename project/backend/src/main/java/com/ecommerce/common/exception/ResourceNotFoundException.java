package com.ecommerce.common.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resource, Long id) {
        super(resource + " avec l'id " + id + " introuvable");
    }
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
