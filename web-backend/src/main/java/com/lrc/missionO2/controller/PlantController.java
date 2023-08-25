package com.lrc.missionO2.controller;


import com.lrc.missionO2.DTO.Request.PlantRequest;
import com.lrc.missionO2.DTO.Response.MiscResponse;
import com.lrc.missionO2.DTO.Response.PlantViewResponse;
import com.lrc.missionO2.services.PlantService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/plant")
public class PlantController {
    private final PlantService plantService;

    @GetMapping("/view/all")
    public ResponseEntity<List<PlantViewResponse>> viewAllPlants(){
        return ResponseEntity.ok(plantService.viewAllPlants());

    }
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/create/add")
    public ResponseEntity<MiscResponse> addProduct(@ModelAttribute PlantRequest AddplantRequest) throws IOException {
        String response = plantService.addPlant(AddplantRequest);
        return ResponseEntity.ok(MiscResponse.builder().response(response).build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlantViewResponse> viewProduct(@PathVariable String id){
        return ResponseEntity.ok(plantService.viewPlant(id));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<MiscResponse> updateProduct(@ModelAttribute PlantRequest updatePlantRequest, @PathVariable String id) throws IOException {
        String response = plantService.updatePlant(id, updatePlantRequest);
        return ResponseEntity.ok(MiscResponse.builder().response(response).build());

    }
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<MiscResponse> deleteProduct(@PathVariable String id){
        String response = plantService.deletePlant(id);
        return ResponseEntity.ok(MiscResponse.builder().response(response).build());
    }


}
