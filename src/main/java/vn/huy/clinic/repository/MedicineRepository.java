package vn.huy.clinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.huy.clinic.model.Medicine;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Integer> {
}
