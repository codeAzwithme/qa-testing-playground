package com.restfulbooker.automation.tests;

import org.testng.annotations.Test;

import com.restfulbooker.automation.clients.PingApi;
import com.restfulbooker.automation.constants.StatusCodes;
import com.restfulbooker.automation.specs.BaseSpec;

public class PingTest {

    private final PingApi pingApi = new PingApi();

    @Test(description = "Ping - HealthCheck: answers 201 within the response time limit")
    public void healthCheckReturnsCreated() {
        pingApi.healthCheck()
                .then()
                .spec(BaseSpec.getResponseSpec(StatusCodes.CREATED));
    }
}
