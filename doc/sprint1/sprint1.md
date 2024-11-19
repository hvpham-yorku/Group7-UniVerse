**Sprint 1 Meeting**
***
**Date:** Nov 11th,2024

**Time:** 7pm - 9pm

**Location:** Held on Discord

**Participants:** Elyse Dhaliwal, Olajonlu Timi Akinbaleye, Sarimah Chindah, Kosy Oraka, Kennie Oraka
***

**Goal for Sprint 1**

The primary objective of this release is to deliver a foundational version of UniVerse that enables the user to:

- Create an account securely and log in.

- Update and personalize their profiles with key details such as a profile picture, bio section, their university and interests. 

- Users can discover, add and manage friends based on their shared interests.
  
- Users are able to able to see the profiles of other users before they add them as friends.

- Users are able to search for friends using criteria such as username, university, city/province, or interests

- Engage in one-on-one messaging with friends, including the ability to view conversation history. 

This sprint focuses on implementing a fully functioning backend for storing all user-related data, including profiles, connections and messages to provide a user-specific experience!

***
**Scope for Sprint 1:**

**Signup/Login Page**

**Key Features:**

- Create an account with a username as your name, email and a secure password.

- Update profile details, such as including:
   - A profile picture
   - Birthday
   - Bio
   - City & University
   - Interests (selected from a predefined list)
   - Log in using email and password

User stories that were covered - also tracked on Trello:

1. As a user, I want to create an account with a password and email so that I can log in safely and the system will remember by details.

2. As a user, I want to personalize my profile with a bio, profile picture, and interest so that others can understand who I am and my hobbies. 
***
**Home Page**

**Key Features:**

- Search for friends
- View suggested matches and their profile details
- Add or remove friends
- Select a friend to initiate messaging

User stories that were covered - also tracked on Trello:

3.  As a user, I want to be able to switch between the home page and the messaging page seamlessly.

4.  As a user, I want to see profile details of suggested matches before decided to add them

5. As a user, I want to search for connections and find people who share my hobbies.

6. As a user, I want to see suggested matches based on my interests so that I can connect with like-minded individuals

***
**Messaging Page**

**Key Features:**

- Send and receive messages in real time with selected friends

- View and retrieve past conversation history

User stories that were covered - also tracked on Trello:

7. As a user, I want to message my connections directly so that I can communicate in a clutter free, one-on-one environment. 

8. As a user, I want my messages to be securely stored so that I can continue conversations without losing chat history.
   
9. As a user, I want to be able to switch between the home page and the messaging page seamlessly. 

***
**Side Bar Feature/User Story**
- Allow for users to seamlessly navigate through the home page and messaging page using feature specified icons on a side bar.

***
**Database Configuration**
**Key Features:**
- Ensure that all accounts, profiles, friends and messages are fully integrated with the database to store, retrieve and manage user-specific data. 

***
** Task Breakdown and Roles Assigned**

| Task  | Trello Ticket (per feature) | Assigned to: |
| --------| --------- |----------  |
| Create sign up & log in page |https://trello.com/c/8WBKSQtI/17-user-profile-creation-personalization-and-management-create-new-profile-page  | Timi |
| Database connection for storing each user|  | Timi|
| UI for sign up/login page  |  | Timi|
| Hompage features | https://trello.com/c/Ap415Tvo/28-homepageview-profile-add-remove-friends| Kennie, Kosy & Timi |
| Database connection for storing friends |  | Kennie & Kosy|
| UI for Homepage page  |  | Kennie & Kosy|
| Messaging page features |https://trello.com/c/hQs7nMuW/29-communication-with-connections-management-messages-screen| Sarimah, Elyse & Timi|
| UI for Messages page  |  | Sarimah|
| Database connection for storing messages |  | Elyse|
| Testing |  | Everyone|

**Team Capacity:** Each member had atleast 2 hours a day to work on the project. When assigned tasks members discussed their schedules and took on roles depending on the amount of work required and split evenly. 

**Spikes** that might need extra focus/time: Database schema, real-time messaging, Interest based matching
***
**Decision Making**
1. Workflow: For workflow purposes our team decided it would be best to work through 3 specific branches - one for each feature (Sign up, Homepage, Messages). Both homepage and messages would be split and allow for 3 people to work each page, jumping in when necessary. Each person would let other members know what they are working on and when they are done/need help and another person would continue the work done on the branch. Meaning that each person only commited their changes. Final PR made at the end with 2 people to review.

2. All of the above was decided together with the group to ensure all members were on the same page and agreed. 
