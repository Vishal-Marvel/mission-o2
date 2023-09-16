package com.lrc.missionO2.repository;

import com.lrc.missionO2.entity.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

public interface OrderRepo extends MongoRepository<Order, String> {
    List<Order> findAllByOrderDateBetween(Date fromDate, Date toDate);
}
