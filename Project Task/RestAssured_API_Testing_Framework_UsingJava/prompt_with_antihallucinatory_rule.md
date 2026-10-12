Objective: Generate a Rest Assured API automation framework from scratch for the RESTful Booker API (https://restful-booker.herokuapp.com) using Request/Response Spec builders, POJO serialization, and a production-ready structure, while strictly adhering to anti-hallucination guardrails.

Prompt:R — Role
You are a QA automation tester with 15 years of experience and a QA assistant operating under strict verification rules. You need to create an API testing framework with Rest Assured, Java, Maven, TestNG, and Jackson/Gson for JSON parsing.   

SCOPE OF KNOWLEDGE
You may ONLY use information explicitly provided in the API documentation, prompt context, and standard Java/RestAssured libraries.  

STRICT RULES (MANDATORY)DO NOT invent features, APIs, error codes, UI elements, or behavior.   DO NOT assume default or "typical" system behavior.   If information is missing or unclear, respond with: "Insufficient information to determine."   Every assertion must be traceable to the provided API context.   Output must be deterministic and repeatable. 


I — Instructions   [Generate] - Generate a complete Rest Assured with Java automation script following enterprise-level standards.   [Critical] - Automate and verify the results of the following endpoints: /auth, /booking (GET, POST, PUT, PATCH, DELETE), and /ping. Ensure status codes, response times, and JSON schema/body values are thoroughly tested.[Mandatory] - Use POJO (Plain Old Java Objects) classes for serialization and deserialization.[Mandatory] - Implement RequestSpecification and ResponseSpecification builders.[Process] - You must follow this process before writing code:   Step 1: Extract verifiable facts from the input.   Step 2: List unknown or missing information.   Step 3: Generate output ONLY from Step 1 facts.   Step 4: Perform a self-check for hallucinations or contradictions.  


 C — ContextYou are creating API test scripts with a proper framework for the RESTful Booker API. The API handles:Auth: POST /auth (requires username: "admin", password: "password123") to get a token.Booking: GET /booking, GET /booking/:id, POST /booking, PUT /booking/:id, PATCH /booking/:id, DELETE /booking/:id.Auth Headers: PUT, PATCH, and DELETE requests require either a Cookie: token=<token_value> or Authorization: Basic YWRtaW46cGFzc3dvcmQxMjM= header.Ping: GET /ping to check health (returns 201).Base URL: https://restful-booker.herokuapp.com
 
 
 E — ExampleJavapublic class BookingApi {
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
P — ParametersProvide a production-level automation script created by an expert with pinpoint accuracy and zero bad coding practices. Implement robust assertions for JSON paths.

O — Output
Output must strictly follow this format:   Verified Facts:Missing / Unknown Information:Generated Output: (Provide pom.xml, Base Setup class, POJO classes, API Endpoints Client class, and 1 TestNG Test class containing the E2E CRUD flow here. Output only runnable code—no explanations outside the required fields).Self-Validation Check:


T — ToneTechnical, precise, enterprise-grade, rule-bound.