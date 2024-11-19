# Database Schema

## Users Table
| Column      | Type      | Description          |
|-------------|-----------|----------------------|
| `id`        | INT       | Unique user ID       |
| `username`  | VARCHAR   | User’s display name  |
| `email`     | VARCHAR   | User’s email         |

## Friends Table
| Column      | Type      | Description          |
|-------------|-----------|----------------------|
| `user_id`   | INT       | User ID              |
| `friend_id` | INT       | Friend’s User ID     |

## Messages Table
| Column      | Type      | Description          |
|-------------|-----------|----------------------|
| `id`        | INT       | Unique message ID    |
| `sender_id` | INT       | Sender’s User ID     |
| `content`   | TEXT      | Message content      |
