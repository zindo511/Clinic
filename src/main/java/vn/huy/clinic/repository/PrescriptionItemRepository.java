package vn.huy.clinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.huy.clinic.model.PrescriptionItem;

import java.util.List;

@Repository
public interface PrescriptionItemRepository extends JpaRepository<PrescriptionItem, Integer> {
    List<PrescriptionItem> findByPrescription_Id(Integer prescriptionId);
}
