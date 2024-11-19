# Major Changes to Sprint 1

## Added Features
1. **Real-Time Messaging**: Enabled secure and live chat functionality with added friends.
2. **Friend Search**: Implemented advanced search filters, allowing users to find friends by:
   - Username
   - University
   - City/Province
   - Interests
3. **Enhanced UI**: Improved homepage navigation and profile viewing experience.
4. **Friend Management**: Users can now add or remove friends and view suggested matches.
5. **Profile Customization**: Users can personalize their profiles with a bio, profile picture, university, and interests.

## Scope for Sprint 1
### Sign Up/Login Page
Key Features:
- Create an account securely with:
  - Username
  - Email
  - Secure password
- Update profile details, such as:
  - Profile picture
  - Birthday
  - Bio
  - City & University
  - Interests (selected from a predefined list)
- Log in securely using email and password.

User Stories Covered:
- As a user, I want to create an account with a password and email so that I can log in safely and the system will remember my details.
- As a user, I want to personalize my profile with a bio, profile picture, and interests so that others can understand who I am and my hobbies.

### Home Page
Key Features:
- Search for friends using advanced filters.
- View suggested matches and their profile details.
- Add or remove friends.
- Seamlessly navigate between the home page and messaging page.

User Stories Covered:
- As a user, I want to see profile details of suggested matches before deciding to add them.
- As a user, I want to search for connections and find people who share my hobbies.
- As a user, I want to see suggested matches based on my interests so that I can connect with like-minded individuals.

### Messaging Page
Key Features:
- Send and receive messages in real time.
- View and retrieve past conversation history.

User Stories Covered:
- As a user, I want to message my connections directly so that I can communicate in a clutter-free, one-on-one environment.
- As a user, I want my messages to be securely stored so that I can continue conversations without losing chat history.

### Sidebar Navigation
Key Features:
- Allow users to seamlessly navigate between the home page and messaging page using intuitive icons on a sidebar.

### Database Configuration
Key Features:
- Ensure all user-related data (accounts, profiles, friends, and messages) are fully integrated with the database.
- Implement data storage, retrieval, and management for a user-specific experience.

## Bug Fixes
1. Lag when the home button is clicked.
2. Add friend label incorrectly appears after a friend has already been added.
3. "Your friends" section labels are not visible on the homepage.
4. Users can add anyone in the database as a friend without proper validation.
5. Duplicated messages for sender and receiver during messaging.

## Challenges
- Database schema design for real-time messaging.
- Ensuring real-time updates for friend recommendations.
- Handling interest-based matching efficiently.

## Team Collaboration
- Tasks were tracked and managed on Trello.
- Team members worked collaboratively, splitting tasks evenly based on schedules and capacity.

