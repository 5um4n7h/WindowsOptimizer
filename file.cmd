
@echo off
Title Windows Optimizer

net session >nul 2>&1
if %errorLevel% == 0 (
    goto :continue
) else (

    echo This script requires administrator privileges. Please click "Yes" in the User Account Control prompt to continue.
    powershell -Command "Start-Process '%~dpnx0' -Verb runAs"
    exit /b %errorlevel%
)

:continue
set username=%USERNAME%
echo Current logged in user: %username%
set tempdir=C:\Users\%username%\AppData\Local\Temp
echo Taking ownership of directory: %tempdir%
takeown /f "%tempdir%" /r /d y
echo Granting full control permissions to directory: %tempdir%
icacls "%tempdir%" /grant "%username%":(F) /t
echo Deleting directory: %tempdir%
RD /S /Q "%tempdir%"
echo Done.
net stop WSearch
sc config WSearch start=disabled
pause

