# UniVerse

note: to have access to firebase you would have to contact me by # or email 
905-965-6789 or timmyola@my.yorku.ca so I can add you to it. : ) 

because of Googles policy we are unable to upload our private keys in this repo because it is public. 
https://1ty.me/8SEfyiikM (if you are not the TA or professor **DO NOT OPEN** as this is a view once link)

store those keys in a json file called #serviceAccountKey.json on your device and add that key to your ~/.zshrc file as 
```
export FIREBASE_KEY_PATH="path-to-serviceAccountKey.json"
```
then run 
```
 source ~/.zshrc  
```
in your terminal to refresh the session. 

## Motivation

**UniVerse** is a social networking application that connects users based on shared hobbies and interests. The project aims to help users find friends with similar passions, enhancing their social experience by allowing them to connect with like-minded individuals. By focusing on specific activities, UniVerse fosters meaningful connections and helps users engage in group activities with new friends.

This project is part of a team initiative to learn about full-stack application development using Java, Swing, JavaFX, and MySQL, structured in a three-tier architecture.

## Installation

To set up and run the project locally, ensure you have the following tools installed:

- **Java 17** or later installed.
- **Apache Maven** installed.

### Steps to Build and Run

1. **Clone the Repository**:
   ```
   git clone https://github.com/hvpham-yorku/Group7-UniVerse.git
   cd Group7-UniVerse/universe
   ```
2. Install Dependecies
```
mvn clean install
```

3. Run the Application
```
mvn exec:java -Dexec.mainClass="com.universe.UniVerse"
```

4. Testing:
- Use JUnit tests to verify the functionality.
- Run tests by navigating to the test folder in Eclipse, Run As > JUnit Test.

### Common issues
**NoClassDefFoundError**: If you encounter this or any dependency-related error, force Maven to update all dependencies by running:
```
mvn clean install -U
```


### Alternatively, you can run the GUI from an IDE like Eclipse:

- Open the project in Eclipse.
- Navigate to com.universe.gui.SignUporIn.java.
- Right-click on the file and select Run As -> Java Application.



## Contribution
We welcome contributions to UniVerse! Please follow these guidelines:

1. **Branching Strategy:** We use Git Flow for branching. Our main branch serves as the primary codebase, with multiple feature branches for individual tasks. When a feature is complete, it is merged back into the main branch.
- Feature branches are named based on the task to be completed (e.g.complete_backend_database_connection)
- Bugfix branches should be named based on the description of the bug (e.g., complete_backend_database_connection_fix).

2. Ticketing:
- We use Trello for ticketing and task management. Each feature or bug is assigned a card in Trello to help track progress and manage tasks.

3. Pull Requests:
- We use Pull Requests for code review. Before merging any feature or bugfix branch into the main branch, a pull request must be created and reviewed by 2 team members.

4. Code Reviews:
- Two team member must review and approve each pull request before merging. Code reviews ensure that all code meets project standards and helps catch potential issues early.
