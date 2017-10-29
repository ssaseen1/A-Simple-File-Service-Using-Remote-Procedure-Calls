Programming Language: JAVA
---------------------------

Implementation:
---------------
>> Server maintains a parent folder called Server_folder under which it has folders of the owners with the respective files.Everytime the server starts up, the parent directory is deleted and a new parent directory is created when write operation is performed.

>>./client remote01.cs.binghamton.edu 9090 --operation <operation type> --filename <file name> --user guest <user name>
(where operation type includes "write", "read" and "list")

>> The write operation writes the content to a file in a newly created folder named after the owner of the file in the parent Server_folder.

>> the operation type (write, read and list) are not case - sensitive, but 
file name and user name are case - sensitive.

>> the order of the arguments --operation, --filename, --user may change, but these parameters are case - sensitive and also should start with --.

>> The client can also send a file with no content to the server, which is treated as a normal file.

>> The output is written in JSON format as per the requirement.
SystemException is also handled amd written in the JSON format wherever required.

Environment setup steps:
------------------------

>> Go to the directory ssaseen1-project1
>> Enter command "bash"
>> source source
>> chmod a+x Server.sh
>> chmod a+x Client.sh

Run the Makefile to compile the program: make
---------------------------------------------

Steps to run the program:
-------------------------

1) For server:
./Server.sh <port_number>

2)For Client: 
./Client.sh <hostname> <port_number> --operation <operation_type> --filename <filename> --user <user_name>

Sample Outputs:
--------------
./Server.sh 1052
 Server is started at port number 1052

Client started at remote02.cs.binghamton.edu

./Client.sh remote01.cs.binghamton.edu 1052 --operation write --filename Example.txt --user shali

./Client.sh remote01.cs.binghamton.edu 1052 --operation write --filename Example1.txt --user shali
{"1":{"i32":1}}

./Client.sh remote01.cs.binghamton.edu 1052 --operation list  --user shali   ["rec",2,{"1":{"str":"Example.txt"},"2":{"i64":1475798371000},"3":{"i64":1475798371000},"4":{"i32":0},"5":{"str":"shali"},"6":{"i32":8},"7":{"str":"02fb126282cb0d596a9052bc21948326"}},{"1":{"str":"Example1.txt"},"2":{"i64":1475798569000},"3":{"i64":1475798569000},"4":{"i32":0},"5":{"str":"shali"},"6":{"i32":16},"7":{"str":"a4ece2c8654020b5c1b835b66d4fc754"}}]

./Client.sh remote01.cs.binghamton.edu 1052 --operation read --filename Example1.txt --user shali
{"1":{"rec":{"1":{"str":"Example1.txt"},"2":{"i64":1475798569000},"3":{"i64":1475798569000},"4":{"i32":0},"5":{"str":"shali"},"6":{"i32":16},"7":{"str":"a4ece2c8654020b5c1b835b66d4fc754"}}},"2":{"str":"Example Example\n"}}

nano Example1.txt

./Client.sh remote01.cs.binghamton.edu 1052 --operation write --filename Example1.txt --user shali
{"1":{"i32":1}}

./Client.sh remote01.cs.binghamton.edu 1052 --operation read --filename Example1.txt --user shali
{"1":{"rec":{"1":{"str":"Example1.txt"},"2":{"i64":1475798569000},"3":{"i64":1475798979000},"4":{"i32":1},"5":{"str":"shali"},"6":{"i32":29},"7":{"str":"70f4ee163a8a5a662060dbbf2fa6d1d2"}}},"2":{"str":"Example Example\n\nHello world\n"}}

./Client.sh remote01.cs.binghamton.edu 1052 --operation list --user shali   ["rec",3,{"1":{"str":"Example.txt"},"2":{"i64":1475798371000},"3":{"i64":1475798371000},"4":{"i32":0},"5":{"str":"shali"},"6":{"i32":8},"7":{"str":"02fb126282cb0d596a9052bc21948326"}},{"1":{"str":"Example1.txt"},"2":{"i64":1475798569000},"3":{"i64":1475798979000},"4":{"i32":1},"5":{"str":"shali"},"6":{"i32":29},"7":{"str":"70f4ee163a8a5a662060dbbf2fa6d1d2"}},{"1":{"str":"Example1.txt"},"5":{"str":"shali"}}]

./Client.sh remote01.cs.binghamton.edu 1052 --operation read --filename Example111.txt --user shali
{"1":{"str":"File Example111.txt does not exist"}}

./Client.sh remote01.cs.binghamton.edu 1052 --operation read --filename Example111.txt --user shalisas
{"1":{"str":"Owner shalisas does not exist"}}

