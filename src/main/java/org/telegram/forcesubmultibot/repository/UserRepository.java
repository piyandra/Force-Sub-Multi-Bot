package org.telegram.forcesubmultibot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.telegram.forcesubmultibot.entity.Users;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
}
