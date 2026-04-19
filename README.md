# Android Book Store Application Documentation

## Project Overview
This Android Book Store application is designed to provide users with an intuitive and easy-to-use platform for browsing and purchasing books. It features a modern user interface and integrates with a backend database to manage book information, user profiles, and transactions.

## Features
- User Registration & Login
- Browse Books by Category
- Search Functionality
- Book Details View
- Add Books to Cart
- Purchase and Payment Integration
- User Profile Management
- Review and Rating System

## Technology Stack
- **Frontend:** Android SDK, Java/Kotlin
- **Backend:** Node.js, Express.js
- **Database:** MongoDB
- **Version Control:** Git

## Database Schema
- **Users:** `id`, `username`, `password`, `email`, `profile_image`
- **Books:** `id`, `title`, `author`, `price`, `category`, `description`, `image_url`
- **Orders:** `id`, `user_id`, `book_ids`, `order_date`, `total_amount`

## Installation Instructions
1. Clone the repository:
   ```bash
   git clone https://github.com/yasserelfar/OnlineBookStore.git
   ```
2. Navigate to the project directory:
   ```bash
   cd OnlineBookStore
   ```
3. Install required dependencies:
   ```bash
   npm install
   ```
4. Configure your environment variables as needed.
5. Launch the application:
   ```bash
   npm start
   ```

## Usage Guide
- Open the application on your Android device.
- Register a new account or log in to your existing account.
- Browse or search for books and add them to your cart.
- Proceed to checkout to complete your purchase.

5. Open a pull request.
