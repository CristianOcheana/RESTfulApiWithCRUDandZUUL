package com.restservice.repository;



import com.restservice.entity.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

//@RepositoryRestResource(path = "/products")
//@RestResource(exported = false)
@Repository
public interface ProductRepository extends CrudRepository<Product, Long> {

}