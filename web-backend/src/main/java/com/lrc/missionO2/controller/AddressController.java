package com.lrc.missionO2.controller;

import com.lrc.missionO2.DTO.Response.Places;
import com.lrc.missionO2.repository.Address.DistrictRepo;
import com.lrc.missionO2.repository.Address.StateRepo;
import com.lrc.missionO2.services.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/address")
public class AddressController {
    private final AddressService addressService;

    @GetMapping("/view-places")
    public ResponseEntity<Places> getPlaces(
            @RequestParam(value = "state" , required = false) String state,
            @RequestParam(value = "district", required = false) String district
    ){
        return ResponseEntity.ok(addressService.getPlace(state, district));

    }
}
