package com.lrc.missionO2.services;


import com.lrc.missionO2.DTO.Request.CreateOrderRequest;
import com.lrc.missionO2.DTO.Request.PostImagesRequest;
import com.lrc.missionO2.DTO.Response.*;
import com.lrc.missionO2.entity.*;
import com.lrc.missionO2.entity.addressList.District;
import com.lrc.missionO2.entity.addressList.State;
import com.lrc.missionO2.exceptions.APIException;
import com.lrc.missionO2.exceptions.ConstraintException;
import com.lrc.missionO2.exceptions.ItemNotFoundException;
import com.lrc.missionO2.exceptions.UserNotFoundException;
import com.lrc.missionO2.repository.Address.DistrictRepo;
import com.lrc.missionO2.repository.Address.StateRepo;
import com.lrc.missionO2.repository.FileRepo;
import com.lrc.missionO2.repository.OrderRepo;
import com.lrc.missionO2.repository.PlantRepo;
import com.lrc.missionO2.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.apache.xpath.operations.Or;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;


@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepo orderRepo;
    private final UserRepo userRepo;
    private final FileRepo fileRepo;
    private final PlantRepo plantRepo;
    private final MongoOperations mongoOperations;
    private final AddressService addressService;
    private final MongoTemplate mongoTemplate;
    private final ImageService imageService;
    private final StateRepo stateRepo;
    private final DistrictRepo districtRepo;
    private final FileService fileService;

    public static boolean onlyDigits(String str) {

        for (int i = 0; i < str.length(); i++) {

            if (!Character.isDigit(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }
    public long getTotalOrderCount() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group().count().as("totalOrderCount")
        );

        AggregationResults<OrderCountResult> results = mongoTemplate.aggregate(aggregation, "orders", OrderCountResult.class);
        OrderCountResult result = results.getUniqueMappedResult();

        if (result != null) {
            return result.getTotalOrderCount();
        } else {
            return 0; // No orders found, return 0
        }
    }

    public String createOrder(CreateOrderRequest createOrderRequest) {
        String id = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepo.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User Not Found"));
        if (onlyDigits(createOrderRequest.getAddress().getPinCode())){
            throw new ConstraintException("Pin Code should not contain characters");
        }
        Order order = new Order();
        order.setUser(user.getId());
        order.setOrderNum("MO2-" + (getTotalOrderCount() + 1));
        order.setOrderStatus(OrderStatus.PENDING);
        order.setDistrict(createOrderRequest.getDistrict());
        order.setTaluk(createOrderRequest.getTaluk());
        order.setState(createOrderRequest.getState());
        order.setAddress(createOrderRequest.getAddress());
        order.setLocationURL(createOrderRequest.getLocationURL());
        order.setOrderDate(new Date());
        order.setImages(createOrderRequest.getImages().stream().map((img) -> {
            FileData fileData = new FileData();
            try {

                fileData.setData(imageService.compressImages(img));
                fileData.setFileName(img.getOriginalFilename());
                fileRepo.save(fileData);
                return fileData.getId();
            } catch (IOException e) {
                throw new APIException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }).toList());
        double price = 0;
        int total = 0;
        List<OrderProduct> orderProducts = new ArrayList<>();
        for (OrderProduct orderProduct : createOrderRequest.getProducts()) {
            Plant plant = plantRepo.findById(orderProduct.getPlantId()).orElseThrow(() -> new ItemNotFoundException("Plant with id: " + id + " Not Found"));
            price += Objects.equals(orderProduct.getType(), "seed") ? plant.getSeedPrice() : plant.getPlantPrice();
            total += orderProduct.getQuantity();
            orderProducts.add(
                    OrderProduct.builder()
                            .plantName(plant.getName())
                            .plantId(plant.getId())
                            .quantity(orderProduct.getQuantity())
                            .type(orderProduct.getType())
                            .build());


        }
        order.setProducts(orderProducts);
        order.setTotalPlants(total);
        order.setTotalPrice(price);

        orderRepo.save(order);
        return order.getId();
    }

    public PaginatedResponse<OrderListResponse> viewOrders(
            String user, String status, Integer offSet, Integer size, String state, String district, String taluk, Date fromDate, Date toDate, String orderNum) {
        Pageable pageable = PageRequest.of(offSet, size);
        List<Order> orders = getOrders(user, status, state, district, taluk, fromDate, toDate, orderNum);

        List<Order> paginatedOrders = orders.stream()
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .toList();
        long totalCount = orders.size();
        int totalPages = (int) Math.ceil((double) totalCount / size);

        PaginatedResponse<OrderListResponse> response = new PaginatedResponse<>();
        response.setData(new PageImpl<>(paginatedOrders, pageable, orders.size()).map((order) ->
                        OrderListResponse.builder()
                                .id(order.getId())
                                .orderNum(order.getOrderNum())
                                .orderDate(order.getOrderDate())
                                .orderStatus(order.getOrderStatus().name())
                                .totalPrice(order.getTotalPrice())
                                .state(order.getState())
                                .district(order.getDistrict())
                                .taluk(order.getTaluk())
                                .approvedBy(order.getApprovedBy() != null ? order.getApprovedBy() : "Not Approved")
                                .totalPlant(order.getTotalPlants())
                                .build()
                )
                .toList());
        response.setTotal(totalCount);
        response.setTotalPages(totalPages);
        return response;
    }

    public List<Order> getOrders(
            String user, String status, String state, String district, String taluk, Date fromDate, Date toDate, String orderNum
    ) {
        Query query = new Query();
        if (state != null) {
            List<State> selectedState = stateRepo.findAllByStateNameLikeIgnoreCase(state);
            query.addCriteria(Criteria.where("state").in(selectedState.stream().map(State::getStateName).toArray()));
        }
        if (district != null) {
            List<District> selectedDistrict = districtRepo.findAllByDistrictNameLikeIgnoreCase(district);
            query.addCriteria(Criteria.where("district").in(selectedDistrict.stream().map(District::getDistrictName).toArray()));
        }
        if (fromDate != null && toDate != null) {
            toDate.setTime(toDate.getTime() + (1000 * 60 * 60 * 24));
            System.out.println("toDate = " + toDate);
            query.addCriteria(Criteria.where("orderDate").gte(fromDate).lte(toDate));
        }
        if (status != null) {
            query.addCriteria(Criteria.where("orderStatus").is(OrderStatus.valueOf(status)));
        }

        query.with(Sort.by(Sort.Direction.DESC, "orderDate"));
        if (user != null) {
            query.addCriteria(Criteria.where("user").is(user));
        } else if (SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().contains(new SimpleGrantedAuthority(UserRole.ROLE_USER.name()))) {
            query.addCriteria(Criteria.where("user")
                    .is(SecurityContextHolder.getContext().getAuthentication().getName()));
        }
        List<Order> orders = mongoOperations.find(query, Order.class);
        if (taluk != null) {
            orders = orders.stream().filter(order -> order.getTaluk().contains(taluk.toUpperCase())).toList();
        }
        if (orderNum != null) {
            System.out.println(orderNum.toUpperCase());
            orders = orders.stream().filter(order -> order.getOrderNum().contains(orderNum.toUpperCase())).toList();
        }

        return orders;

    }

    public OrderResponse viewOrder(String id) {
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Order with id : " + id + " Not Found"));
        return OrderResponse.builder()
                .id(order.getId())
                .orderNum(order.getOrderNum())
                .orderStatus(order.getOrderStatus().name())
                .locationURL(order.getLocationURL())
                .totalPrice(order.getTotalPrice())
                .totalPlant(order.getTotalPlants())
                .userId(order.getUser())
                .address(order.getAddress())
                .state(order.getState())
                .district(order.getDistrict())
                .taluk(order.getTaluk())
                .orderDate(order.getOrderDate())
                .postImages(order.getPostImages())
                .user(userRepo.findById(order.getUser())
                        .orElseThrow(() -> new UserNotFoundException("User Not Found")).getUsername())
                .approvedBy(order.getApprovedBy() != null ? order.getApprovedBy() : "Not Approved")
                .products(order.getProducts())
                .images(order.getImages())
                .build();

    }

    public String updateStatus(String id, String status) {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepo.findById(name)
                .orElseThrow(() -> new UserNotFoundException("User Not Found"));
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Order with Id : " + id + " Not Found"));
        order.setOrderStatus(OrderStatus.valueOf(status));
        order.setApprovedBy(user.getUsername());
        orderRepo.save(order);
        return "Order Status set - " + status;
    }

    public List<PlaceCount> viewCount(String state, String district) {
        Aggregation aggregation;

        if (district == null && state == null) {
            aggregation = newAggregation(
                    Aggregation.match(Criteria.where("orderStatus").is(OrderStatus.COMPLETED)),
                    Aggregation.group("state").sum("totalPlants").as("count")
            );
        } else if (state != null && district == null) {
            aggregation = newAggregation(
                    Aggregation.match(Criteria.where("state").is(state)),
                    Aggregation.match(Criteria.where("orderStatus").is(OrderStatus.COMPLETED)),
                    Aggregation.group("district").sum("totalPlants").as("count")
            );
        } else {
            aggregation = newAggregation(
                    Aggregation.match(Criteria.where("district").is(district)),
                    Aggregation.match(Criteria.where("orderStatus").is(OrderStatus.COMPLETED)),
                    Aggregation.group("taluk").sum("totalPlants").as("count")
            );
        }

        AggregationResults<PlaceCount> result = mongoTemplate.aggregate(
                aggregation, "orders", PlaceCount.class
        );
        List<PlaceCount> modifiableResult = new ArrayList<>(result.getMappedResults());
        List<String> resultPlaces = modifiableResult.stream().map(PlaceCount::get_id).toList();
        ListResponse allPlaces = addressService.getPlace(state, district);
        for (String place : allPlaces.getElements()) {
            if (!resultPlaces.contains(place)) {

                modifiableResult.add(
                        PlaceCount.builder().count(0)._id(place).build()
                );

            }
        }
        modifiableResult.sort(Comparator.comparing(PlaceCount::get_id));
        return modifiableResult;

    }

    public long getTotalCount() {
        Aggregation aggregation = newAggregation(
                match(Criteria.where("orderStatus").is(OrderStatus.COMPLETED)), // Filter completed orders
                group().sum("totalPlants").as("totalPlantsSum")
        );

        AggregationResults<SumResult> results = mongoTemplate.aggregate(aggregation, "orders", SumResult.class);
        return results.getMappedResults().isEmpty() ? 0 : results.getMappedResults().get(0).getTotalPlantsSum();

    }

    public String getOrderStatus(String id) {
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Order with id: " + id + " not Found"));
        return order.getOrderStatus().name();
    }

    public String addPostImages(String orderId, PostImagesRequest postImagesRequest) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ItemNotFoundException("Order with id: " + orderId + " not Found"));
        if  (!Objects.equals(order.getOrderStatus(), OrderStatus.APPROVED)){
            throw new ConstraintException("Order Not yet Approved");
        }
        order.setPostImages(postImagesRequest.getImages().stream().map((img) -> {
            FileData fileData = new FileData();
            try {
                fileData.setData(imageService.compressImages(img));
                fileData.setFileName(img.getOriginalFilename());
                fileRepo.save(fileData);
                return fileData.getId();
            } catch (IOException e) {
                throw new APIException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }).toList());
        order.setOrderStatus(OrderStatus.COMPLETED);
        orderRepo.save(order);
        return "Images Added";
    }

    public List<OrderSummaryDTO> getOrderSummaryForLast7Days() {
        Date currentDate = removeTimeComponent(new Date());
        currentDate.setTime(currentDate.getTime() + (1000 * 60 * 60 * 24));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        Date startDate = calendar.getTime();

        List<Order> orders = orderRepo.findAllByOrderDateBetweenAndOrderStatus(startDate, currentDate, OrderStatus.COMPLETED);
        Map<Date, Integer> dateTotalPlantsMap = new HashMap<>();

        Calendar dateIterator = Calendar.getInstance();
        dateIterator.setTime(startDate);
        while (dateIterator.getTime().before(currentDate)) {
            dateTotalPlantsMap.put(dateIterator.getTime(), 0);
            dateIterator.add(Calendar.DAY_OF_YEAR, 1);
        }
        for (Order order : orders) {

            Date orderDateWithoutTime = removeTimeComponent(order.getOrderDate());
            dateTotalPlantsMap.put(orderDateWithoutTime, dateTotalPlantsMap.getOrDefault(orderDateWithoutTime, 0)+order.getTotalPlants());
        }

        return dateTotalPlantsMap.entrySet().stream()
                .map(entry -> new OrderSummaryDTO(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(OrderSummaryDTO::getDate)).collect(Collectors.toList());
    }

    private Date removeTimeComponent(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public OrderStats findHighestOrderStats() {
        // Find all orders
        List<Order> orders = orderRepo.findAllByOrderStatus(OrderStatus.COMPLETED);

        // Pre-fetch user information and store it in a map
        Map<String, String> userNames = new HashMap<>();
        for (Order order : orders) {
            String userId = order.getUser();
            if (!userNames.containsKey(userId)) {
                if (userRepo.findById(userId).isPresent()) {
                    User user = userRepo.findById(userId).get();
                    userNames.put(userId, user.getUsername());
                }else{
                    userNames.put(userId, "user Name");
                }
            }
        }

        // Create maps to store order counts
        Map<String, Integer> plantCountMap = new HashMap<>();
        Map<String, Integer> userCountMap = new HashMap<>();
        Map<String, Integer> stateCountMap = new HashMap<>();
        Map<String, Integer> districtCountMap = new HashMap<>();
        Map<String, Integer> talukCountMap = new HashMap<>();

        // Calculate the order count for each combination and update the map
        for (Order order : orders) {
            String user = userNames.get(order.getUser());
            String state = order.getState();
            String district = order.getDistrict();
            String taluk = order.getTaluk();

            for (OrderProduct product : order.getProducts()) {
                String plant = product.getPlantName();
                plantCountMap.put(plant, plantCountMap.getOrDefault(plant, 0) + product.getQuantity());
                userCountMap.put(user, userCountMap.getOrDefault(user, 0) + product.getQuantity());
                stateCountMap.put(state, stateCountMap.getOrDefault(state, 0) + product.getQuantity());
                districtCountMap.put(district, districtCountMap.getOrDefault(district, 0) + product.getQuantity());
                talukCountMap.put(taluk, talukCountMap.getOrDefault(taluk, 0) + product.getQuantity());
            }
        }

        // Find the combination with the highest order count of users
        Map.Entry<String, Integer> maxUserEntry = Collections.max(userCountMap.entrySet(), Map.Entry.comparingByValue());

        // Find the combination with the highest order count of state
        Map.Entry<String, Integer> maxStateEntry = Collections.max(stateCountMap.entrySet(), Map.Entry.comparingByValue());

        // Find the combination with the highest order count of district
        Map.Entry<String, Integer> maxDistrictEntry = Collections.max(districtCountMap.entrySet(), Map.Entry.comparingByValue());

        // Find the combination with the highest order count of taluk
        Map.Entry<String, Integer> maxTalukEntry = Collections.max(talukCountMap.entrySet(), Map.Entry.comparingByValue());

        // Find the combination with the highest order count of plant
        Map.Entry<String, Integer> maxPlantEntry = Collections.max(plantCountMap.entrySet(), Map.Entry.comparingByValue());

        OrderStats result = new OrderStats();
        result.setUser(maxUserEntry.getKey());
        result.setUserOrderCount(maxUserEntry.getValue());
        result.setState(maxStateEntry.getKey());
        result.setStateOrderCount(maxStateEntry.getValue());
        result.setDistrict(maxDistrictEntry.getKey());
        result.setDistrictOrderCount(maxDistrictEntry.getValue());
        result.setTaluk(maxTalukEntry.getKey());
        result.setTalukOrderCount(maxTalukEntry.getValue());
        result.setPlant(maxPlantEntry.getKey());
        result.setPlantOrderCount(maxPlantEntry.getValue());
        return result;
    }


    public ListResponse getGalleryImages() {
        Pageable pageable = PageRequest.of(0, 50);

        Query query = new Query().with(pageable);
        query.with(Sort.by(Sort.Direction.DESC, "orderDate"));
        query.addCriteria(Criteria.where("orderStatus").is(OrderStatus.COMPLETED));

        List<Order> orders = mongoOperations.find(query, Order.class);
//        System.out.println("orders = " + orders);
        return ListResponse.builder()
                .elements(orders.stream()
                        .map(order -> {
                            if (order.getPostImages().size() > 0) {
                                return order.getPostImages().get(0);
                            }
                            return null;
                        }).toList())
                .build();
    }

    public ByteArrayResource downloadOrder(
            String user, String state, String district, String taluk, Date fromDate, Date toDate, String status, String type
    ) {
        try {
            String[] heads = {"Order Number", "Order Date", "Location URL", "State", "Taluk", "District", "Order Status", "Total Price", "Total Plants"};
            List<Order> orders = getOrders(user, status, state, district, taluk, fromDate, toDate, null);


            if (Objects.equals(type, "XLS")) {
                return fileService.createOrdersExcel(heads, orders, user, status, state, district, taluk, fromDate, toDate);
            } else if (Objects.equals(type, "PDF")) {
                return fileService.createOrdersPDF(heads, orders);
            } else {
                return null;
            }


        } catch (Exception e) {
            throw new APIException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
