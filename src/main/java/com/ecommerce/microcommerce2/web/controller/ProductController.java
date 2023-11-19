package com.ecommerce.microcommerce2.web.controller;

import com.ecommerce.microcommerce2.model.Product;
import com.ecommerce.microcommerce2.web.dao.ProductDao;
import com.ecommerce.microcommerce2.web.exceptions.ProduitIntrouvableException;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import jakarta.validation.Valid;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Objects;

@RestController
public class ProductController {

    private final ProductDao productDao;

    public ProductController(ProductDao productDao) {
        this.productDao = productDao;
    }

    @GetMapping("/Produits")
    public MappingJacksonValue listeProduits(){
        Iterable<Product> produits = productDao.findAll();
        SimpleBeanPropertyFilter monFiltre = SimpleBeanPropertyFilter.serializeAllExcept("prixAchat");
        FilterProvider listDeNosFiltres = new SimpleFilterProvider().addFilter("monFiltreDynamique", monFiltre);
        MappingJacksonValue produitsFiltres = new MappingJacksonValue(produits);
        produitsFiltres.setFilters(listDeNosFiltres);
        return produitsFiltres;

    }


    @GetMapping(value="/Produits/{id}")
    public MappingJacksonValue afficherUnrProduct(@PathVariable int id){
        Product productSearch = productDao.findById(id);
        SimpleBeanPropertyFilter monFiltre = SimpleBeanPropertyFilter.serializeAllExcept("prixAchat");
        FilterProvider listDeNosFiltres = new SimpleFilterProvider().addFilter("monFiltreDynamique", monFiltre);
        MappingJacksonValue produitsFiltres = new MappingJacksonValue(productSearch);
        produitsFiltres.setFilters(listDeNosFiltres);
        if(produitsFiltres == null) throw new ProduitIntrouvableException("Le produit avec l'id "+ id + "est INTROUVABLE.");
        return produitsFiltres;
    }
    @GetMapping(value="test/produits/{prixLimit}")
    public List<Product> tesDeRequete(@PathVariable int prixLimite)
    {
        return productDao.findByPrixGreaterThan(400);
    }
    @PostMapping(value="/Produits")
    public ResponseEntity<Product> ajouterProduits(@Valid @RequestBody Product product){
        Product productAdded = productDao.save(product);
        if(Objects.isNull(productAdded)){
            return ResponseEntity.noContent().build();
        }
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(productAdded.getId())
                .toUri();
        return ResponseEntity.created(location).build();

    }
    @DeleteMapping(value="Produits/{id}")
    public void supprimerProduit(@PathVariable int id){
        productDao.deleteById(id);
    }
    @PutMapping(value="Produits")
    public void updateProduits(@RequestBody Product product){
        productDao.save(product);
    }

    @Query("SELECT id, nom, prix from Product p WHERE p.prix >: prixLimit")
    List<Product> chercherUnProduitCher(@Param("PrixLimit") int prix) {
        return null;
    }
}
