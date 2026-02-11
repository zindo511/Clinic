package vn.huy.clinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.huy.clinic.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    //Dùng cho việc đăng nhập
    Optional<User> findByUsername(String username);

    // Dùng cho việc đăng ký (Register) - Check trùng
    boolean existsByUsername(String username);

    // Check trùng email khi đăng ký
    boolean existsByEmail(String email);
}
