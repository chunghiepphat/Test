
package me.toanlnb.services;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import me.toanlnb.entity.Role;
import me.toanlnb.entity.User;
import me.toanlnb.repository.UserRepository;

@Service("userService")
public class UserServiceImpl implements UserService {

	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleServiceImpl roleService;

	@Override
	public User findUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public void saveUser(User user, String role) {
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		user.setActive(1);
		Role userRole = roleService.findRoleByRoleName(role);
		user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
		userRepository.save(user);

	}

	@Override
	public List<User> getAllUsersFilterByNameAndEmail(String keyword) {
		return userRepository.search(keyword);
	}

	@Override
	public User getUserById(Long id) {
		return userRepository.findById(id).get();
	}

	@Override
	public void saveUserEdit(User user) {
		userRepository.update(user.getFirstname(), user.getLastname(), user.getId());

	}

	@Override
	public void saveUserRegister(User user) {
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		user.setActive(1);
		Role userRole = roleService.findRoleByRoleName("customer");
		user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
		userRepository.save(user);

	}

	@Override
	public List<User> findByRoles(Role role) {
		return userRepository.findByRoles(role);
	}

	@Override
	public void setRole(User user, String role) {

	}

	@Override
	public List<User> listAllUsers() {
		return userRepository.findAll();
	}

	@Override
	public boolean checkPassword(User user, String password) {
		return bCryptPasswordEncoder.matches(password, user.getPassword());
	}

	@Override
	public void updatePassword(String password, Long userId) {
		userRepository.updatePassword(bCryptPasswordEncoder.encode(password), userId);
	}
}
