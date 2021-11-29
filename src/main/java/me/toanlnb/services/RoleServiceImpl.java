package me.toanlnb.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import me.toanlnb.entity.Role;
import me.toanlnb.entity.User;
import me.toanlnb.repository.RoleRepository;
import me.toanlnb.repository.UserRepository;

@Service
public class RoleServiceImpl implements RoleService {

	@Qualifier("roleRepository")
	@Autowired
	RoleRepository roleRepository;

	@Autowired
	UserRepository userRepository;

	@Override
	public List<Role> getAllRoles() {
		return roleRepository.findAll();
	}

	@Override
	public void setRole(User user, String role) {
		Role userRole = roleRepository.findByRoleName(role);
		roleRepository.setRole(userRole.getId(), user.getId());
	}

	@Override
	public List<User> findUsersByRole(Role role) {
		return userRepository.findByRoles(role);
	}

	@Override
	public Role findRoleByRoleName(String role) {
		return roleRepository.findByRoleName(role);
	}

}
