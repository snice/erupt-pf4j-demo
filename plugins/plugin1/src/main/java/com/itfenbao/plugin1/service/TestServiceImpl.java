package com.itfenbao.plugin1.service;

import org.springframework.stereotype.Service;

@Service
public class TestServiceImpl implements TestService {

    @Override
    public void say(String name) {
        System.out.println("Hi," + name);
    }
}
