package com.boliviajug.demo.backend.modelo;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


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
    private String nombre;

    @Getter @Setter
    private String direccion;

    @Getter @Setter
    private String ciudad;

    @Getter @Setter
    private Double telefono;

    @Getter @Setter
    private String email;

    @Getter @Setter
    private TipoTarjeta tipoTarjeta;



}
