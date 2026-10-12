#Here is the step-by-step plan for what we are going to create. I will first outline the structure and 
#strategy based on the RICEPOT method, and then I will generate the complete prompt you can use to build #your Rest Assured API testing framework for RESTful Booker.

### **Step-by-Step Plan**

1. **Analyze the Target (Context):** We are targeting the RESTful Booker API, which includes endpoints for Authentication (`/auth`), Booking CRUD operations (`/booking` via GET, POST, PUT, PATCH, DELETE), and a Health Check (`/ping`).


2. **Define the Role (R):** Set the persona to a Senior QA Automation Tester with 15 years of experience in Java and API testing.


3. **Draft the Instructions (I):** We will instruct the AI to build an enterprise-grade framework using Java, Rest Assured, Maven, and TestNG. We will add specific requirements like using POJO classes for JSON serialization/deserialization, Request/Response Specification Builders, and comprehensive assertions.


4. **Set the Context (C):** Provide a brief overview of the RESTful Booker endpoints and payload requirements (e.g., passing tokens via `Cookie` or `Authorization` headers for PUT/PATCH/DELETE).


5. **Provide an Example (E):** Give a small snippet of how we expect a Rest Assured call to look to guide the AI's coding style.


6. **Set Parameters & Output (P & O):** Restrict the output to runnable code only, asking for specific files (pom.xml, POJOs, Base Setup, and Test classes) without unnecessary fluff.


7. **Set Tone (T):** Keep it technical, precise, and enterprise-grade.



Below is the generated prompt that you can copy and paste into an LLM to generate your Rest Assured framework.

---

### **Generated Prompt for Rest Assured API Framework**

**Objective**: We have to generate a Rest Assured API automation framework from scratch for the RESTful Booker API ([https://restful-booker.herokuapp.com](https://restful-booker.herokuapp.com)) where you need to implement Request/Response Spec builders, POJO serialization, and a production-ready structure.

**Prompt:**

**R — Role**
You are a QA automation tester with 15 years of experience. You have a very good understanding of API testing and backend automation. You need to create an API testing framework with Rest Assured, Java, Maven, TestNG, and Jackson/Gson for JSON parsing. It should be an enterprise-level framework.

**I — Instructions**

* [Generate] - Generate a complete Rest Assured with Java automation script following enterprise-level standards.
* [Critical] - Automate and verify the results of the following endpoints: `/auth`, `/booking` (GET, POST, PUT, PATCH, DELETE), and `/ping`. Ensure status codes, response times, and JSON schema/body values are thoroughly tested.
* [Critical] - Apply TestNG annotations (`@Test`, `@BeforeClass`, etc.) and necessary setup/teardown logic.
* [Mandatory] - Use POJO (Plain Old Java Objects) classes for serialization and deserialization of the Request and Response bodies (e.g., Auth, Booking, BookingDates).
* [Mandatory] - Implement `RequestSpecification` and `ResponseSpecification` builders to avoid code duplication (e.g., setting base URI, Content-Type, and default assertions).
* [Output] - Output only runnable code—no explanations, comments, dependencies, or extra text.
* [Don't] - Don't add comments, `Thread.sleep`, or bad coding practices.
* [Generate] - Generate E2E test scripts covering the token generation, creating a booking, fetching it, updating it, and deleting it.
* [Maintain] - Maintain a consistent structure, readability, and modularity across all generated scripts.

**C — Context**
You are creating API test scripts with a proper framework for the RESTful Booker API. The API handles:

* Auth: POST `/auth` (requires `username`: "admin", `password`: "password123") to get a token.
* Booking: GET `/booking`, GET `/booking/:id`, POST `/booking`, PUT `/booking/:id`, PATCH `/booking/:id`, DELETE `/booking/:id`.
* Auth Headers: PUT, PATCH, and DELETE requests require either a `Cookie: token=<token_value>` or `Authorization: Basic YWRtaW46cGFzc3dvcmQxMjM=` header.
* Ping: GET `/ping` to check health (returns 201).
Base URL: [https://restful-booker.herokuapp.com](https://restful-booker.herokuapp.com)

**E — Example**
Example structure for Rest Assured setup:

```java
public class BookingApi {
    public static Response createBooking(BookingPayload payload) {
        return RestAssured.given()
            .spec(BaseSpec.getRequestSpec())
            .body(payload)
            .when()
            .post("/booking")
            .then()
            .spec(BaseSpec.getResponseSpec(200))
            .extract().response();
    }
}

```

**P — Parameters**
Provide a production-level automation script created by an expert with pinpoint accuracy and zero bad coding practices. Implement robust assertions for JSON paths.

**O — Output**
Provide only:

1. `pom.xml` with required dependencies (Rest Assured, TestNG, Jackson).
2. Base Setup class (Spec Builders).
3. POJO classes (Auth, BookingDates, Booking).
4. API Endpoints Client class.
5. 1 TestNG Test class containing the E2E CRUD flow.
No explanations or additional content.

**T — Tone**
Technical, precise, enterprise-grade, code-only.