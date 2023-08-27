package com.lrc.missionO2.repository;

import com.lrc.missionO2.entity.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface OrderRepo extends MongoRepository<Order, String> {
    List<Order> findAllByState(String state);
    List<Order> findAllByOrderStatus(String status);
    List<Order> findAllByDistrict(String district);
    List<Order> findAllByTaluk(String taluk);
}
