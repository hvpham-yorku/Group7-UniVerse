# UniVerse

## Motivation

**UniVerse** is a social networking application that connects users based on shared hobbies and interests. The project aims to help users find friends with similar passions, enhancing their social experience by allowing them to connect with like-minded individuals. By focusing on specific activities, UniVerse fosters meaningful connections and helps users engage in group activities with new friends.

This project is part of a team initiative to learn about full-stack application development using Java, Swing, JavaFX, and MySQL, structured in a three-tier architecture.

## Installation

To set up and run the project locally, ensure you have the following tools installed:

- **Java Development Kit (JDK) 1.8 or higher**
- **MySQL** (with a local instance)
- **Eclipse IDE** 
- **JUnit** (for testing)

### Steps to Build and Run

1. **Clone the Repository**:
   ```
   git clone https://github.com/hvpham-yorku/UniVerse.git
   cd UniVerse
   ```
2. Set Up the MySQL Database:
- Create a new MySQL database called friend_finder_db.
- Run the SQL script (uni_verse_db_users.sql) included in the project to create necessary tables.
- Update the DatabaseConnection class with your MySQL credentials.

3. Open the Project in Eclipse:
- Import the project into Eclipse as an existing Java project.
- Make sure all required libraries (JUnit, MySQL Connector) are added to the build path.

4. Run the Application:
- Run the main class to start the backend.

5. Testing:
- Use JUnit tests to verify the functionality.
- Run tests by navigating to the test folder in Eclipse, right-clicking UserProfileDAOTest, and selecting Run As > JUnit Test.


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