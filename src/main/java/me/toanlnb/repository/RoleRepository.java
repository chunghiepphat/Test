package me.toanlnb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import me.toanlnb.entity.Role;

@Repository("roleRepository")
public interface RoleRepository extends JpaRepository<Role, Long> {

	Role findByRoleName(String roleName);

	@Transactional
	@Modifying
	@Query(value = "UPDATE user_role set role_id = ?1 WHERE user_id =?2", nativeQuery = true)
	public void updateRole(int roleId, int userId);

	@Transactional
	@Modifying
	@Query(value = "Update user_role u set u.role_id=?1 where u.user_id =?2", nativeQuery = true)
	public void setRole(Long role_id, Long user_id);

}
