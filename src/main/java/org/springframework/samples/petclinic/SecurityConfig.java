package org.springframework.samples.petclinic;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

@Configuration
public class SecurityConfig {

    @Bean
    InMemoryUserDetailsManager userDetailsService() {
        var users = User.withDefaultPasswordEncoder();
        return new InMemoryUserDetailsManager(
                users.username("admin").password("admin").roles("USER", "ADMIN").build(),
                users.username("user").password("admin").roles("USER").build()
        );
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity security) throws Exception {
        return security.
                authorizeHttpRequests(http->http
                        //.anyRequest().authenticated())
                .anyRequest().permitAll())
                .formLogin(Customizer.withDefaults())
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logoutSuccess=true")
                        .deleteCookies("JSESSIONID"))
                //.exceptionHandling(exception -> exception
                  //      .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login?loginRequired=true")))
                .oneTimeTokenLogin(configurer -> configurer.generatedOneTimeTokenHandler((request, response, oneTimeToken) -> {
                    var msg = "go to http://localhost:8080/login/ott?token=" + oneTimeToken.getTokenValue();
                    System.out.println(msg);
                    response.setContentType(MediaType.TEXT_PLAIN_VALUE);
                    response.getWriter().print("you've got console mail!");}))
                .build();
    }

}
