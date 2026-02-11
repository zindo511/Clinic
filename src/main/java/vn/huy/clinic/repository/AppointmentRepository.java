package vn.huy.clinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.huy.clinic.model.Appointment;
import vn.huy.clinic.model.Doctor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {

    @Query("SELECT a FROM Appointment a " +
            "JOIN FETCH a.patient " +        // Lấy thông tin bệnh nhân
            "JOIN FETCH a.status " +         // Lấy thông tin bảng status (vì nó là bảng riêng)
            "WHERE a.doctor.id = :doctorId " +
            "AND CAST(a.appointmentDatetime AS date) = :date " +
            "AND (:statusName IS NULL OR a.status.statusName = :statusName) " +
            "ORDER BY a.appointmentDatetime ASC")
    List<Appointment> findDoctorAppointments(
            @Param("doctorId") Integer doctorId,
            @Param("date") LocalDate date,
            @Param("statusName") String statusName
    );

    boolean existsByDoctorAndAppointmentDatetimeBetween(Doctor doctor, LocalDateTime localDateTime, LocalDateTime localDateTime1);
}

