package com.github;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.javamoney.moneta.format.AmountFormatParams;
import org.javamoney.moneta.spi.DefaultMonetaryAmountsSingletonQuerySpi;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.format.AmountFormatContext;
import javax.money.format.AmountFormatContextBuilder;
import java.math.BigDecimal;

import static java.util.Locale.US;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class MockEnvIntegrationTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExchangeRateClient exchangeRateClient;

    @Test
    void createOrder() throws Exception {
        mockMvc.perform(post("/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": \"100.0EUR\"}"))
                .andExpect(status().isCreated());
        // TODO: протестируйте успешное создание заказа на 100 евро
    }




    @Test
    @Sql("/unpaid-order.sql")
    void payOrder() throws Exception {
        mockMvc.perform(post("/order/{id}/payment", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"creditCardNumber\": \"4532756279624064\"}"))
                .andExpect(status().isCreated());
        // TODO: протестируйте успешную оплату ранее созданного заказа валидной картой
    }

    @Test
    @Sql("/paid-order.sql")
    void getReceipt() throws Exception {
        // TODO: Протестируйте получение чека на заказ №1 c currency = USD
        CurrencyUnit eur = Monetary.getCurrency("EUR");
        CurrencyUnit usd = Monetary.getCurrency("USD");

        when(exchangeRateClient.getExchangeRate(eur, usd)).thenReturn(BigDecimal.valueOf(0.8412));

        mockMvc.perform(get("/order/{id}/receipt?currency=USD", 1))
                .andExpect(status().isOk());
    }
}
