# Database Export Script
# Run: $env:PGPASSWORD = "your_password"; .\export-database.ps1

$PG_DUMP = "C:\Program Files\PostgreSQL\18\bin\pg_dump.exe"
$DB_NAME = "employee_management_system_new"
$DB_USER = "postgres"
$DB_HOST = "localhost"

Write-Host "Starting database export..." -ForegroundColor Green

# 1. Export complete schema
Write-Host "`n1. Exporting complete schema..." -ForegroundColor Cyan
& $PG_DUMP -h $DB_HOST -U $DB_USER -d $DB_NAME --schema-only --no-owner -t departments -t designations -t employees | Out-File -FilePath "schema.sql" -Encoding UTF8
Write-Host "   schema.sql created" -ForegroundColor Green

# 2. Export individual tables
Write-Host "`n2. Exporting individual tables..." -ForegroundColor Cyan
& $PG_DUMP -h $DB_HOST -U $DB_USER -d $DB_NAME --schema-only --no-owner -t departments | Out-File -FilePath "tables/departments.sql" -Encoding UTF8
& $PG_DUMP -h $DB_HOST -U $DB_USER -d $DB_NAME --schema-only --no-owner -t designations | Out-File -FilePath "tables/designations.sql" -Encoding UTF8
& $PG_DUMP -h $DB_HOST -U $DB_USER -d $DB_NAME --schema-only --no-owner -t employees | Out-File -FilePath "tables/employees.sql" -Encoding UTF8
Write-Host "   Individual table schemas created" -ForegroundColor Green

# 3. Export seed data (departments and designations only)
Write-Host "`n3. Exporting seed data..." -ForegroundColor Cyan
& $PG_DUMP -h $DB_HOST -U $DB_USER -d $DB_NAME --data-only --inserts --no-owner -t departments -t designations | Out-File -FilePath "seed/departments_designations_data.sql" -Encoding UTF8
Write-Host "   seed/departments_designations_data.sql created" -ForegroundColor Green

Write-Host "`nExport complete!" -ForegroundColor Green
