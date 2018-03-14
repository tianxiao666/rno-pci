package com.hgicreate.rno.lte.common.repo;

import com.hgicreate.rno.lte.common.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

/**
 * @author chen.c10
 */
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(@Param("username") String username);

    Boolean existsByUsername(String username);
}
