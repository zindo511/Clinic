package vn.huy.clinic.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.huy.clinic.model.Doctor;
import vn.huy.clinic.model.common.Gender;

import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Integer> {

    @Query("SELECT DISTINCT d FROM Doctor d " +
            "LEFT JOIN FETCH d.specializations s " + // 1. Join sang bảng chuyên khoa
            "WHERE " +
            "(:keyword IS NULL OR " +
            "  LOWER(d.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "  LOWER(d.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))" +
            ") " +
            "AND " +
            "(:specializationId IS NULL OR s.id = :specializationId)")
    Page<Doctor> searchDoctors(@Param("keyword") String keyword,
                                       @Param("specializationId") Long specializationId, Pageable pageable);

    @Query("select d from Doctor d " +
            "left join fetch d.specializations " +
            "where d.id = :id")
    Optional<Doctor> findByIdWithSpecializations(Integer id);

    boolean existsByUserId(Long id);

    @Query("SELECT d FROM Doctor d " +
            "LEFT JOIN FETCH d.specializations " +
            "WHERE d.user.id = :userId")
    Optional<Doctor> findByUserIdWithSpecializations(@Param("userId") Integer userId);

}
