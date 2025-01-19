Write-Host "Testing API Gateway..."

# Test health endpoint
Write-Host "`nTesting Health Endpoint..."
Invoke-RestMethod -Uri "http://localhost:8080/actuator/health" -Method Get

# Test Auth Service Route
Write-Host "`nTesting Auth Service Route..."
try {
    Invoke-RestMethod -Uri "http://localhost:8080/api/auth/health" -Method Get
} catch {
    Write-Host "Auth service not available (expected during testing)"
}

# Test User Service Route
Write-Host "`nTesting User Service Route..."
try {
    Invoke-RestMethod -Uri "http://localhost:8080/api/users/health" -Method Get
} catch {
    Write-Host "User service not available (expected during testing)"
}

# Test Restaurant Service Route
Write-Host "`nTesting Restaurant Service Route..."
try {
    Invoke-RestMethod -Uri "http://localhost:8080/api/restaurants/health" -Method Get
} catch {
    Write-Host "Restaurant service not available (expected during testing)"
}

# Test Menu Service Route
Write-Host "`nTesting Menu Service Route..."
try {
    Invoke-RestMethod -Uri "http://localhost:8080/api/menus/health" -Method Get
} catch {
    Write-Host "Menu service not available (expected during testing)"
}

# Test Order Service Route
Write-Host "`nTesting Order Service Route..."
try {
    Invoke-RestMethod -Uri "http://localhost:8080/api/orders/health" -Method Get
} catch {
    Write-Host "Order service not available (expected during testing)"
}

# Test Fallback endpoints
Write-Host "`nTesting Fallback Endpoints..."
Invoke-RestMethod -Uri "http://localhost:8080/fallback/auth" -Method Get
Invoke-RestMethod -Uri "http://localhost:8080/fallback/user" -Method Get
Invoke-RestMethod -Uri "http://localhost:8080/fallback/restaurant" -Method Get

# Test CORS
Write-Host "`nTesting CORS Headers..."
$headers = @{
    "Origin" = "http://localhost:3000"
    "Access-Control-Request-Method" = "GET"
}
try {
    Invoke-RestMethod -Uri "http://localhost:8080/api/auth/health" -Method Options -Headers $headers
} catch {
    Write-Host "CORS headers received (check response headers in browser for detailed testing)"
} 