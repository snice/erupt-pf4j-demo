package com.github.snice.erupt.pf4j.jpa.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;
import org.springframework.core.annotation.Order;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import xyz.erupt.core.service.EruptCoreService;
import xyz.erupt.core.util.EruptSpringUtil;
import xyz.erupt.core.view.EruptModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Aspect
@Order(1)
@Slf4j
public class EruptTransactionInterceptor {

    private List<TransactionManager> transactionManagers = new ArrayList<>();

    public void addTransactionManager(TransactionManager transactionManager) {
        if (!transactionManagers.contains(transactionManager)) transactionManagers.add(transactionManager);
    }

    public void removeTransactionManager(TransactionManager transactionManager) {
        if (transactionManagers.contains(transactionManager)) transactionManagers.remove(transactionManager);
    }

    @Pointcut("execution(public * xyz.erupt.core.controller.*.*(..)) && @annotation(javax.transaction.Transactional)")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object tsBefore(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("Erupt-pf4j JPA 事物拦截器");
        TransactionInterceptor transactionInterceptor = EruptSpringUtil.getApplicationContext().getBean(TransactionInterceptor.class);
        transactionInterceptor.setTransactionManager(null);
        if (joinPoint instanceof MethodInvocationProceedingJoinPoint) {
            MethodInvocationProceedingJoinPoint invocationProceedingJoinPoint = ((MethodInvocationProceedingJoinPoint) joinPoint);
            Object[] args = invocationProceedingJoinPoint.getArgs();
            if (args != null && args.length > 0) {
                EruptModel eruptModel = EruptCoreService.getErupt(args[0].toString());
                if (eruptModel != null) {
                    if (!transactionManagers.isEmpty()) {
                        List<TransactionManager> tms = transactionManagers.stream().filter(tm -> {
                            try {
                                return ((JpaTransactionManager) tm).getEntityManagerFactory().getMetamodel().entity(eruptModel.getClazz()) != null;
                            } catch (Exception e) {
                                return false;
                            }
                        }).collect(Collectors.toList());
                        if (tms != null && !tms.isEmpty()) {
                            transactionInterceptor.setTransactionManager(tms.get(0));
                        }
                    }
                }
            }
        }
        return joinPoint.proceed();
    }


}
