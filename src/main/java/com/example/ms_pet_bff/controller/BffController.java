package com.example.ms_pet_bff.controller;

import com.example.ms_pet_bff.client.MascotaClient;
import com.example.ms_pet_bff.client.PropietarioClient;
import com.example.ms_pet_bff.dto.DashboardPetResponseDto;
import com.example.ms_pet_bff.dto.MascotaResponseDto;
import com.example.ms_pet_bff.dto.PropietarioResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bff")
@CrossOrigin(origins = "http://localhost:5173")
public class BffController {

    @Autowired
    private PropietarioClient propClient;

    @Autowired
    private MascotaClient mascClient;

    @GetMapping("/dashboard")
    public ResponseEntity<List<DashboardPetResponseDto>> getDashboardData() {
        var propietarios = propClient.getAll();

        var listaCombinada = propietarios.stream().map(p -> {
            List<MascotaResponseDto> mascotas = mascClient.getByPropietarioId(p.id());
            MascotaResponseDto m = (mascotas != null && !mascotas.isEmpty()) ? mascotas.get(0) : null;

            return new DashboardPetResponseDto(
                    p.id(),
                    p.name(),
                    p.phoneNumber(),
                    p.address(),
                    m != null ? m.name() : "Sin mascota",
                    m != null ? m.breed() : "N/A",
                    m != null ? com.example.ms_pet_bff.enums.EstadoBusqueda.valueOf(m.status().equals("LOST") ? "BUSCANDO" : "ENCONTRADO") : null,
                    p.tipoPropietario(),
                    p.estadoCuenta(),
                    p.image()
            );
        }).toList();

        return ResponseEntity.ok(listaCombinada);
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registrarTodo(@RequestBody Map<String, Object> payload) {
        try {
            //Guardar Propietario
            PropietarioResponseDto propGuardado = propClient.registrar(payload);

            //Preparar datos para Mascota con campos por defecto para evitar el 400
            Map<String, Object> mascotaData = new HashMap<>();
            mascotaData.put("name", payload.get("nameMascota"));
            mascotaData.put("ownerId", propGuardado.id());
            mascotaData.put("lastKnownLocation", payload.getOrDefault("address", "No especificada"));
            mascotaData.put("status", "LOST");

            mascotaData.put("species", "No especificada");
            mascotaData.put("breed", "Mestizo");
            mascotaData.put("status", "LOST");
            mascotaData.put("description", "Reportado vía Mascotas Bien");

            mascClient.crearMascota(mascotaData);

            return ResponseEntity.ok(propGuardado);

        } catch (feign.FeignException.BadRequest e) {
            // Esto atrapará los errores 400 (como el email duplicado) y los mostrará mejor
            return ResponseEntity.status(400).body("Error de validación: " + e.contentUTF8());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error orquestando el registro: " + e.getMessage());
        }
    }

    @DeleteMapping("/propietario/{id}")
    public ResponseEntity<?> eliminarReporte(@PathVariable UUID id) {
        try {
            propClient.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al intentar eliminar: " + e.getMessage());
        }
    }
}