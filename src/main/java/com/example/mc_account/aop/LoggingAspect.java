package com.example.mc_account.aop;


import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Arrays;
import java.util.Map;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Around("@annotation(Loggable)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        // Получаем HttpServletRequest
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = null;
        if (requestAttributes instanceof ServletRequestAttributes) {
            request = ((ServletRequestAttributes) requestAttributes).getRequest();
        }

        // Логируем до вызова метода
        log.info("Before execution of {}", joinPoint.getSignature().getName());
        if (request != null) {
            var pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            var parameterMap = request.getParameterMap();

            log.info("Request method: {}", request.getMethod());
            log.info("Request URI: {}", request.getRequestURI());
            log.info("Header Authorization: {}", request.getHeader("Authorization"));

            if (pathVariables != null && !pathVariables.isEmpty()) {
                log.info("Path Variables:");
                pathVariables.forEach((k, v) -> log.info("\t{}: {}", k, v));
            }

            if (!parameterMap.isEmpty()) {
                log.info("Parameter Map:");
                parameterMap.forEach((k, v) -> log.info("\t{}: {}", k, Arrays.toString(v)));
            }
        }

        try {
            // Выполняем целевой метод
            Object result = joinPoint.proceed();

            long duration = System.currentTimeMillis() - start;
            log.info("After returning from {}, with result {}. Execution time: {} ms",
                    joinPoint.getSignature().getName(), result, duration);

            return result;
        } catch (Throwable ex) {
            long duration = System.currentTimeMillis() - start;
            log.error("Method {} threw exception after {} ms", joinPoint.getSignature().getName(), duration, ex);
            throw ex;
        } finally {
            log.info("After execution of {}", joinPoint.getSignature().getName());
        }
    }
}