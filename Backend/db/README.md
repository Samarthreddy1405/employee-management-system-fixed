# Database Design

This folder contains the database schema, ER diagram, seed data, and migration scripts for the Employee Management System.

## Database Schema

The application uses PostgreSQL with three main tables:

### Tables

#### 1. **departments**
- Primary identifier: `id` (UUID)
- Fields: `name` (VARCHAR, unique)
- Purpose: Stores organizational departments (IT, Engineering, HR, Finance, etc.)

#### 2. **designations**
- Primary identifier: `id` (UUID)
- Fields: `name` (VARCHAR), `department_id` (UUID, foreign key)
- Purpose: Stores job titles/roles within departments
- Relationship: Each designation belongs to one department

#### 3. **employees**
- Primary identifier: `id` (UUID)
- Core fields: `org_id` (UUID), `name` (VARCHAR), `email` (VARCHAR, unique), `phone` (VARCHAR), `is_active` (BOOLEAN)
- Foreign keys: 
  - `department_id` (UUID) → references `departments(id)`
  - `designation_id` (UUID) → references `designations(id)`
- Additional: `metadata` (JSONB) for flexible employee attributes
- Purpose: Stores employee records with department and designation assignments

### Relationships

- **One-to-Many: Department → Designations**
  - One department can have many designations
  - Foreign key: `designations.department_id` → `departments.id`

- **One-to-Many: Department → Employees**
  - One department can have many employees
  - Foreign key: `employees.department_id` → `departments.id`

- **One-to-Many: Designation → Employees**
  - One designation can be held by many employees
  - Foreign key: `employees.designation_id` → `designations.id`

All foreign key columns (`department_id`, `designation_id` in employees table) are UUIDs and include CASCADE constraints for referential integrity.

## Files in this folder

### Core Schema Files

- **`ER-diagram.png`** - Entity-Relationship diagram showing tables and relationships
- **`schema.sql`** - Complete PostgreSQL schema with table definitions, indexes, and constraints

### Directory Structure

- **`liquibase/`** - Liquibase migration scripts for database versioning
  - `db.changelog-master.yaml` - Master changelog file
  - `employee-table.xml` - Employee table migration
  
- **`seed/`** - Initial data for departments and designations
  - Contains INSERT statements for populating reference data

- **`tables/`** - Individual table creation scripts
  - `departments.sql` - Department table DDL
  - `designations.sql` - Designation table DDL

## Usage

1. **Initial Setup**: Run `schema.sql` to create all tables with proper constraints
2. **Seeding**: Execute scripts in `seed/` to populate reference tables
3. **Migrations**: Use Liquibase with `liquibase/db.changelog-master.yaml` for versioned changes
4. **Development**: Reference `ER-diagram.png` for understanding table relationships

## Notes

- All IDs are UUIDs for better distribution and security
- The `metadata` JSONB column in employees table allows flexible schema evolution
- Foreign key constraints use CASCADE for maintaining referential integrity
- Indexes are created on frequently queried columns for performance
