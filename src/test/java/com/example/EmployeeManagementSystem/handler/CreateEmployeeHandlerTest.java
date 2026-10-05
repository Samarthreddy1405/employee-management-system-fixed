package com.example.employeemanagementsystem.handler;

import com.example.employee.jooq.generated.tables.records.EmployeesRecord;
import com.example.employeemanagementsystem.dto.metadata.EmployeeMetadata;
import com.example.employeemanagementsystem.dto.request.CreateEmployeeRequestDTO;
import com.example.employeemanagementsystem.dto.response.EmployeeResponseDTO;
import com.example.employeemanagementsystem.mapper.EmployeeMapper;
import com.example.employeemanagementsystem.repository.EmployeeRepository;
import org.jooq.JSONB;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateEmployeeHandlerTest {

    @Mock EmployeeRepository employeeRepository;
    @Mock EmployeeMapper     employeeMapper;

    @InjectMocks CreateEmployeeHandler handler;

    private CreateEmployeeRequestDTO request;
    private EmployeesRecord          savedRecord;
    private EmployeeResponseDTO      responseDTO;

    @BeforeEach
    void setup() {
        request = new CreateEmployeeRequestDTO();
        request.setOrgId(UUID.randomUUID());
        request.setName("Jane Smith");
        request.setEmail("jane@example.com");
        request.setPhone("9876543210");
        request.setDepartment("IT");
        request.setDesignation("Developer");

        EmployeeMetadata meta = EmployeeMetadata.builder()
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .gender("Female")
                .employeeType("Full-time")
                .build();
        request.setMetadata(meta);

        savedRecord = new EmployeesRecord();
        savedRecord.setId(UUID.randomUUID());
        savedRecord.setIsActive(true);

        responseDTO = new EmployeeResponseDTO();
        responseDTO.setId(savedRecord.getId());
        responseDTO.setName("Jane Smith");
        responseDTO.setMetadata(meta);
    }

    @Test
    void createEmployee_success_returnsResponseWithMetadata() {
        when(employeeRepository.existsByEmail("jane@example.com")).thenReturn(false);
        when(employeeRepository.existsByPhone("9876543210")).thenReturn(false);
        when(employeeMapper.toNewEmployee(request)).thenReturn(savedRecord);
        when(employeeMapper.toJsonb(any(EmployeeMetadata.class))).thenReturn(JSONB.valueOf("{}"));
        when(employeeRepository.save(any())).thenReturn(savedRecord);
        when(employeeMapper.toResponse(savedRecord)).thenReturn(responseDTO);

        EmployeeResponseDTO result = handler.handle(request);

        assertThat(result.getName()).isEqualTo("Jane Smith");
        assertThat(result.getMetadata()).isNotNull();
        assertThat(result.getMetadata().getGender()).isEqualTo("Female");
        verify(employeeRepository).save(any(EmployeesRecord.class));
    }

    @Test
    void createEmployee_setsIsActiveTrue_beforeSave() {
        when(employeeRepository.existsByEmail(anyString())).thenReturn(false);
        when(employeeRepository.existsByPhone(anyString())).thenReturn(false);
        when(employeeMapper.toNewEmployee(any())).thenReturn(savedRecord);
        when(employeeMapper.toJsonb(any(EmployeeMetadata.class))).thenReturn(JSONB.valueOf("{}"));
        when(employeeRepository.save(any())).thenReturn(savedRecord);
        when(employeeMapper.toResponse(any())).thenReturn(responseDTO);

        handler.handle(request);

        verify(employeeRepository).save(argThat(r -> Boolean.TRUE.equals(r.getIsActive())));
    }

    @Test
    void createEmployee_setsCreatedOnAndUpdatedOn() {
        when(employeeRepository.existsByEmail(anyString())).thenReturn(false);
        when(employeeRepository.existsByPhone(anyString())).thenReturn(false);
        when(employeeMapper.toNewEmployee(any())).thenReturn(savedRecord);
        when(employeeMapper.toJsonb(any(EmployeeMetadata.class))).thenReturn(JSONB.valueOf("{}"));
        when(employeeRepository.save(any())).thenReturn(savedRecord);
        when(employeeMapper.toResponse(any())).thenReturn(responseDTO);

        handler.handle(request);

        verify(employeeRepository).save(argThat(r ->
                r.getCreatedOn() != null && r.getUpdatedOn() != null));
    }

    @Test
    void createEmployee_duplicateEmail_throwsIllegalArgument() {
        when(employeeRepository.existsByEmail("jane@example.com")).thenReturn(true);

        assertThatThrownBy(() -> handler.handle(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("email already exists");

        verify(employeeRepository, never()).save(any());
    }

    @Test
    void createEmployee_duplicatePhone_throwsIllegalArgument() {
        when(employeeRepository.existsByEmail("jane@example.com")).thenReturn(false);
        when(employeeRepository.existsByPhone("9876543210")).thenReturn(true);

        assertThatThrownBy(() -> handler.handle(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("phone number already exists");

        verify(employeeRepository, never()).save(any());
    }

    @Test
    void createEmployee_nullPhone_skipsPhoneCheck() {
        request.setPhone(null);
        when(employeeRepository.existsByEmail(anyString())).thenReturn(false);
        when(employeeMapper.toNewEmployee(any())).thenReturn(savedRecord);
        when(employeeMapper.toJsonb(any(EmployeeMetadata.class))).thenReturn(JSONB.valueOf("{}"));
        when(employeeRepository.save(any())).thenReturn(savedRecord);
        when(employeeMapper.toResponse(any())).thenReturn(responseDTO);

        assertThatCode(() -> handler.handle(request)).doesNotThrowAnyException();
        verify(employeeRepository, never()).existsByPhone(anyString());
    }

    @Test
    void createEmployee_nullMetadata_succeeds_andSkipsJsonbConversion() {
        request.setMetadata(null);
        when(employeeRepository.existsByEmail(anyString())).thenReturn(false);
        when(employeeRepository.existsByPhone(anyString())).thenReturn(false);
        when(employeeMapper.toNewEmployee(any())).thenReturn(savedRecord);
        when(employeeRepository.save(any())).thenReturn(savedRecord);
        when(employeeMapper.toResponse(any())).thenReturn(responseDTO);

        assertThatCode(() -> handler.handle(request)).doesNotThrowAnyException();
        // toJsonb should never be called when metadata is null
        verify(employeeMapper, never()).toJsonb(any(EmployeeMetadata.class));
    }

    @Test
    void createEmployee_emailTrimmed_beforeCheck() {
        request.setEmail("  jane@example.com  ");
        when(employeeRepository.existsByEmail("jane@example.com")).thenReturn(false);
        when(employeeRepository.existsByPhone(anyString())).thenReturn(false);
        when(employeeMapper.toNewEmployee(any())).thenReturn(savedRecord);
        when(employeeMapper.toJsonb(any(EmployeeMetadata.class))).thenReturn(JSONB.valueOf("{}"));
        when(employeeRepository.save(any())).thenReturn(savedRecord);
        when(employeeMapper.toResponse(any())).thenReturn(responseDTO);

        handler.handle(request);

        // existsByEmail called with trimmed value
        verify(employeeRepository).existsByEmail("jane@example.com");
    }
}
