::in progress
@echo off
Title Windows Optimizer
echo "Disabling Windows tips and tricks"
reg add HKCU\Software\Microsoft\Windows\CurrentVersion\Explorer\Advanced /v ShowSyncProviderNotifications /t REG_DWORD /d 0 /f
pause
