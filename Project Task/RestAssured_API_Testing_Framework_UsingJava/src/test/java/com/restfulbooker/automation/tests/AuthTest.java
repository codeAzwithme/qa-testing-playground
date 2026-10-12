package com.restfulbooker.automation.tests;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.notNullValue;

import org.testng.annotations.Test;

import com.restfulbooker.automation.clients.AuthApi;
import com.restfulbooker.automation.config.ConfigManager;
import com.restfulbooker.automation.constants.StatusCodes;
import com.restfulbooker.automation.models.Auth;
import com.restfulbooker.automation.specs.BaseSpec;

public class AuthTest {

    private final AuthApi authApi = new AuthApi();

    @Test(description = "Auth - CreateToken: admin credentials answer 200 with a token")
    public void validCredentialsReturnToken() {
        authApi.createToken(new Auth(ConfigManager.username(), ConfigManager.password()))
                .then()
                .spec(BaseSpec.getResponseSpec(StatusCodes.OK))
                .body(matchesJsonSchemaInClasspath(Schemas.AUTH_TOKEN))
                .body("token", notNullValue());
    }
}
