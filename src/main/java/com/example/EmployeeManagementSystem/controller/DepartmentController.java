package com.example.employeemanagementsystem.controller;

import com.example.employeemanagementsystem.dto.request.CreateDepartmentRequestDTO;
import com.example.employeemanagementsystem.dto.response.DepartmentResponseDTO;
import com.example.employeemanagementsystem.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/departments")
@Tag(name = "Department Management")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping
    @Operation(summary = "Get all departments")
    @ApiResponse(responseCode = "200", description = "List of departments")
    public ResponseEntity<List<DepartmentResponseDTO>> getAllDepartments() {
        return ResponseEntity.ok(departmentService.getAllDepartments());
    }

    @PostMapping
    @Operation(summary = "Create a department")
    @ApiResponse(responseCode = "201", description = "Department created")
    @ApiResponse(responseCode = "400", description = "Invalid payload or duplicate name")
    public ResponseEntity<DepartmentResponseDTO> createDepartment(
            @Valid @RequestBody CreateDepartmentRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(departmentService.createDepartment(request));
    }
}
