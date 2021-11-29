package me.toanlnb.services;

import java.util.List;

import me.toanlnb.entity.Role;
import me.toanlnb.entity.User;

public interface UserService {

	public User findUserByEmail(String email);

	public void saveUser(User user, String role);

	public List<User> getAllUsersFilterByNameAndEmail(String keyword);

	public User getUserById(Long id);

	public void saveUserEdit(User user);

	public void saveUserRegister(User user);

	public List<User> findByRoles(Role role);

	public void setRole(User user, String role);

	List<User> listAllUsers();

	public boolean checkPassword(User user, String password);

	public void updatePassword(String password, Long userId);
}
