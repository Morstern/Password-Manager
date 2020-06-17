# Erlang server (as process)

In this project I created module, which receives messages from Java GUI application.  
Main function (start()) creates a process, register its under module name (password manage) and loops itself by awaiting for message. After receiving message it checks for action sent by user application and take further steps.

# How to compile and run

1. Start CMD
2. cd "C:\Program Files\erl10.7\bin" ← (insert path to your bin folder)
3. werl -sname server -setcookie password

   (Inside erlang shell)

4. cd("DISK:/PATH/FOLDER/WITH/CODE").
5. c(passwordmanager).
6. passwordmanager:start().

# Further possible developments

- Instead of creating one process, it's possible to create agents, and clients would talk to clients thru server, it'd allow to have multiple clients connected to one server (server would just pass data between client and agent)
- exception handling
- Creating a password generator (now it's based on hash: input password + date)
