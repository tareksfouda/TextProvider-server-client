The TextProvider should serve individual lines from a static text file to clients over the network. The client-
server protocol for this system should be the following:

GET <n> => If <n> is a valid line number for the
text file, you should return "OK\r\n" followed by the <n>th
line from the text file.
If <n> is NOT a valid line number, you should return
"ERR\r\n".
Note that the lines in the file should be indexed starting from 1,
not 0.
QUIT => This command should disconnect the client.
SHUTDOWN => This command should shutdown the server.
The TextProvider server:
* must support at least one client and may optionally support many concurrent clients
.
* must listen for client connections on TCP port 10322.
You can make the following assumptions about the format of the text file:
* Every line is newline ('\n') terminated
* Each line will fit into memory
* Every character in the file is valid ASCII; there will be no Unicode characters
TextProvider

Specification

You may perform any type of pre-processing on the file as long as the behavior of the server remains correct.
Your system should work well for both large and small files and it should work well as the number of GET
requests/second increases.

Given a file whose contents are:
Humpty Dumpty sat on a wall,
Humpty Dumpty had a great fall.
All the king's horses and all the king's men
Couldn't put Humpty together again.
You might expect the following:
CLIENT -> GET 1
SERVER <- OK
SERVER <- Humpty Dumpty sat on a wall,
CLIENT -> GET -2
SERVER <- ERR
CLIENT -> GET 3
SERVER <- OK
SERVER <- All the king's horses and all the king's men
CLIENT -> QUIT

You can assume that your system will execute in an environment like that of a m4.xlarge EC2 instance running
Cent OS 7.x. An m4.xlarge instance has the following properties:
* four vCPUs
* 16 GB of RAM
* EBS Storage
Nothing else will run on that machine so all the resources are available to your system.


How to run the system?
 	The server is started by running the run.sh script with the name of the file to server as an argument.
 	The client is started by running the run.sh script without an argument.

Socket programming is how I implemented this assessment.
The system works by reading the file to be served line by line and
 	dividing it into separate files for each line. When a
 	line is requested, the appropriate file is read and transfered over a network Socket.

 The system should perform well as the number of requests increases 
 	per second because the data file is already parsed, and the use
 	of Buffered I/O reduces low-level OS interaction, hopefully
 	increasing speed (according to Oracle docs). But, because my
 	system uses Java's file IO instead of a database, as file and
 	line size grow, performance will suffer greatly. 

 Currently, The system handles large data files well as long as each 
 	line is below ~100MB. Java's heap allocation can't handle that 
 	much data. If we change how to store lines from storing them as 
 	Strings to actually use a database, or probably 
 	using StringBuilder, it will be better as wel

Problems:
	* This software will not perform at scale. It does not utilize other
	drives (for more space on disk), it uses normal String types (depleting
	Java's heap space quickly), and the transfer over the network is line
	by line (the size of the line to be transferred is therefore constrained
	by the size of the buffer).
	
	* Multithreading was not implemented as I did not have enough time at all I apologize. but to handle concurrent clients
	we would be able to do that using multithreading.
