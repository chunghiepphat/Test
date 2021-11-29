package me.toanlnb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import me.toanlnb.entity.Bus;
import me.toanlnb.entity.Route;

public interface BusRepository extends JpaRepository<Bus, Long>{
	Bus findById(long id);
	List<Bus> findByRoute(Route route);

}
