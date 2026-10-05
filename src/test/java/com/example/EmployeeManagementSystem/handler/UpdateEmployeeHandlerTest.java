package com.example.employeemanagementsystem.handler;

import com.example.employee.jooq.generated.tables.records.EmployeesRecord;
import com.example.employeemanagementsystem.dto.metadata.EmployeeMetadata;
import com.example.employeemanagementsystem.dto.request.UpdateEmployeeRequestDTO;
import com.example.employeemanagementsystem.dto.response.EmployeeResponseDTO;
import com.example.employeemanagementsystem.exception.EntityNotFoundException;
import com.example.employeemanagementsystem.mapper.EmployeeMapper;
import com.example.employeemanagementsystem.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateEmployeeHandlerTest {

    @Mock EmployeeRepository employeeRepository;
    @Mock EmployeeMapper     employeeMapper;

    @InjectMocks UpdateEmployeeHandler handler;

    private UUID                     employeeId;
    private UpdateEmployeeRequestDTO request;
    private EmployeesRecord          existing;
    private EmployeeResponseDTO      responseDTO;

    @BeforeEach
    void setup() {
        employeeId = UUID.randomUUID();

        request = new UpdateEmployeeRequestDTO();
        request.setName("Jane Updated");
        request.setEmail("jane.updated@example.com");
        request.setPhone("9000000001");
        request.setDepartment("Engineering");
        request.setDesignation("Senior Developer");

        EmployeeMetadata meta = EmployeeMetadata.builder()
                .employeeType("Full-time")
                .workLocation("Remote")
                .build();
        request.setMetadata(meta);

        existing = new EmployeesRecord();
        existing.setId(employeeId);
        existing.setEmail("jane@example.com");
        existing.setPhone("9876543210");

        responseDTO = new EmployeeResponseDTO();
        responseDTO.setId(employeeId);
        responseDTO.setMetadata(meta);
    }

    @Test
    void updateEmployee_success_returnsUpdatedResponse() {
        when(employeeRepository.findById(employeeId)).thenReturn(existing);
        when(employeeRepository.existsByEmailAndIdNot("jane.updated@example.com", employeeId))
                .thenReturn(false);
        when(employeeRepository.existsByPhoneAndIdNot("9000000001", employeeId))
                .thenReturn(false);
        when(employeeRepository.update(eq(employeeId), any())).thenReturn(existing);
        when(employeeMapper.toResponse(existing)).thenReturn(responseDTO);

        EmployeeResponseDTO result = handler.handle(employeeId, request);

        assertThat(result.getId()).isEqualTo(employeeId);
        assertThat(result.getMetadata()).isNotNull();
        assertThat(result.getMetadata().getWorkLocation()).isEqualTo("Remote");
        verify(employeeMapper).updateEmployee(eq(request), eq(existing));
    }

    @Test
    void updateEmployee_notFound_throwsEntityNotFoundException() {
        when(employeeRepository.findById(employeeId)).thenReturn(null);

        assertThatThrownBy(() -> handler.handle(employeeId, request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(employeeId.toString());

        verify(employeeRepository, never()).update(any(), any());
    }

    @Test
    void updateEmployee_duplicateEmail_throwsIllegalArgument() {
        when(employeeRepository.findById(employeeId)).thenReturn(existing);
        when(employeeRepository.existsByEmailAndIdNot("jane.updated@example.com", employeeId))
                .thenReturn(true);

        assertThatThrownBy(() -> handler.handle(employeeId, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("email already exists");

        verify(employeeRepository, never()).update(any(), any());
    }

    @Test
    void updateEmployee_duplicatePhone_throwsIllegalArgument() {
        when(employeeRepository.findById(employeeId)).thenReturn(existing);
        when(employeeRepository.existsByEmailAndIdNot(anyString(), any())).thenReturn(false);
        when(employeeRepository.existsByPhoneAndIdNot("9000000001", employeeId))
                .thenReturn(true);

        assertThatThrownBy(() -> handler.handle(employeeId, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("phone number already exists");

        verify(employeeRepository, never()).update(any(), any());
    }

    @Test
    void updateEmployee_sameEmailAsSelf_doesNotTriggerDuplicateError() {
        // existsByEmailAndIdNot returns false → same employee's own email is fine
        when(employeeRepository.findById(employeeId)).thenReturn(existing);
        when(employeeRepository.existsByEmailAndIdNot("jane.updated@example.com", employeeId))
                .thenReturn(false);
        when(employeeRepository.existsByPhoneAndIdNot("9000000001", employeeId))
                .thenReturn(false);
        when(employeeRepository.update(eq(employeeId), any())).thenReturn(existing);
        when(employeeMapper.toResponse(existing)).thenReturn(responseDTO);

        assertThatCode(() -> handler.handle(employeeId, request)).doesNotThrowAnyException();
    }

    @Test
    void updateEmployee_nullPhone_skipsPhoneCheck() {
        request.setPhone(null);
        when(employeeRepository.findById(employeeId)).thenReturn(existing);
        when(employeeRepository.existsByEmailAndIdNot(anyString(), any())).thenReturn(false);
        when(employeeRepository.update(eq(employeeId), any())).thenReturn(existing);
        when(employeeMapper.toResponse(existing)).thenReturn(responseDTO);

        assertThatCode(() -> handler.handle(employeeId, request)).doesNotThrowAnyException();
        verify(employeeRepository, never()).existsByPhoneAndIdNot(anyString(), any());
    }

    @Test
    void updateEmployee_nullMetadata_succeeds() {
        request.setMetadata(null);
        when(employeeRepository.findById(employeeId)).thenReturn(existing);
        when(employeeRepository.existsByEmailAndIdNot(anyString(), any())).thenReturn(false);
        when(employeeRepository.existsByPhoneAndIdNot(anyString(), any())).thenReturn(false);
        when(employeeRepository.update(eq(employeeId), any())).thenReturn(existing);
        when(employeeMapper.toResponse(existing)).thenReturn(responseDTO);

        assertThatCode(() -> handler.handle(employeeId, request)).doesNotThrowAnyException();
    }
}
