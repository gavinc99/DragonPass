# project
Password Manager that uses dynamic password changing, hashing protocols, two-factor authentication, SQL injection prevention, AES-GCM encryption and other various secure programming protocols. (Java)

## NOTE
Please follow the below steps before using applcaiton:

### Database Access
Localhost database access requires 'user' and 'password' to be configured in config.properties.

### Lib Folder
* All required folders and files for the library can be found and downloaded from the Library Requirments zip folder.
* Add the Javafx lib folder to the project select -> Project Structure -> + ->  Libraries -> Java ->  point to the downloaded javaFx sdk lib folder in file explorer.


### The Following Library is required for this application
* mysql:mysql-connector-java -> Project Structure -> Libraries -> + -> Java -> add mysql-connector in file explorer to the library.
* sqlite-jdbc-3.32.3.2 -> Project Structure -> Libraries -> + -> Java -> add sqlite zip folder to library.

### Test Applications
The main application is accompanied by 3 test applications that can be used in conjunction with the main application. 
The purpose of these test applications is to demonstrate how the applicaiton would function in a commercial or real life environment.

#### Technologies used
The main technologies used in the development and implementation of this project are Java, JavaFX, IntelliJ IDEA, MySQL, SQLite, and Google Authenticator.

#### Algorithms used
* AES-GCM Encryption Algoirthm.
* SHA-256 Hashing Algorithm.
* TOTP Algorithm for Two-Factor Authentication.

#### About This Project - Dragon Pass
This application was created as my final year software project for Bachelor of Science (Honours) in Computing in National College of Ireland. 
My selected specialisation is Cyber Security and this application implemetns and follows various secure coding methods, techniques, procedures and algorithms.
DragonPass is a password manager that acts as a solution to counter password leaks and password cracking methods by dynamically and securely changing user passwords stored within the manager, while still maintaing the usual convenience other password managers have to offer. This unqique feature is called 'Auto-Change' and automizes the password changing process while still maintaing secure procedures and methods.


The application has been designed to defend against SQL injection, buffer overflow, password cracking methods such as brute force attacks, data leaks, company breaches, phishing attacks and other various vulnerabilities.
