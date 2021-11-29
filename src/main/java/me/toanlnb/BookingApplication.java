package me.toanlnb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import me.toanlnb.repository.BillRepository;
import me.toanlnb.repository.BusRepository;
import me.toanlnb.repository.DetailsRepository;
import me.toanlnb.repository.LocationRepository;
import me.toanlnb.repository.RouteRepository;
import me.toanlnb.repository.TicketRepository;
import me.toanlnb.services.UserService;

@SpringBootApplication
public class BookingApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(BookingApplication.class, args);
	}

	@Autowired
	LocationRepository locationRepository;

	@Autowired
	RouteRepository routeRepository;

	@Autowired
	BusRepository busRepository;

	@Autowired
	TicketRepository ticketRepository;

	@Autowired
	UserService userService;

	@Autowired
	BillRepository billRepository;

	@Autowired
	DetailsRepository detailsRepository;

	@Override
	public void run(String... args) throws Exception {

	}
}
