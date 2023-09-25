package com.lrc.missionO2.controller;


import com.lrc.missionO2.DTO.Request.AddPlantRequest;
import com.lrc.missionO2.DTO.Response.FileResponse;
import com.lrc.missionO2.DTO.Response.MiscResponse;
import com.lrc.missionO2.DTO.Response.PaginatedResponse;
import com.lrc.missionO2.DTO.Response.PlantViewResponse;
import com.lrc.missionO2.services.PlantService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/plant")
public class PlantController {
    private final PlantService plantService;

    @GetMapping("/view/all/{offSet}/{size}")
    public ResponseEntity<PaginatedResponse<PlantViewResponse>> viewAllPlants(
            @PathVariable Integer offSet, @PathVariable Integer size){
        return ResponseEntity.ok(plantService.viewAllPlants(offSet, size));

    }
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_ADMIN_ASSIST')")
    @PostMapping("/create/add")
    public ResponseEntity<MiscResponse> addProduct(@ModelAttribute AddPlantRequest AddplantRequest) throws IOException {
        String response = plantService.addPlant(AddplantRequest);
        return ResponseEntity.ok(MiscResponse.builder().response(response).build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlantViewResponse> viewProduct(@PathVariable String id){
        return ResponseEntity.ok(plantService.viewPlant(id));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_ADMIN_ASSIST')")
    @PutMapping("/{id}")
    public ResponseEntity<MiscResponse> updateProduct(@ModelAttribute AddPlantRequest updateAddPlantRequest, @PathVariable String id) throws IOException {
        String response = plantService.updatePlant(id, updateAddPlantRequest);
        return ResponseEntity.ok(MiscResponse.builder().response(response).build());

    }
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<MiscResponse> deleteProduct(@PathVariable String id){
        String response = plantService.deletePlant(id);
        return ResponseEntity.ok(MiscResponse.builder().response(response).build());
    }

    @GetMapping("/image/{id}")
    public ResponseEntity<FileResponse> getImage(@PathVariable String id){
        return ResponseEntity.ok(plantService.getPlantImage(id));
    }

}
