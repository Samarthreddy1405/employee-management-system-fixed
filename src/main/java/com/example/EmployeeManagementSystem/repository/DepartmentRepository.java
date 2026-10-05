package com.example.employeemanagementsystem.repository;

import com.example.employee.jooq.generated.tables.records.DepartmentsRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.example.employee.jooq.generated.tables.Departments.DEPARTMENTS;

@Repository
public class DepartmentRepository {

    private final DSLContext dsl;

    public DepartmentRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public List<DepartmentsRecord> findAll() {
        return dsl.selectFrom(DEPARTMENTS)
                .orderBy(DEPARTMENTS.NAME.asc())
                .fetch();
    }

    public DepartmentsRecord findById(UUID id) {
        return dsl.selectFrom(DEPARTMENTS)
                .where(DEPARTMENTS.ID.eq(id))
                .fetchOne();
    }

    public boolean existsByName(String name) {
        return dsl.fetchExists(
                dsl.selectFrom(DEPARTMENTS)
                        .where(DEPARTMENTS.NAME.equalIgnoreCase(name))
        );
    }

    public DepartmentsRecord save(String name) {
        return dsl.insertInto(DEPARTMENTS)
                .set(DEPARTMENTS.ID, UUID.randomUUID())
                .set(DEPARTMENTS.NAME, name.trim())
                .set(DEPARTMENTS.CREATED_ON, LocalDateTime.now())
                .set(DEPARTMENTS.UPDATED_ON, LocalDateTime.now())
                .returning()
                .fetchOne();
    }
}
