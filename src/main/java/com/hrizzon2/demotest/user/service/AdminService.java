package com.hrizzon2.demotest.user.service;

import com.hrizzon2.demotest.model.enums.NiveauDroit;
import com.hrizzon2.demotest.user.dao.AdminDao;
import com.hrizzon2.demotest.user.model.Admin;
import com.hrizzon2.demotest.user.model.enums.TypeAdmin;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    private final AdminDao adminDao;


    public AdminService(AdminDao adminDao) {
        this.adminDao = adminDao;
    }

    /**
     * Récupère les admins d'un certain type et niveau de droit.
     * Par exemple, ici, on récupère les "RESPONSABLE_FORMATION" avec niveau "ADMIN".
     */
    public List<Admin> getAdminsByTypeAndNiveauDroit() {
        return adminDao.findByTypeAdminAndNiveauDroit(TypeAdmin.RESPONSABLE_FORMATION, NiveauDroit.ADMIN);
    }

    /**
     * Récupère tous les admins d'un type donné.
     */
    public List<Admin> getAdminsByType(TypeAdmin typeAdmin) {
        return adminDao.findByTypeAdmin(typeAdmin);
    }

    /**
     * Récupère tous les admins d'un niveau de droit donné.
     */
    public List<Admin> getAdminsByNiveauDroit(NiveauDroit niveauDroit) {
        return adminDao.findByNiveauDroit(niveauDroit);
    }

    /**
     * Récupère tous les admins de type et niveau de droit donnés.
     */
    public List<Admin> getAdminsByTypeAndNiveau(TypeAdmin typeAdmin, NiveauDroit niveauDroit) {
        return adminDao.findByTypeAdminAndNiveauDroit(typeAdmin, niveauDroit);
    }

}

// todo
//  - getAdmins, gestion droits
//  Créer
//  Gestion admins