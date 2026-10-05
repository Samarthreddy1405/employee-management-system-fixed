package com.example.employeemanagementsystem.repository;

import com.example.employee.jooq.generated.tables.records.EmployeesRecord;
import com.example.employeemanagementsystem.dto.request.GetAllEmployeesRequestDTO;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.SortField;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.example.employee.jooq.generated.tables.Employees.EMPLOYEES;
import static org.jooq.impl.DSL.noCondition;

@Repository
public class EmployeeRepository {

    private final DSLContext dsl;

    public EmployeeRepository(DSLContext context) {
        dsl = context;
    }

    public EmployeesRecord findById(UUID id) {
        return dsl.selectFrom(EMPLOYEES)
                .where(EMPLOYEES.ID.eq(id))
                .fetchOne();
    }

    public boolean existsByEmail(String email) {
        return dsl.fetchExists(
                dsl.selectFrom(EMPLOYEES).where(EMPLOYEES.EMAIL.eq(email))
        );
    }

    public boolean existsByEmailAndIdNot(String email, UUID id) {
        return dsl.fetchExists(
                dsl.selectFrom(EMPLOYEES)
                        .where(EMPLOYEES.EMAIL.eq(email).and(EMPLOYEES.ID.ne(id)))
        );
    }

    public boolean existsByPhone(String phone) {
        return dsl.fetchExists(
                dsl.selectFrom(EMPLOYEES).where(EMPLOYEES.PHONE.eq(phone))
        );
    }

    public boolean existsByPhoneAndIdNot(String phone, UUID id) {
        return dsl.fetchExists(
                dsl.selectFrom(EMPLOYEES)
                        .where(EMPLOYEES.PHONE.eq(phone).and(EMPLOYEES.ID.ne(id)))
        );
    }

    public List<EmployeesRecord> findByOrganizationId(UUID orgId) {
        return dsl.selectFrom(EMPLOYEES)
                .where(EMPLOYEES.ORG_ID.eq(orgId))
                .fetch();
    }

    public EmployeesRecord save(EmployeesRecord employee) {
        return dsl.insertInto(EMPLOYEES)
                .set(employee)
                .returning()
                .fetchOne();
    }

    public EmployeesRecord update(UUID id, EmployeesRecord employee) {
        return dsl.update(EMPLOYEES)
                .set(EMPLOYEES.NAME,           employee.getName())
                .set(EMPLOYEES.EMAIL,          employee.getEmail())
                .set(EMPLOYEES.PHONE,          employee.getPhone())
                .set(EMPLOYEES.DEPARTMENT_ID,  employee.getDepartmentId())
                .set(EMPLOYEES.DESIGNATION_ID, employee.getDesignationId())
                .set(EMPLOYEES.METADATA,       employee.getMetadata())
                .set(EMPLOYEES.UPDATED_ON,     employee.getUpdatedOn())
                .where(EMPLOYEES.ID.eq(id))
                .returning()
                .fetchOne();
    }

    public void updateStatus(UUID id, boolean isActive) {
        dsl.update(EMPLOYEES)
                .set(EMPLOYEES.IS_ACTIVE,  isActive)
                .set(EMPLOYEES.UPDATED_ON, LocalDateTime.now())
                .where(EMPLOYEES.ID.eq(id))
                .execute();
    }

    public List<EmployeesRecord> findAllWithFilters(GetAllEmployeesRequestDTO request) {
        Condition condition = buildFilterCondition(request);
        SortField<?> sort   = buildSortField(request);
        int offset = (request.getPage() - 1) * request.getPageSize();

        return dsl.select(
                        EMPLOYEES.ID,
                        EMPLOYEES.ORG_ID,
                        EMPLOYEES.IS_ACTIVE
                )
                .from(EMPLOYEES)
                .where(condition)
                .orderBy(sort)
                .limit(request.getPageSize())
                .offset(offset)
                .fetchInto(EmployeesRecord.class);
    }

    public long countWithFilters(GetAllEmployeesRequestDTO request) {
        return dsl.selectCount()
                .from(EMPLOYEES)
                .where(buildFilterCondition(request))
                .fetchOne(0, Long.class);
    }

    private Condition buildFilterCondition(GetAllEmployeesRequestDTO request) {
        Condition condition = noCondition();

        if (request.getOrgId() != null) {
            condition = condition.and(EMPLOYEES.ORG_ID.eq(request.getOrgId()));
        }
        if (request.getNameFilter() != null && !request.getNameFilter().isBlank()) {
            condition = condition.and(EMPLOYEES.NAME.containsIgnoreCase(request.getNameFilter()));
        }
        // Filter by department UUID instead of old text column
        if (request.getDepartmentId() != null) {
            condition = condition.and(EMPLOYEES.DEPARTMENT_ID.eq(request.getDepartmentId()));
        }
        if (request.getIsActive() != null) {
            condition = condition.and(EMPLOYEES.IS_ACTIVE.eq(request.getIsActive()));
        }
        return condition;
    }

    private SortField<?> buildSortField(GetAllEmployeesRequestDTO request) {
        if (request.isSortByNameAsc())         return EMPLOYEES.NAME.asc();
        if (request.isSortByNameDesc())        return EMPLOYEES.NAME.desc();
        if (request.isSortByCreatedDateDesc()) return EMPLOYEES.CREATED_ON.desc();
        return EMPLOYEES.CREATED_ON.desc();
    }
}
