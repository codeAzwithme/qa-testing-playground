package com.restfulbooker.automation.clients;

import static io.restassured.RestAssured.given;

import io.restassured.specification.RequestSpecification;

public abstract class BaseApi {

    private final RequestSpecification requestSpec;

    protected BaseApi(RequestSpecification requestSpec) {
        this.requestSpec = requestSpec;
    }

    protected RequestSpecification request() {
        return given().spec(requestSpec);
    }
}
