package com.example.ms_pet_bff.dto;

public record MascotaResponseDto(
        String name,
        String breed,
        String species,
        String status
) {
}
