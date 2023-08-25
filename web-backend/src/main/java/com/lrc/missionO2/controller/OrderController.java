package com.lrc.missionO2.controller;

import com.lrc.missionO2.DTO.Request.CreateOrderRequest;
import com.lrc.missionO2.DTO.Response.MiscResponse;
import com.lrc.missionO2.DTO.Response.OrderResponse;
import com.lrc.missionO2.DTO.Response.PlaceCount;
import com.lrc.missionO2.services.OrderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;


    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/create")
    public ResponseEntity<MiscResponse> createOrder(@ModelAttribute CreateOrderRequest createOrderRequest){
        String response = orderService.createOrder(createOrderRequest);
        return ResponseEntity.ok(MiscResponse.builder().response(response).build());
    }
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/view-orders")
    public ResponseEntity<List<OrderResponse>> viewOrders(){
        return ResponseEntity.ok(orderService.viewOrders(null));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping("/view-orders/{uid}")
    public ResponseEntity<List<OrderResponse>> viewUserOrders(@PathVariable String uid){
        return ResponseEntity.ok(orderService.viewOrders(uid));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping("/view-order/{id}")
    public ResponseEntity<OrderResponse> viewOrder(@PathVariable String id){
        return ResponseEntity.ok(orderService.viewOrder(id));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/approve/{id}/{status}")
    public ResponseEntity<MiscResponse> approveOrder(@PathVariable String id, @PathVariable String status){
        String response = orderService.updateStatus(id, status);
        return ResponseEntity.ok(MiscResponse.builder().response(response).build());
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/view-count")
    public ResponseEntity<List<PlaceCount>> viewCount(
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(value = "district", required = false) String district
    ){
        return ResponseEntity.ok(orderService.viewCount(state, district));
    }

}
