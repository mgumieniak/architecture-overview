package com.mgumieniak.architecture.webapp.controllers;

import com.mgumieniak.architecture.models.products.ProductDTO;
import com.mgumieniak.architecture.webapp.services.ProductService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public void addProductToWarehouse(final @NonNull @RequestBody @Valid ProductDTO productDTO) {
        productService.add(productDTO);
    }
}
