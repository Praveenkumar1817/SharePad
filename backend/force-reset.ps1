$ErrorActionPreference = "Stop"
Write-Host "Taking control and resetting PostgreSQL password..."

Write-Host "1/6 Stopping PostgreSQL Service..."
Stop-Service -Name postgresql-x64-17 -Force

Write-Host "2/6 Enabling Trust Authentication..."
$hbaConf = "C:\Program Files\PostgreSQL\17\data\pg_hba.conf"
$originalHba = Get-Content $hbaConf
$modifiedHba = $originalHba -replace 'scram-sha-256', 'trust' -replace 'md5', 'trust'
Set-Content -Path $hbaConf -Value $modifiedHba

Write-Host "3/6 Restarting PostgreSQL..."
Start-Service -Name postgresql-x64-17
Start-Sleep -Seconds 3

Write-Host "4/6 Altering postgres user password..."
& "C:\Program Files\PostgreSQL\17\bin\psql.exe" -U postgres -c "ALTER USER postgres PASSWORD 'sharepad';"

Write-Host "5/6 Restoring Authentication settings..."
Stop-Service -Name postgresql-x64-17 -Force
Set-Content -Path $hbaConf -Value $originalHba

Write-Host "6/6 Starting PostgreSQL Service..."
Start-Service -Name postgresql-x64-17

Write-Host "SUCCESS! The database password is now 'sharepad'."
Write-Host "You can close this window now."
Start-Sleep -Seconds 10
