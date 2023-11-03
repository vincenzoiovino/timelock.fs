@echo off
title timelockfs setup


if not "%1"=="am_admin" (
    powershell -Command "Start-Process -Verb RunAs -FilePath '%0' -ArgumentList 'am_admin'"
    exit /b
)

:: Change this line to point to the path of your JRE installation.
setx YOUR_JAVA "C:\Program Files\Java\jdk-15.0.2"

reg ADD "HKEY_CLASSES_ROOT\*\shell\timelock.fs.encrypt"  /f
reg ADD "HKEY_CLASSES_ROOT\*\shell\timelock.fs.encrypt" /f /t REG_SZ /d "timelock.fs.encrypt"
reg ADD "HKEY_CLASSES_ROOT\*\shell\timelock.fs.encrypt\command" /f
reg ADD "HKEY_CLASSES_ROOT\*\shell\timelock.fs.encrypt\command" /f /ve /d "%YOUR_JAVA%\bin\javaw.exe -jar %~dp0timelock.fs.jar encrypt \"%%1\""

reg ADD "HKEY_CLASSES_ROOT\.tlcs\shell\timelock.fs.decrypt"  /f
reg ADD "HKEY_CLASSES_ROOT\.tlcs\shell\timelock.fs.decrypt" /f /t REG_SZ /d "timelock.fs.decrypt"
reg ADD "HKEY_CLASSES_ROOT\.tlcs\shell\timelock.fs.decrypt\command" /f
reg ADD "HKEY_CLASSES_ROOT\.tlcs\shell\timelock.fs.decrypt\command" /f /ve /d "%YOUR_JAVA%\bin\javaw.exe -jar %~dp0timelock.fs.jar decrypt \"%%1\""

echo ---------------------------------------------------------------------------------------------
echo Installation terminated. 
echo If you cannot find the timelock.fs extensions you must edit in this install.bat file the line:
echo setx YOUR_JAVA "C:\Program Files\Java\jdk-15.0.2"
echo and change it to:
echo setx YOUR_JAVA "Path"
echo where Path is a path to your Java Runtime Environment (JRE) installation.
echo If you do not have JRE you can download it from here: https://www.oracle.com/java/technologies/downloads/
echo You can close this window.
pause
