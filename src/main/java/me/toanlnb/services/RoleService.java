package me.toanlnb.services;

import java.util.List;

import me.toanlnb.entity.Role;
import me.toanlnb.entity.User;

public interface RoleService {
	public List<Role> getAllRoles();

	public void setRole(User user, String role);
	
	public List<User> findUsersByRole(Role role);

	public Role findRoleByRoleName(String role);
}
