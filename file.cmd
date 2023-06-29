@echo off
Title Windows Optimizer

net session >nul 2>&1%
if %errorLevel% == 0 (
    goto :continue
) else (

    echo This script requires administrator privileges. Please click "Yes" in the User Account Control prompt to continue.
    pause
    powershell -Command "Start-Process '%~dpnx0' -Verb runAs"
    exit /b %errorlevel%
)

:continue
set username=%USERNAME%
echo Current logged in user: %username%
pause
echo deleting C:\Users\%username%\AppData\Local\Temp, C:\Windows\Prefetch

set tempdir=C:\Users\%username%\AppData\Local\Temp
del /F /S /Q "%tempdir%" > nul 2>&1

set tempdir=C:\Windows\Prefetch
del /F /S /Q "%tempdir%" > nul 2>&1

echo Done.
pause
echo deleting windows delivery optimization files
net stop dosvc > nul 2>&1
del /F /S /Q "%SystemDrive%\Windows\SoftwareDistribution\Download\*" > nul 2>&1
net start dosvc > nul 2>&1
echo Done.
pause
echo stopping search indexing
net stop WSearch > nul 2>&1
sc config WSearch start=disabled > nul 2>&1
echo done
pause
