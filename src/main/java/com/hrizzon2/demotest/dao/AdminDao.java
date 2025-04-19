package com.hrizzon2.demotest.dao;

import com.hrizzon2.demotest.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminDao extends JpaRepository<Admin, Integer> {

    Optional<Admin> findByEmail(String email);

}
