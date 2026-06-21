package org.dtt.msorder.service.sagaService;

public interface IStep {
    void execute(SagaContext context) throws Exception;
    void compensate(SagaContext context);
}