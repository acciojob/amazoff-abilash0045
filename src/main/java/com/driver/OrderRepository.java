package com.driver;

import io.swagger.models.auth.In;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class OrderRepository {

    HashMap<String,Order> orderHashMap = new HashMap<>();
    HashMap<String,DeliveryPartner> deliveryPartnerHashMap = new HashMap<>();
    HashMap<DeliveryPartner, List<Order>> orderPartnerPairHashMap = new HashMap<>();
    public void addOrder(Order order){
        orderHashMap.put(order.getId(),order);
    }
    public void addDeliveryPartner(DeliveryPartner deliveryPartner){
        deliveryPartnerHashMap.put(deliveryPartner.getId(),deliveryPartner);
    }
    public void addOrderPartnerPair(String orderId,String partnerId){
        Order order = orderHashMap.get(orderId);
        DeliveryPartner partner = deliveryPartnerHashMap.get(partnerId);
        List<Order> orderList;
        if (orderPartnerPairHashMap.containsKey(partner)){
            orderList = orderPartnerPairHashMap.get(partner);
            orderList.add(order);
            orderPartnerPairHashMap.put(partner,orderList);
        }else{
            orderList = new ArrayList<>();
            orderList.add(order);
            orderPartnerPairHashMap.put(partner,orderList);
        }
    }
    public Order getOrderById(String orderId){
        return orderHashMap.get(orderId);
    }
    public DeliveryPartner getPartnerById(String partnerId){
        return deliveryPartnerHashMap.get(partnerId);
    }
    public Integer getOrderCountByPartnerId(String partnerId){
        return deliveryPartnerHashMap.get(partnerId).getNumberOfOrders();
    }
    public List<String> getOrdersByPartnerId(String partnerId){
        List<String> orders = new ArrayList<>();
        for (Order order : orderPartnerPairHashMap.get(partnerId)){
            orders.add(order.getId());
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
        Integer count = 0;
        for (Order order : orderHashMap.values()){
            boolean flag = false;
            for (List<Order> orders : orderPartnerPairHashMap.values()){
                for (Order order1 : orders){
                    if (order1.equals(order)){
                        flag = true;
                        break;
                    }
                }
            }
            if (flag == false){
                count ++;
            }
        }
        return count;
    }
    public Integer getOrdersLeftAfterGivenTimeByPartnerId(String time,String partnerId){
        Integer count = 0;
        String[] str = time.split(":");
        Integer currTime = Integer.parseInt(str[0])*60 + Integer.parseInt(str[1]);
        List<Order> orders = orderPartnerPairHashMap.get(deliveryPartnerHashMap.get(partnerId));
        for (Order order : orders){
            if (order.getDeliveryTime() < currTime){
                count++;
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
        orderPartnerPairHashMap.remove(deliveryPartnerHashMap.get(partnerId));
        deliveryPartnerHashMap.remove(partnerId);
    }
    public void deleteOrderById(String orderId){
        for (List<Order> orders : orderPartnerPairHashMap.values()){
            for (Order order : orders){
                if (order.getId().equals(orderId)){
                    orders.remove(order);
                    orderHashMap.remove(orderId);
                    return;
                }
            }
        }
    }
}
