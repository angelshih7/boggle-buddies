Here is where you will explain your plan for the Walking Skeleton.

We will talk more about this in the future. In summary, the Walking Skeleton is a plan for setting up a minimal version of your tech stack. This is less than a MVP (minimum viable product) as this is not meant to be a product. It is to prove that you are able to integrate the three main components of your application: front end, back end, and database. 

To complete the Skeleton you must be able to interact with your front end, have that interaction be sent to your backend, have something be stored in your database, and return a result back to the front end. This feature does not have to be particularly powerful or meaningful, but you must prove that you can communicate between each component of your application.

# Boggle Your Mind Project

## Project name: Boggle your Mind

### 1. Overview
Implement an application for the game **Boggle**. The application should include algorithms to (1) randomly shuffle the letter dice to generate a board and (2) check whether submitted words are valid.

The application should support **real-time multiplayer** games with other users, as well as games where a user can compete against the **machine**. Users should be able to play as a **guest** or create an **account**.

Core gameplay features include tracking and displaying **user stats** (e.g., wins/losses), showing each player’s **word list** at the end of the game, and **highlighting unique words**. Games should include a **timer**, and the application should allow **customizable game settings** and board options (within project scope).

### 2. Scope

#### 2.1 Must-have (MVP)
- **Account registration** (authentication)
- **Login** 
- **Guest PLay**
- **Real-time multiplayer** 
- **Play vs Machine**
- **Settings Customization**
- **Stats display**
- **Display words and highlight unique words**

#### 2.2 Extra/Out of Scope
- **Share custom boards**
- **Machine difficulty setting** (applicable to board and machine)
- **Ranking system**
- **multi-alphabet**

### 3. High-Level Requirements

#### 3.1 Gameplay
- Generate board with shuffling algorithm.
- Add/Removing words
- Word validation (Word should exist in dictionary)
- Game Rule check algorithm
    1.  Duplicate words not allowed
    2.  No using the same letter twice
    3.  Word length 3 or greater
    4.  Words have to be diagonally or side by side to connect.
- Scoring Screen
    1.  Show each player's submitted words
    2.  highlights **Unique** vs **Shared** entries
    3.  display total score per player and winner/loser/ties

#### 3.2 Multiplayer
- Users can create/join game rooms
- Real-time game state sync

#### 3.3 Accounts + stats
- Registered users can view stats
- Guests can play without saving long-term stats

### 4. Tech Stack

#### Frontend
- React/React Native

#### Backend
- Java
- Spring Boot
- REST API 
- Real-time communication **TBD**

#### Database
- SQL
- **Cloud base TBD**

#### Tooling
- Docker (development consistency)
- Gitlab (Versioning)
- Postman (API Testing)
- JUNIT + Mockito (backend unit/integration tests)

#### Source
- **Dictionary database TBD**





