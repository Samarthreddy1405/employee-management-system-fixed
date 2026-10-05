package com.example.employeemanagementsystem.controller;

import com.example.employeemanagementsystem.dto.request.CreateDesignationRequestDTO;
import com.example.employeemanagementsystem.dto.response.DesignationResponseDTO;
import com.example.employeemanagementsystem.service.DesignationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/designations")
@Tag(name = "Designation Management")
@RequiredArgsConstructor
public class DesignationController {

    private final DesignationService designationService;

    @GetMapping
    @Operation(summary = "Get all designations, optionally filtered by departmentId")
    @ApiResponse(responseCode = "200", description = "List of designations")
    public ResponseEntity<List<DesignationResponseDTO>> getAllDesignations(
            @RequestParam(required = false) UUID departmentId) {
        return ResponseEntity.ok(designationService.getAllDesignations(departmentId));
    }

    @PostMapping
    @Operation(summary = "Create a designation under a department")
    @ApiResponse(responseCode = "201", description = "Designation created")
    @ApiResponse(responseCode = "400", description = "Invalid payload or duplicate")
    @ApiResponse(responseCode = "404", description = "Department not found")
    public ResponseEntity<DesignationResponseDTO> createDesignation(
            @Valid @RequestBody CreateDesignationRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(designationService.createDesignation(request));
    }
}
