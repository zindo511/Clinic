package vn.huy.clinic.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "appointment_status")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "status_name", length = 100, nullable = false)
    private String statusName;


}
