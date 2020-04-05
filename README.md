# MutualNeighbors

To run the program:

1. Move the MutualNeighbors.java file to your home/user directory in the zoidberg.cs.ndsu.nodak.edu host
2. Move your test data files to the same directory
3. SSH into the zoidberg.cs.ndsu.nodak.edu host via the command line
4. Move into the directory containing that MutualNeighbors.java file via the command line
5. Set up input directory in your Hadoop file system using command:
	 hadoop fs -mkdir /user/<yourName>/<directoryName>
	 hadoop fs -mkdir /user/<yourName>/<directoryName>/input
6. Move the test data files to Hadoop:
	hadoop fs -copyFromLocal /home/<yourName>/<directoryName>/<fileName> 
7. Run command: 
	hadoop com.sun.tools.javac.Main MutualNeighbors.java
8. Run command:
	jar cf MutualNeighbors.jar *.class  
9. Run command:
     hadoop jar MutualNeighbors.jar MutualNeighbors /user/<yourName>/<directoryName>/input /user/<yourName>/<directoryName>/output
