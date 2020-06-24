package com.restservice.controller;


import com.restservice.repository.ProductRepository;
import com.restservice.entity.Product;
import javassist.tools.web.BadHttpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping(path = "/products")
public class ProductController {

    @Autowired
    private ProductRepository repository;

    @GetMapping
    public Iterable<Product> findAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Product> find(@PathVariable Long id) {
        return repository.findById(id);
    }


    @PostMapping
    public Product create(@RequestBody Product product) {

        product.setCreatedDate(new Date());
        return repository.save(product);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }

    @PutMapping("/{id}")
    public Product update(@PathVariable Long id,  @RequestBody Product product) throws BadHttpRequest {

        if (repository.findById(id).isPresent()) {

            product.setId(id);
            if (product.getName() != null || !product.getName().isEmpty()) {
                product.setName(product.getName());
            }
            if (product.getPrice() > 0 ) {
                product.setPrice(product.getPrice());
            }
            if (product.getCategory() != null || !product.getCategory().isEmpty()) {
                product.setCategory(product.getCategory());
            }
            product.setCreatedDate(repository.findById(id).get().getCreatedDate());
            product.setUpdatedDate(new Date());
            return repository.save(product);
        } else {
            throw new BadHttpRequest();
        }
    }

}