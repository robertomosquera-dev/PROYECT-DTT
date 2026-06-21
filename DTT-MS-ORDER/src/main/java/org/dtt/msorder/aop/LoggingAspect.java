package org.dtt.msorder.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Around("@annotation(org.dtt.msorder.aop.TrackExecutionTime)")
    public Object trackTime(
            ProceedingJoinPoint joinPoint
    ) throws Throwable {

        String methodName =
                joinPoint.getSignature().getName();

        String className =
                joinPoint.getTarget()
                        .getClass()
                        .getSimpleName();

        long start = System.nanoTime();

        Object result = joinPoint.proceed();

        long end = System.nanoTime();

        long durationMs =
                (end - start) / 1_000_000;

        log.info(
                "[PERFORMANCE] {}.{} took {} ms",
                className,
                methodName,
                durationMs
        );

        return result;
    }
}
