package com.lrc.missionO2.DTO.Request;

import com.lrc.missionO2.entity.Address;
import com.lrc.missionO2.entity.OrderProduct;
import com.lrc.missionO2.validators.ValidAddress;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data

public class CreateOrderRequest {
    @NotBlank(message = "LocationURL is mandatory")
    private String locationURL;

    @NotNull(message = "Address is mandatory")
    private Address address;

    @NotNull(message = "Images are mandatory")
    private List<MultipartFile> images;

    @NotNull(message = "Products is mandatory")
    private List<OrderProduct> products;

    @NotBlank(message = "State is mandatory")
    private String state;

    @NotBlank(message = "District is mandatory")
    private String district;

    @NotBlank(message = "Taluk is mandatory")
    private String taluk;

}
