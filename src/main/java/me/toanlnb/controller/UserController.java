package me.toanlnb.controller;

import java.util.ArrayList;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import me.toanlnb.entity.Bill;
import me.toanlnb.entity.Bus;
import me.toanlnb.entity.Details;
import me.toanlnb.entity.Location;
import me.toanlnb.entity.Route;
import me.toanlnb.entity.Ticket;
import me.toanlnb.entity.User;
import me.toanlnb.repository.BillRepository;
import me.toanlnb.repository.BusRepository;
import me.toanlnb.repository.DetailsRepository;
import me.toanlnb.repository.LocationRepository;
import me.toanlnb.repository.RouteRepository;
import me.toanlnb.repository.TicketRepository;
import me.toanlnb.services.UserServiceImpl;

@Controller
// USER-EXCLUSIVE MAPPINGS
public class UserController {
	@Autowired
	private UserServiceImpl userService;

	@Autowired
	RouteRepository routeRepository;

	@Autowired
	LocationRepository locationRepository;

	@Autowired
	BusRepository busRepository;

	@Autowired
	TicketRepository ticketRepository;

	@Autowired
	BillRepository billRepository;

	@Autowired
	DetailsRepository detailsRepository;

	// MAPPING: USER DASHBOARD
	@GetMapping("/user-dashboard")
	public String showDashboard(Model model) {
		// GET CURRENT USER
		User currentUser = this.getCurrentUser();
		model.addAttribute("currentUser", currentUser);
		// GET CURRENT DATE
		Date currentDate = this.getCurrentDate();
		model.addAttribute("date", currentDate.toString());
		// LOAD DATA FOR DROP-DOWN LISTS
		model.addAttribute("fromCities", this.getAllFromCities());
		model.addAttribute("toCities", this.getAllToCities());
		return "user/user-dashboard";
	}

	// FIND TRIPS
	@GetMapping(value = { "/find-trips" })
	public String searchBus(Model model, @RequestParam("cityFrom") String cityFrom,
			@RequestParam("cityTo") String cityTo, @RequestParam("date") Date date) {
		// GET CURRENT USER
		User currentUser = this.getCurrentUser();
		model.addAttribute("currentUser", currentUser);
		// GET CURRENT DATE
		Date currentDate = this.getCurrentDate();
		model.addAttribute("currentDate", currentDate.toString());
		// FUNCTIONS
		Location start = locationRepository.findByLocationName(cityFrom);
		Location des = locationRepository.findByLocationName(cityTo);
		// LOAD DATA FOR DROP-DOWN LISTS
		model.addAttribute("fromCities", this.getAllFromCities());
		model.addAttribute("toCities", this.getAllToCities());
		// SEARCH FOR RESULTS
		Route route = routeRepository.findByDestinationAndStart(des, start);
		List<Bus> listBus = busRepository.findByRoute(route);
		model.addAttribute("busList", listBus);
		// RETAIN SELECTED VALUES FOR DROP-DOWN LISTS
		model.addAttribute("cityFrom", cityFrom);
		model.addAttribute("cityTo", cityTo);
		// VALIDATE DATE INPUT
		if (date == null) {
			date = currentDate;
		}
		model.addAttribute("date", date.toString());

		return "user/user-dashboard";
	}

	// FIND TICKETS
	@GetMapping(value = "/find-tickets/{id}/{date}")
	public String showTicket(@PathVariable(name = "id") int id, @PathVariable("date") java.sql.Date date, Model model,
			@ModelAttribute("error") String error) {
		// CHECK ERROR MESSAGE: BUYING TICKETS ALREADY BOOKED
		if (error.length() > 0) {
			model.addAttribute("msg", error);
		}
		// GET CURRENT USER
		User currentUser = this.getCurrentUser();
		model.addAttribute("currentUser", currentUser);
		// GET CURRENT DATE
		Date currentDate = this.getCurrentDate();
		model.addAttribute("currentDate", currentDate);
		// FUNCTIONS
		Bus bus = busRepository.findById(id);
		model.addAttribute("seat", bus.getSeatCapacity());
		model.addAttribute("bus", bus);
		List<Ticket> ticketList = ticketRepository.findByBusAndDate(bus, date);
		HashMap<Integer, Boolean> listSeat = new HashMap<>();
		for (int i = 0; i < bus.getSeatCapacity(); i++) {
			listSeat.put(i + 1, true);
		}
		for (int i = 0; i < ticketList.size(); i++) {
			if (listSeat.containsKey(ticketList.get(i).getSeatNumber())) {
				listSeat.put(ticketList.get(i).getSeatNumber(), false);
			}
		}
		model.addAttribute("listSeat", listSeat);
		model.addAttribute("ticketList", ticketList);
		model.addAttribute("date", date);
		return "user/list-tickets";
	}

	// BOOK TICKETS
	@GetMapping(value = "/book-tickets/{id}/{date}")
	public ModelAndView buyTicket(@PathVariable(name = "id") long busId,
			@RequestParam(value = "checkTicket", required = false) List<Integer> checkTicket,
			@PathVariable("date") java.sql.Date date) {
		ModelAndView model = new ModelAndView();
		// VALIDATE ZERO SEAT SELECTION
		if (checkTicket == null) {
			model.addObject("error", "You have not selected a seat.");
			model.setViewName("redirect:/find-tickets/" + busId + "/" + date.toString());
			return model;
		}
		// VALIDATE MAX SEATS SELECTION
		if (checkTicket.size() > 5) {
			model.addObject("error", "You cannot book more than 5 seats");
			model.setViewName("redirect:/find-tickets/" + busId + "/" + date.toString());
			return model;
		}
		// VALIDATE SEAT AVAILABILITY
		List<Ticket> allTickets = ticketRepository.findAll();
		List<Integer> bookedSeats = new ArrayList<>();
		for (Integer seat : checkTicket) {
			for (Ticket ticket : allTickets) {
				// CHECK IF TICKET IS ALREADY BOOKED
				if (ticket.getBus().getId() == busId && ticket.getSeatNumber() == seat
						&& ticket.getDate().equals(date)) {
					// ADD UNAVAILABLE SEATS TO LIST
					bookedSeats.add(ticket.getSeatNumber());
				}
			}
		}
		if (bookedSeats.size() > 0) {
			// CREATE ERROR MESSAGE
			StringBuilder error = new StringBuilder("Seat number ");
			for (Integer number : bookedSeats) {
				error.append(number + " ");
			}
			error.append(" have been booked by someone else");
			// ADD ERROR MESSAGE TO MODEL
			model.addObject("error", error);
			model.setViewName("redirect:/find-tickets/" + busId + "/" + date.toString());
			return model;
		}
		// GET CURRENT USER
		User currentUser = this.getCurrentUser();
		model.addObject("currentUser", currentUser);
		// GET CURRENT DATE
		Date currentDate = this.getCurrentDate();
		model.addObject("currentDate", currentDate);
		// FUNCTIONS
		for (Integer i : checkTicket) {
			System.out.println(i);
		}
		Bus bus = busRepository.findById(busId);
		for (Integer seat : checkTicket) {
			Bill bill = new Bill();
			Ticket ticket = new Ticket();
			ticket.setDate(date);
			ticket.setBus(bus);
			ticket.setPrice(bus.getPrice());
			ticket.setSeatNumber(seat);
			bill.setUser(currentUser);
			Details detail = new Details();
			detail.setBill(bill);
			detail.setTicket(ticket);
			billRepository.save(bill);
			ticketRepository.save(ticket);
			detailsRepository.save(detail);
		}
		model.addObject("checkTicket", checkTicket);
		model.setViewName("/user/redirect-booking-success");
		return model;
	}

	// MAPPING: USER PROFILE PAGE
	@GetMapping("/view-profile")
	public String viewUserProfile(Model model) {
		// GET CURRENT USER
		User currentUser = this.getCurrentUser();
		model.addAttribute("currentUser", currentUser);
		// GET CURRENT DATE
		Date currentDate = this.getCurrentDate();
		model.addAttribute("currentDate", currentDate);
		return "user/user-profile";
	}

	// UPDATE USER PROFILE
	@PostMapping({ "/update-profile" })
	public String updateUserProfile(Model model, @ModelAttribute("currentUser") User userInfo) {
		Date currentDate = this.getCurrentDate();
		model.addAttribute("currentDate", currentDate);
		userInfo.setId(this.getCurrentUser().getId());
		userService.saveUserEdit(userInfo);
		model.addAttribute("msg", "Your profile has been updated successfully.");
		return "redirect:/view-profile";
	}

	// MAPPING: CHANGE PASSWORD PAGE
	@GetMapping("/change-password")
	public String viewUserPassword(Model model) {
		// GET CURRENT USER
		User currentUser = this.getCurrentUser();
		model.addAttribute("currentUser", currentUser);
		// GET CURRENT DATE
		Date currentDate = this.getCurrentDate();
		model.addAttribute("currentDate", currentDate);
		return "user/user-password";
	}

	// UPDATE USER PASSWORD
	@PostMapping({ "/edit-password" })
	public String updateUserPassword(Model model, @RequestParam("passwordOld") String passwordOld,
			@RequestParam("passwordNew") String passwordNew, @RequestParam("passwordConfirm") String passwordConfirm) {
		User currentUser = this.getCurrentUser();
		boolean acceptUpdate = true;
		// CHECK IF NEW PASSWORD CONFIRMATION MATCHES
		if (!passwordNew.equals(passwordConfirm)) {
			model.addAttribute("failConfirm", "Password does not match.");
			acceptUpdate = false;
		}
		// CHECK IF OLD PASSWORD IS CORRECT
		if (!userService.checkPassword(currentUser, passwordOld)) {
			model.addAttribute("incorrectPassword", "Incorrect password.");
			acceptUpdate = false;
		}
		// CHECK IF NEW PASSWORD IS THE SAME AS OLD PASSWORD
		if (passwordNew.equals(passwordOld)) {
			model.addAttribute("duplicatePassword", "Your new password cannot be the same as the existing one.");
			acceptUpdate = false;
		}
		if (acceptUpdate) {
			userService.updatePassword(passwordNew, currentUser.getId());
			model.addAttribute("success", "Your password has been successfully updated.");
		}
		model.addAttribute("currentUser", currentUser);
		return "user/user-password";
	}

	// MAPPING: BOOKING HISTORY PAGE
	@RequestMapping(value = { "view-bookings" }, method = RequestMethod.GET)
	public String viewBooking(Model model) {
		// GET CURRENT USER
		User currentUser = this.getCurrentUser();
		model.addAttribute("currentUser", currentUser);
		// GET CURRENT DATE
		Date currentDate = this.getCurrentDate();
		model.addAttribute("currentDate", currentDate);
		// FUNCTIONS
		List<Bill> bills = billRepository.findByUser(currentUser);
		List<Details> details = new ArrayList<>();
		for (Bill bill : bills) {
			details.add(detailsRepository.findByBillId(bill.getId()));
		}
		model.addAttribute("details", details);
		return "user/user-bookings";
	}

	// DELETE BOOKING
	@RequestMapping(value = "/delete-booking", method = RequestMethod.POST)
	public String xoa(Model model, @RequestParam("DetailId") long DetailId) {
		// GET CURRENT USER
		User currentUser = this.getCurrentUser();
		model.addAttribute("currentUser", currentUser);
		// GET CURRENT DATE
		Date currentDate = this.getCurrentDate();
		model.addAttribute("currentDate", currentDate);
		// FUNCTIONS
		Details details = detailsRepository.findById(DetailId);

		detailsRepository.deleteById(DetailId);
		billRepository.deleteById(details.getBill().getId());
		ticketRepository.deleteById(details.getTicket().getId());
		return "redirect:/view-bookings";
	}

	// UTILS
	private User getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(authentication.getName());
		return user;
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
