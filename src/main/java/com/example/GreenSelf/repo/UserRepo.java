package com.example.GreenSelf.repo;
import com.example.GreenSelf.entity.Role;
import com.example.GreenSelf.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User,Integer> {
    Optional<User> findUserByUsername(String username);
    Optional<User> findByUsernameAndRole(String username, Role role);
}
