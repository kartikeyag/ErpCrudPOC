	package org.springframework.samples.erpcrud;

	import org.springframework.context.annotation.Bean;
	import org.springframework.context.annotation.Configuration;
	import org.springframework.http.MediaType;
	import org.springframework.security.config.Customizer;
	import org.springframework.security.config.annotation.web.builders.HttpSecurity;
	import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
	import org.springframework.security.core.userdetails.User;
	import org.springframework.security.provisioning.InMemoryUserDetailsManager;
	import org.springframework.security.web.SecurityFilterChain;

	@Configuration
@EnableWebSecurity
public class SecurityConfig {
	  @Bean
	  InMemoryUserDetailsManager userDetailsService() {
		  var users = User.withDefaultPasswordEncoder();
		  return new InMemoryUserDetailsManager(users.username("admin").password("admin").roles("USER","ADMIN").build(),
				  users.username("user").password("admin").roles("USER").build());
	  }

	  @Bean
	  SecurityFilterChain securityFilterChain(HttpSecurity security) throws Exception {
		  return security.csrf(csrf -> csrf.disable())
		  .authorizeHttpRequests(http -> http.anyRequest().authenticated())
	  .formLogin(Customizer.withDefaults()) .logout(logout ->
	  logout.logoutSuccessUrl("/login?logoutSuccess=true").deleteCookies("JSESSIONID"))
	  .oneTimeTokenLogin( configurer -> configurer.generatedOneTimeTokenHandler((request, response,
	  oneTimeToken) -> { var msg = "go to http://localhost:8080/login/ott?token=" +
	  oneTimeToken.getTokenValue(); System.out.println(msg);
	  response.setContentType(MediaType.TEXT_PLAIN_VALUE);
	  response.getWriter().print("you've got console mail!"); })) .build(); }

}
