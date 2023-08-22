package com.lrc.missionO2.repository;

import com.lrc.missionO2.entity.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderRepo extends MongoRepository<Order, String> {

}
