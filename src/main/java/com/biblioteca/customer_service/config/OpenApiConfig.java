package com.biblioteca.customer_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customerOpenAPI() {
        Server server = new Server();
        server.setUrl("/");   // ruta relativa: usará el mismo host desde donde se carga (el gateway)

        return new OpenAPI()
                .servers(List.of(server))
                .info(new Info()
                        .title("Customer Service API")
                        .description("Gestión de clientes de la biblioteca")
                        .version("1.0"));
    }
}