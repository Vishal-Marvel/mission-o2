package com.lrc.missionO2.controller;

import com.lrc.missionO2.DTO.Request.CreateOrderRequest;
import com.lrc.missionO2.DTO.Response.MiscResponse;
import com.lrc.missionO2.DTO.Response.OrderListResponse;
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
    public ResponseEntity<List<OrderListResponse>> viewOrders(){
        return ResponseEntity.ok(orderService.viewOrders(null));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping("/view-user-orders")
    public ResponseEntity<List<OrderListResponse>> viewUserOrders(
            @RequestParam(value = "user", required = false) String user){
        return ResponseEntity.ok(orderService.viewOrders(user));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping("/view-order/{orderId}")
    public ResponseEntity<OrderResponse> viewOrder(@PathVariable String orderId){
        return ResponseEntity.ok(orderService.viewOrder(orderId));
    }

    @SecurityRequirement(name= "Bearer Authentication")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/view-pending-orders")
    public ResponseEntity<List<OrderListResponse>> viewPendingOrders(){
        return ResponseEntity.ok(orderService.viewPendingOrders());
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/status/{id}/{status}")
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

    @GetMapping("/total-plants")
    public ResponseEntity<MiscResponse> getTotalPlantedCount(){
        String count= String.valueOf(orderService.getTotalCount());
        return ResponseEntity.ok(MiscResponse.builder().response(count).build());
    }

}
