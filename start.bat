@echo off
setlocal

:: --- Main Python script file ---
set PYTHON_SCRIPT=camera_viewer.py

:: --- List of required libraries ---
set "LIBRARIES=PyQt5 opencv-python onvif-zeep"

:: Check if Python is installed and accessible
python --version >nul 2>nul
if errorlevel 1 (
    echo.
    echo =================================================================
    echo  ERROR: Python not found.
    echo  Please make sure Python is installed and added to your system's PATH.
    echo =================================================================
    echo.
    pause
    exit /b
)

:: This part will be hidden by the VBS script, but is useful for direct debugging
echo Checking for required Python libraries...
echo.

:check_libs
for %%L in (%LIBRARIES%) do (
    python -c "import %%L" >nul 2>nul
    if errorlevel 1 (
        echo Library %%L is missing. Installing...
        pip install %%L >nul
        if errorlevel 1 (
            echo.
            echo =================================================================
            echo  ERROR: Failed to install %%L.
            echo  Please try to install it manually with 'pip install %%L'
            echo =================================================================
            echo.
            pause
            exit /b
        )
    )
)

:: --- Start the Python script without a console window ---
:: 'start' is used to launch the pythonw.exe process independently
:: 'pythonw.exe' is the windowless Python interpreter for GUI applications.
start "" pythonw "%PYTHON_SCRIPT%"

:: The batch script will now exit.
exit
