# Colors for output
$Green = "`e[32m"
$Red = "`e[31m"
$Reset = "`e[0m"

# Array of services with actual directory names
$services = @(
    "auth-service",
    "menu-service",
    "order-service",
    "payments-service",
    "notification-service",
    "document-service",
    "hail-waiter-service",
    "analytics-service",
    "api-gateway"
)

# Function to build a service
function Build-Service {
    param (
        [string]$service
    )
    
    Write-Host "`n${Green}Building ${service}...${Reset}"
    
    if (-not (Test-Path $service)) {
        Write-Host "${Red}Directory not found: ${service}${Reset}"
        return $false
    }

    Push-Location $service
    
    # Build with Maven
    mvn clean package -DskipTests
    if ($LASTEXITCODE -ne 0) {
        Write-Host "${Red}Maven build failed for ${service}${Reset}"
        Pop-Location
        return $false
    }

    # Build Docker image
    docker build -t "quisin/${service}:latest" .
    if ($LASTEXITCODE -ne 0) {
        Write-Host "${Red}Docker build failed for ${service}${Reset}"
        Pop-Location
        return $false
    }

    Pop-Location
    Write-Host "${Green}Successfully built ${service}${Reset}"
    return $true
}

# Main build process
Write-Host "${Green}Starting build process for all services...${Reset}"

# Build each service
$failedServices = @()
foreach ($service in $services) {
    if (-not (Build-Service $service)) {
        $failedServices += $service
    }
}

# Summary
Write-Host "`n${Green}Build process completed${Reset}"
if ($failedServices.Count -eq 0) {
    Write-Host "${Green}All services built successfully${Reset}"
} else {
    Write-Host "${Red}The following services failed to build:${Reset}"
    foreach ($service in $failedServices) {
        Write-Host "${Red}- ${service}${Reset}"
    }
}

# Instructions for running
Write-Host "`n${Green}To run all services:${Reset}"
Write-Host "docker-compose up -d" 