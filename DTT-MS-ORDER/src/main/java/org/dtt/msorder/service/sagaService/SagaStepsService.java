package org.dtt.msorder.service.sagaService;

import java.util.ArrayList;
import java.util.List;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dtt.msorder.dto.Response.OrderResponse;
import org.dtt.msorder.mapper.ManualOrderMapping;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SagaStepsService implements ISagaStepsService<IStep, OrderResponse> {

    private final ManualOrderMapping manualOrderMapping;

    @Override
    public OrderResponse runSteps(List<IStep> steps, SagaContext context) {

        List<IStep> executedSteps = new ArrayList<>();

        for (IStep step : steps) {
            try {
                log.info("Ejecutando paso: {}", step.getClass().getSimpleName());
                step.execute(context);
                executedSteps.add(step);
            } catch (Exception e) {
                log.error("Error en paso {}: {}", step.getClass().getSimpleName(), e.getMessage(), e);
                compensate(executedSteps, context);
                throw new RuntimeException("Saga fallida", e);
            }
        }

        return manualOrderMapping.toResponse(context);
    }

    private void compensate(List<IStep> executedSteps, SagaContext context) {
        log.warn("Iniciando compensación...");

        for (int i = executedSteps.size() - 1; i >= 0; i--) {
            IStep step = executedSteps.get(i);
            try {
                log.info("Compensando paso: {}", step.getClass().getSimpleName());
                step.compensate(context);
            } catch (Exception e) {
                log.error("Error compensando {}: {}", step.getClass().getSimpleName(), e.getMessage(), e);
            }
        }

    }
}
