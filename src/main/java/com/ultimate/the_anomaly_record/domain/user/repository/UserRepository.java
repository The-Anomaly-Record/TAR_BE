package com.ultimate.the_anomaly_record.domain.user.repository;

import com.ultimate.the_anomaly_record.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
