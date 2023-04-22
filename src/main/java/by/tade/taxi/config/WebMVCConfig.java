package by.tade.taxi.config;

import by.tade.taxi.dto.UserSessionDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebMVCConfig extends WebMvcConfigurerAdapter {

    @Override
    @Description("Custom Conversion Service")
    public void addFormatters(FormatterRegistry registry) {
//        registry.addFormatter(new NameFormatter());
    }

    @Bean
    @SessionScope
    public UserSessionDto userSession() {
        return new UserSessionDto(null, null);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/index");
    }
}
