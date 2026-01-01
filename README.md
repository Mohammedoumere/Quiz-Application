# Quiz Application

A comprehensive JavaFX-based Quiz Application that uses a Client-Server architecture with RMI (Remote Method Invocation). This application allows users to take quizzes generated automatically from various document formats.

## Features

### Client-Side
*   **User Authentication**: Secure Sign Up and Sign In functionality.
*   **Dashboard**: View available courses and question types.
*   **Quiz Generation**: Automatically generates questions from uploaded course materials.
*   **Quiz Modes**:
    *   Multiple Choice
    *   True/False
    *   Fill in the Blank
    *   Workout (Self-reflection)
*   **Profile Management**: Update profile picture and change password.
*   **Settings**:
    *   Toggle Light/Dark themes.
    *   Enable/Disable sound effects.
    *   Manage notifications.

### Server-Side
*   **RMI Services**: Exposes `AuthService` and `QuizService` for client communication.
*   **Data Persistence**: Stores users and courses data in `.dat` files.
*   **Document Parsing**: Extracts text from `.txt`, `.docx`, `.pptx`, and `.pdf` files to generate questions.
*   **Question Generation Logic**: Algorithms to create different types of questions from raw text.

## Project Structure

The project is organized into two main packages:

*   `com.client`: Contains all JavaFX controllers, UI logic, and the main application entry point.
*   `com.server`: Contains the RMI server implementation, data models, and business logic.

## Prerequisites

*   Java Development Kit (JDK) 8 or higher.
*   Maven (for dependency management).

## How to Run

### 1. Start the Server
The server must be running first to handle authentication and data requests.

1.  Navigate to `com.server.ServerMain`.
2.  Run the `main` method.
3.  The console should display: `Quiz and Auth RMI Services are running...`

### 2. Start the Client
Once the server is up:

1.  Navigate to `com.client.QuizApp`.
2.  Run the `main` method.
3.  The Login screen will appear.

## Configuration

*   **Server IP**: By default, the client connects to `192.168.56.1`. You may need to update the `SERVER_IP` constant in `QuizApp.java`, `MainController.java`, and other controllers if running on a different network configuration.
*   **File Support**: Ensure you have the necessary libraries (Apache POI, PDFBox) in your `pom.xml` to support document parsing.

## Technologies Used

*   **JavaFX**: For the User Interface.
*   **Java RMI**: For Client-Server communication.
*   **Apache POI**: For parsing Word (.docx) and PowerPoint (.pptx) files.
*   **Apache PDFBox**: For parsing PDF files.
