package com.example.employeemanagementsystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Slim projection returned by POST /employees/all.
 * Contains only id, orgId, and isActive — no PII or detail fields.
 * Full employee details are served by GET /employees/{id}.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeListItemDTO {

    private UUID    id;
    private UUID    orgId;
    private Boolean isActive;
}
