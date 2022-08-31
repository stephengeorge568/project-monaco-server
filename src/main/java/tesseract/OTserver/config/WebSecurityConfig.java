package tesseract.OTserver.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.core.env.Environment;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

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

            if (!isSecurityDisabled) {
                System.out.println("Prod security configurations activating...");
                http
                    .requiresChannel(channel ->
                            channel.anyRequest().requiresSecure())
                    .authorizeRequests(authorize ->
                            authorize.anyRequest().permitAll())
                    .csrf().disable(); // otherwise post requests get 403 forbidden. temporary hotfix
            } else {
                System.out.println("Dev security configurations activating...");
                http.
                        authorizeRequests()
                        .antMatchers("/**").permitAll()
                        .anyRequest().authenticated();
                http.csrf().disable();
            }
    }
}
