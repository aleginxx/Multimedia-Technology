# Task Management System - MediaLab Assistant

![Java](https://img.shields.io/badge/Java-%23f89820.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-%232233aa.svg?style=for-the-badge&logo=java&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-%23c71a36.svg?style=for-the-badge&logo=apachemaven&logoColor=white)
![JSON](https://img.shields.io/badge/JSON-%23cccccc.svg?style=for-the-badge&logo=json&logoColor=black)
![Eclipse IDE](https://img.shields.io/badge/Eclipse_IDE-%230062b1.svg?style=for-the-badge&logo=eclipseide&logoColor=white)

GitHub repository for a JavaFX-based Task Management System, developed for the Multimedia Technology course in ECE-NTUA.

---

## Overview

This repository contains the implementation of a graphical **Task Management System** using Java and JavaFX, created as part of the 7th-semester course *Multimedia Technology* at NTUA. The application provides task organization and productivity tools through an intuitive interface and persistent storage via JSON files.

### Key Features
- **Task CRUD**:
  - Title, description, category, priority, due date (day-level), and status
  - Status options: `Open`, `In Progress`, `Postponed`, `Completed`, `Delayed`
- **Category Management**:
  - Add, rename, and delete categories
  - Deletion also removes associated tasks and reminders
- **Priority Levels**:
  - Add, rename, delete levels (except the default)
  - On deletion, affected tasks are assigned the default level
- **Reminders**:
  - Types: 1 day/week/month before due date or user-defined date
  - Reminders auto-deleted for completed tasks
- **Status Auto-update**:
  - Tasks past due on launch change to `Delayed`
- **Search Functionality**:
  - Filter tasks by title, category, and priority

### Extra Functionalities
- **Backup & Restore**:
  - Backups JSON files to `medialab/backup/`
  - Restores backup on demand
- **Refresh Button**:
  - Reloads data and shows delayed task popup
- **Summary Panel**:
  - Displays: 
    - Total tasks
    - Completed tasks
    - Delayed tasks
    - Tasks due within 7 days

---

## Project Structure
src/ <br>
└── main/ <br>
└── java/com/taskmanager/ <br>
├── models/ <br>
├── controllers/ <br>
├── gui/ <br>
└── utils/ <br>
medialab/
├── Tasks.json <br>
├── Categories.json <br>
├── Priorities.json <br>
└── Reminders.json <br>


---

## 🧾 JSON Schema

- `Tasks.json`  
  Stores task info:  
  `taskId`, `title`, `description`, `category`, `priority`, `dueDate`, `status`, `reminders`

- `Categories.json`  
  Stores task categories with field: `name`

- `Priorities.json`  
  Stores priority levels: `Low`, `Medium`, `High`, `Urgent`, `Optional`

- `Reminders.json`  
  Stores reminder data with fields: `taskTitle`, `reminderDate`

---

## Technologies Used

- Java 22
- JavaFX 23.0.1
- Eclipse IDE for Java Developers (2024-09)
- Maven
- JSON file-based persistence

---

## How to Run

1. Clone the repository:
   ```bash
   git clone https://github.com/aleginxx/TaskManagementSystem.git
2. Open with Eclipse IDE.
3. Run `Main.java`.
4. Make sure the medialab/ directory exists with initial JSON files or empty versions.

## Notes
- All methods in at least one class are documented using javadoc.
- The implementation follows object-oriented principles and modular design.
- The app is localized for Greek usage but can be extended with i18n.
