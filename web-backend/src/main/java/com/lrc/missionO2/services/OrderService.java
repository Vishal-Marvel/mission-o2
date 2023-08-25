package com.lrc.missionO2.services;

import com.lrc.missionO2.DTO.Request.CreateOrderRequest;
import com.lrc.missionO2.DTO.Response.OrderResponse;
import com.lrc.missionO2.DTO.Response.PlaceCount;
import com.lrc.missionO2.entity.*;
import com.lrc.missionO2.entity.addressList.District;
import com.lrc.missionO2.entity.addressList.State;
import com.lrc.missionO2.exceptions.ItemNotFoundException;
import com.lrc.missionO2.exceptions.UserNotFoundException;
import com.lrc.missionO2.repository.Address.DistrictRepo;
import com.lrc.missionO2.repository.Address.StateRepo;
import com.lrc.missionO2.repository.FileRepo;
import com.lrc.missionO2.repository.OrderRepo;
import com.lrc.missionO2.repository.PlantRepo;
import com.lrc.missionO2.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.DoubleStream;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepo orderRepo;
    private final UserRepo userRepo;
    private final FileRepo fileRepo;
    private final PlantRepo plantRepo;
    private final MongoOperations mongoOperations;
    private final StateRepo stateRepo;
    private final DistrictRepo districtRepo;


    private byte[] getImage(String id){
        return fileRepo.findById(id).orElseThrow(()->new ItemNotFoundException("Image with id: " + id + " not Found"))
                .getData();
    }

    public String createOrder(CreateOrderRequest createOrderRequest) {
        String id = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepo.findById(id)
                .orElseThrow(()->new UserNotFoundException("User Not Found"));
        Order order = new Order();
        order.setUser(user.getId());
        order.setOrderStatus("PENDING");
        order.setDistrict(createOrderRequest.getDistrict());
        order.setTaluk(createOrderRequest.getTaluk());
        order.setState(createOrderRequest.getState());
        order.setLocationURL(createOrderRequest.getLocationURL());
        order.setImages(createOrderRequest.getImages().stream().map((img)->{
            FileData fileData = new FileData();
            try {
                fileData.setData(img.getBytes());
                fileRepo.save(fileData);
                return fileData.getId();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).toList());
        order.setTotalPrice(createOrderRequest.getProducts().stream().mapToDouble((product)->{
            float price;
            Plant plant = plantRepo.findById(product.getPlantId())
                    .orElseThrow(()->new ItemNotFoundException("Product Not Found"));
            if (Objects.equals(product.getType(), "seed")){
                price = plant.getSeedPrice()*product.getQuantity();
            }
            else{
                price = plant.getPlantPrice()*product.getQuantity();
            }
            return price;

        }).sum());
        order.setTotalPlants(createOrderRequest.getProducts().stream().mapToInt(OrderProduct::getQuantity).sum());
        order.setProducts(createOrderRequest.getProducts());

        orderRepo.save(order);
        return "Order Saved";
    }

    public List<OrderResponse> viewOrders(String user) {
        Query query = new Query();

        if (user != null) {
            query.addCriteria(Criteria.where("user").is(user));
        }
        List<Order> orders = mongoOperations.find(query, Order.class);
        return orders.stream().map((order)->
                OrderResponse.builder()
                        .id(order.getId())
                        .orderNum(order.getOrderNum())
                        .orderStatus(order.getOrderStatus())
                        .totalPrice(order.getTotalPrice())
                        .state(order.getState())
                        .district(order.getDistrict())
                        .taluk(order.getTaluk())
                        .build()
                )
                .toList();
    }

    public OrderResponse viewOrder(String id) {
        Order order = orderRepo.findById(id)
                .orElseThrow(()->new ItemNotFoundException("Order with id : " + id + " Not Found"));
        return OrderResponse.builder()
                .id(order.getId())
                .orderNum(order.getOrderNum())
                .orderStatus(order.getOrderStatus())
                .locationURL(order.getLocationURL())
                .totalPrice(order.getTotalPrice())
                .totalPlant(order.getTotalPlants())
                .user(userRepo.findById(order.getUser())
                        .orElseThrow(() -> new UserNotFoundException("User Not Found")).getUsername())
                .products(order.getProducts())
                .state(order.getState())
                .district(order.getDistrict())
                .taluk(order.getTaluk())
                .images(order.getImages().stream().map(this::getImage).toList())
                .build();

    }

    public String updateStatus(String id, String status) {
        Order order = orderRepo.findById(id)
                .orElseThrow(()-> new ItemNotFoundException("Order with Id : " + id + " Not Found"));
        order.setOrderStatus(status);
        orderRepo.save(order);
        return "Order Status set - " + status;
    }

    public List<PlaceCount> viewCount(String state, String district) {
        if (district==null && state==null){
            List<String> states = stateRepo.findAll().stream().map(State::getStateName).toList();
            return states.stream().map((place)->
                PlaceCount.builder()
                        .place(place)
                        .count(orderRepo.findAllByState(place).stream().mapToInt(Order::getTotalPlants).sum())
                        .build())
                    .toList();

        } else if (state!=null && district==null) {
            State requiredState = stateRepo.findByStateNameLikeIgnoreCase(state)
                    .orElseThrow(()->new ItemNotFoundException("State: "+ state+" Not Found"));
            List<String> districts = districtRepo.findAllByState(requiredState).stream()
                    .map(District::getDistrictName).toList();
            return districts.stream().map((place)->
                            PlaceCount.builder()
                                    .place(place)
                                    .count(orderRepo.findAllByDistrict(place).stream().mapToInt(Order::getTotalPlants).sum())
                                    .build())
                    .toList();
            
        }
        else {
            District requiredDistrict = districtRepo.findByDistrictNameLikeIgnoreCase(district)
                    .orElseThrow(()->new ItemNotFoundException("District: "+ district+" Not Found"));
            List<String> taluks = requiredDistrict.getTaluks();
            return taluks.stream().map((place)->
                            PlaceCount.builder()
                                    .place(place)
                                    .count(orderRepo.findAllByTaluk(place).stream().mapToInt(Order::getTotalPlants).sum())
                                    .build())
                    .toList();
        }
    }
}
