package com.hrizzon2.demotest.user.dao;

import com.hrizzon2.demotest.user.model.Admin;
import com.hrizzon2.demotest.user.model.enums.NiveauDroit;
import com.hrizzon2.demotest.user.model.enums.TypeAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminDao extends JpaRepository<Admin, Integer> {

    Optional<Admin> findByEmail(String email);

    // Méthode pour récupérer les admins par type de rôle (TypeAdmin)
    List<Admin> findByTypeAdmin(TypeAdmin typeAdmin);

    // Méthode pour récupérer les admins par niveau de droit (NiveauDroit)
    List<Admin> findByNiveauDroit(NiveauDroit droit);

    // Méthode pour récupérer les admins par type et niveau de droit
    List<Admin> findByTypeAdminAndNiveauDroit(TypeAdmin typeAdmin, NiveauDroit niveauDroit);


    // Pour récupérer tous les Admins sans filtrer :
    // List<Admin> findAll();
}

