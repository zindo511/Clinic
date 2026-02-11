package vn.huy.clinic.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "prescription_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_id", nullable = false)
    private Prescription prescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;

    @Column(nullable = false)
    private Integer quantity;

    @Column(length = 100)
    private String dosage; // Tần suất

    @Column(length = 100)
    private String frequency;
}

/*
-- Bảng Detail: Lưu chi tiết từng loại thuốc trong toa
CREATE TABLE prescription_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    prescription_id INT NOT NULL,
    medicine_id INT NOT NULL, -- Link tới danh mục thuốc
    quantity INT NOT NULL,    -- Tổng số lượng cấp (VD: 20 viên)
    dosage VARCHAR(100),      -- Liều lượng (VD: 1 viên/lần)
    frequency VARCHAR(100),   -- Tần suất (VD: Sáng - Chiều, sau ăn)
    FOREIGN KEY (prescription_id) REFERENCES prescriptions(id),
    FOREIGN KEY (medicine_id) REFERENCES medicines(id)
);
*/