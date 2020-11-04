# CSDS 325 Project 1--Ethan Voss (ehv3)
Hello! Included in this directory is the source code for my HTTP server project.
1. The location of the source code is under src->main->java, and the 
package is ehv3.src.java. Here you'll find three different Java files I created
for this project. The config file (config.properties) and the two html files I 
created are in src->main->resources.
2. To start my server, I created a .jar file that can be easily launched from the
terminal using the command "java -jar multithread-http-server-1.0-SNAPSHOT.jar",
and the .jar file is located in the "target" folder.
3. To test my stuff I used Brave, which runs on Chromium 86.0.4240.183, so any 
similar Chromium browser should work.

Some other things: I did not get to finish establishing persistent connections,
so I just reverted to a version that worked without them. I used Maven to compile
and package my code. Everything in the config file includes relative paths, so it
shouldn't really be messed with.

Thanks, and let me know if you have any questions or anything!