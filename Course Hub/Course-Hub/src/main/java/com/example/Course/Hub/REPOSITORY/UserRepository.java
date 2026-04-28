package com.example.Course.Hub.REPOSITORY;

import com.example.Course.Hub.ENTITY.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users , Integer> {

    boolean existsByEmail(String email);

    Users  getByEmail(String email);


}
