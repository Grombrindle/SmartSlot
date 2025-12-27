package com.appointment.system.AOP;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Aspect
@Component
public class ClearConsoleLogger {
    
    private static final DateTimeFormatter TIME_FORMATTER = 
        DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
    
    private static final String SEPARATOR = "══════════════════════════════════════════════════════════════════";
    private static final String SUB_SEPARATOR = "──────────────────────────────────────────────────────────────";
    
    @Around("execution(* com.appointment.system.controller..*(..)) || " +
            "execution(* com.appointment.system.service..*(..)) || " +
            "execution(* com.appointment.system.repository..*(..))")
    public Object logMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String fullMethod = joinPoint.getSignature().toShortString();
        
        // Skip proxy classes
        if (className.contains("$Proxy") || className.contains("$$")) {
            className = className.substring(0, Math.min(30, className.length()));
        }
        
        // Clean up class name
        if (className.length() > 30) {
            className = "..." + className.substring(className.length() - 27);
        }
        
        // Print START banner
        System.out.println("\n" + SEPARATOR);
        System.out.printf("┌──▶️  METHOD START  [%s]%n", LocalTime.now().format(TIME_FORMATTER));
        System.out.printf("├─ Class: %s%n", className);
        System.out.printf("├─ Method: %s%n", methodName);
        System.out.printf("└─ Signature: %s%n", fullMethod);
        System.out.println(SUB_SEPARATOR);
        
        long startTime = System.currentTimeMillis();
        
        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - startTime;
            
            // Print SUCCESS banner
            System.out.println(SUB_SEPARATOR);
            System.out.printf("┌──✅  SUCCESS  (%d ms)%n", duration);
            System.out.printf("├─ Class: %s%n", className);
            System.out.printf("├─ Method: %s%n", methodName);
            
            // Safely show result info
            if (result != null) {
                if (result instanceof java.util.Collection) {
                    java.util.Collection<?> collection = (java.util.Collection<?>) result;
                    System.out.printf("└─ Returned: %d item(s)%n", collection.size());
                } else {
                    String resultType = result.getClass().getSimpleName();
                    if (resultType.length() > 20) {
                        resultType = resultType.substring(0, 17) + "...";
                    }
                    System.out.printf("└─ Returned: %s%n", resultType);
                }
            } else {
                System.out.println("└─ Returned: null");
            }
            System.out.println(SEPARATOR + "\n");
            
            return result;
            
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            
            // Print ERROR banner
            System.err.println("\n" + SEPARATOR.replace('═', '─'));
            System.err.printf("┌──❌  ERROR  (%d ms)%n", duration);
            System.err.printf("├─ Class: %s%n", className);
            System.err.printf("├─ Method: %s%n", methodName);
            System.err.printf("├─ Exception: %s%n", e.getClass().getSimpleName());
            System.err.printf("├─ Message: %s%n", e.getMessage());
            
            // Find where the error originated in YOUR code
            for (StackTraceElement element : e.getStackTrace()) {
                if (element.getClassName().startsWith("com.appointment.system") &&
                    !element.getClassName().contains("AOP")) {
                    String shortClassName = element.getClassName();
                    if (shortClassName.length() > 30) {
                        shortClassName = "..." + shortClassName.substring(shortClassName.length() - 27);
                    }
                    System.err.printf("└─ Location: %s.%s (line %d)%n", 
                                    shortClassName, 
                                    element.getMethodName(), 
                                    element.getLineNumber());
                    break;
                }
            }
            System.err.println(SEPARATOR.replace('═', '─') + "\n");
            
            throw e;
        }
    }
}