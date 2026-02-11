package vn.huy.clinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.huy.clinic.model.DoctorSchedule;

import java.time.DayOfWeek;
import java.util.List;

public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, Integer> {

    List<DoctorSchedule> findAllByDoctorIdAndDow(Integer doctorId, DayOfWeek dow);

    List<DoctorSchedule> findAllByDoctorId(Integer doctorId);
}
