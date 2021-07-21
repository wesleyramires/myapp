package com.project.myapp.utils;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;

public class MyAppTestUtils {

    public static ValidatableResponse createPost(String url, Object body, HttpStatus httpStatus) {
        return given().
                spec(createRequestSpecification(body)).
                log().
                all().
                when().
                post(url).
                then().
                statusCode(httpStatus.value());
    }

    public static ValidatableResponse createPost(String url, RequestSpecification requestSpecification, HttpStatus httpStatus) {
        return given().
                spec(requestSpecification).
                log().
                all().
                when().
                post(url).
                then().
                statusCode(httpStatus.value());
    }

    public static ValidatableResponse createPut(String url, Object body, HttpStatus httpStatus) {
        return given().
                spec(createRequestSpecification(body)).
                log().
                all().
                when().
                put(url).
                then().
                statusCode(httpStatus.value());
    }

    public static ValidatableResponse createDelete(String url, RequestSpecification requestSpecification, HttpStatus httpStatus) {
        return given().
                spec(requestSpecification).
                log().
                all().
                when().
                delete(url).
                then().
                statusCode(httpStatus.value());
    }

    public static ValidatableResponse createDelete(String url, HttpStatus httpStatus) {
        return given().
                log().
                all().
                when().
                get(url).
                then().
                statusCode(httpStatus.value());
    }

    public static ValidatableResponse createGet(String url, RequestSpecification requestSpecification, HttpStatus httpStatus) {
        return given().
                spec(requestSpecification).
                log().
                all().
                when().
                get(url).
                then().
                statusCode(httpStatus.value());
    }

    public static ValidatableResponse createGet(String url, HttpStatus httpStatus) {
        return given().
                log().
                all().
                when().
                get(url).
                then().
                statusCode(httpStatus.value());
    }

    private static RequestSpecification createRequestSpecification(Object body) {
        return new RequestSpecBuilder().setContentType(ContentType.JSON)
                .addHeader("Accept", ContentType.JSON.toString())
                .setBody(body)
                .build();
    }

}
