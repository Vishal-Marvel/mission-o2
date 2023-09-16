package com.lrc.missionO2.repository.Address;

import com.lrc.missionO2.entity.addressList.State;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface StateRepo extends MongoRepository<State, String> {
    boolean existsByStateName(String state);
    Optional<State> findByStateNameLikeIgnoreCase(String state);
    List<State> findAllByStateNameLikeIgnoreCase(String state);

}
