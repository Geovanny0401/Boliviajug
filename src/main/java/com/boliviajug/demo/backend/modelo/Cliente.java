package com.boliviajug.demo.backend.modelo;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter @Setter
    private Long id;

    @Getter @Setter
    @NotEmpty
    private String nombre;

    @Getter @Setter
    @NotEmpty
    @Email(message = "${validatedValue} no es un email valido")
    private String email;

    @Getter @Setter
    private BigDecimal sueldo;

    @Getter @Setter
    @Past
    private LocalDate fechaNacimiento;

    @NotNull
    @Size(min = 6, max = 100)
    private String clave;


}
