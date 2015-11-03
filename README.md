# Release
OsParking, 3 device simulators, etc.-needs parkinglot-db(another repository of OsParking)

OsParking (pronounced 'Oz' Parking, as in The Wizard of Oz) is an Open Source Parking Lot Management Software. In an extended sense, it is a term which describes a simulation package which includes OsParking(manager) and three device simulators(Camera, E-Board, GateBar).

It is going to be officially public as of Oct 30, 2015, Seoul, South Korea time)
OsParking software assumes the use of LPR (License Plate Recognition) technologyas a external module to identify car tag number.

Open source Parking Inc

Oct. 30, 2015

-

Softwares Needed to Run

1. JRE 1.8.0

2. MySQL 5.6.24

Additionally Needed Softwares for Developers

1. IDE used: netbeans 8.0.2

2. IDE augmenting Maven : apache-maven-3.3.3

3. Java Compiler: JDK 1.8.0_45

4. (Optionally) Toad for MySQL 7.5.0

-

After the Maven is installed, the rs2xml.jar need to be registered manually as follows(this jar file is in 'lib' folder of this repository):

C:\DOS> // first move to the directory where this jar file exists

C:\DOS> mvn install:install-file -Dfile=rs2xml.jar -DgroupId=net.proteanit.sql -DartifactId=rs2xml -Dversion=1.0 -Dpackaging=jar
