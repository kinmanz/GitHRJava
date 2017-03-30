package GitHR.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebMvc
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    private static final String[] CLASSPATH_RESOURCE_LOCATIONS = {
            "classpath:/META-INF/resources/", "classpath:/resources/",
            "classpath:/templates/",
            "classpath:/static/", "classpath:/public/" };

    private static final String resourcePath = "classpath:/templates/styled/";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/res/**").addResourceLocations(
//                CLASSPATH_RESOURCE_LOCATIONS).setCachePeriod(31556926);
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/");

        registry.addResourceHandler("/css/**")
                .addResourceLocations(resourcePath + "/css/");

        registry.addResourceHandler("/img/**")
                .addResourceLocations(resourcePath + "/img/");

        registry.addResourceHandler("/fonts/**")
                .addResourceLocations(resourcePath + "/fonts/");

        registry.addResourceHandler("/js/**")
                .addResourceLocations(resourcePath + "/js/");

        registry.addResourceHandler("/font-awesome/**")
                .addResourceLocations(resourcePath + "/font-awesome/");

    }

}
