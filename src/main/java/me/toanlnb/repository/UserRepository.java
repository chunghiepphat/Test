package me.toanlnb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import me.toanlnb.entity.Role;
import me.toanlnb.entity.User;

@Repository("userRepository")
public interface UserRepository extends JpaRepository<User, Long> {
	User findByEmail(String email);

	@Transactional
	@Modifying
	@Query(value = "UPDATE user set firstname = ?1,lastname= ?2 WHERE id =?3", nativeQuery = true)
	public void update(String firstname, String lastname, Long id);

	@Query("SELECT u FROM User u WHERE CONCAT(u.firstname, ' ', u.lastname, ' ', u.email) LIKE %?1%")
	public List<User> search(String keyword);

	List<User> findByRoles(Role role);

	List<User> findAll();

	@Transactional
    @Modifying
    @Query(value = "UPDATE User u SET u.password = ?1 WHERE u.id = ?2")
    public void updatePassword(String password, Long userId);
}
