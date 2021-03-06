# LspConsole

[![Maintainability Rating](https://scm.thm.de/sonar/api/project_badges/measure?project=MopeSWTP-SS21&metric=sqale_rating)](https://scm.thm.de/sonar/dashboard?id=MopeSWTP-SS21)
[![Reliability Rating](https://scm.thm.de/sonar/api/project_badges/measure?project=MopeSWTP-SS21&metric=reliability_rating)](https://scm.thm.de/sonar/dashboard?id=MopeSWTP-SS21)
[![Security Rating](https://scm.thm.de/sonar/api/project_badges/measure?project=MopeSWTP-SS21&metric=security_rating)](https://scm.thm.de/sonar/dashboard?id=MopeSWTP-SS21)
[![Quality Gate Status](https://scm.thm.de/sonar/api/project_badges/measure?project=MopeSWTP-SS21&metric=alert_status)](https://scm.thm.de/sonar/dashboard?id=MopeSWTP-SS21)

This Readme currently provides a guideline to set up the MopeConsoleClient:

• Guide currently is only for linux systems due to a omc path issue on windows

• We currently work on delivering jars to make the setup easier


## Prerequisites:

[GIT](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git) 

# User setup:

### Openmodelica

1. Install Openmodelica as guided [here](https://openmodelica.org/download/download-linux) or below:
  
   1.1 Open a terminal/shell
  
   1.2 To update the sources.list of the releases, execute:
   
   ```
   for deb in deb deb-src; do echo "$deb http://build.openmodelica.org/apt `lsb_release -cs` stable"; done | sudo tee /etc/apt/sources.list.d/openmodelica.list
   ```
    
   1.3 Execute the following command to import the GPG key used to sign the releases :
   
   `wget -q http://build.openmodelica.org/apt/openmodelica.asc -O- | sudo apt-key add - `
   
   1.4 Execute `sudo apt update`, then execute `sudo apt install openmodelica`
   
### Java

2. Install a Java JDK (version  >= 11). Set up a $JAVA_HOME environment variable. On Linux-Systems you can create one by adding the following line to your ~/.bashrc-file:

   `export JAVA_HOME=<path-to-java>`
   
### OS

3. Inside your terminal, navigate to the folder where you want to clone the repository. Then execute 

   `git clone git@github.com:MopeSWTP-SS21/LspConsole.git`
   
4. Navigate to the newly cloned folder LspConsole to get finally started using the server and consoleclient.


#### | TIP: It is recommended to start two instances of the shell, one for the server task and one for the client task. |


### Getting started & usage of the client:

1. Execute `./gradlew startMopeServer` in one of the shells. After the build is completed, the server should run on port 4200.

2. Execute `./gradlew startConsoleClient` in the other shell. 

3. After the build is completed, you are asked for the serverip & serverport. 

   •	Serverip: Type `127.0.0.1` or `localhost` and confirm.
   
   •	Port: Type 4200 and confirm.
   
4. You are now connected with the server and can control it by typing and confirming the right instruction-number out oft the following list:
```
1: Initialize server
2: Get compiler version
3: Load File
4: Load model
5: Check Model
6: Initialize Model
7: Add Folder to ModelicaPath
8: Show ModelicaPath
9: Complete
10: Get Documentation  
98: Exit - Disconnect
99: Exit - Shutdown Server
```

5. `1: Initialize server`

   Currently it is _necessary to proceed this instruction as the first one_ to initialize the server and its capabilities.
   The Server will launch an Instance of OMC, connect to it and Load the ModelicaStandardLibrary.

6. After Initialization you can view the compiler version by typing `2` in the menue

7. Typing `8` shows you the folder(s), which are in the scope of modelica and where your models are stored

8. Typing `7` lets you add a folder to the modelica path. Obviously, this folder should contain at least one model.

9. The commands `3`,`4`,`5`,`6` are proposed for loading a file, loading a model, check a model or initialize a model 

10. Typing `9` allows you to use the completion feature. It will ask you for a file and you have to specify which line and column.

11. Typing `10` will return a html-documentation to a provided modelica-class name.

12. The commands `98` and `99` are used to disconnect the client. Additionally command `99` will shutdown the server.

