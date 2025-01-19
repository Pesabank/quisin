@echo off
setlocal EnableDelayedExpansion

:: Colors for output
set "GREEN=[92m"
set "RED=[91m"
set "NC=[0m"

:: Array of services with actual directory names
set "services=auth-service menu-service order-service payments-service notification-service document-service hail-waiter-service analytics-service api-gateway"

echo [92mStarting build process for all services...[0m

:: Build each service
set "failed_services="
for %%s in (%services%) do (
    echo.
    echo [92mBuilding %%s...[0m
    
    if not exist "%%s" (
        echo [91mDirectory not found: %%s[0m
        set "failed_services=!failed_services! %%s"
        goto :continue_%%s
    )

    pushd %%s
    
    :: Build with Maven
    call mvn clean package -DskipTests
    if errorlevel 1 (
        echo [91mMaven build failed for %%s[0m
        popd
        set "failed_services=!failed_services! %%s"
        goto :continue_%%s
    )

    :: Build Docker image
    docker build -t "quisin/%%s:latest" .
    if errorlevel 1 (
        echo [91mDocker build failed for %%s[0m
        popd
        set "failed_services=!failed_services! %%s"
        goto :continue_%%s
    )

    popd
    echo [92mSuccessfully built %%s[0m

    :continue_%%s
)

:: Summary
echo.
echo [92mBuild process completed[0m
if "!failed_services!"=="" (
    echo [92mAll services built successfully[0m
) else (
    echo [91mThe following services failed to build:[0m
    for %%s in (!failed_services!) do (
        echo [91m- %%s[0m
    )
)

:: Instructions for running
echo.
echo [92mTo run all services:[0m
echo docker-compose up -d

endlocal 