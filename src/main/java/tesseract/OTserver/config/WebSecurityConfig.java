package tesseract.OTserver.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import tesseract.OTserver.controllers.DocumentController;
import tesseract.OTserver.services.DocumentService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private static final Logger logger = LogManager.getLogger(WebSecurityConfig.class);

    @Autowired
    private Environment env;

    @Value("${application.security.disabled:false}")
    private boolean isSecurityDisabled;

//    @Value("${spring.profiles.active}")
//    private String profile;

    @Value("${spring.profiles.active:}")
    private String activeProfile;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
            logger.info("App Version: 0.0.8");
            if (!isSecurityDisabled) {
                logger.info("Prod security configurations activating...");
                http
                    .requiresChannel(channel ->
                            channel.anyRequest().requiresSecure())
                    .authorizeRequests(authorize ->
                            authorize.anyRequest().permitAll())
                    .csrf().disable(); // otherwise post requests get 403 forbidden. temporary hotfix
            } else {
                logger.info("Dev security configurations activating...");
                http.
                        authorizeRequests()
                        .antMatchers("/**").permitAll()
                        .anyRequest().authenticated();
                http.csrf().disable();
            }
    }
    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
}
