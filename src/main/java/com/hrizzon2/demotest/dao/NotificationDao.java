package com.hrizzon2.demotest.dao;

import com.hrizzon2.demotest.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationDao extends JpaRepository<Notification, Integer> {

    List<Notification> findByDestinataireId(Integer userId);

}