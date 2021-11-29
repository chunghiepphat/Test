package me.toanlnb.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import me.toanlnb.entity.Location;
import me.toanlnb.entity.Route;

public interface RouteRepository extends JpaRepository<Route, Long> {
	Route findById(long id);
	Route findByDestinationAndStart(Location destination, Location start);
	
}
