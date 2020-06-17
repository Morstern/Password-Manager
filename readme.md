# Password Manager

**I had to make a project which uses functional language, so I decided to combine Java and Erlang applications by using [JInterface](http://erlang.org/doc/apps/jinterface/jinterface_users_guide.html)**

### Main ideas of project

1. *"Generate"* complicated passwords, by using simple input password.
I wanted also to be able to generate diffrent passwords based on same input password.  

   For example:
   - input password: zxc
   - examples of generated passwords: t7AB2nvMe6madB **OR** Ez/0OZTrTX/7t7a)  
 

2. Passwords are stored on Erlang server (they're written into file)
3. If an user wants to get access to generated password, firstly he has to provide the input password that was used to generate it.
4. If user provides correct input password, server sends generated password to the user, which is automatically copied to the clipboard.

### Erlang application
Erlang program creates process which acts like a server, it recieves message, do some actions and replies to the sender. I mainly wanted to make use of all basic capabilities of functional language such as:
- higher order functions;
- **I/O** to file;
- switch case and if...else;
- concatenation of lists (`L++[H]`), deleting element from lists (`L--[H]`);
- various functions from standard modules (*lists, string, file, io, base64, crypto, erlang*).

Server interpretes requests from client (for example creating/deleting a new entry) and sends back response.

### Java application

Client was written in Java (with GUI library **JavaFX**) is able to do following actions:
- Fetch entries;
- Add new entry;
- Remove entry;
- Fetch password

All above actions sends to the server message: `{From, Operation, Data}`, server based on field *Operation* does an action and sends back to the client response.