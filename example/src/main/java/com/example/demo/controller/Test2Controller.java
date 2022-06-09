package com.example.demo.controller;

import com.example.demo.dao.ArticleRepository;
import com.example.demo.model.Article;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class Test2Controller {

    @Autowired
    private ArticleRepository articleRepository;

    @RequestMapping("/test2")
    public List<Article> testArticle() {
        return articleRepository.findAll();
    }

}