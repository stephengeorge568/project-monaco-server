package tesseract.OTserver.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private Environment env;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        System.out.println("11/11/2022 11:44PM");
        try {
            if (env.getProperty("spring.profiles.active").equals("prod")) {
                registry.addMapping("/**").allowedOriginPatterns("*").allowedMethods("POST","PUT","GET"); // TODO
            } else {
                registry.addMapping("/**").allowedOriginPatterns("*");
            }
        } catch (NullPointerException e) {
            registry.addMapping("/**").allowedOriginPatterns("*");
        }
    }
}
