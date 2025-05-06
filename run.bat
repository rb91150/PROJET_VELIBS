@echo off
REM Compilation
javac --module-path "C:\Program Files\Java\javafx-sdk-24.0.1\lib" ^
  --add-modules javafx.controls,javafx.fxml ^
  -cp ".;lib/json-20210307.jar" ^
  VelibApp.java

IF ERRORLEVEL 1 (
  echo.
  echo ❌ Erreur de compilation. Corrige les erreurs et relance.
  pause
  exit /b
)

REM Exécution
echo.
echo ✅ Compilation réussie. Lancement de l'application...
echo.

java --module-path "C:\Program Files\Java\javafx-sdk-24.0.1\lib" ^
  --add-modules javafx.controls,javafx.fxml ^
  -cp ".;lib/json-20210307.jar" ^
  VelibApp

pause
