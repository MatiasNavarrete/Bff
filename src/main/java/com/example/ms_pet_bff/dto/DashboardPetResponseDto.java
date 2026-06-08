package com.example.ms_pet_bff.dto;

import com.example.ms_pet_bff.enums.EstadoBusqueda;
import com.example.ms_pet_bff.enums.EstadoCuenta;
import com.example.ms_pet_bff.enums.TipoPropietario;

import java.util.UUID;

public record DashboardPetResponseDto(
        UUID propietarioId,
        String nombreDueno,
        String telefono,
        String direccion,
        String nombreMascota,
        String razaMascota,
        EstadoBusqueda estadoBusqueda,
        TipoPropietario tipoPropietario,
        EstadoCuenta estadoCuenta,
        String image
) {
}
