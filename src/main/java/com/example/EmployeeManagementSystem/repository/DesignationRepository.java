package com.example.employeemanagementsystem.repository;

import com.example.employee.jooq.generated.tables.records.DesignationsRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.example.employee.jooq.generated.tables.Designations.DESIGNATIONS;

@Repository
public class DesignationRepository {

    private final DSLContext dsl;

    public DesignationRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public List<DesignationsRecord> findAll() {
        return dsl.selectFrom(DESIGNATIONS)
                .orderBy(DESIGNATIONS.NAME.asc())
                .fetch();
    }

    public List<DesignationsRecord> findByDepartmentId(UUID departmentId) {
        return dsl.selectFrom(DESIGNATIONS)
                .where(DESIGNATIONS.DEPARTMENT_ID.eq(departmentId))
                .orderBy(DESIGNATIONS.NAME.asc())
                .fetch();
    }

    public DesignationsRecord findById(UUID id) {
        return dsl.selectFrom(DESIGNATIONS)
                .where(DESIGNATIONS.ID.eq(id))
                .fetchOne();
    }

    public boolean existsByNameAndDepartmentId(String name, UUID departmentId) {
        return dsl.fetchExists(
                dsl.selectFrom(DESIGNATIONS)
                        .where(DESIGNATIONS.NAME.equalIgnoreCase(name))
                        .and(DESIGNATIONS.DEPARTMENT_ID.eq(departmentId))
        );
    }

    public DesignationsRecord save(String name, UUID departmentId) {
        return dsl.insertInto(DESIGNATIONS)
                .set(DESIGNATIONS.ID, UUID.randomUUID())
                .set(DESIGNATIONS.NAME, name.trim())
                .set(DESIGNATIONS.DEPARTMENT_ID, departmentId)
                .set(DESIGNATIONS.CREATED_ON, LocalDateTime.now())
                .set(DESIGNATIONS.UPDATED_ON, LocalDateTime.now())
                .returning()
                .fetchOne();
    }
}
