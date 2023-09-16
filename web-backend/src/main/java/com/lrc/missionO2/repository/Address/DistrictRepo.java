package com.lrc.missionO2.repository.Address;

import com.lrc.missionO2.entity.addressList.District;
import com.lrc.missionO2.entity.addressList.State;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface DistrictRepo extends MongoRepository<District, String> {
    boolean existsByDistrictName(String district);
    Optional<District> findByDistrictNameLikeIgnoreCaseAndState(String district, State state);
    List<District> findAllByDistrictNameLikeIgnoreCase(String district );
    List<District> findAllByState(State state);

}
