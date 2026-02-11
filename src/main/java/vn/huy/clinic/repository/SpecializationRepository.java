package vn.huy.clinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.huy.clinic.model.Specialization;

@Repository
public interface SpecializationRepository extends JpaRepository<Specialization, Long> {
}
