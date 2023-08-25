package com.lrc.missionO2.DTO.Request;

import com.lrc.missionO2.entity.OrderProduct;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data

public class CreateOrderRequest {

    private String locationURL;
    private String district;
    private String state;
    private String taluk;
    private List<MultipartFile> images;
    private List<OrderProduct> products;

}
