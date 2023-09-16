package com.lrc.missionO2.services;

import com.lrc.missionO2.DTO.Request.PlantRequest;
import com.lrc.missionO2.DTO.Response.FileResponse;
import com.lrc.missionO2.DTO.Response.PaginatedResponse;
import com.lrc.missionO2.DTO.Response.PlantSummaryDTO;
import com.lrc.missionO2.DTO.Response.PlantViewResponse;
import com.lrc.missionO2.entity.FileData;
import com.lrc.missionO2.entity.Order;
import com.lrc.missionO2.entity.Plant;
import com.lrc.missionO2.exceptions.ItemNotFoundException;
import com.lrc.missionO2.repository.FileRepo;
import com.lrc.missionO2.repository.PlantRepo;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlantService {
    private final PlantRepo plantRepo;
    private final FileRepo fileRepo;
    private final ImageService imageService;
    private final MongoOperations mongoOperations;


    public FileResponse getPlantImage(String id){
        FileData file = fileRepo.findById(id).orElseThrow(()->new ItemNotFoundException("File: " + id + "not Found"));
        return FileResponse.builder().image(file.getData()).fileName(file.getFileName()).build();
    }

    public PlantViewResponse viewPlant(String id) {
        Plant plant = plantRepo.findById(id)
                .orElseThrow(()->new ItemNotFoundException("Plant with id: " + id + " not Found"));
        return PlantViewResponse.builder()
                .plantPrice(plant.getPlantPrice())
                .seedPrice(plant.getSeedPrice())
                .name(plant.getName())
                .id(plant.getId())
                .images(plant.getImages())
                .build();

    }

    public PaginatedResponse<PlantViewResponse> viewAllPlants(Integer offSet, Integer size) {
        Pageable pageable = PageRequest.of(offSet, size);
        Query query = new Query();
        Page<Plant> plants = plantRepo.findAll(pageable);
        PaginatedResponse<PlantViewResponse> response = new PaginatedResponse<>();
        long totalCount = mongoOperations.count(query, Plant.class);
        int totalPages = (int) Math.ceil((double) totalCount / size);
        response.setData(plants.stream().map((plant)-> PlantViewResponse.builder()
                .images(plant.getImages())
                .id(plant.getId())
                .plantPrice(plant.getPlantPrice())
                .name(plant.getName())
                .seedPrice(plant.getSeedPrice())
                .build())
                .toList());
        response.setTotal(totalCount);
        response.setTotalPages(totalPages);
        return response;
    }

    public String addPlant(PlantRequest plantRequest) throws IOException {
        Plant plant = new Plant();
        plant.setPlantPrice(plantRequest.getPlantPrice());
        plant.setName(plantRequest.getName());
        plant.setSeedPrice(plantRequest.getSeedPrice());
        List<String> files = new ArrayList<>();
        saveImages(plantRequest, files, plant);
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
        saveImages(updatePlantRequest, files, plant);
        plantRepo.save(plant);
        return "Plant with id: " + id + " Updated";
    }

    private void saveImages(PlantRequest updatePlantRequest, List<String> files, Plant plant) throws IOException {
        for (MultipartFile image : updatePlantRequest.getImages()) {
            FileData file = new FileData();
            file.setData(imageService.compressImages(image));
            file.setFileName(image.getOriginalFilename());
            fileRepo.save(file);
            files.add(file.getId());
        }
        plant.setImages(files);

    }

    public String deletePlant(String id) {
        Plant plant = plantRepo.findById(id)
                .orElseThrow(()->new ItemNotFoundException("Plant with id: " + id + "Not Found"));
        fileRepo.deleteAllById(plant.getImages());
        plantRepo.deleteById(plant.getId());
        return "Plant with id: " + id + " Deleted";
    }

}
