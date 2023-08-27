package com.lrc.missionO2.services;

import com.lrc.missionO2.DTO.Response.Places;
import com.lrc.missionO2.entity.addressList.District;
import com.lrc.missionO2.entity.addressList.State;
import com.lrc.missionO2.exceptions.ItemNotFoundException;
import com.lrc.missionO2.repository.Address.DistrictRepo;
import com.lrc.missionO2.repository.Address.StateRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final StateRepo stateRepo;
    private final DistrictRepo districtRepo;
    public Places getPlace(String state, String district) {
        if (state==null && district==null){
            return Places.builder().places(stateRepo.findAll().stream().map(State::getStateName).toList()).build();
        } else if (state!=null && district==null) {
            State rcqdState = stateRepo.findByStateNameLikeIgnoreCase(state)
                    .orElseThrow(()->new ItemNotFoundException("State " + state + " Not Found"));
            return Places.builder().places(districtRepo.findAllByState(rcqdState).stream().map(District::getDistrictName).toList()).build();
        }else if (district!=null && state!=null){
            State rcqdState = stateRepo.findByStateNameLikeIgnoreCase(state)
                    .orElseThrow(()->new ItemNotFoundException("State " + state + " Not Found"));
            District rcqdDistrict = districtRepo.findByDistrictNameLikeIgnoreCaseAndState(district, rcqdState)
                    .orElseThrow(()-> new ItemNotFoundException("District " + district + " Not Found"));
            return Places.builder().places(rcqdDistrict.getTaluks()).build();
        }
        else {
            return null;
        }
    }
}
