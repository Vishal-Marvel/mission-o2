package com.lrc.missionO2.services;

import com.lrc.missionO2.DTO.Request.PlantRequest;
import com.lrc.missionO2.DTO.Response.PlantViewResponse;
import com.lrc.missionO2.entity.FileData;
import com.lrc.missionO2.entity.Plant;
import com.lrc.missionO2.exceptions.ItemNotFoundException;
import com.lrc.missionO2.repository.FileRepo;
import com.lrc.missionO2.repository.PlantRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlantService {
    private final PlantRepo plantRepo;
    private final FileRepo fileRepo;

    private byte[] getPlantImage(String id){
        return fileRepo.findById(id).orElseThrow(()->new ItemNotFoundException("Plant Image with id: " + id + " not Found"))
                .getData();
    }

    public PlantViewResponse viewPlant(String id) {
        Plant plant = plantRepo.findById(id)
                .orElseThrow(()->new ItemNotFoundException("Plant with id: " + id + " not Found"));
        return PlantViewResponse.builder()
                .plantPrice(plant.getPlantPrice())
                .seedPrice(plant.getSeedPrice())
                .name(plant.getName())
                .id(plant.getId())
                .images(plant.getImages().stream().map(this::getPlantImage).toList())
                .build();

    }

    public List<PlantViewResponse> viewAllPlants() {
        List<Plant> plants = plantRepo.findAll();
        return plants.stream().map((plant)-> PlantViewResponse.builder()
                .images(plant.getImages().stream().map(this::getPlantImage).toList())
                .id(plant.getId())
                .plantPrice(plant.getPlantPrice())
                .name(plant.getName())
                .seedPrice(plant.getSeedPrice())
                .build())
                .toList();
    }

    public String addPlant(PlantRequest plantRequest) throws IOException {
        Plant plant = new Plant();
        plant.setPlantPrice(plantRequest.getPlantPrice());
        plant.setName(plantRequest.getName());
        plant.setSeedPrice(plantRequest.getSeedPrice());
        List<String> files = new ArrayList<>();
        for (MultipartFile image : plantRequest.getImages()) {
            FileData file = new FileData();
            file.setData(image.getBytes());
            fileRepo.save(file);
            files.add(file.getId());
        }
        plant.setImages(files);
        plantRepo.save(plant);
        return "Plant " + plantRequest.getName() + " Saved";

    }

    public String updatePlant(String id, PlantRequest updatePlantRequest) throws IOException {
        List<String> files = new ArrayList<>();
        Plant plant = plantRepo.findById(id)
                .orElseThrow(()->new ItemNotFoundException("Plant with id: " + id + " Not Found"));
        plant.setName(updatePlantRequest.getName());
        plant.setPlantPrice(updatePlantRequest.getPlantPrice());
        plant.setSeedPrice(updatePlantRequest.getSeedPrice());
        fileRepo.deleteAllById(plant.getImages());
        for (MultipartFile image : updatePlantRequest.getImages()) {
            FileData file = new FileData();
            file.setData(image.getBytes());
            fileRepo.save(file);
            files.add(file.getId());
        }
        plant.setImages(files);
        plantRepo.save(plant);

        return "Plant with id: " + id + " Updated";
    }

    public String deletePlant(String id) {
        Plant plant = plantRepo.findById(id)
                .orElseThrow(()->new ItemNotFoundException("Plant with id: " + id + "Not Found"));
        fileRepo.deleteAllById(plant.getImages());
        plantRepo.deleteById(plant.getId());
        return "Plant with id: " + id + " Deleted";
    }
}
