# 🚚 Railse Workforce Management API

Welcome to the Workforce Management API — built as part of the **Railse Backend Engineer Challenge**. This application helps managers assign and track tasks for staff (salespeople, operations, etc.) and includes features like task creation, updates, priorities, activity history, and more.

---

## 📁 Project Structure

src/main/java/com/railse/workforcemgmt/
├── Application.java
├── controller/
│ └── TaskManagementController.java
├── service/
│ └── TaskmanagementService.java
├── service/impl/
│ └── TaskmanagementServiceImpl.java
├── model/
│ ├── TaskManagement.java
│ ├── TaskComment.java
│ └── TaskActivityLog.java
├── dto/
│ ├── TaskManagementDto.java
│ ├── TaskCreateRequest.java
│ ├── UpdateTaskRequest.java
│ ├── AssignByReferenceRequest.java
│ ├── TaskPriorityUpdateRequest.java
│ ├── TaskCommentRequedt.java
│ └── TaskFetchByDateRequest.java
├── enums/
│ ├── TaskStatus.java
│ ├── Priority.java
│ ├── Task.java
│ └── ReferenceType.java
├── common/exception/
│ ├── customExceptionHandler.java
│ ├── ResourceNotFoundException.java
│ └── StatusCode.java
├── common/model/response
│ ├── pagination.java
│ ├── Response.java
│ └── ResponseStatus.java
├── mapper/
│ └── ITaskManagementMapper.java
├── dto/
│ ├── InMemoryTaskActivityRepository.java
│ ├── InMemoryTaskCommentRepository.java
│ ├── InMemoryTaskRepository.java
│ ├── TaskActivityLogRepository.java
│ ├── TaskCommentRepository.java
└── TaskRepository.java



---

## 🧰 Tech Stack

- **Java 17**
- **Spring Boot 3.0.4**
- **maven**
- **Lombok**
- **MapStruct**
- **In-Memory Java Collections (no DB)**

---

## 🚀 How to Run

```bash
# Clone the repository
git clone https://github.com/your-username/railse-workforce-mgmt-api.git
cd railse-workforce-mgmt-api

# Build and run
./maven bootRun

---
🐞 Bug Fixes Implemented
✅ Bug 1: Task Reassignment Creates Duplicates
Problem: When reassigning a task using /assign-by-ref, old tasks weren't removed, creating duplicates.
Fix: Existing tasks are now marked as CANCELLED before assigning a new one.

✅ Bug 2: Cancelled Tasks Clutter the View
Problem: The /fetch-by-date endpoint included cancelled tasks.
Fix: Cancelled tasks are now excluded from fetch results.

✨ New Features
🔍 Feature 1: Smart Daily Task View
Enhanced the /fetch-smart endpoint to return:

Tasks that started within the date range AND

Tasks that started before the range but are still ACTIVE

📊 Feature 2: Task Prioritization
Added Priority enum: LOW, MEDIUM, HIGH

Tasks can be assigned a priority at creation

Added endpoint to update priority:
POST /task-mgmt/update-priority

Added endpoint to fetch by priority:
GET /task-mgmt/tasks/priority/{priority}

💬 Feature 3: Comments & Activity History
Every task now tracks:

Creation

Status updates

Priority changes

Comments can be added by users

GET /task-mgmt/{id} returns complete task info with:

Activity log (auto-generated)

Comments (user-submitted)
POST /task-mgmt/task/comment adding comment to task Id
---
👨‍💻 Author
Name: Pothina Venkata Sai Jaswanth Kumar
Email: pothinajaswanthkumar@gmail.com
GitHub: [github.com/Jaswanth778012]

✅ Submission Checklist
 Project set up using Spring Boot with proper structure

 Fixed both reported bugs

 Implemented all 3 new features

 Submitted GitHub link with full code

 Uploaded video demonstration

