package com.example.employeemanagementsystem.dto.metadata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyContact {

    private String name;
    private String relationship;  // Spouse|Parent|Sibling|Child|Friend|Other
    private String phone;
}
