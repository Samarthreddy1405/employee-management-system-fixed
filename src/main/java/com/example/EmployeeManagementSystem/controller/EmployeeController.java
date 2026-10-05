package com.example.employeemanagementsystem; 
 
import com.example.employeemanagementsystem.dto.request.*; 
import com.example.employeemanagementsystem.dto.response.*; 
import com.example.employeemanagementsystem.service.EmployeeService; 
 
import io.swagger.v3.oas.annotations.Operation; 
import io.swagger.v3.oas.annotations.responses.ApiResponse; 
import io.swagger.v3.oas.annotations.responses.ApiResponses; 
import io.swagger.v3.oas.annotations.tags.Tag; 
 
import jakarta.validation.Valid; 
 
import org.springframework.http.HttpStatus; 
import org.springframework.http.ResponseEntity; 
import org.springframework.web.bind.annotation.*; 
 
import java.util.List; 
import java.util.UUID; 
 
@RestController 
@RequestMapping("/employees") 
@Tag(name = "Employee Management") 
public class EmployeeController { 
 
    private final EmployeeService employeeService; 
 
    public EmployeeController(EmployeeService service) { 
        employeeService = service; 
    } 
 
    @GetMapping("/{id}") 
    @Operation(summary = "Get employee by ID") 
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "Employee found"), 
            @ApiResponse(responseCode = "404", description = "Employee not found") 
    }) 
    public ResponseEntity<EmployeeResponseDTO> getEmployee( 
            @PathVariable UUID id) { 
        return ResponseEntity.ok(employeeService.getEmployee(id)); 
    } 
 
    @GetMapping("/organization/{orgId}") 
    @Operation(summary = "Get employees by organization") 
    public ResponseEntity<List<EmployeeResponseDTO>> getEmployeesByOrganization( 
            @PathVariable UUID orgId) { 
        return ResponseEntity.ok( 
                employeeService.getEmployeesByOrganization(orgId) 
        ); 
    } 
 
    @PostMapping 
    @Operation(summary = "Create an employee") 
    @ApiResponses({ 
            @ApiResponse(responseCode = "201", description = "Employee created"), 
            @ApiResponse(responseCode = "400", description = "Invalid payload") 
    }) 
    public ResponseEntity<EmployeeResponseDTO> createEmployee( 
            @Valid @RequestBody CreateEmployeeRequestDTO request) { 
        return ResponseEntity.status(HttpStatus.CREATED) 
                .body(employeeService.createEmployee(request)); 
    } 
 
    @PutMapping("/{id}") 
    @Operation(summary = "Update an employee") 
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "Employee updated"), 
            @ApiResponse(responseCode = "400", description = "Invalid payload"), 
            @ApiResponse(responseCode = "404", description = "Employee not found") 
    }) 
    public ResponseEntity<EmployeeResponseDTO> updateEmployee( 
            @PathVariable UUID id, 
            @Valid @RequestBody UpdateEmployeeRequestDTO request) { 
        return ResponseEntity.ok( 
                employeeService.updateEmployee(id, request) 
        ); 
    } 
 
    @PatchMapping("/{id}/deactivate") 
    @Operation(summary = "Deactivate an employee") 
    @ApiResponse(responseCode = "404", description = "Employee not found") 
    public ResponseEntity<ActivateDeactivateEmployeeResponseDTO> deactivate( 
            @PathVariable UUID id) { 
        return ResponseEntity.ok( 
                employeeService.deactivateEmployee(id) 
        ); 
    } 
 
    @PatchMapping("/{id}/activate") 
    @Operation(summary = "Activate an employee") 
    @ApiResponse(responseCode = "404", description = "Employee not found") 
    public ResponseEntity<ActivateDeactivateEmployeeResponseDTO> activate( 
            @PathVariable UUID id) { 
        return ResponseEntity.ok( 
                employeeService.activateEmployee(id) 
        ); 
    } 
 
    @PostMapping("/all") 
    @Operation(summary = "Get all employees") 
    @ApiResponse(responseCode = "400", description = "Invalid request") 
    public ResponseEntity<GetAllEmployeesResponseDTO> getAllEmployees( 
            @Valid @RequestBody GetAllEmployeesRequestDTO request) { 
        return ResponseEntity.ok( 
                employeeService.getAllEmployees(request) 
        ); 
    } 
}