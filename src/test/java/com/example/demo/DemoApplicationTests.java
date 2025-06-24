package com.example.demo;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WireMockTest(httpPort = 8083)
class DemoApplicationTests {

	@LocalServerPort
	private int port;

	private WebTestClient webTestClient;

	@BeforeEach
	void setUp() {
		this.webTestClient = WebTestClient.bindToServer()
				.baseUrl("http://localhost:" + port)
				.build();
	}

	@Test
	void shouldReturnOrderWhenServiceReturnsData() {
		stubFor(get(urlPathEqualTo("/orderSearchService/order/phone"))
				.withQueryParam("phoneNumber", equalTo("123456789"))
				.willReturn(aResponse()
						.withStatus(200)
						.withHeader("Content-Type", "application/json")
						.withBody("""
                                [
                                    {
                                        "orderNumber": "Order_0",
                                        "phoneNumber": "123456789",
                                        "productCode": "3852"
                                    }
                                ]
                                """)));

		stubFor(get(urlPathEqualTo("/product/3852"))
				.willReturn(aResponse()
						.withStatus(200)
						.withHeader("Content-Type", "application/json")
						.withBody("""
                                {
                                    "productId": "333",
                                    "productName": "Meal"
                                }
                                """)));

		webTestClient.get()
				.uri(uriBuilder -> uriBuilder
						.path("/items/order/retrieve")
						.queryParam("phoneNumber", "123456789")
						.build())
				.header("X-Request-ID", "REQ-001")
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$[0].phoneNumber").isEqualTo("123456789")
				.jsonPath("$[0].orderNumber").exists()
				.jsonPath("$[0].productCode").exists()
				.jsonPath("$[0].productName").exists()
				.jsonPath("$[0].productId").exists()
				.jsonPath("$[0].userName").isEqualTo("UserName");
	}

	@Test
	void shouldReturnEmptyWhenNoOrdersFound() {
		stubFor(get(urlPathEqualTo("/orderSearchService/order/phone"))
				.withQueryParam("phoneNumber", equalTo("123456789"))
				.willReturn(aResponse()
						.withStatus(200)
						.withHeader("Content-Type", "application/json")
						.withBody("[]")));

		webTestClient.get()
				.uri(uriBuilder -> uriBuilder
						.path("/items/order/retrieve")
						.queryParam("phoneNumber", "123456789")
						.build())
				.header("X-Request-ID", "REQ-002")
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.json("[]");
	}
}
