@echo off
set arg1=%1
set arg2=%2
set arg3=%3
Rem scalac %~dp0\scala\main.scala %~dp0\scala\VersionControlSystem\*.scala
scala %~dp0\scala\main.scala %1 %2 %3
echo reached end