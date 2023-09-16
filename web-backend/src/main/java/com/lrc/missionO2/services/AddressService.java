package com.lrc.missionO2.services;

import com.lrc.missionO2.DTO.Response.ListResponse;
import com.lrc.missionO2.entity.addressList.District;
import com.lrc.missionO2.entity.addressList.State;
import com.lrc.missionO2.exceptions.ItemNotFoundException;
import com.lrc.missionO2.repository.Address.DistrictRepo;
import com.lrc.missionO2.repository.Address.StateRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final StateRepo stateRepo;
    private final DistrictRepo districtRepo;
    public ListResponse getPlace(String state, String district) {
        long startTime = System.currentTimeMillis();
        ListResponse places = ListResponse.builder().elements(new ArrayList<>()).build();

        if (state==null && district==null){
            places = ListResponse.builder()
                    .elements(stateRepo.findAll().stream()
                            .map(State::getStateName).toList())
                    .build();

        } else if (state!=null && district==null) {
            State rcqdState = stateRepo.findByStateNameLikeIgnoreCase(state)
                    .orElseThrow(()->new ItemNotFoundException("State " + state + " Not Found"));
            places = ListResponse.builder()
                    .elements(districtRepo.findAllByState(rcqdState)
                            .stream().map(District::getDistrictName).toList())
                    .build();

        }else if (district!=null && state!=null){
            State rcqdState = stateRepo.findByStateNameLikeIgnoreCase(state)
                    .orElseThrow(()->new ItemNotFoundException("State " + state + " Not Found"));
            District rcqdDistrict = districtRepo.findByDistrictNameLikeIgnoreCaseAndState(district, rcqdState)
                    .orElseThrow(()-> new ItemNotFoundException("District " + district + " Not Found"));
            places = ListResponse.builder().elements(rcqdDistrict.getTaluks()).build();
        }
        long endTime = System.currentTimeMillis();
//        System.out.println("time = " + (endTime - startTime));
        places.setElements(places.getElements().stream().sorted().toList());
        return places;
    }
}
