::in progress
@echo off
Title Windows Optimizer
echo "Disabling Windows tips and tricks"
reg add HKCU\Software\Microsoft\Windows\CurrentVersion\Explorer\Advanced /v ShowSyncProviderNotifications /t REG_DWORD /d 0 /f
pause
set username=%USERNAME%
echo Current logged in user: %username%
set tempdir=C:\Users\%username%\AppData\Local\Temp
echo Deleting directory: %tempdir%
rmdir /s /q %tempdir%
echo Done.
pause
