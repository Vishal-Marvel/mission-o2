package com.lrc.missionO2.repository;

import com.lrc.missionO2.entity.Plant;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PlantRepo extends MongoRepository<Plant, String> {

}
