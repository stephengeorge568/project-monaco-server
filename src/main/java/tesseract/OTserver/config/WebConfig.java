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
        //registry.addMapping("/**").allowedOriginPatterns("*").maxAge(3600);
        // Set to accept all ... insecure
        try {
            if (env.getProperty("spring.profiles.active").equals("prod")) {
                registry.addMapping("/**"); // TODO
            } else {
                registry.addMapping("/**");
            }
        } catch (NullPointerException e) {
            registry.addMapping("/**");
        }
    }
}
