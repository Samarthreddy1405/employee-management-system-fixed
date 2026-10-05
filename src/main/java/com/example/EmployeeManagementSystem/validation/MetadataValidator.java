package com.example.employeemanagementsystem.validation;

import com.example.employeemanagementsystem.dto.metadata.EmergencyContact;
import com.example.employeemanagementsystem.dto.metadata.EmployeeMetadata;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

public class MetadataValidator
        implements ConstraintValidator<ValidEmployeeMetadata, EmployeeMetadata> {

    @Override
    public boolean isValid(EmployeeMetadata m, ConstraintValidatorContext ctx) {
        if (m == null) return true;  // null metadata is permitted; DTOs decide if required

        ctx.disableDefaultConstraintViolation();
        boolean valid = true;

        // ── Date of Birth ──────────────────────────────────────────────────────
        if (m.getDateOfBirth() != null) {
            LocalDate today = LocalDate.now();
            if (!m.getDateOfBirth().isBefore(today)) {
                addViolation(ctx, "metadata.dateOfBirth", "Date of birth must be in the past");
                valid = false;
            } else {
                int age = Period.between(m.getDateOfBirth(), today).getYears();
                if (age < 18) {
                    addViolation(ctx, "metadata.dateOfBirth",
                            "Employee must be at least 18 years old");
                    valid = false;
                } else if (age > 65) {
                    addViolation(ctx, "metadata.dateOfBirth",
                            "Employee must be 65 years old or younger");
                    valid = false;
                }
            }
        }

        // ── Date of Joining ────────────────────────────────────────────────────
        if (m.getDateOfJoining() != null) {
            LocalDate today = LocalDate.now();
            if (m.getDateOfJoining().isAfter(today.plusYears(1))) {
                addViolation(ctx, "metadata.dateOfJoining",
                        "Date of joining cannot be more than 1 year in the future");
                valid = false;
            }
            if (m.getDateOfBirth() != null
                    && !m.getDateOfJoining().isAfter(m.getDateOfBirth())) {
                addViolation(ctx, "metadata.dateOfJoining",
                        "Date of joining must be after date of birth");
                valid = false;
            }
        }

        // ── Probation End Date ─────────────────────────────────────────────────
        if (m.getProbationEndDate() != null) {
            boolean permanentType = "Full-time".equals(m.getEmployeeType())
                    || "Part-time".equals(m.getEmployeeType());
            if (!permanentType) {
                addViolation(ctx, "metadata.probationEndDate",
                        "Probation end date is only applicable for Full-time or Part-time employees");
                valid = false;
            }
            if (m.getDateOfJoining() != null
                    && !m.getProbationEndDate().isAfter(m.getDateOfJoining())) {
                addViolation(ctx, "metadata.probationEndDate",
                        "Probation end date must be after date of joining");
                valid = false;
            }
        }

        // ── Salary requires Pay Frequency and Currency ─────────────────────────
        if (m.getSalary() != null && m.getSalary().signum() > 0) {
            if (m.getPayFrequency() == null || m.getPayFrequency().isBlank()) {
                addViolation(ctx, "metadata.payFrequency",
                        "Pay frequency is required when salary is provided");
                valid = false;
            }
            if (m.getCurrency() == null || m.getCurrency().isBlank()) {
                addViolation(ctx, "metadata.currency",
                        "Currency is required when salary is provided");
                valid = false;
            }
        }

        // ── Government ID: number required when type is set ────────────────────
        if (m.getGovIdType() != null && !m.getGovIdType().isBlank()) {
            if (m.getGovIdNumber() == null || m.getGovIdNumber().isBlank()) {
                addViolation(ctx, "metadata.govIdNumber",
                        "Government ID number is required when ID type is selected");
                valid = false;
            }
        }

        // ── Emergency contact row completeness ─────────────────────────────────
        if (m.getEmergencyContacts() != null) {
            List<EmergencyContact> contacts = m.getEmergencyContacts();
            for (int i = 0; i < contacts.size(); i++) {
                EmergencyContact ec = contacts.get(i);
                if (ec == null) continue;
                boolean hasName = ec.getName() != null && !ec.getName().isBlank();
                if (hasName) {
                    if (ec.getRelationship() == null || ec.getRelationship().isBlank()) {
                        addViolation(ctx,
                                "metadata.emergencyContacts[" + i + "].relationship",
                                "Relationship is required when contact name is provided");
                        valid = false;
                    }
                    if (ec.getPhone() == null || ec.getPhone().isBlank()) {
                        addViolation(ctx,
                                "metadata.emergencyContacts[" + i + "].phone",
                                "Phone is required when contact name is provided");
                        valid = false;
                    }
                }
            }
        }

        // ── Weekly hours range ─────────────────────────────────────────────────
        if (m.getWeeklyHours() != null
                && (m.getWeeklyHours() < 1 || m.getWeeklyHours() > 60)) {
            addViolation(ctx, "metadata.weeklyHours",
                    "Weekly hours must be between 1 and 60");
            valid = false;
        }

        // ── Skills: each tag max 50 chars ──────────────────────────────────────
        if (m.getSkills() != null) {
            for (int i = 0; i < m.getSkills().size(); i++) {
                String skill = m.getSkills().get(i);
                if (skill != null && skill.length() > 50) {
                    addViolation(ctx,
                            "metadata.skills[" + i + "]",
                            "Each skill tag must not exceed 50 characters");
                    valid = false;
                }
            }
        }

        return valid;
    }

    private void addViolation(ConstraintValidatorContext ctx,
                               String field, String message) {
        ctx.buildConstraintViolationWithTemplate(message)
           .addPropertyNode(field)
           .addConstraintViolation();
    }
}
