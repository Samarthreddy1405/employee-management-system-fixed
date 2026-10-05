package com.example.employeemanagementsystem.mapper;

import com.example.employee.jooq.generated.tables.records.EmployeesRecord;
import com.example.employeemanagementsystem.dto.metadata.EmployeeMetadata;
import com.example.employeemanagementsystem.dto.response.EmployeeResponseDTO;
import org.jooq.JSONB;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EmployeeMapperTest {

    private final EmployeeMapper mapper = Mappers.getMapper(EmployeeMapper.class);

    // ── toJsonb ────────────────────────────────────────────────────────────────

    @Test
    void toJsonb_null_returnsNull() {
        assertThat(mapper.toJsonb(null)).isNull();
    }

    @Test
    void toJsonb_producesValidJsonbString() {
        EmployeeMetadata meta = EmployeeMetadata.builder()
                .gender("Male")
                .employeeType("Full-time")
                .build();
        JSONB jsonb = mapper.toJsonb(meta);
        assertThat(jsonb).isNotNull();
        assertThat(jsonb.data()).contains("\"gender\"");
        assertThat(jsonb.data()).contains("\"employeeType\"");
    }

    // ── fromJsonb ──────────────────────────────────────────────────────────────

    @Test
    void fromJsonb_null_returnsNull() {
        assertThat(mapper.fromJsonb(null)).isNull();
    }

    @Test
    void fromJsonb_emptyObject_returnsEmptyMetadata() {
        JSONB empty = JSONB.valueOf("{}");
        EmployeeMetadata m = mapper.fromJsonb(empty);
        assertThat(m).isNotNull();
        assertThat(m.getGender()).isNull();
        assertThat(m.getEmployeeType()).isNull();
    }

    @Test
    void fromJsonb_unknownKeys_ignoredGracefully() {
        // Existing DB records may have keys not present in EmployeeMetadata
        JSONB legacy = JSONB.valueOf("{\"location\":\"Hyderabad\",\"experience\":\"1 year\"}");
        EmployeeMetadata m = mapper.fromJsonb(legacy);
        assertThat(m).isNotNull();  // @JsonIgnoreProperties(ignoreUnknown=true) applies
    }

    // ── round-trip ─────────────────────────────────────────────────────────────

    @Test
    void roundTrip_preservesAllScalarFields() {
        EmployeeMetadata original = EmployeeMetadata.builder()
                .gender("Female")
                .maritalStatus("Single")
                .nationality("Indian")
                .employeeType("Contract")
                .workLocation("Remote")
                .badgeId("EMP-001")
                .employmentStatus("Active")
                .shift("Morning")
                .weeklyHours(40)
                .leavePolicy("Standard")
                .govIdType("PAN")
                .govIdNumber("ABCDE1234F")
                .notes("Test note")
                .bgCheckConsent(true)
                .build();

        JSONB jsonb = mapper.toJsonb(original);
        EmployeeMetadata result = mapper.fromJsonb(jsonb);

        assertThat(result.getGender()).isEqualTo("Female");
        assertThat(result.getMaritalStatus()).isEqualTo("Single");
        assertThat(result.getNationality()).isEqualTo("Indian");
        assertThat(result.getEmployeeType()).isEqualTo("Contract");
        assertThat(result.getWorkLocation()).isEqualTo("Remote");
        assertThat(result.getBadgeId()).isEqualTo("EMP-001");
        assertThat(result.getEmploymentStatus()).isEqualTo("Active");
        assertThat(result.getShift()).isEqualTo("Morning");
        assertThat(result.getWeeklyHours()).isEqualTo(40);
        assertThat(result.getLeavePolicy()).isEqualTo("Standard");
        assertThat(result.getGovIdType()).isEqualTo("PAN");
        assertThat(result.getGovIdNumber()).isEqualTo("ABCDE1234F");
        assertThat(result.getNotes()).isEqualTo("Test note");
        assertThat(result.getBgCheckConsent()).isTrue();
    }

    @Test
    void roundTrip_localDate_serialisedAsIso8601() {
        EmployeeMetadata original = EmployeeMetadata.builder()
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .dateOfJoining(LocalDate.of(2024, 1, 10))
                .probationEndDate(LocalDate.of(2024, 7, 10))
                .build();

        JSONB jsonb = mapper.toJsonb(original);

        // Verify wire format is ISO-8601 string, not numeric array
        assertThat(jsonb.data()).contains("\"dateOfBirth\":\"1990-05-15\"");
        assertThat(jsonb.data()).contains("\"dateOfJoining\":\"2024-01-10\"");
        assertThat(jsonb.data()).contains("\"probationEndDate\":\"2024-07-10\"");

        // Verify deserialization back to LocalDate
        EmployeeMetadata result = mapper.fromJsonb(jsonb);
        assertThat(result.getDateOfBirth()).isEqualTo(LocalDate.of(1990, 5, 15));
        assertThat(result.getDateOfJoining()).isEqualTo(LocalDate.of(2024, 1, 10));
        assertThat(result.getProbationEndDate()).isEqualTo(LocalDate.of(2024, 7, 10));
    }

    @Test
    void roundTrip_salary_preservesPrecision() {
        EmployeeMetadata original = EmployeeMetadata.builder()
                .salary(new BigDecimal("75000.50"))
                .payFrequency("Monthly")
                .currency("INR")
                .build();

        EmployeeMetadata result = mapper.fromJsonb(mapper.toJsonb(original));
        assertThat(result.getSalary()).isEqualByComparingTo(new BigDecimal("75000.50"));
        assertThat(result.getPayFrequency()).isEqualTo("Monthly");
        assertThat(result.getCurrency()).isEqualTo("INR");
    }

    @Test
    void roundTrip_skillsList_preservesOrder() {
        EmployeeMetadata original = EmployeeMetadata.builder()
                .skills(List.of("Java", "React", "SQL"))
                .build();

        EmployeeMetadata result = mapper.fromJsonb(mapper.toJsonb(original));
        assertThat(result.getSkills()).containsExactly("Java", "React", "SQL");
    }

    @Test
    void roundTrip_benefitsList_preserved() {
        EmployeeMetadata original = EmployeeMetadata.builder()
                .benefits(List.of("Health Insurance", "Provident Fund"))
                .build();

        EmployeeMetadata result = mapper.fromJsonb(mapper.toJsonb(original));
        assertThat(result.getBenefits()).containsExactlyInAnyOrder("Health Insurance", "Provident Fund");
    }

    @Test
    void roundTrip_reportingManagerId_preserved() {
        UUID managerId = UUID.randomUUID();
        EmployeeMetadata original = EmployeeMetadata.builder()
                .reportingManagerId(managerId)
                .build();

        EmployeeMetadata result = mapper.fromJsonb(mapper.toJsonb(original));
        assertThat(result.getReportingManagerId()).isEqualTo(managerId);
    }

    // ── toResponse with metadata ───────────────────────────────────────────────

    @Test
    void toResponse_populatesMetadataFromJsonb() {
        EmployeeMetadata meta = EmployeeMetadata.builder()
                .gender("Female")
                .employeeType("Contract")
                .workLocation("Hybrid")
                .build();

        EmployeesRecord record = new EmployeesRecord();
        record.setId(UUID.randomUUID());
        record.setOrgId(UUID.randomUUID());
        record.setName("Test User");
        record.setEmail("test@example.com");
        record.setPhone("9876543210");
        record.setDepartment("IT");
        record.setDesignation("Developer");
        record.setIsActive(true);
        record.setMetadata(mapper.toJsonb(meta));

        EmployeeResponseDTO response = mapper.toResponse(record);

        assertThat(response.getMetadata()).isNotNull();
        assertThat(response.getMetadata().getGender()).isEqualTo("Female");
        assertThat(response.getMetadata().getEmployeeType()).isEqualTo("Contract");
        assertThat(response.getMetadata().getWorkLocation()).isEqualTo("Hybrid");
    }

    @Test
    void toResponse_nullMetadataColumn_yieldsNullMetadataField() {
        EmployeesRecord record = new EmployeesRecord();
        record.setId(UUID.randomUUID());
        record.setOrgId(UUID.randomUUID());
        record.setName("No Meta");
        record.setEmail("nometa@example.com");
        record.setIsActive(true);
        record.setMetadata(null);

        EmployeeResponseDTO response = mapper.toResponse(record);
        assertThat(response.getMetadata()).isNull();
    }
}
