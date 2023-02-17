package com.driver;

import io.swagger.models.auth.In;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Repository
public class OrderRepository {

    HashMap<String,Order> orderHashMap = new HashMap<>();
    HashMap<String,DeliveryPartner> deliveryPartnerHashMap = new HashMap<>();
    HashMap<DeliveryPartner, List<Order>> orderPartnerPairHashMap = new HashMap<>();
    HashSet<Order> assigned = new HashSet<>();
    public void addOrder(Order order){
        orderHashMap.put(order.getId(),order);
    }
    public void addDeliveryPartner(DeliveryPartner deliveryPartner){
        deliveryPartnerHashMap.put(deliveryPartner.getId(),deliveryPartner);
    }
    public void addOrderPartnerPair(String orderId,String partnerId){
        if (deliveryPartnerHashMap.containsKey(partnerId) && orderHashMap.containsKey(orderId)){
            List<Order> orders = new ArrayList<>();
            if (orderPartnerPairHashMap.containsKey(deliveryPartnerHashMap.get(partnerId))){
                orders = orderPartnerPairHashMap.get(deliveryPartnerHashMap.get(partnerId));
            }
            orders.add(orderHashMap.get(orderId));
            orderPartnerPairHashMap.put(deliveryPartnerHashMap.get(partnerId),orders);
            assigned.add(orderHashMap.get(orderId));
            deliveryPartnerHashMap.get(partnerId).setNumberOfOrders(orders.size());
        }
    }
    public Order getOrderById(String orderId){
        return orderHashMap.get(orderId);
    }
    public DeliveryPartner getPartnerById(String partnerId){
        return deliveryPartnerHashMap.get(partnerId);
    }
    public Integer getOrderCountByPartnerId(String partnerId){
        Integer count = 0;
        if (orderPartnerPairHashMap.containsKey(deliveryPartnerHashMap.get(partnerId))){
            count = orderPartnerPairHashMap.get(deliveryPartnerHashMap.get(partnerId)).size();
        }
        return count;
    }
    public List<String> getOrdersByPartnerId(String partnerId){
        List<String> orders = new ArrayList<>();
        if (deliveryPartnerHashMap.containsKey(partnerId)) {
            if (orderPartnerPairHashMap.containsKey(deliveryPartnerHashMap.get(partnerId))) {
                List<Order> orderList = orderPartnerPairHashMap.get(deliveryPartnerHashMap.get(partnerId));
                for (Order order : orderList){
                    orders.add(order.getId());
                }
            }
        }
        return orders;
    }
    public List<String> getAllOrders(){
        List<String> orders = new ArrayList<>();
        for (String orderId : orderHashMap.keySet()){
            orders.add(orderId);
        }
        return orders;
    }
    public Integer getCountOfUnassignedOrders(){
        return orderHashMap.size()-assigned.size();
    }
    public Integer getOrdersLeftAfterGivenTimeByPartnerId(String time,String partnerId){
        Integer count = 0;
        String[] str = time.split(":");
        Integer currTime = Integer.parseInt(str[0])*60 + Integer.parseInt(str[1]);
        if (orderPartnerPairHashMap.containsKey(deliveryPartnerHashMap.get(partnerId))){
            List<Order> orders = orderPartnerPairHashMap.get(deliveryPartnerHashMap.get(partnerId));
            for (Order order : orders){
                if (orderHashMap.containsKey(order.getId())){
                    Order currOrder = orderHashMap.get(order.getId());
                    if (currTime < currOrder.getDeliveryTime()){
                        count++;
                    }
                }
            }
        }
        return count;
    }
    public String getLastDeliveryTimeByPartnerId(String partnerId){
        Integer time = 0;
        List<Order> orders = orderPartnerPairHashMap.get(deliveryPartnerHashMap.get(partnerId));
        for (Order order : orders){
            time = Math.max(time,order.getDeliveryTime());
        }
        Integer minutes = time%60;
        Integer hours = time/60;
        String lastTime = "";
        if (hours < 10){
            lastTime = "0"+ hours;
        }
        else{
            lastTime += hours;
        }
        lastTime += ":";
        if (minutes == 0 || minutes < 10){
            lastTime += "0" + minutes;
        }
        else{
            lastTime += minutes;
        }
        return lastTime;
    }
    public void deletePartnerById(String partnerId){
        if (orderPartnerPairHashMap.containsKey(deliveryPartnerHashMap.get(partnerId))){
            List<Order> orders = orderPartnerPairHashMap.get(deliveryPartnerHashMap.get(partnerId));
            for (Order order : orders){
                assigned.remove(order);
            }
            orderPartnerPairHashMap.remove(deliveryPartnerHashMap.get(partnerId));
            deliveryPartnerHashMap.remove(partnerId);
        }
    }
    public void deleteOrderById(String orderId) {
        if (orderHashMap.containsKey(orderId)){
            if (assigned.contains(orderHashMap.get(orderId))){
                assigned.remove(orderHashMap.get(orderId));
            }
            for (List<Order> orderList : orderPartnerPairHashMap.values()){
                for (Order order : orderList){
                    if (order.equals(orderHashMap.get(orderId))){
                        orderList.remove(order);
                        orderHashMap.remove(orderId);
                        return;
                    }
                }
            }
            orderHashMap.remove(orderId);
        }
    }
}
