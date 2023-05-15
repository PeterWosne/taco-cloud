package ru.ermakow.tacocloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class TacoCloudApplication implements WebMvcConfigurer {

	public static void main(String[] args) {

		SpringApplication.run(TacoCloudApplication.class, args);
	}

	// TODO контроллер представлений можно сделать как представлено ниже
	//@Override
	//public void addViewControllers(ViewControllerRegistry registry) {
	//	registry.addViewController("/").setViewName("home");
	//}
    // HomeController можно заменить методом в классе конфигурации(необходимо реализовать интерфейс WebMvcConfigurer, нужный метод -> addViewController,
    // в HomeControllerTest убрать ссылку на класс HomeController)
}

// @SpringBootApplication - составная аннотация из
// -@SpringBootConfiguration(@Configuration) класс конфигурации
// -@EnableAutoConfiguration
// -@ComponentScan вкл сканирование компонентов(чтобы Spring обнаруживал другие классы с разл аннотациями, регистрировал их как компоненты в контексте приложения)

// mvnw spring-boot:run/ mvnw test
