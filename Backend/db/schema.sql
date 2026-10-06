--
-- PostgreSQL database dump
-- Database: employee_management_system_new
-- Dumped by: Export Script
--

-- Departments table
CREATE TABLE departments (
    id          uuid            NOT NULL DEFAULT gen_random_uuid(),
    name        character varying(100)  NOT NULL,
    created_on  timestamp(6) without time zone NOT NULL DEFAULT now(),
    updated_on  timestamp(6) without time zone NOT NULL DEFAULT now(),
    CONSTRAINT departments_pkey PRIMARY KEY (id),
    CONSTRAINT uk_departments_name UNIQUE (name)
);

CREATE INDEX idx_departments_name ON departments USING btree (name);


-- Designations table
CREATE TABLE designations (
    id              uuid            NOT NULL DEFAULT gen_random_uuid(),
    name            character varying(100)  NOT NULL,
    department_id   uuid            NOT NULL,
    created_on      timestamp(6) without time zone NOT NULL DEFAULT now(),
    updated_on      timestamp(6) without time zone NOT NULL DEFAULT now(),
    CONSTRAINT designations_pkey PRIMARY KEY (id),
    CONSTRAINT designations_dept_name_uq UNIQUE (department_id, name),
    CONSTRAINT fk_designations_department FOREIGN KEY (department_id)
        REFERENCES departments(id)
);

CREATE INDEX idx_designations_department_id ON designations USING btree (department_id);


-- Employees table
CREATE TABLE employees (
    id                uuid            NOT NULL,
    org_id            uuid            NOT NULL,
    name              character varying(150)  NOT NULL,
    email             character varying(255)  NOT NULL,
    phone             character varying(20),
    department_id     uuid,
    designation_id    uuid,
    metadata          jsonb,
    created_on        timestamp(6) without time zone NOT NULL,
    updated_on        timestamp(6) without time zone NOT NULL,
    is_active         boolean         NOT NULL DEFAULT true,
    created_by        uuid,
    updated_by        uuid,
    CONSTRAINT employees_pkey PRIMARY KEY (id),
    CONSTRAINT uk_employees_email UNIQUE (email),
    CONSTRAINT uk_employees_phone UNIQUE (phone),
    CONSTRAINT fk_employees_department FOREIGN KEY (department_id)
        REFERENCES departments(id),
    CONSTRAINT fk_employees_designation FOREIGN KEY (designation_id)
        REFERENCES designations(id)
);

CREATE INDEX idx_employees_org_id ON employees USING btree (org_id);
CREATE INDEX idx_employees_department_id ON employees USING btree (department_id);
CREATE INDEX idx_employees_designation_id ON employees USING btree (designation_id);
CREATE INDEX idx_employees_metadata_gin ON employees USING gin (metadata jsonb_path_ops);
