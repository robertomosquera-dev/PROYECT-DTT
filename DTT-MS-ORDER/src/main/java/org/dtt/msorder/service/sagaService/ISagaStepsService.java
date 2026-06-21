package org.dtt.msorder.service.sagaService;

import java.util.List;

public interface ISagaStepsService<Steps,T> {
    T runSteps(List<Steps> base,SagaContext context);
}
