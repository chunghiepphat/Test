package me.toanlnb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import me.toanlnb.entity.*;
import me.toanlnb.repository.*;
import me.toanlnb.services.UserService;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;

@Controller
// SELLER-EXCLUSIVE MAPPINGS
public class SellerController {
    @Autowired
    UserService userService;
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

    // MAPPING: SELLER DASHBOARD
    @RequestMapping(value = {"/seller-dashboard"})
    public String viewSellerPage(Model model) {
        // GET CURRENT USER
        User currentUser = this.getCurrentUser();
        model.addAttribute("currentUser", currentUser);
        // GET CURRENT DATE
        Date currentDate = this.getCurrentDate();
        model.addAttribute("date", currentDate.toString());
        // FUNCTIONS
        model.addAttribute("fromCities", this.getAllFromCities());
        model.addAttribute("toCities", this.getAllToCities());
        return "seller/seller-dashboard";
    }

    // LIST TRIPS
    @GetMapping(value = {"/view-trips"})
    public String searchBusSeller(Model model, @RequestParam("cityFrom") String cityFrom,
                                  @RequestParam("cityTo") String cityTo, @RequestParam("date") Date date) {
        // GET CURRENT USER
        User currentUser = this.getCurrentUser();
        model.addAttribute("currentUser", currentUser);
        // SEARCH FOR BUS
        Location start = locationRepository.findByLocationName(cityFrom);
        Location des = locationRepository.findByLocationName(cityTo);
        Route route = routeRepository.findByDestinationAndStart(des, start);
        List<Bus> listBus = busRepository.findByRoute(route);
        model.addAttribute("busList", listBus);
        // LOAD DATA FOR DROP-DOWN LISTS
        model.addAttribute("fromCities", this.getAllFromCities());
        model.addAttribute("toCities", this.getAllToCities());
        // RETAIN SELECTED VALUES FOR DROP-DOWN LISTS
        model.addAttribute("cityFrom", cityFrom);
        model.addAttribute("cityTo", cityTo);
        model.addAttribute("date", date.toString());
        return "seller/seller-dashboard";
    }

    // LIST TICKETS
    @GetMapping(value = "/view-tickets/{id}/{date}")
    public String showTicketForSeller(@PathVariable(name = "id") int id, Model model,
                                      @PathVariable("date") java.sql.Date date) {
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
        HashMap<Integer, Boolean> listSeat = new HashMap<Integer, Boolean>();
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
        return "seller/manage-tickets";
    }

    // FILTER TICKETS
    @RequestMapping(value = "/filter-tickets/{id}/{date}")
    public String filterTicket(@PathVariable(name = "id") int id, @PathVariable("date") Date date, Model model,
                               @RequestParam(name = "filter") String filter) {
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
        HashMap<Integer, Boolean> listSeat = new HashMap<Integer, Boolean>();
        HashMap<Integer, Boolean> listSeat1 = new HashMap<Integer, Boolean>();
        for (int i = 0; i < bus.getSeatCapacity(); i++) {
            listSeat1.put(i + 1, true);
        }
        if (filter.equalsIgnoreCase("Available tickets")) {
            if (ticketList.isEmpty()) {
                listSeat = listSeat1;
            } else {
                for (int i = 0; i < ticketList.size(); i++) {
                    System.out.println(ticketList.get(i).getSeatNumber());
                    if (listSeat1.containsKey(ticketList.get(i).getSeatNumber())) {
                        listSeat1.remove(ticketList.get(i).getSeatNumber());
                        listSeat = listSeat1;
                    }
                }
            }
        }
        if (filter.equalsIgnoreCase("Unvailable tickets")) {
            for (int i = 0; i < ticketList.size(); i++) {
                if (listSeat1.containsKey(ticketList.get(i).getSeatNumber())) {
                    listSeat.put(ticketList.get(i).getSeatNumber(), false);
                }
            }
        }
        model.addAttribute("filterValue", filter);
        model.addAttribute("listSeat", listSeat);
        model.addAttribute("ticketList", ticketList);
        model.addAttribute("date", date);
        return "seller/manage-tickets";
    }

    // LIST ALL BOOKED TICKETS
    @RequestMapping(value = {"/view-all-bookings"}, method = RequestMethod.GET)
    public String viewBooking(Model model) {
        // GET CURRENT USER
        User currentUser = this.getCurrentUser();
        model.addAttribute("currentUser", currentUser);
        // GET CURRENT DATE
        Date currentDate = this.getCurrentDate();
        model.addAttribute("currentDate", currentDate);
        // FUNCTIONS
        List<Bill> bills = billRepository.findAll();
        List<Details> details = new ArrayList<Details>();
        for (Bill bill : bills) {
            if (detailsRepository.findByBillId(bill.getId()) != null) {
                details.add(detailsRepository.findByBillId(bill.getId()));
            }
        }
        model.addAttribute("details", details);
        return "seller/manage-bookings";
    }

    // REMOVE BOOKED TICKET
    @RequestMapping(value = "/remove-booking", method = RequestMethod.POST)
    public ModelAndView xoa(@RequestParam(value = "DetailId", required = false) List<Long> DetailId) {
        // GET CURRENT USER
        ModelAndView model = new ModelAndView();
        User currentUser = this.getCurrentUser();
        model.addObject("currentUser", currentUser);
        // GET CURRENT DATE
        Date currentDate = this.getCurrentDate();
        model.addObject("currentDate", currentDate);
        // FUNCTIONS
        if (DetailId == null) {
            model.addObject("error", "Please select ticket you want delete");
            model.setViewName("redirect:/view-all-bookings");
            return model;
        }
        for (long id : DetailId) {
            List<Details> details = detailsRepository.findAllById(id);
            for (Details detail : details) {
                detailsRepository.deleteById(detail.getId());
                billRepository.deleteById(detail.getBill().getId());
                ticketRepository.deleteById(detail.getTicket().getId());
            }

        }
        model.setViewName("redirect:/view-all-bookings");
        return model;
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