package com.example.ms_pet_bff.client;

import com.example.ms_pet_bff.dto.DashboardPetResponseDto;
import com.example.ms_pet_bff.dto.MascotaResponseDto;
import com.example.ms_pet_bff.dto.PropietarioResponseDto;
import com.example.ms_pet_bff.enums.EstadoBusqueda;
import com.example.ms_pet_bff.enums.EstadoCuenta;
import com.example.ms_pet_bff.enums.TipoPropietario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BffModelsTest {

    @Test
    @DisplayName("Debería instanciar y acceder a los DTOs (Records)")
    void testDTOs() {
        UUID id = UUID.randomUUID();
        PropietarioResponseDto propDto = new PropietarioResponseDto(
                id, "Fade", "123456", "Informatica", "img.png", TipoPropietario.NATURAL, EstadoCuenta.ACTIVO
        );
        assertEquals("Fade", propDto.name());
        assertEquals("123456", propDto.phoneNumber());
        MascotaResponseDto mascDto = new MascotaResponseDto("Rex", "Pastor", "Perro", "LOST");
        assertEquals("Rex", mascDto.name());
        assertEquals("LOST", mascDto.status());
        DashboardPetResponseDto dashboardDto = new DashboardPetResponseDto(
                id, "Fade", "123456", "Informatica", "Rex", "Pastor",
                EstadoBusqueda.BUSCANDO, TipoPropietario.NATURAL, EstadoCuenta.ACTIVO, "img.png"
        );
        assertEquals("Fade", dashboardDto.nombreDueno());
        assertEquals("Rex", dashboardDto.nombreMascota());
        assertEquals(EstadoBusqueda.BUSCANDO, dashboardDto.estadoBusqueda());
    }

    @Test
    @DisplayName("Debería cubrir los métodos internos de todos los Enums")
    void testEnums() {
        assertTrue(EstadoBusqueda.values().length > 0);
        assertEquals(EstadoBusqueda.BUSCANDO, EstadoBusqueda.valueOf("BUSCANDO"));

        assertTrue(EstadoCuenta.values().length > 0);
        assertEquals(EstadoCuenta.ACTIVO, EstadoCuenta.valueOf("ACTIVO"));

        assertTrue(TipoPropietario.values().length > 0);
        assertEquals(TipoPropietario.NATURAL, TipoPropietario.valueOf("NATURAL"));
    }
}