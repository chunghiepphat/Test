package me.toanlnb.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import me.toanlnb.entity.Location;

import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Long> {
	Location findByLocationName(String locationName);
	List<Location> findAll();
}
