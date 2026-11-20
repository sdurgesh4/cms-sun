package com.inturn.suncomputer.user.repository;

import com.inturn.suncomputer.user.entity.Role;
import com.inturn.suncomputer.user.entity.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(RoleName name);
}