package de.mariokramer.wsrlock.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	@Autowired
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication()
			.withUser("test").password("test").roles("USER");
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.logout()
				.logoutUrl("/login?logout")
				.logoutSuccessUrl("/login?logout")
				.invalidateHttpSession(true).and()
				
			.authorizeRequests()
				.antMatchers("/resources/**").permitAll()
				.antMatchers("/**").hasRole("USER")
				.anyRequest().authenticated().and()
				.formLogin().loginPage("/login").permitAll()
							.failureUrl("/login?error")
							.defaultSuccessUrl("/start")
							.loginProcessingUrl("/login");
		
//		http.authorizeRequests()
//			.antMatchers("/admin/**").hasRole("USER")
//			.and()
//				.formLogin().defaultSuccessUrl("/start")
//			.and()
//		    	.formLogin().loginPage("/login").failureUrl("/login?error")
//		    	.usernameParameter("username").passwordParameter("password")		
//		    .and()
//		    	.formLogin().loginProcessingUrl("/j_spring_security_check")
//		    .and()
//		    	.logout().logoutSuccessUrl("/login")
//		    .and()
//		    	.logout().logoutUrl("/j_spring_security_logout")
//		    .and()
//		    	.csrf();
		
//		http.formLogin().and().authorizeRequests()
//						.antMatchers("/wsrlock/login").hasRole("USER")
//						.antMatchers(HttpMethod.POST, "/marco").hasRole("USER")
//						.anyRequest().permitAll();
	}
}
