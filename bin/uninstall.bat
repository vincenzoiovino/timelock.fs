@echo off
title timelockfs setup
if not "%1"=="am_admin" (
    powershell -Command "Start-Process -Verb RunAs -FilePath '%0' -ArgumentList 'am_admin'"
    exit /b
)
reg DELETE "HKEY_CLASSES_ROOT\*\shell\timelock.fs.encrypt"  /f

reg DELETE "HKEY_CLASSES_ROOT\.tlcs\shell\timelock.fs.decrypt"  /f


echo Program uninstalled. You can close this window.
pause