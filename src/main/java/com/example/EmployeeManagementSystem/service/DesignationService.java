package com.example.employeemanagementsystem.service;

import com.example.employeemanagementsystem.dto.request.CreateDesignationRequestDTO;
import com.example.employeemanagementsystem.dto.response.DesignationResponseDTO;

import java.util.List;
import java.util.UUID;

public interface DesignationService {
    List<DesignationResponseDTO> getAllDesignations(UUID departmentId);
    DesignationResponseDTO createDesignation(CreateDesignationRequestDTO request);
}
