package com.lrc.missionO2.bootstarp;

import com.lrc.missionO2.entity.addressList.District;
import com.lrc.missionO2.entity.addressList.State;
import com.lrc.missionO2.repository.Address.DistrictRepo;
import com.lrc.missionO2.repository.Address.StateRepo;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class StatesDistrictTalukCreation implements CommandLineRunner {
    private final StateRepo stateRepo;
    private final MongoTemplate mongoTemplate;

    @Override
    public void run(String... args) throws Exception {
        if (stateRepo.findAll().size() == 0) {

            long startTime = System.currentTimeMillis();
            FileInputStream excel = new FileInputStream("details.xls");
            Workbook workbook = new HSSFWorkbook(excel);
            Sheet sheet = workbook.getSheetAt(0);
            Map<String , State> states = new HashMap<>();
            Map<String, District> districts = new HashMap<>();

//        for (Sheet sheet : workbook){
            for (Row row : sheet) {
                if (row.getRowNum() > 0) {
                    String stateName = row.getCell(0).toString().toUpperCase();
                    String capital = row.getCell(1).toString().toUpperCase();
                    String districtName = row.getCell(2).toString().toUpperCase();
                    String talukName = row.getCell(3).toString().toUpperCase();

                    State stateObj = new State();
                    stateObj.setStateName(stateName);
                    stateObj.setCapital(capital);

                    District districtObj = new District();
                    districtObj.setDistrictName(districtName);
                    districtObj.setState(states.getOrDefault(stateName, stateObj));
                    if (!states.containsKey(stateName)){
                        states.put(stateName, stateObj);
                    }

                    District district = districts.getOrDefault(districtName, districtObj);
                    district.getTaluks().add(talukName);
                    districts.put(districtName, district);

                }
            }
            BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, State.class);
            bulkOps.insert(states.values().stream().toList());
            bulkOps.execute();
            bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, District.class);
            bulkOps.insert(districts.values().stream().toList());
            bulkOps.execute();



            long endTime = System.currentTimeMillis();
            System.out.println("time = " + (endTime - startTime));
            System.out.println(districts.size());

//        }
        }
    }
}
