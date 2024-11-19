# Code Architecture

## Overview
UniVerse follows a three-tier architecture:
1. **Frontend (Presentation Layer)**:
   - Built using Java Swing and JavaFX.
   - Handles user interaction via graphical components like JFrame.
2. **Backend (Business Logic Layer)**:
   - Java-based APIs for managing data flow and logic.
   - Maven Structure
   - Features real-time messaging and friend management.
3. **Database (Data Access Layer)**:
   - Firebase database for user, friend, and message data.

## Folder Structure
- `src/`: Main source code.
- `gui/`: Contains UI-related classes.
- `utils/`: Database connectivity and schema management.
- `test/`: JUnit test cases for all features.

## API Integration
The backend interacts with the database for:
- User authentication.
- Friend profile view and add/remove.
- Real-time messaging.
