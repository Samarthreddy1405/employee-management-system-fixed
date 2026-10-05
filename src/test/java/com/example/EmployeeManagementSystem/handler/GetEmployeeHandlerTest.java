package com.example.employeemanagementsystem.handler;

import com.example.employee.jooq.generated.tables.records.EmployeesRecord;
import com.example.employeemanagementsystem.dto.metadata.EmployeeMetadata;
import com.example.employeemanagementsystem.dto.response.EmployeeResponseDTO;
import com.example.employeemanagementsystem.exception.EntityNotFoundException;
import com.example.employeemanagementsystem.mapper.EmployeeMapper;
import com.example.employeemanagementsystem.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetEmployeeHandlerTest {

    @Mock EmployeeRepository employeeRepository;
    @Mock EmployeeMapper     employeeMapper;

    @InjectMocks GetEmployeeHandler handler;

    @Test
    void getEmployee_found_returnsResponseWithMetadata() {
        UUID id = UUID.randomUUID();
        EmployeesRecord record = new EmployeesRecord();
        record.setId(id);

        EmployeeMetadata meta = EmployeeMetadata.builder()
                .gender("Male")
                .employeeType("Contract")
                .workLocation("On-site")
                .build();

        EmployeeResponseDTO dto = new EmployeeResponseDTO();
        dto.setId(id);
        dto.setName("John Doe");
        dto.setMetadata(meta);

        when(employeeRepository.findById(id)).thenReturn(record);
        when(employeeMapper.toResponse(record)).thenReturn(dto);

        EmployeeResponseDTO result = handler.getEmployee(id);

        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getName()).isEqualTo("John Doe");
        assertThat(result.getMetadata()).isNotNull();
        assertThat(result.getMetadata().getGender()).isEqualTo("Male");
        assertThat(result.getMetadata().getEmployeeType()).isEqualTo("Contract");
        assertThat(result.getMetadata().getWorkLocation()).isEqualTo("On-site");
    }

    @Test
    void getEmployee_found_nullMetadata_returnsNullMetadataField() {
        UUID id = UUID.randomUUID();
        EmployeesRecord record = new EmployeesRecord();
        record.setId(id);

        EmployeeResponseDTO dto = new EmployeeResponseDTO();
        dto.setId(id);
        dto.setMetadata(null);  // employee created before metadata fields existed

        when(employeeRepository.findById(id)).thenReturn(record);
        when(employeeMapper.toResponse(record)).thenReturn(dto);

        EmployeeResponseDTO result = handler.getEmployee(id);

        assertThat(result.getMetadata()).isNull();
    }

    @Test
    void getEmployee_notFound_throwsEntityNotFoundException() {
        UUID id = UUID.randomUUID();
        when(employeeRepository.findById(id)).thenReturn(null);

        assertThatThrownBy(() -> handler.getEmployee(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(id.toString());

        verify(employeeMapper, never()).toResponse(any());
    }

    @Test
    void getEmployee_callsRepositoryWithCorrectId() {
        UUID id = UUID.randomUUID();
        EmployeesRecord record = new EmployeesRecord();
        record.setId(id);
        EmployeeResponseDTO dto = new EmployeeResponseDTO();
        dto.setId(id);

        when(employeeRepository.findById(id)).thenReturn(record);
        when(employeeMapper.toResponse(record)).thenReturn(dto);

        handler.getEmployee(id);

        verify(employeeRepository).findById(id);
        verify(employeeMapper).toResponse(record);
    }
}
