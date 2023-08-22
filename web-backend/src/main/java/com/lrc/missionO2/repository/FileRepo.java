package com.lrc.missionO2.repository;

import com.lrc.missionO2.entity.FileData;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FileRepo extends MongoRepository<FileData, String> {
}
