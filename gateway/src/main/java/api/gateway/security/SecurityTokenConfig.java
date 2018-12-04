package api.gateway.security;

import api.gateway.config.JwtConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;

@EnableWebSecurity
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled=true)
public class SecurityTokenConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtConfig jwtConfig;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        System.out.println("Blank configured");
        System.out.println("JWT config is: " +  jwtConfig.getUri().toString());
        http
                .csrf().disable()
                // make sure we use stateless session; session won't be used to store user's state.
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .anonymous()

                .and()
                // handle an authorized attempts

                .exceptionHandling().authenticationEntryPoint((req, rsp, e) -> rsp.sendError(HttpServletResponse.SC_UNAUTHORIZED))
                .and()
                // Add a filter to validate the tokens with every request
                .addFilterAfter(new JwtTokenAuthenticationFilter(jwtConfig), UsernamePasswordAuthenticationFilter.class)
                // authorization requests config
                .authorizeRequests()
                // allow all who are accessing "auth" service
                .antMatchers(HttpMethod.POST,  jwtConfig.getUri()).permitAll()
                .antMatchers(HttpMethod.GET,  "/client/**").permitAll()
                .antMatchers("/gateway/hello").permitAll()



                // must be an admin if trying to access admin area (authentication is also required here)
                .antMatchers("/client" + "/admin/**").hasRole("ADMIN")

                // Any other request must be authenticated
                .anyRequest().authenticated();
    }



    @Bean
    public JwtConfig jwtConfig() {
        return new JwtConfig();
    }
}
