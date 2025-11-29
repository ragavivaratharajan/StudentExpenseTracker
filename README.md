# Student Expense Tracker

A **Java console-based application** to help students efficiently track, categorize, and analyze their expenses while maintaining a personal monthly budget by generating expense reports.

---

## Features
- Create or load a user profile with saved budget
- Add categorized expenses (Food, Travel, Education, Rent & Utilities, Entertainment, Miscellaneous)
- View session-wise and cumulative reports
- Detect and handle budget exceedance dynamically
- Persistent storage of user data and reports
- Text report generation

---

## Architecture Overview

- **User.java** – Manages user information and cumulative budget tracking  
- **ExpenseManager.java** – Handles expense logic, validation, and reporting  
- **Expense.java** (sealed) – Base class for all expense types  
- **ExpenseReport.java** (record) – Immutable report summary  
- **ExpenseUtils.java** – Helper functions for formatting and file operations  
- **Custom Exceptions:** `InvalidExpenseException`, `BudgetExceededException`  

UML Diagram:
<img width="960" height="409" alt="image" src="https://github.com/user-attachments/assets/577eb615-3952-4f7f-ab2b-dcb88731106f" />

---

## Tech Stack
- **Language:** Java 21 and features of Java 22 
- **IDE:** Eclipse IDE  
- **Version Control:** Git & GitHub  
- **Libraries:** Java Standard Library only  

---

## How to Run
1. Clone the repository:
   ```bash
   git clone https://github.com/ragavivaratharajan/StudentExpenseTracker.git
