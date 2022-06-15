package com.example.demo.controller;

import com.example.demo.dao.ArticleRepository;
import com.example.demo.model.Article;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.erupt.core.util.EruptSpringUtil;

import javax.persistence.EntityManager;
import java.util.Collection;
import java.util.List;

@RestController
public class Test2Controller {

    @Autowired
    private ArticleRepository articleRepository;

    @RequestMapping("/test2")
    public List<Article> testArticle() {
        Collection<EntityManager> m = EruptSpringUtil.getApplicationContext().getBeansOfType(EntityManager.class).values();
        m.forEach(e -> {
            e.getMetamodel().getEntities();
        });
        return articleRepository.findAll();
    }

}