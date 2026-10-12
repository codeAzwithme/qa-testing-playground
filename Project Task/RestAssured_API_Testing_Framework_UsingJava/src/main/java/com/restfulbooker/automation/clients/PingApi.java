package com.restfulbooker.automation.clients;

import io.restassured.response.Response;

import com.restfulbooker.automation.constants.Endpoints;
import com.restfulbooker.automation.specs.BaseSpec;

public class PingApi extends BaseApi {

    public PingApi() {
        super(BaseSpec.getRequestSpec());
    }

    public Response healthCheck() {
        return request()
                .when()
                .get(Endpoints.PING);
    }
}
