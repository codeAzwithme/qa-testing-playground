package com.restfulbooker.automation.clients;

import io.restassured.response.Response;

import com.restfulbooker.automation.config.ConfigManager;
import com.restfulbooker.automation.constants.Endpoints;
import com.restfulbooker.automation.constants.StatusCodes;
import com.restfulbooker.automation.models.Auth;
import com.restfulbooker.automation.models.AuthResponse;
import com.restfulbooker.automation.specs.BaseSpec;

public class AuthApi extends BaseApi {

    public AuthApi() {
        super(BaseSpec.getRequestSpec());
    }

    public Response createToken(Auth credentials) {
        return request()
                .body(credentials)
                .when()
                .post(Endpoints.AUTH);
    }

    public String generateToken() {
        Response response = createToken(new Auth(ConfigManager.username(), ConfigManager.password()));
        response.then().spec(BaseSpec.getResponseSpec(StatusCodes.OK));
        String token = response.as(AuthResponse.class).getToken();
        if (token == null || token.isBlank()) {
            throw new IllegalStateException("Auth response did not contain a token: " + response.asString());
        }
        return token;
    }
}
