package de.mariokramer.wsrlock.persistence;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import de.mariokramer.wsrlock.model.Users;

@Service("customUserDetailsService")
public class CustomUserDetailsService implements UserDetailsService {
	private final UserDao userDao;
	private final UserRoleDao userRoleDao;

	@Autowired
	public CustomUserDetailsService(UserDao userDao, UserRoleDao userRolesDao) {
		this.userDao = userDao;
		this.userRoleDao = userRolesDao;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Users user = userDao.getUsersByUserName(username);

		if (user == null) {
			throw new UsernameNotFoundException("No user present with username: " + username);
		} else {
			List<String> userRoles = userRoleDao.findRoleByUserName(username);
			return new CustomUserDetails(user, userRoles);
		}
	}

}
