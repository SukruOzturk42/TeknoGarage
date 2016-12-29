package com.musu.service;

import com.musu.model.OrdersEntity;
import com.musu.model.ShoppingCart;
import com.musu.repository.CartRepository;
import com.musu.repository.OrdersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrdersRepository ordersRepository;

    @Override
    public void save(OrdersEntity ordersEntity) {
        ordersRepository.save(ordersEntity);
    }
}