package com.example.ms_pet_bff.dto;

import com.example.ms_pet_bff.enums.EstadoCuenta;
import com.example.ms_pet_bff.enums.TipoPropietario;

import java.util.UUID;

public record PropietarioResponseDto (
        UUID id,
        String name,
        String phoneNumber,
        String address,
        String image,
        TipoPropietario tipoPropietario,
        EstadoCuenta estadoCuenta
){
}
