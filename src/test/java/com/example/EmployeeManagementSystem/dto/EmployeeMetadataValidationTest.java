package com.example.employeemanagementsystem.dto;

import com.example.employeemanagementsystem.dto.metadata.EmergencyContact;
import com.example.employeemanagementsystem.dto.metadata.EmployeeMetadata;
import com.example.employeemanagementsystem.validation.ValidEmployeeMetadata;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class EmployeeMetadataValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setup() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    // Wrap metadata so @ValidEmployeeMetadata fires via validator.validate()
    static class Wrapper {
        @ValidEmployeeMetadata
        final EmployeeMetadata metadata;
        Wrapper(EmployeeMetadata m) { this.metadata = m; }
    }

    private Set<String> violatedFields(EmployeeMetadata m) {
        return validator.validate(new Wrapper(m))
                .stream()
                .map(v -> v.getPropertyPath().toString())
                .collect(Collectors.toSet());
    }

    private boolean hasNoViolations(EmployeeMetadata m) {
        return validator.validate(new Wrapper(m)).isEmpty();
    }

    // ── null / valid ───────────────────────────────────────────────────────────

    @Test
    void nullMetadata_passes() {
        assertThat(validator.validate(new Wrapper(null))).isEmpty();
    }

    @Test
    void fullyValidMetadata_noViolations() {
        EmployeeMetadata m = EmployeeMetadata.builder()
                .dateOfBirth(LocalDate.now().minusYears(30))
                .dateOfJoining(LocalDate.now().minusMonths(6))
                .employeeType("Full-time")
                .probationEndDate(LocalDate.now().plusMonths(3))
                .weeklyHours(40)
                .salary(new BigDecimal("60000"))
                .payFrequency("Monthly")
                .currency("INR")
                .govIdType("PAN")
                .govIdNumber("ABCDE1234F")
                .skills(List.of("Java", "React"))
                .notes("Good candidate")
                .build();
        assertThat(hasNoViolations(m)).isTrue();
    }

    // ── Date of Birth ──────────────────────────────────────────────────────────

    @Test
    void dobInFuture_fails() {
        EmployeeMetadata m = EmployeeMetadata.builder()
                .dateOfBirth(LocalDate.now().plusDays(1))
                .build();
        assertThat(violatedFields(m)).contains("metadata.dateOfBirth");
    }

    @Test
    void dobAgeLessThan18_fails() {
        EmployeeMetadata m = EmployeeMetadata.builder()
                .dateOfBirth(LocalDate.now().minusYears(17))
                .build();
        assertThat(violatedFields(m)).contains("metadata.dateOfBirth");
    }

    @Test
    void dobAgeOver65_fails() {
        EmployeeMetadata m = EmployeeMetadata.builder()
                .dateOfBirth(LocalDate.now().minusYears(66))
                .build();
        assertThat(violatedFields(m)).contains("metadata.dateOfBirth");
    }

    @Test
    void dobExactly18_passes() {
        EmployeeMetadata m = EmployeeMetadata.builder()
                .dateOfBirth(LocalDate.now().minusYears(18))
                .build();
        assertThat(violatedFields(m)).doesNotContain("metadata.dateOfBirth");
    }

    // ── Date of Joining ────────────────────────────────────────────────────────

    @Test
    void dojMoreThan1YearInFuture_fails() {
        EmployeeMetadata m = EmployeeMetadata.builder()
                .dateOfJoining(LocalDate.now().plusYears(2))
                .build();
        assertThat(violatedFields(m)).contains("metadata.dateOfJoining");
    }

    @Test
    void dojBeforeDob_fails() {
        EmployeeMetadata m = EmployeeMetadata.builder()
                .dateOfBirth(LocalDate.now().minusYears(30))
                .dateOfJoining(LocalDate.now().minusYears(35))
                .build();
        assertThat(violatedFields(m)).contains("metadata.dateOfJoining");
    }

    @Test
    void dojWithin1Year_passes() {
        EmployeeMetadata m = EmployeeMetadata.builder()
                .dateOfJoining(LocalDate.now().plusMonths(6))
                .build();
        assertThat(violatedFields(m)).doesNotContain("metadata.dateOfJoining");
    }

    // ── Probation End Date ─────────────────────────────────────────────────────

    @Test
    void probationEndBeforeDoj_fails() {
        EmployeeMetadata m = EmployeeMetadata.builder()
                .employeeType("Full-time")
                .dateOfJoining(LocalDate.now().minusMonths(3))
                .probationEndDate(LocalDate.now().minusMonths(6))
                .build();
        assertThat(violatedFields(m)).contains("metadata.probationEndDate");
    }

    @Test
    void probationEndForContractType_fails() {
        EmployeeMetadata m = EmployeeMetadata.builder()
                .employeeType("Contract")
                .dateOfJoining(LocalDate.now().minusMonths(1))
                .probationEndDate(LocalDate.now().plusMonths(3))
                .build();
        assertThat(violatedFields(m)).contains("metadata.probationEndDate");
    }

    @Test
    void probationEndForInternType_fails() {
        EmployeeMetadata m = EmployeeMetadata.builder()
                .employeeType("Intern")
                .dateOfJoining(LocalDate.now().minusMonths(1))
                .probationEndDate(LocalDate.now().plusMonths(3))
                .build();
        assertThat(violatedFields(m)).contains("metadata.probationEndDate");
    }

    @Test
    void probationEndForFullTimeAfterDoj_passes() {
        EmployeeMetadata m = EmployeeMetadata.builder()
                .employeeType("Full-time")
                .dateOfJoining(LocalDate.now().minusMonths(3))
                .probationEndDate(LocalDate.now().plusMonths(3))
                .build();
        assertThat(violatedFields(m)).doesNotContain("metadata.probationEndDate");
    }

    // ── Salary / compensation ──────────────────────────────────────────────────

    @Test
    void salaryWithoutPayFrequency_fails() {
        EmployeeMetadata m = EmployeeMetadata.builder()
                .salary(new BigDecimal("50000"))
                .currency("INR")
                .build();
        assertThat(violatedFields(m)).contains("metadata.payFrequency");
    }

    @Test
    void salaryWithoutCurrency_fails() {
        EmployeeMetadata m = EmployeeMetadata.builder()
                .salary(new BigDecimal("50000"))
                .payFrequency("Monthly")
                .build();
        assertThat(violatedFields(m)).contains("metadata.currency");
    }

    @Test
    void salaryWithBothFrequencyAndCurrency_passes() {
        EmployeeMetadata m = EmployeeMetadata.builder()
                .salary(new BigDecimal("50000"))
                .payFrequency("Monthly")
                .currency("INR")
                .build();
        assertThat(violatedFields(m))
                .doesNotContain("metadata.payFrequency", "metadata.currency");
    }

    @Test
    void noSalary_noFrequencyOrCurrencyRequired() {
        EmployeeMetadata m = EmployeeMetadata.builder().build();
        assertThat(hasNoViolations(m)).isTrue();
    }

    // ── Government ID ──────────────────────────────────────────────────────────

    @Test
    void govIdTypeWithoutNumber_fails() {
        EmployeeMetadata m = EmployeeMetadata.builder()
                .govIdType("PAN")
                .build();
        assertThat(violatedFields(m)).contains("metadata.govIdNumber");
    }

    @Test
    void govIdTypeWithNumber_passes() {
        EmployeeMetadata m = EmployeeMetadata.builder()
                .govIdType("PAN")
                .govIdNumber("ABCDE1234F")
                .build();
        assertThat(violatedFields(m)).doesNotContain("metadata.govIdNumber");
    }

    @Test
    void noGovIdType_noNumberRequired() {
        EmployeeMetadata m = EmployeeMetadata.builder().build();
        assertThat(hasNoViolations(m)).isTrue();
    }

    // ── Emergency contacts ─────────────────────────────────────────────────────

    @Test
    void emergencyContactNameWithoutPhone_fails() {
        EmergencyContact ec = EmergencyContact.builder()
                .name("Jane Doe")
                .relationship("Spouse")
                .build();
        EmployeeMetadata m = EmployeeMetadata.builder()
                .emergencyContacts(List.of(ec))
                .build();
        assertThat(violatedFields(m))
                .anyMatch(f -> f.contains("emergencyContacts") && f.contains("phone"));
    }

    @Test
    void emergencyContactNameWithoutRelationship_fails() {
        EmergencyContact ec = EmergencyContact.builder()
                .name("Jane Doe")
                .phone("+91 9876543210")
                .build();
        EmployeeMetadata m = EmployeeMetadata.builder()
                .emergencyContacts(List.of(ec))
                .build();
        assertThat(violatedFields(m))
                .anyMatch(f -> f.contains("emergencyContacts") && f.contains("relationship"));
    }

    @Test
    void emergencyContactComplete_passes() {
        EmergencyContact ec = EmergencyContact.builder()
                .name("Jane Doe")
                .relationship("Spouse")
                .phone("+91 9876543210")
                .build();
        EmployeeMetadata m = EmployeeMetadata.builder()
                .emergencyContacts(List.of(ec))
                .build();
        assertThat(hasNoViolations(m)).isTrue();
    }

    @Test
    void emergencyContactEmptyRow_passes() {
        // A row with no name set should not trigger cross-field errors
        EmergencyContact ec = EmergencyContact.builder().build();
        EmployeeMetadata m = EmployeeMetadata.builder()
                .emergencyContacts(List.of(ec))
                .build();
        assertThat(hasNoViolations(m)).isTrue();
    }

    // ── Weekly hours ───────────────────────────────────────────────────────────

    @Test
    void weeklyHoursZero_fails() {
        EmployeeMetadata m = EmployeeMetadata.builder().weeklyHours(0).build();
        assertThat(violatedFields(m)).contains("metadata.weeklyHours");
    }

    @Test
    void weeklyHoursOver60_fails() {
        EmployeeMetadata m = EmployeeMetadata.builder().weeklyHours(61).build();
        assertThat(violatedFields(m)).contains("metadata.weeklyHours");
    }

    @Test
    void weeklyHours40_passes() {
        EmployeeMetadata m = EmployeeMetadata.builder().weeklyHours(40).build();
        assertThat(violatedFields(m)).doesNotContain("metadata.weeklyHours");
    }

    // ── Skills ─────────────────────────────────────────────────────────────────

    @Test
    void skillsOver15_fails() {
        List<String> tooMany = List.of(
                "Java", "Python", "JS", "TS", "React", "Angular", "Vue",
                "Spring", "Node", "SQL", "AWS", "Azure", "GCP", "Docker", "K8s", "Extra");
        EmployeeMetadata m = EmployeeMetadata.builder().skills(tooMany).build();
        assertThat(violatedFields(m)).anyMatch(f -> f.contains("skills"));
    }

    @Test
    void skillTagOver50Chars_fails() {
        EmployeeMetadata m = EmployeeMetadata.builder()
                .skills(List.of("A".repeat(51)))
                .build();
        assertThat(violatedFields(m)).anyMatch(f -> f.contains("skills"));
    }

    @Test
    void skillsExactly15_passes() {
        List<String> exactly15 = List.of(
                "Java", "Python", "JS", "TS", "React", "Angular", "Vue",
                "Spring", "Node", "SQL", "AWS", "Azure", "GCP", "Docker", "K8s");
        EmployeeMetadata m = EmployeeMetadata.builder().skills(exactly15).build();
        assertThat(violatedFields(m)).doesNotContain("metadata.skills");
    }
}
