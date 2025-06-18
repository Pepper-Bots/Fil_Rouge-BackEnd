package com.hrizzon2.demotest.mock;

public class MockStagiaireDao {
}


//import edu.fbansept.cda_2025_demo.dao.ProduitDao;
//import edu.fbansept.cda_2025_demo.model.Produit;
//import edu.fbansept.cda_2025_demo.model.Vendeur;
//import org.springframework.data.domain.Example;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.data.repository.query.FluentQuery;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.function.Function;
//
//public class MockProduitDao implements ProduitDao {
//
//    @Override
//    public Produit findByNom(String name) {
//        return null;
//    }
//
//    @Override
//    public void flush() {
//
//    }
//
//    @Override
//    public <S extends Produit> S saveAndFlush(S entity) {
//        return null;
//    }
//
//    @Override
//    public <S extends Produit> List<S> saveAllAndFlush(Iterable<S> entities) {
//        return List.of();
//    }
//
//    @Override
//    public void deleteAllInBatch(Iterable<Produit> entities) {
//
//    }
//
//    @Override
//    public void deleteAllByIdInBatch(Iterable<Integer> integers) {
//
//    }
//
//    @Override
//    public void deleteAllInBatch() {
//
//    }
//
//    @Override
//    public Produit getOne(Integer integer) {
//        return null;
//    }
//
//    @Override
//    public Produit getById(Integer integer) {
//        return null;
//    }
//
//    @Override
//    public Produit getReferenceById(Integer integer) {
//        return null;
//    }
//
//    @Override
//    public <S extends Produit> Optional<S> findOne(Example<S> example) {
//        return Optional.empty();
//    }
//
//    @Override
//    public <S extends Produit> List<S> findAll(Example<S> example) {
//        return List.of();
//    }
//
//    @Override
//    public <S extends Produit> List<S> findAll(Example<S> example, Sort sort) {
//        return List.of();
//    }
//
//    @Override
//    public <S extends Produit> Page<S> findAll(Example<S> example, Pageable pageable) {
//        return null;
//    }
//
//    @Override
//    public <S extends Produit> long count(Example<S> example) {
//        return 0;
//    }
//
//    @Override
//    public <S extends Produit> boolean exists(Example<S> example) {
//        return false;
//    }
//
//    @Override
//    public <S extends Produit, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
//        return null;
//    }
//
//    @Override
//    public <S extends Produit> S save(S entity) {
//        return null;
//    }
//
//    @Override
//    public <S extends Produit> List<S> saveAll(Iterable<S> entities) {
//        return List.of();
//    }
//
//    @Override
//    public Optional<Produit> findById(Integer integer) {
//
//        Vendeur fauxVendeur = new Vendeur();
//        fauxVendeur.setId(1);
//
//        Produit fauxProduit = new Produit();
//        fauxProduit.setId(integer);
//        fauxProduit.setCreateur(fauxVendeur);
//
//        if (integer == 1) {
//            return Optional.of(fauxProduit);
//        }
//
//        return Optional.empty();
//    }
//
//    @Override
//    public boolean existsById(Integer integer) {
//        return false;
//    }
//
//    @Override
//    public List<Produit> findAll() {
//        return List.of();
//    }
//
//    @Override
//    public List<Produit> findAllById(Iterable<Integer> integers) {
//        return List.of();
//    }
//
//    @Override
//    public long count() {
//        return 0;
//    }
//
//    @Override
//    public void deleteById(Integer integer) {
//
//    }
//
//    @Override
//    public void delete(Produit entity) {
//
//    }
//
//    @Override
//    public void deleteAllById(Iterable<? extends Integer> integers) {
//
//    }
//
//    @Override
//    public void deleteAll(Iterable<? extends Produit> entities) {
//
//    }
//
//    @Override
//    public void deleteAll() {
//
//    }
//
//    @Override
//    public List<Produit> findAll(Sort sort) {
//        return List.of();
//    }
//
//    @Override
//    public Page<Produit> findAll(Pageable pageable) {
//        return null;
//    }
