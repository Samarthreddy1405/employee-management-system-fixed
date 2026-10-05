package com.example.employeemanagementsystem.mapper;

import com.example.employeemanagementsystem.dto.metadata.EmployeeMetadata;
import com.example.employeemanagementsystem.dto.request.CreateEmployeeRequestDTO;
import com.example.employeemanagementsystem.dto.request.UpdateEmployeeRequestDTO;
import com.example.employeemanagementsystem.dto.response.EmployeeResponseDTO;
import com.example.employee.jooq.generated.tables.records.EmployeesRecord;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.jooq.JSONB;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    /**
     * Maps CreateEmployeeRequestDTO → new EmployeesRecord (partial — caller sets id/timestamps/isActive).
     * departmentId and designationId are UUID FK columns; MapStruct maps them by name automatically.
     */
    @Mapping(target = "id",           ignore = true)
    @Mapping(target = "orgId",        source = "orgId")
    @Mapping(target = "departmentId", source = "departmentId")
    @Mapping(target = "designationId",source = "designationId")
    @Mapping(target = "metadata",     source = "metadata")
    @Mapping(target = "isActive",     ignore = true)
    @Mapping(target = "createdOn",    ignore = true)
    @Mapping(target = "updatedOn",    ignore = true)
    @Mapping(target = "createdBy",    ignore = true)
    @Mapping(target = "updatedBy",    ignore = true)
    EmployeesRecord toNewEmployee(CreateEmployeeRequestDTO requestDTO);

    /**
     * Merges UpdateEmployeeRequestDTO into an existing EmployeesRecord in-place.
     */
    @Mapping(target = "id",           ignore = true)
    @Mapping(target = "orgId",        ignore = true)
    @Mapping(target = "departmentId", source = "departmentId")
    @Mapping(target = "designationId",source = "designationId")
    @Mapping(target = "metadata",     source = "metadata")
    @Mapping(target = "isActive",     ignore = true)
    @Mapping(target = "createdOn",    ignore = true)
    @Mapping(target = "updatedOn",    ignore = true)
    @Mapping(target = "createdBy",    ignore = true)
    @Mapping(target = "updatedBy",    ignore = true)
    void updateEmployee(UpdateEmployeeRequestDTO requestDTO,
                        @MappingTarget EmployeesRecord employee);

    /**
     * Maps EmployeesRecord → EmployeeResponseDTO.
     * departmentName and designationName must be set by the caller (handler/service)
     * after resolving the FK names — MapStruct cannot join tables.
     */
    @Mapping(target = "metadata",        expression = "java(fromJsonb(employee.getMetadata()))")
    @Mapping(target = "departmentName",  ignore = true)
    @Mapping(target = "designationName", ignore = true)
    EmployeeResponseDTO toResponse(EmployeesRecord employee);

    // ── JSONB helpers ─────────────────────────────────────────────────────────

    default JSONB toJsonb(EmployeeMetadata metadata) {
        if (metadata == null) return null;
        try {
            return JSONB.valueOf(OBJECT_MAPPER.writeValueAsString(metadata));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to serialise employee metadata", e);
        }
    }

    default EmployeeMetadata fromJsonb(JSONB jsonb) {
        if (jsonb == null || jsonb.data() == null || jsonb.data().isBlank()) return null;
        try {
            return OBJECT_MAPPER.readValue(jsonb.data(), EmployeeMetadata.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to deserialise employee metadata", e);
        }
    }
}
