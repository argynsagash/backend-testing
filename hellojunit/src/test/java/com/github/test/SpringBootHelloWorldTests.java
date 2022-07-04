package com.github.test;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/*
	TODO
	Добавить WebApplicationContext для тестов
	Добавить MockMvc
	Создать метод с аннотацией @Before которая создает mockMvc на основе webApplicationContext
	Написать метод тестирования метода /employee
		Должен проверяться HTTP статус ответа
		Должен проверять contentType ответа
		Должно проверять значение поля "name"
		Должно проверять значение поля "designation"
		Должно проверять значение поля "salary"
		Должно проверять значение поля "empId"
*/
@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class SpringBootHelloWorldTests {

	@Autowired
	private WebApplicationContext webApplicationContext;
	private MockMvc mockMvc;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
	}

	@Test
	public void contextLoads() {
	}

	@Test
    public void testHello() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/employee"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType("application/json"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.name").value("emp1"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.designation").value("manager"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.salary").value("3000.0"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.empId").value("1")).andDo(print());
    }



}
