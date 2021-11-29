package me.toanlnb.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import me.toanlnb.entity.Details;

import java.util.List;

public interface DetailsRepository extends JpaRepository<Details, Long> {
	List<Details> findAllById(long id);

	Details findByBillId(long id);

	void deleteById(long id);
	Details findById(long id);
}
