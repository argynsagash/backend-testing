package com.github;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ServerIntegrationTests {
    @Autowired
    private WebTestClient webClient;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    private static MockWebServer mockWebServer;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("exchange-rate-api.base-url", () -> mockWebServer.url("/").url().toString());
    }

    @BeforeAll
    static void setupMockWebServer() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterEach
    void deleteEntities() {
        paymentRepository.deleteAll();
        orderRepository.deleteAll();
    }

    @Test
    void createOrder() {
        // TODO: протестируйте успешное создание заказа на 100 евро
        // используя webClient
        webClient.post().uri("/order")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"amount\": \"100.0EUR\"}")
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    void payOrder() {
        // TODO: протестируйте успешную оплату ранее созданного заказа валидной картой
        // используя webClient
        // Получите `id` заказа из базы данных, используя orderRepository
        Order order = new Order(LocalDateTime.now(), BigDecimal.valueOf(100.0), false);
        Long orderId = orderRepository.save(order).getId();

        webClient.post().uri("/order/{id}/payment", orderId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"creditCardNumber\": \"4532756279624064\"}")
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    void getReceipt() {
        // TODO: Протестируйте получение чека на заказ №1 c currency = USD
        // Создайте объект Order, Payment и выполните save, используя orderRepository
        // Используйте mockWebServer для получения conversion_rate
        // Сделайте запрос через webClient
        Order order = new Order(LocalDateTime.now(), BigDecimal.valueOf(100.0), true);
        Payment payment = new Payment(order, "4532756279624064");

        Long orderId = orderRepository.save(order).getId();
        paymentRepository.save(payment);

        mockWebServer.enqueue(
                new MockResponse().setResponseCode(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setBody("{\"conversion_rate\": 0.8412}")
        );

        webClient.get().uri("/order/{id}/receipt?currency=USD", orderId)
                .exchange()
                .expectStatus().isOk();
    }
}
