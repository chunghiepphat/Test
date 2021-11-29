package me.toanlnb.controller;

import java.util.ArrayList;
import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import me.toanlnb.entity.Role;
import me.toanlnb.entity.Route;
import me.toanlnb.entity.User;
import me.toanlnb.repository.BusRepository;
import me.toanlnb.repository.RouteRepository;
import me.toanlnb.repository.TicketRepository;
import me.toanlnb.services.UserServiceImpl;
import me.toanlnb.validator.UserValidator;

@Controller
// GLOBAL MAPPINGS
public class MainController {
	@Autowired
	private UserServiceImpl userService;

	@Autowired
	private UserValidator userValidator;

	@Autowired
	RouteRepository routeRepository;

	@Autowired
	BusRepository busRepository;

	@Autowired
	TicketRepository ticketRepository;

	// MAPPING: WELCOME PAGE
	@GetMapping({ "/index","/"  })
	public String accessIndexPage(Model model) {
		// GET CURRENT USER
		User currentUser;
		if (this.getCurrentUser() != null) {
			currentUser = this.getCurrentUser();
			model.addAttribute("currentUser", currentUser);
		}
		// GET CURRENT DATE
		Date currentDate = this.getCurrentDate();
		model.addAttribute("currentDate", currentDate.toString());
		// Load data for drop-down list
		model.addAttribute("fromCities", this.getAllFromCities());
		model.addAttribute("toCities", this.getAllToCities());

		// Check role of current user
		if (this.getCurrentUser() != null) {
			for (Role role : this.getCurrentUser().getRoles()) {
				if (role.getRoleName().contains("admin")) {
					return "admin/admin-dashboard";
				} else if (role.getRoleName().contains("seller")) {
					return "seller/seller-dashboard";
				}
			}
		}
		return "home/home";
	}

	// MAPPING: LOGIN PAGE
	@GetMapping({ "/login"})
	public String accessLoginPage() {
		User user = this.getCurrentUser();
		if (user != null) {
			return "redirect:/";
		}
		return "home/sign-in";
	}

	// MAPPING: REGISTRATION PAGE
	@GetMapping("/register")
	public String accessSignUpPage(Model model) {
		User user = new User();
		model.addAttribute("user", user);
		return "home/sign-up";
	}

	// REGISTRATION HANDLER
	@PostMapping("/signup")
	public String signUp(Model model, User user, BindingResult bindingResult) {
		userValidator.validate(user, bindingResult);
		User userExists = userService.findUserByEmail(user.getEmail());
		if (userExists != null) {
			bindingResult.rejectValue("email", "error.user", "This email already exists!");
		}
		System.out.println("check exist");
		userService.saveUserRegister(user);
		model.addAttribute("msg", "User has been registered successfully!");
		model.addAttribute("user", new User());
		return "home/sign-up";
	}

	// MAPPING: ABOUT PAGE
	@GetMapping("/about-us")
	public String accessAboutPage(Model model) {
		// GET CURRENT USER
		User currentUser = this.getCurrentUser();
		model.addAttribute("currentUser", currentUser);
		return "home/about-us";
	}

	// UTILS
	private User getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return userService.findUserByEmail(authentication.getName());
	}

	private Date getCurrentDate() {
		return new Date(System.currentTimeMillis());
	}

	private List<String> getAllFromCities() {
		List<Route> routes = routeRepository.findAll();
		List<String> fromCities = new ArrayList<>();
		for (Route route : routes) {
			String fromCity = route.getStart().getLocationName();

			// Duplicate city, ignore it
			if (fromCities.contains(fromCity)) {
				continue;
			}
			fromCities.add(fromCity);
		}
		return fromCities;
	}

	private List<String> getAllToCities() {
		List<Route> routes = routeRepository.findAll();
		List<String> toCities = new ArrayList<>();
		for (Route route : routes) {
			String toCity = route.getDestination().getLocationName();

			// Duplicate city, ignore it
			if (toCities.contains(toCity)) {
				continue;
			}
			toCities.add(toCity);
		}
		return toCities;
	}
}
