package me.toanlnb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import me.toanlnb.entity.Role;
import me.toanlnb.entity.User;
import me.toanlnb.services.RoleService;
import me.toanlnb.services.UserService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Controller
// ADMIN-EXCLUSIVE MAPPINGS
public class AdminController {
	@Autowired
	UserService userService;

	@Autowired
	RoleService roleService;

	// MAPPING: ADMIN DASHBOARD
	@RequestMapping(value = { "/admin-dashboard" }, method = RequestMethod.GET)
	public String showDashboard(Model model) {
		// GET CURRENT USER
		User currentUser = this.getCurrentUser();
		model.addAttribute("currentUser", currentUser);
		// GET CURRENT DATE
		String currentDate = this.getCurrentDate();
		model.addAttribute("currentDate", currentDate);
		// FUNCTIONS
		return "admin/admin-dashboard";
	}

	// MAPPING: USER MANAGEMENT PAGE
	@RequestMapping(value = "/manage-users")
	public String listAllUser(@Param("keyword") String keyword, Model model) {
		// GET CURRENT USER
		model.addAttribute("currentUser", this.getCurrentUser());
		// LOAD ROLES FOR DROP-DOWN LIST
		model.addAttribute("roleNames", this.getAllRoleName());
		// FUNCTIONS
		List<User> listUser = userService.listAllUsers();
		// GET ROLE LIST BY USER ID
		model.addAttribute("roleNamesList", this.getRoleList(listUser));
		model.addAttribute("listUser", listUser);
		model.addAttribute("keyword", keyword);
		return "/admin/manage-users";
	}

	// SEARCH USERS
	@RequestMapping(value = "/search-users")
	public String searchUser(@Param("keyword") String keyword, Model model, @RequestParam("cbxRole") String roleValue) {
		// GET CURRENT USER
		User currentUser = this.getCurrentUser();
		model.addAttribute("currentUser", currentUser);
		// LOAD ROLES FOR DROP-DOWN LIST
		model.addAttribute("roleNames", this.getAllRoleName());
		// FUNCTIONS
		List<User> result = new ArrayList<>();
		List<User> usersByRole;
		// FILTER USERS BY ROLE
		if (roleValue.equals("All roles")) {
			usersByRole = userService.listAllUsers();
		} else {
			usersByRole = userService.findByRoles(roleService.findRoleByRoleName(roleValue));
		}
		// FIND USERS MATCHING KEYWORD
		List<User> usersBySearch = userService.getAllUsersFilterByNameAndEmail(keyword);
		// MATCH RESULTS FROM KEYWORD SEARCH WITH ROLE FILTER
		for (User user : usersByRole) {
			for (User user1 : usersBySearch) {
				// IF MATCHING, ADD INTO FINAL RESULTS
				if (user.getId() == user1.getId()) {
					result.add(user1);
				}
			}
		}
		// GET ROLE LIST BY USER ID
		model.addAttribute("roleNamesList", this.getRoleList(result));
		// FINAL RESULT OF SEARCHING
		model.addAttribute("listUser", result);
		// RETAIN VALUE FOR SEARCHING
		model.addAttribute("keyword", keyword);
		model.addAttribute("selectedRole", roleValue);
		return "/admin/manage-users";
	}

	// MAPPING: USER CREATION PAGE
	@RequestMapping(value = { "/create-users" }, method = RequestMethod.GET)
	public String signupAdmin(Model model) {
		// GET CURRENT USER
		User currentUser = this.getCurrentUser();
		model.addAttribute("currentUser", currentUser);
		// GET CURRENT DATE
		String currentDate = this.getCurrentDate();
		model.addAttribute("currentDate", currentDate);
		// FUNCTIONS
		User user = new User();
		model.addAttribute("user", user);
		return "admin/create-users";
	}

	// COMMIT NEW USER CREATION
	@RequestMapping(value = { "/create-new-user" }, method = RequestMethod.POST)
	public String saveNewUser(Model model, User user, @RequestParam("roleRadioNew") String role,
			BindingResult bindingResult) {
		// GET CURRENT USER
		User currentUser = this.getCurrentUser();
		model.addAttribute("currentUser", currentUser);
		// GET CURRENT DATE
		String currentDate = this.getCurrentDate();
		model.addAttribute("currentDate", currentDate);
		// FUNCTIONS
		User userExists = userService.findUserByEmail(user.getEmail());
		if (userExists != null) {
			bindingResult.rejectValue("email", "error.user", "This email already exists!");
			model.addAttribute("err", "This email already exists.");
			return "admin/create-users";
		}
		if (bindingResult.hasErrors()) {
			model.addAttribute("err", "There was an error.");
			return "admin/create-users";
		} else {
			userService.saveUser(user, role);
			model.addAttribute("msg", "Account creation successful.");
			model.addAttribute("user", new User());
			return "admin/create-users";
		}
	}

	// MAPPING: USER DETAILS EDIT PAGE
	@RequestMapping(value = { "/edit-user-details/{id}" }, method = RequestMethod.GET)
	public String showEditProductPage(Model model, @PathVariable(name = "id") Long id,
			@ModelAttribute("success") String msg) {
		// RETURN ALERT MESSAGE
		if (msg.length() > 0) {
			model.addAttribute("msg", msg);
		}
		// GET CURRENT USER
		User currentUser = this.getCurrentUser();
		model.addAttribute("currentUser", currentUser);
		// FUNCTIONS
		User user = userService.getUserById(id);
		boolean isCustomer = false;
		boolean isSeller = false;
		for (Role role : user.getRoles()) {
			if (role.getRoleName().equals("seller")) {
				isSeller = true;
			}
			if (role.getRoleName().equals("customer")) {
				isCustomer = true;
			}
		}
		model.addAttribute("isCustomer", isCustomer);
		model.addAttribute("isSeller", isSeller);
		model.addAttribute("user", user);
		return "admin/edit-user";
	}

	// SAVE CHANGES TO USER
	@RequestMapping(value = "/save-changes-to-user", method = RequestMethod.POST)
	public ModelAndView saveEditUser(@ModelAttribute("user") User user,
			@RequestParam(value = "roleRadio", required = false) String role) {
		ModelAndView model = new ModelAndView();
		// GET CURRENT USER
		User currentUser = this.getCurrentUser();
		model.addObject("currentUser", currentUser);
		// GET CURRENT DATE
		String currentDate = this.getCurrentDate();
		model.addObject("currentDate", currentDate);
		// FUNCTIONS
		userService.saveUserEdit(user);
		if (role != null) {
			roleService.setRole(user, role);
		}
		model.addObject("success", "Account successfully updated.");
		model.setViewName("redirect:/edit-user-details/" + user.getId());
		return model;
	}

	// UTILS
	private User getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(authentication.getName());
		return user;
	}

	private String getCurrentDate() {
		String pattern = "yyyy-MM-dd";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		String date = simpleDateFormat.format(new Date());
		return date;
	}

	private List<String> getAllRoleName() {
		List<String> roleNames = new ArrayList<>();
		roleNames.add("All role");
		List<Role> roles = roleService.getAllRoles();
		for (Role role : roles) {
			roleNames.add(role.getRoleName());
		}
		return roleNames;
	}

	private HashMap<Long, String> getRoleList(List<User> users) {
		// Map contains pairs of (userId, roleName)
		HashMap<Long, String> roleNames = new HashMap<>();
		for (User user : users) {
			for (Role role : user.getRoles()) {
				roleNames.put(user.getId(), role.getRoleName());
			}
		}
		return roleNames;
	}
}
