# 🎬 MyMovieList

![Version](https://img.shields.io/badge/version-v2-blue)
![Java](https://img.shields.io/badge/java-%23ED8B00.svg)
![MySQL](https://img.shields.io/badge/mysql-%2300f.svg)
![Hibernate](https://img.shields.io/badge/Hibernate-59666C)
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg)
![License](https://img.shields.io/badge/license-MIT-green)

## ℹ️ Overview
This is my second portfolio project and serves as my Capstone Project for my Advanced Vocational Training (CFGS) in 
Multiplatform Applications Development. The original version was nearly complete but outdated, so I'm rebuilding it 
from scratch.

The project is called **MyMovieList**. It's based on the famous anime/manga web page [MyAnimeList](https://myanimelist.net). The app 
allows users to create personalized lists of movies and TV shows to keep track of what they’ve watched or plan to watch.  
It also provides detailed information about each title using data from the TMDB API.

## ⭐ Highlights

Here are some highlights of the project:

* **Client-Server Architecture:** A distributed system featuring a dedicated backend and a desktop client.
* **Custom Socket Communication:** Implements a low-level communication protocol using Java Sockets for real-time 
data exchange. 
* **Secure Session Management:** Features a custom authentication system with session persistence and refresh logic.
* **Advanced Security:** User credentials are secured using modern hashing algorithm (**SHA**) to ensure data privacy.
* **API Usage:** Integration with the TMDB API to provide detailed information about movies or series.
* **Event-Driven UI:** Built with an **EventBus (Publisher-Subscriber)** pattern to ensure decoupled and reactive 
communication between Swing components.
* **Multi-threaded Server:** The backend is designed to handle multiple concurrent client connections efficiently.
* **ORM & Persistence:** Persistence managed by **Hibernate** and **MySQL**.
* **Resilient Containerization:** Fully containerized with **Docker** and **Docker Compose**, featuring automated 
healthchecks and database connection retries for seamless deployment.

## 🛠️ Tech Stack

* **Language:** Java 23
* **ORM:** Hibernate
* **Database engine:** MySQL
* **Container platform:** Docker / Docker Compose
* **Build tool:** Maven

## 🚀 Setup Instructions

1. Install Docker Desktop and have the docker daemon running.
2. Clone the repository: 
    ```bash
    git clone https://github.com/Josegs95/MyMovieList.git
    cd MyMovieList
    ```
3. Rename `.env.example` file to `.env` and adjust it. [^1]
4. Launch the server: [^2]
    ```bash
    docker compose up --build
    ``` 
5. Launch the client:
    - Download the latest `MyMovieList-Client.jar` in the [Releases](https://github.com/Josegs95/MyMovieList/releases) 
    section.
    - **Important:** Place the `.jar` file in the root directory of the project (where the `.env` file is located).
    - Run the application from that directory:
    ```bash 
    java -jar MyMovieList-Client.jar
    ```

[^1]: You can obtain the API keys for free at TMDB Settings
[^2]: You must have the Docker daemon running. Start Docker Desktop to launch it.

## 📚 Credits & References

### Powered by
- **[TMDB API:](https://themoviedb.org)** The source of all movies and series data.

### Libraries & Resources
- **[Jackson](https://github.com/FasterXML/jackson):** High-performance JSON processor for Java.
- **[DotEnv Java](https://github.com/cdimascio/dotenv-java):** For environment variable management.
- **[MigLayout](https://github.com/mikaelgrev/miglayout):** A flexible and powerful Swing layout manager.
- **[StretchIcon](https://github.com/tips4java/tips4java/blob/main/source/StretchIcon.java):** A utility class for 
smart image scaling in Swing.

### Acknowledgement
- AI Support: Used **[ChatGPT](https://chatgpt.com/)** and **[Gemini](https://gemini.google.com/)** for architectural brainstorming, 
documentation refinement, and troubleshooting.

