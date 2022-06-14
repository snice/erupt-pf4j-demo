package com.itfenbao.plugin2.service;

import org.springframework.stereotype.Service;

@Service("testService2")
public class TestServiceImpl implements TestService {

    public TestServiceImpl() {
        System.out.println("TestServiceImpl2===" + this);
    }

    @Override
    public void say(String name) {
        System.out.println("Hi," + name);
    }
}
