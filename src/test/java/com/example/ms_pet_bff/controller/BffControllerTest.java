package com.example.ms_pet_bff.controller;
import com.example.ms_pet_bff.client.MascotaClient;
import com.example.ms_pet_bff.client.PropietarioClient;
import com.example.ms_pet_bff.dto.MascotaResponseDto;
import com.example.ms_pet_bff.dto.PropietarioResponseDto;
import com.example.ms_pet_bff.enums.EstadoCuenta;
import com.example.ms_pet_bff.enums.TipoPropietario;
import feign.FeignException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest(BffController.class)
class BffControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PropietarioClient propClient;

    @MockitoBean
    private MascotaClient mascClient;

    @Autowired
    private tools.jackson.databind.ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /dashboard - Debería retornar datos combinados (Dueño con mascota)")
    void getDashboardData_ConMascota() throws Exception {
        UUID propId = UUID.randomUUID();
        PropietarioResponseDto prop = new PropietarioResponseDto(propId, "Juan", "123", "Calle 1", "img.jpg", TipoPropietario.NATURAL, EstadoCuenta.ACTIVO);
        MascotaResponseDto masc = new MascotaResponseDto("Rex", "Pastor", "Perro", "LOST");

        when(propClient.getAll()).thenReturn(List.of(prop));
        when(mascClient.getByPropietarioId(propId)).thenReturn(List.of(masc));

        mockMvc.perform(get("/api/v1/bff/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreDueno").value("Juan"))
                .andExpect(jsonPath("$[0].nombreMascota").value("Rex"))
                .andExpect(jsonPath("$[0].estadoBusqueda").value("BUSCANDO"));
    }

    @Test
    @DisplayName("GET /dashboard - Debería manejar dueños sin mascota")
    void getDashboardData_SinMascota() throws Exception {
        UUID propId = UUID.randomUUID();
        PropietarioResponseDto prop = new PropietarioResponseDto(propId, "Maria", "456", "Calle 2", "img.jpg", TipoPropietario.NATURAL, EstadoCuenta.ACTIVO);

        when(propClient.getAll()).thenReturn(List.of(prop));
        when(mascClient.getByPropietarioId(propId)).thenReturn(List.of()); // Lista vacía

        mockMvc.perform(get("/api/v1/bff/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreDueno").value("Maria"))
                .andExpect(jsonPath("$[0].nombreMascota").value("Sin mascota"))
                .andExpect(jsonPath("$[0].razaMascota").value("N/A"));
    }

    @Test
    @DisplayName("POST /registro - Debería registrar dueño y mascota exitosamente")
    void registrarTodo_Exito() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("nameMascota", "Firulais");
        payload.put("address", "Avenida 3");

        PropietarioResponseDto propGuardado = new PropietarioResponseDto(UUID.randomUUID(), "Pedro", "789", "Avenida 3", "img.jpg", TipoPropietario.NATURAL, EstadoCuenta.ACTIVO);

        when(propClient.registrar(anyMap())).thenReturn(propGuardado);
        when(mascClient.crearMascota(anyMap())).thenReturn(null);

        mockMvc.perform(post("/api/v1/bff/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Pedro"));
    }

    @Test
    @DisplayName("POST /registro - Debería manejar BadRequest de Feign (Ej. Email duplicado)")
    void registrarTodo_FeignBadRequest() throws Exception {
        Map<String, Object> payload = new HashMap<>();

        FeignException.BadRequest mockException = mock(FeignException.BadRequest.class);
        when(mockException.contentUTF8()).thenReturn("Email ya existe");
        when(propClient.registrar(anyMap())).thenThrow(mockException);

        mockMvc.perform(post("/api/v1/bff/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error de validación: Email ya existe"));
    }

    @Test
    @DisplayName("POST /registro - Debería manejar errores genéricos 500")
    void registrarTodo_ErrorInterno() throws Exception {
        when(propClient.registrar(anyMap())).thenThrow(new RuntimeException("Falla en la base de datos"));

        mockMvc.perform(post("/api/v1/bff/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error orquestando el registro: Falla en la base de datos"));
    }

    @Test
    @DisplayName("DELETE /propietario/{id} - Debería eliminar exitosamente")
    void eliminarReporte_Exito() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(propClient).eliminar(id);

        mockMvc.perform(delete("/api/v1/bff/propietario/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /propietario/{id} - Debería manejar error interno 500")
    void eliminarReporte_ErrorInterno() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new RuntimeException("Error de conexión")).when(propClient).eliminar(id);

        mockMvc.perform(delete("/api/v1/bff/propietario/{id}", id))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error al intentar eliminar: Error de conexión"));
    }
    @Test
    @DisplayName("GET /dashboard - Debería manejar lista de mascotas nula (Cubre rama mascotas != null)")
    void getDashboardData_MascotasNull() throws Exception {
        UUID propId = UUID.randomUUID();
        PropietarioResponseDto prop = new PropietarioResponseDto(propId, "Ana", "111", "Calle 3", "img.jpg", TipoPropietario.NATURAL, EstadoCuenta.ACTIVO);

        when(propClient.getAll()).thenReturn(List.of(prop));
        when(mascClient.getByPropietarioId(propId)).thenReturn(null);

        mockMvc.perform(get("/api/v1/bff/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreDueno").value("Ana"))
                .andExpect(jsonPath("$[0].nombreMascota").value("Sin mascota"));
    }

    @Test
    @DisplayName("GET /dashboard - Debería mapear estado distinto a LOST como ENCONTRADO (Cubre rama ternaria)")
    void getDashboardData_MascotaFound() throws Exception {
        UUID propId = UUID.randomUUID();
        PropietarioResponseDto prop = new PropietarioResponseDto(propId, "Luis", "222", "Calle 4", "img.jpg", TipoPropietario.NATURAL, EstadoCuenta.ACTIVO);
        MascotaResponseDto masc = new MascotaResponseDto("Boby", "Pug", "Perro", "FOUND");

        when(propClient.getAll()).thenReturn(List.of(prop));
        when(mascClient.getByPropietarioId(propId)).thenReturn(List.of(masc));

        mockMvc.perform(get("/api/v1/bff/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estadoBusqueda").value("ENCONTRADO"));
    }
}