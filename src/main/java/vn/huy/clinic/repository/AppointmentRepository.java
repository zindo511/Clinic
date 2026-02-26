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
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {

        @Query("SELECT a FROM Appointment a " +
                        "JOIN FETCH a.patient " + // Lấy thông tin bệnh nhân
                        "JOIN FETCH a.status " + // Lấy thông tin bảng status (vì nó là bảng riêng)
                        "WHERE a.doctor.id = :doctorId " +
                        "AND CAST(a.appointmentDatetime AS date) = :date " +
                        "AND (:statusName IS NULL OR a.status.statusName = :statusName) " +
                        "ORDER BY a.appointmentDatetime ASC")
        List<Appointment> findDoctorAppointments(
                        @Param("doctorId") Integer doctorId,
                        @Param("date") LocalDate date,
                        @Param("statusName") String statusName);

        @Query("SELECT a FROM Appointment a " +
                        "JOIN FETCH a.patient " + // Nạp luôn patient vào memory
                        "JOIN FETCH a.doctor " + // Nạp luôn doctor vào memory
                        "WHERE a.id = :id")
        Optional<Appointment> findByIdWithPatientAndDoctor(@Param("id") Integer id);

        boolean existsByDoctorAndAppointmentDatetimeBetween(Doctor doctor, LocalDateTime localDateTime,
                        LocalDateTime localDateTime1);

        @Query("SELECT a FROM Appointment a " +
                        "JOIN FETCH a.doctor " +
                        "JOIN FETCH a.status " +
                        "WHERE a.patient.id = :patientId " +
                        "ORDER BY a.appointmentDatetime DESC")
        List<Appointment> findPatientAppointments(@Param("patientId") Integer patientId);

        @Query("SELECT a FROM Appointment a " +
                        "JOIN FETCH a.patient " +
                        "JOIN FETCH a.status " +
                        "JOIN FETCH a.doctor " +
                        "where a.patient.id = :patientId " +
                        "AND (:statusName IS NULL OR a.status.statusName = :statusName) " +
                        "ORDER BY a.appointmentDatetime DESC")
        List<Appointment> findPatientAppointmentByStatus(
                        @Param("patientId") Integer patientId,
                        @Param("statusName") String statusName);

        @Query("SELECT a FROM Appointment a " +
                        "JOIN FETCH a.patient p " +
                        "JOIN FETCH p.user " +
                        "JOIN FETCH a.status " +
                        "JOIN FETCH a.doctor " +
                        "WHERE a.id = :appointmentId " +
                        "AND a.status.statusName = :statusName")
        Optional<Appointment> findByIdByStatus(
                        @Param("appointmentId") Integer appointmentId,
                        @Param("statusName") String statusName);
}
