package com.lrc.missionO2.controller;

import com.lrc.missionO2.DTO.Request.CreateOrderRequest;
import com.lrc.missionO2.DTO.Request.PostImagesRequest;
import com.lrc.missionO2.DTO.Response.*;
import com.lrc.missionO2.services.OrderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;


    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/create")
    public ResponseEntity<MiscResponse> createOrder(@ModelAttribute CreateOrderRequest createOrderRequest) {
        String response = orderService.createOrder(createOrderRequest);
        return ResponseEntity.ok(MiscResponse.builder().response(response).build());
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_ADMIN_ASSIST')")
    @GetMapping("/view-orders/{offSet}/{size}")
    public ResponseEntity<PaginatedResponse<OrderListResponse>> viewOrders(
            @PathVariable Integer offSet,
            @PathVariable Integer size,
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(value = "district", required = false) String district,
            @RequestParam(value = "taluk", required = false) String taluk,
            @RequestParam(value = "from", required = false) Date fromDate,
            @RequestParam(value = "to", required = false) Date toDate,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "orderNum", required = false) String orderNum
    ) {
        return ResponseEntity.ok(
                orderService.viewOrders(null, status, offSet, size, state, district, taluk, fromDate, toDate, orderNum)
        );
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER', 'ROLE_ADMIN_ASSIST')")
    @GetMapping("/view-user-orders/{offSet}/{size}")
    public ResponseEntity<PaginatedResponse<OrderListResponse>> viewUserOrders(
            @PathVariable Integer offSet,
            @PathVariable Integer size,
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(value = "district", required = false) String district,
            @RequestParam(value = "taluk", required = false) String taluk,
            @RequestParam(value = "user", required = false) String user,
            @RequestParam(value = "from", required = false) Date fromDate,
            @RequestParam(value = "to", required = false) Date toDate,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "orderNum", required = false) String orderNum

    ) {
        return ResponseEntity.ok(
                orderService.viewOrders(user, status, offSet, size, state, district, taluk, fromDate, toDate, orderNum)
        );
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER',  'ROLE_ADMIN_ASSIST')")
    @GetMapping("/view-order/{orderId}")
    public ResponseEntity<OrderResponse> viewOrder(@PathVariable String orderId) {
        return ResponseEntity.ok(orderService.viewOrder(orderId));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_ADMIN_ASSIST')")
    @GetMapping("/view-pending-orders/{offSet}/{size}")
    public ResponseEntity<PaginatedResponse<OrderListResponse>> viewPendingOrders(
            @PathVariable Integer offSet,
            @PathVariable Integer size,
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(value = "district", required = false) String district,
            @RequestParam(value = "taluk", required = false) String taluk,
            @RequestParam(value = "from", required = false) Date fromDate,
            @RequestParam(value = "to", required = false) Date toDate,
            @RequestParam(value = "orderNum", required = false) String orderNum
    ) {
        return ResponseEntity.ok(
                orderService.viewOrders(null, "PENDING", offSet, size, state, district, taluk, fromDate, toDate, orderNum)
        );
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_ADMIN_ASSIST')")
    @PostMapping("/status/{id}/{status}")
    public ResponseEntity<MiscResponse> approveOrder(@PathVariable String id, @PathVariable String status) {
        String response = orderService.updateStatus(id, status);
        return ResponseEntity.ok(MiscResponse.builder().response(response).build());
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN_ASSIST', 'ROLE_ADMIN')")
    @GetMapping("/status/{id}")
    public ResponseEntity<MiscResponse> getOrderStatus(@PathVariable String id) {
        String response = orderService.getOrderStatus(id);
        return ResponseEntity.ok(MiscResponse.builder().response(response).build());
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_ADMIN_ASSIST')")
    @GetMapping("/view-count")
    public ResponseEntity<List<PlaceCount>> viewCount(
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(value = "district", required = false) String district
    ) {
        return ResponseEntity.ok(orderService.viewCount(state, district));
    }

    @GetMapping("/total-plants")
    public ResponseEntity<MiscResponse> getTotalPlantedCount() {
        String count = String.valueOf(orderService.getTotalCount());
        return ResponseEntity.ok(MiscResponse.builder().response(count).build());
    }

//    @SecurityRequirement(name = "Bearer Authentication")
//    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_ADMIN_ASSIST')")
    @GetMapping("/get-order-summary")
    public ResponseEntity<List<OrderSummaryDTO>> getOrderSummary() {
        return ResponseEntity.ok(orderService.getOrderSummaryForLast7Days());
    }

    @GetMapping("/get-state-analysis")
    public ResponseEntity<List<PlaceCount>> getStateAnalysis(){
        return ResponseEntity.ok(orderService.viewCount(null, null));
    }

    @GetMapping("/analysis")
    public ResponseEntity<OrderStats> getStats(){
        return ResponseEntity.ok(orderService.findHighestOrderStats());
    }

    @PostMapping("/add-post-images/{orderId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<MiscResponse> addPostImages(@PathVariable String orderId, @ModelAttribute PostImagesRequest postImagesRequest) {
        String response = orderService.addPostImages(orderId, postImagesRequest);
        return ResponseEntity.ok(MiscResponse.builder().response(response).build());

    }

    @GetMapping("/index-gallery")
    public ResponseEntity<ListResponse> getGalleryImages() {
        return ResponseEntity.ok(orderService.getGalleryImages());
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_ADMIN_ASSIST')")
    @GetMapping("/download")
    public ResponseEntity<Resource> downloadOrders(
            @RequestParam(value = "user", required = false) String user,
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(value = "district", required = false) String district,
            @RequestParam(value = "taluk", required = false) String taluk,
            @RequestParam(value = "from", required = false) Date fromDate,
            @RequestParam(value = "to", required = false) Date toDate,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "type") String type

    ) {
        HttpHeaders headers = new HttpHeaders();
        MediaType mediaType = null;
        if (Objects.equals(type, "XLS")) {
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Orders.xlsx");
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        } else if (Objects.equals(type, "PDF")) {
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Orders.pdf");
            mediaType = MediaType.APPLICATION_PDF;
        }
        assert mediaType != null;
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(mediaType)
                .body(orderService.downloadOrder(user, state, district, taluk, fromDate, toDate, status, type));
    }


}
