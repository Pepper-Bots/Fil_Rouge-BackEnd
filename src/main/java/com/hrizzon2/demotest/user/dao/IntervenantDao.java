package com.hrizzon2.demotest.user.dao;

import com.hrizzon2.demotest.user.model.Intervenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface IntervenantDao extends JpaRepository<Intervenant, Integer> {


}
