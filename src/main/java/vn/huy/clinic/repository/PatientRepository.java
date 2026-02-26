package vn.huy.clinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.huy.clinic.model.Patient;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Integer> {

    Optional<Patient> findByUser_Id(Integer userId);

    @Query("select p from Patient p where " +
            "(:keyword is null or p.lastName like concat('%', :keyword, '%')) " +
            "and (:phone is null or p.phone = :phone)")
    List<Patient> searchPatient(String keyword, String phone);
}