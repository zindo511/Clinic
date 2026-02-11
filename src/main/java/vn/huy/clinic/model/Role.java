package vn.huy.clinic.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "role")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 20, nullable = false, unique = true)
    private String name;
}

/*
create table role (
	id int auto_increment primary key,
    name varchar(20) not null unique -- VD: ROLE_ADMIN, ROLE_DOCTOR, ROLE_PATIENT
);
 */