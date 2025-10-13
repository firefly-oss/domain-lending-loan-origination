package com.firefly.domain.lending.loan.origination.core.loan.origination.handlers;

import com.firefly.common.cqrs.annotations.QueryHandlerComponent;
import com.firefly.common.cqrs.query.QueryHandler;
import com.firefly.core.lending.origination.sdk.api.ApplicationStatusApi;
import com.firefly.core.lending.origination.sdk.model.ApplicationStatusDTO;
import com.firefly.domain.lending.loan.origination.core.loan.origination.queries.GetApplicationStatusQuery;
import reactor.core.publisher.Mono;

@QueryHandlerComponent(cacheable = false)
public class GetApplicationStatusHandler extends QueryHandler<GetApplicationStatusQuery, ApplicationStatusDTO> {

    private final ApplicationStatusApi applicationStatusApi;

    public GetApplicationStatusHandler(ApplicationStatusApi applicationStatusApi) {
        this.applicationStatusApi = applicationStatusApi;
    }

    @Override
    protected Mono<ApplicationStatusDTO> doHandle(GetApplicationStatusQuery cmd) {
        if(null!=cmd.getApplicationStatusId()){
            return applicationStatusApi.getApplicationStatus(cmd.getApplicationStatusId());
        }
        return applicationStatusApi.getApplicationStatusByCode(cmd.getApplicationStatusCode());
    }
}