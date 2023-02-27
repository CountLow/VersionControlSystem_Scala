# VersionControlSystem_Scala
A simple local Version Control System written in Scala
\
\
Steps to use the Version Control System:

* You can either compile the main scala file each time you want to execute a new command (like stage, commit etc) \
  * scala  [path]\scala\main.scala [operation]
* When it is your first time calling the file, you need to use the command scalac before:
  * scalac [path]\scala\*.scala  [path]\scala\VersionControlSystem\*.scala \
  
* Or you can create a new command for your console
* In order to do that, for windows operation system, you need to copy the following lines and create a new .cmd or .bat file \

@echo off \
call scalac [path]\scala\*.scala  [path]\scala\VersionControlSystem\*.scala \
scala  [path]\scala\main.scala %1 %2 %3 %4 %5 %6 %7 %8 %9 \

\

* save your file with the name VCS in the system32 folder 
* now you can use the command VCS [operation] in your Terminal \

Substitute [path] for your location of the Version Control System in the above text
