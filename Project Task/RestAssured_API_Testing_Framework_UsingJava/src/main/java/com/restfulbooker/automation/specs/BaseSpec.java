package com.restfulbooker.automation.specs;

import static org.hamcrest.Matchers.lessThan;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.config.LogConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import com.restfulbooker.automation.config.ConfigManager;

public final class BaseSpec {

    private static final String TOKEN_COOKIE = "token";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String JSON_MEDIA_TYPE = "application/json";

    private BaseSpec() {
    }

    public static RequestSpecification getRequestSpec() {
        return baseRequestBuilder().build();
    }

    public static RequestSpecification getTokenAuthRequestSpec(String token) {
        return baseRequestBuilder()
                .addCookie(TOKEN_COOKIE, token)
                .build();
    }

    public static RequestSpecification getBasicAuthRequestSpec() {
        String credentials = ConfigManager.username() + ":" + ConfigManager.password();
        String encoded = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
        return baseRequestBuilder()
                .addHeader(AUTHORIZATION_HEADER, "Basic " + encoded)
                .build();
    }

    public static ResponseSpecification getResponseSpec(int expectedStatusCode) {
        return baseResponseBuilder(expectedStatusCode).build();
    }

    public static ResponseSpecification getJsonResponseSpec(int expectedStatusCode) {
        return baseResponseBuilder(expectedStatusCode)
                .expectContentType(ContentType.JSON)
                .build();
    }

    private static RequestSpecBuilder baseRequestBuilder() {
        return new RequestSpecBuilder()
                .setBaseUri(ConfigManager.baseUri())
                .setContentType(ContentType.JSON)
                .setAccept(JSON_MEDIA_TYPE)
                .setConfig(RestAssuredConfig.config()
                        .logConfig(LogConfig.logConfig()
                                .enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.ALL)));
    }

    private static ResponseSpecBuilder baseResponseBuilder(int expectedStatusCode) {
        return new ResponseSpecBuilder()
                .expectStatusCode(expectedStatusCode)
                .expectResponseTime(lessThan(ConfigManager.maxResponseTimeMillis()), TimeUnit.MILLISECONDS);
    }
}
