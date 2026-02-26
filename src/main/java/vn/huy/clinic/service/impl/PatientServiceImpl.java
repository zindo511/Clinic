package vn.huy.clinic.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.huy.clinic.dto.Appointment.AppointmentRequest;
import vn.huy.clinic.dto.Appointment.AppointmentResponse;
import vn.huy.clinic.dto.Patient.PatientRequest;
import vn.huy.clinic.dto.Patient.PatientResponse;
import vn.huy.clinic.exception.InvalidDataException;
import vn.huy.clinic.exception.ResourceNotFoundException;
import vn.huy.clinic.model.*;
import vn.huy.clinic.repository.*;
import vn.huy.clinic.service.PatientService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

        private final PatientRepository patientRepository;
        private final DoctorRepository doctorRepository;
        private final AppointmentRepository appointmentRepository;
        private final AppointmentStatusRepository appointmentStatusRepository;
        private final UserRepository userRepository;

        @Override
        public Page<Patient> getAllPatients(int page, int size, String sortBy, String sortDir) {

            // 1. Xử lí hướng sắp xếp
            Sort.Direction direction = sortDir.equalsIgnoreCase("desc")
                            ? Sort.Direction.DESC
                            : Sort.Direction.ASC;

            // 2. Tạo đối tượng sort
            Sort sortObj = Sort.by(direction, sortBy);

            // 3. Validate page
            if (page < 1)
                    page = 1;
            int realPage = page - 1;

            // 4. Tạo đối tượng pageable(trang + kích thước + sắp xếp)
            Pageable pageable = PageRequest.of(realPage, size, sortObj);
            return patientRepository.findAll(pageable);
        }

        @Override
        @Transactional
        public AppointmentResponse createAppointment(String username, AppointmentRequest request) {

            if (request.getAppointmentDatetime().isBefore(LocalDateTime.now())) {
                    throw new InvalidDataException("Không thể đặt lịch trong quá khứ");
            }

            User user = userRepository.findByUsername(username)
                            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
            Patient patient = patientRepository.findByUser_Id(user.getId())
                            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bệnh nhân"));
            Doctor doctor = doctorRepository.findById(request.getDoctorId())
                            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bác sĩ"));
            AppointmentStatus defaultStatus = appointmentStatusRepository.findByStatusName("PENDING")
                            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy trạng thái cuộc hẹn"));

            boolean isBusy = appointmentRepository.existsByDoctorAndAppointmentDatetimeBetween(
                            doctor,
                            request.getAppointmentDatetime().minusMinutes(29),
                            request.getAppointmentDatetime().plusMinutes(29));
            if (isBusy) {
                    throw new InvalidDataException("Bác sĩ đã kín lịch vào khung giờ này");
            }
            Appointment appointment = Appointment.builder()
                            .patient(patient)
                            .doctor(doctor)
                            .appointmentDatetime(request.getAppointmentDatetime())
                            .status(defaultStatus)
                            .reason(request.getReason())
                            .build();
            Appointment saved = appointmentRepository.save(appointment);
            return AppointmentResponse.fromEntity(saved);
        }

        @Override
        public List<PatientResponse> searchPatient(String keyword, String phone) {
            log.info("Tìm kiếm bệnh nhân theo last name {} hoặc sdt {}", keyword, phone);
            return patientRepository.searchPatient(keyword, phone).stream().map(PatientResponse::fromEntity)
                            .toList();
        }

        @Override
        public PatientResponse getPatientById(Integer id) {
            Patient patient = patientRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Không tìm thấy bệnh nhân với id: " + id));
            return PatientResponse.fromEntity(patient);
        }

        @Override
        @Transactional
        public PatientResponse updatePatient(Integer id, PatientRequest request) {
            Patient patient = patientRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Không tìm thấy bệnh nhân với id: " + id));

            patient.setFirstName(request.getFirstName());
            patient.setLastName(request.getLastName());
            patient.setGender(request.getGender());
            patient.setDob(request.getDob());
            patient.setPhone(request.getPhone());
            patient.setAddress(request.getAddress());

            Patient updatedPatient = patientRepository.save(patient);
            return PatientResponse.fromEntity(updatedPatient);
        }

        @Override
        @Transactional
        public void deletePatient(Integer id) {
            if (!patientRepository.existsById(id)) {
                throw new ResourceNotFoundException("Không tìm thấy bệnh nhân với id: " + id);
            }
            patientRepository.deleteById(id);
        }

    @Override
    public PatientResponse getProfile(String username) {
        Patient patient = patientRepository.findByUser_Username(username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bệnh nhân"));

        return PatientResponse.fromEntity(patient);
    }


}
