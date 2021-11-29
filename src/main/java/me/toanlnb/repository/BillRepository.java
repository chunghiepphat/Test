package me.toanlnb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import me.toanlnb.entity.Bill;
import me.toanlnb.entity.User;

public interface BillRepository extends JpaRepository<Bill, Long> {
	List<Bill> findByUser(User user);
	void deleteById(long id);

	List<Bill> findAll();
}
