package com.ifsc.tarefas.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// classe de configuração
// serve para redirecionar o usuario para o login ou para o /templates
@Configuration
public class WebConfig implements WebMvcConfigurer {

    // sobrescreve a função que tem no WebMvcConfigurer
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/", "/templates");
    }
}
