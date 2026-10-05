package com.example.employeemanagementsystem.dto.metadata;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmployeeMetadata {

    // ── Personal ─────────────────────────────────────────────────────────────
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    private String gender;           // Male|Female|Non-binary|Prefer not to say
    private String maritalStatus;    // Single|Married|Divorced|Widowed
    private String nationality;
    private String profilePhotoUrl;

    // ── Contact ───────────────────────────────────────────────────────────────
    private String alternatePhone;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String country;
    private String postalCode;

    // ── Organizational ────────────────────────────────────────────────────────
    private String employeeType;     // Full-time|Part-time|Contract|Intern
    private String workLocation;     // On-site|Remote|Hybrid
    private UUID   reportingManagerId;

    @Size(max = 15, message = "Skills list must not exceed 15 entries")
    private List<String> skills;

    // ── Employment ────────────────────────────────────────────────────────────
    private String badgeId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfJoining;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate probationEndDate;

    private String  employmentStatus;  // Active|Inactive|On Leave|Probation
    private String  shift;             // Morning|Afternoon|Night|Flexible
    private Integer weeklyHours;

    // ── Compensation (Phase 2 will add access control) ────────────────────────
    private BigDecimal salary;
    private String     payFrequency;   // Monthly|Bi-weekly|Weekly|Annual
    private String     currency;       // INR|USD|EUR|GBP|AED|SGD|AUD|CAD

    // ── Benefits ─────────────────────────────────────────────────────────────
    private List<String> benefits;
    private String       leavePolicy;  // Standard|Senior|Executive|Contractual

    // ── Emergency contacts (max 2) ────────────────────────────────────────────
    @Valid
    @Size(max = 2, message = "At most 2 emergency contacts are allowed")
    private List<EmergencyContact> emergencyContacts;

    // ── Documents & Notes ─────────────────────────────────────────────────────
    private String       govIdType;
    private String       govIdNumber;
    private String       resumeUrl;
    private List<String> additionalDocs;
    private Boolean      bgCheckConsent;

    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;
}
