package vn.huy.clinic.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.huy.clinic.dto.Appointment.AppointmentResponse;
import vn.huy.clinic.dto.Doctor.request.DoctorCreationRequest;
import vn.huy.clinic.dto.Doctor.request.DoctorUpdate;
import vn.huy.clinic.dto.Doctor.response.DoctorAppointmentResponse;
import vn.huy.clinic.dto.Doctor.response.DoctorResponse;
import vn.huy.clinic.dto.Doctor.response.ScheduleResponse;
import vn.huy.clinic.dto.MedicineResponse;
import vn.huy.clinic.dto.Prescription.PrescriptionPatient;
import vn.huy.clinic.exception.InvalidDataException;
import vn.huy.clinic.exception.ResourceNotFoundException;
import vn.huy.clinic.model.*;
import vn.huy.clinic.repository.*;
import vn.huy.clinic.service.DoctorService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final DoctorScheduleRepository scheduleRepository;
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionItemRepository prescriptionItemRepository;

    @Override
    @Transactional
    public Page<DoctorResponse> searchDoctors(String keyword, Long specializationId, int page, int size, String sortBy, String sortDir) {
        // Xử lý hướng sắp xếp
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        // Tạo đối tượng sort
        Sort sortObj = Sort.by(direction, sortBy);

        if (page < 1) page = 1;
        page -= 1;

        // Tạo đối tuọượng pageable (trang + kích thước + sắp xếp)
        Pageable pageable = PageRequest.of(page, size, sortObj);
        return doctorRepository.searchDoctors(keyword, specializationId, pageable).map(DoctorResponse::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public DoctorResponse getDoctorById(Integer id) {
        log.info("Đang truy xuất thông tin bác sĩ với id: {}", id);
        Doctor doctor = doctorRepository.findByIdWithSpecializations(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bác sĩ với ID: " + id));
        return DoctorResponse.fromEntity(doctor);
    }

    @Override
    public List<LocalTime> getDoctorAvailability(Integer doctorId, LocalDate date) {
        // 1. Lấy lịch làm việc cố định của bác sĩ (vd: Thứ 4 làm 8h-12h)
        List<DoctorSchedule> schedules = scheduleRepository.findAllByDoctorIdAndDow(doctorId, date.getDayOfWeek());

        // 2. Lấy danh sách lịch đã hẹn trong ngày đó
        List<Appointment> occupiedAppointments = appointmentRepository.findDoctorAppointments(doctorId, date, null);

        // 3. Chuyển danh sách Appointment thành Set các giờ đã bị đặt để tra cứu nhanh O(1)
        Set<LocalTime> busyTimes = occupiedAppointments.stream()
                .map(appointment -> appointment.getAppointmentDatetime().toLocalTime())
                .collect(Collectors.toSet());

        // 4. Tạo danh sách các khung giờ trống
        List<LocalTime> avaiableSlots = new ArrayList<>();
        for (DoctorSchedule shift : schedules) {
            LocalTime currentTime = shift.getStartTime();

            while (currentTime.isBefore(shift.getEndTime())){
                if (!busyTimes.contains(currentTime)) {
                    avaiableSlots.add(currentTime);
                }
                // Giả sử mỗi ca khám là 30 phút
                currentTime = currentTime.plusMinutes(30);
            }
        }
        return avaiableSlots;
    }

    @Override
    public DoctorResponse getDoctorProfile() {
        log.info("Xem thống tin cá nhân của mình");
        var auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();

        // Tìm bác sĩ
        Doctor doctor = doctorRepository.findByUserIdWithSpecializations(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin bác sĩ"));

        return DoctorResponse.fromEntity(doctor);
    }

    @Override
    public List<ScheduleResponse> getDoctorSchedule() {
        log.info("Xem lịch làm việc cá nhân của mình");
        var auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();

        // Tìm bác sĩ
        Doctor doctor = doctorRepository.findByUserIdWithSpecializations(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin bác sĩ"));
        List<DoctorSchedule> schedules = scheduleRepository.findAllByDoctorId(doctor.getId());
        return schedules.stream().map(ScheduleResponse::fromEntity).toList();
    }

    @Transactional
    @Override
    public Doctor updateDoctor(String username, DoctorUpdate update) {
        if (update == null) {
            throw new InvalidDataException("Cần có trường thông tin");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user"));
        Doctor doctor = doctorRepository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy doctor"));

        if (update.getEmail() != null && !update.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(update.getEmail()))
                throw new InvalidDataException("Email đã có người sử dụng rồi");
            user.setEmail(update.getEmail());
        }
        userRepository.save(user);

        if (update.getFirstName() != null) doctor.setFirstName(update.getFirstName());
        if (update.getLastName() != null) doctor.setLastName(update.getLastName());
        if (update.getGender() != null) doctor.setGender(update.getGender());
        if (update.getBio() != null) doctor.setBio(update.getBio());
        if (update.getPhone() != null) doctor.setPhone(update.getPhone());

        return doctorRepository.save(doctor);
    }

    @Override
    public DoctorResponse createDoctor(DoctorCreationRequest request) {
        return null;
    }

    @Override
    @Transactional
    public DoctorAppointmentResponse completeAppointment(Integer appointmentId, PrescriptionPatient prescriptionPatient) {
        Appointment appointment = appointmentRepository.findByIdWithPatientAndDoctor(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin cuộc hẹn"));
        Patient patient = appointment.getPatient();
        Doctor doctor = appointment.getDoctor();

        String patientName = patient.getFirstName() + " " + patient.getLastName();
        String doctorName = doctor.getFirstName() + " " + doctor.getLastName();
        LocalDateTime appointmentDatetime = appointment.getAppointmentDatetime();
        String status = "Completed";
        String reason = appointment.getReason();
        String doctorDiagnosis = prescriptionPatient.getDoctorDiagnosis();
        String note = prescriptionPatient.getNote();

        Prescription prescription = prescriptionRepository.findByAppointment_Id(appointment.getId());
        List<PrescriptionItem> prescriptionItems = prescriptionItemRepository.findByPrescription_Id(prescription.getId());
        List<MedicineResponse> medicineResponses = prescriptionItems.stream()
                .map(prescriptionItem -> {
                    Medicine medicine = prescriptionItem.getMedicine();
                    MedicineResponse response = new MedicineResponse();
                    response.setMedicineName(medicine.getName());
                    response.setActiveIngredient(medicine.getActiveIngredient());
                    response.setFrequency(prescriptionItem.getFrequency());
                    response.setDosage(prescriptionItem.getDosage());
                    if (prescriptionItem.getQuantity() > medicine.getStockQuantity())
                        throw new InvalidDataException("Số lượng thuốc trong kho không đủ");
                    medicine.setStockQuantity(medicine.getStockQuantity() - prescriptionItem.getQuantity());
                    response.setQuantity(prescriptionItem.getQuantity());
                    return response;
                })
                .toList();
        return DoctorAppointmentResponse.builder()
                .patientName(patientName)
                .doctorName(doctorName)
                .appointmentDatetime(appointmentDatetime)
                .status(status)
                .reason(reason)
                .doctorDiagnosis(doctorDiagnosis)
                .note(note)
                .medicineResponses(medicineResponses)
                .build();
    }

    // map to dto

}
