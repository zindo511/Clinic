package vn.huy.clinic.dto;

import lombok.Data;

@Data
public class MedicineResponse {
    private String medicineName;
    private String activeIngredient;
    private String frequency;
    private String dosage;
    private Integer quantity;
}
