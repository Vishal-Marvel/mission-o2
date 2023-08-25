package com.lrc.missionO2.controller;


import com.lrc.missionO2.DTO.Request.PlantRequest;
import com.lrc.missionO2.DTO.Response.MiscResponse;
import com.lrc.missionO2.DTO.Response.PlantViewResponse;
import com.lrc.missionO2.DTO.Response.PlantsResponse;
import com.lrc.missionO2.services.PlantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/product")
public class PlantController {
    private final PlantService plantService;

    @GetMapping("/all")
    public ResponseEntity<List<PlantsResponse>> viewAllPlants(){
        return ResponseEntity.ok(plantService.viewAllPlants());

    }

    @PostMapping("/add")
    public ResponseEntity<MiscResponse> addProduct(@ModelAttribute PlantRequest AddplantRequest) throws IOException {
        String response = plantService.addPlant(AddplantRequest);
        return ResponseEntity.ok(MiscResponse.builder().response(response).build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlantViewResponse> viewProduct(@PathVariable String id){
        return ResponseEntity.ok(plantService.viewPlant(id));
    }


    @PutMapping("/{id}")
    public ResponseEntity<MiscResponse> updateProduct(@ModelAttribute PlantRequest updatePlantRequest, @PathVariable String id) throws IOException {
        String response = plantService.updatePlant(id, updatePlantRequest);
        return ResponseEntity.ok(MiscResponse.builder().response(response).build());

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MiscResponse> deleteProduct(@PathVariable String id){
        String response = plantService.deletePlant(id);
        return ResponseEntity.ok(MiscResponse.builder().response(response).build());
    }


}
