package com.itfenbao.plugin1;

import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.Resource;
import javax.sql.DataSource;

@Configuration
@ComponentScan
@EnableJpaRepositories(basePackages = "com.itfenbao.plugin1.repository", entityManagerFactoryRef = "plugin1EntityFactoryBean")
public class PluginConfiguration {

    @Resource
    private EntityManagerFactoryBuilder entityManagerFactoryBuilder;

    @Resource
    private DataSource dataSource;

    @Bean
    public LocalContainerEntityManagerFactoryBean plugin1EntityFactoryBean() {
        return entityManagerFactoryBuilder.dataSource(dataSource).persistenceUnit("plugin1Persistence").packages("com.itfenbao.plugin1.entity").build();
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setPersistenceUnitName("plugin1Persistence");
        return transactionManager;
    }

}
