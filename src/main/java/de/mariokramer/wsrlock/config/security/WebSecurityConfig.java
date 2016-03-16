package de.mariokramer.wsrlock.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.RequestMatcher;

import de.mariokramer.wsrlock.model.Users;
import de.mariokramer.wsrlock.persistence.CustomUserDetailsService;
import de.mariokramer.wsrlock.persistence.UserDao;

@EnableWebSecurity
@Configuration
@ComponentScan(basePackageClasses = CustomUserDetailsService.class)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	UserDetailsService userDetailsService;
	@Autowired
	UserDao ud;
	
//	@Override
//	@Autowired
//	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//		auth.inMemoryAuthentication().withUser("mario").password("test").roles("USER");
//		auth.inMemoryAuthentication().withUser("q").password("q").roles("USER");
//	}
	
	@Autowired
	public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {
		auth.eraseCredentials(false);
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}
	
	@Bean(name = "passwordEncoder")
	public PasswordEncoder passwordEncoder() {
//		BCryptPasswordEncoder bc = new BCryptPasswordEncoder();		
//		
//		Users user = new Users();
//		user.setEnabled(1);
//		user.setUserName("mario");
//		user.setUserPass(bc.encode("test"));	
//		ud.save(user);
		return new BCryptPasswordEncoder();
	}

	@Bean(name = "getMatcher")
	public RequestMatcher getMatcher() {
		return new CsrfSecurityRequestMatcher();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.formLogin().loginPage("/login").permitAll().failureUrl("/login?error").defaultSuccessUrl("/start");
		http.authorizeRequests()
			.antMatchers("/resources/**").permitAll()
			.anyRequest().authenticated();
		http.logout().logoutUrl("/logout").logoutSuccessUrl("/login?logout").permitAll().invalidateHttpSession(true);
		http.headers().frameOptions().sameOrigin();
		http.csrf().requireCsrfProtectionMatcher(getMatcher());
	}
}
