# Library-Management-System
It lets a librarian manage books, members, and book issue/return, with automatic fine calculation for late returns. Data is stored in a database using JDBC.
How to Run
Make sure Java is installed (`java -version`).
Compile:
   javac -d bin src/\*.java
Run:
   java -cp "bin:lib/sqlite-jdbc-3.46.1.3.jar" Main
Features
Add, update, delete, and search books
Register members
Issue and return books with automatic late fines
View reports: currently issued books, overdue books, total fines collected
Simulated concurrent issue requests (demonstrates multithreading)
Custom exceptions for invalid actions (e.g. book already issued, member not found)
