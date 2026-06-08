package com.example.ms_pet_bff.client;

import com.example.ms_pet_bff.dto.MascotaResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "mascota", url = "http://localhost:8081/api/v1/mascotas")
public interface MascotaClient {
    @GetMapping("/propietario/{ownerId}")
    List<MascotaResponseDto> getByPropietarioId(@PathVariable("ownerId")UUID ownerId);

    @PostMapping
    Object crearMascota(@RequestBody Map<String, Object> mascotaData);
}
