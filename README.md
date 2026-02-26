# 🍽️ Food Planner App (Android - MVP + Clean Architecture)

A modern Android Food Planner application built using **Java**, following **MVP + Clean Architecture**, that helps users discover meals, search recipes, save favorites, and plan their food experience using TheMealDB API.

> Inspired by apps like SideChef: Recipes & Meal Plans
> Designed for scalability, maintainability, and professional Android standards.

---

# 📱 Features

## 🔐 Authentication (Room + SharedPreferences)

* User Registration (stored locally using Room)
* Secure Login system
* Persistent session using SharedPreferences
* Auto-login after successful authentication
* Delete Account (removes user + favorites from database)

---

## 🍲 Home Screen

* Random Meal of the Day (from API)
* Categories horizontal list
* Clickable Random Meal → opens Meal Details
* Swipe-to-refresh support
* Modern UX with dynamic content loading

---

## 🔍 Search Screen

* Search bar with real-time filtering
* Displays meals by default
* Live search using TheMealDB API
* Click any result → navigate to Meal Details

---

## ❤️ Favorites System (Offline Support)

* Add / Remove favorite meals
* Stored locally using Room Database
* User-specific favorites (per account)
* Favorites screen with modern card UI
* Works offline (no network required)

---

## 📄 Meal Details Screen

Displays:

* Meal Name
* Meal Image
* Country of Origin
* Ingredients (structured display)
* Cooking Instructions
* Embedded YouTube Video (playable inside app)
* Favorite toggle icon (lights when saved)

---

## 👤 Profile Screen

* Displays logged-in user data (name & email)
* Logout functionality
* Delete account (with confirmation dialog)
* Clean Material UI following UX standards

---

## ✨ Splash Screen

* Lottie animation
* Session check
* Smart navigation (Login or Home)

---

# 🧱 Architecture & Design Pattern

This project follows **Clean Architecture + MVP (Model View Presenter)**

### 📂 Project Structure

```
com.example.foodplanner
│
├── data
│   ├── model (POJOs & API Response Classes)
│   ├── network (Retrofit + API Services)
│   ├── datasource (Local + Remote)
│   ├── db (Room Database + Entities + DAO)
│   └── repository
│
├── presentation
│   ├── splash (View + Presenter)
│   ├── login
│   ├── register
│   ├── home
│   ├── search
│   ├── favorites
│   ├── mealdetails
│   └── profile
│
└── utils (SessionManager, constants, etc.)
```

### Why MVP?

* Separation of concerns
* Testable business logic
* Cleaner UI layer
* Required by project specifications

---

# 🛠️ Tech Stack & Libraries

### Core

* Java (Android)
* Android Jetpack Navigation (Safe Args)
* ViewBinding (No findViewById)

### Networking

* Retrofit
* Gson
* TheMealDB API

### Database

* Room Database (Local Storage)
* RxJava3 (Reactive Streams with Room)

### UI/UX

* Material Design (Material 3)
* Lottie Animations (Splash)
* RecyclerView (Lists)
* Glide (Image Loading)
* SwipeRefreshLayout

---

# 🌐 API Used

The app uses **TheMealDB Free API**:

* Random Meal:
  `https://www.themealdb.com/api/json/v1/1/random.php`
* Search Meals:
  `https://www.themealdb.com/api/json/v1/1/search.php?s=`
* Categories:
  `https://www.themealdb.com/api/json/v1/1/categories.php`
* Meal Details:
  `https://www.themealdb.com/api/json/v1/1/lookup.php?i=`

---

# 🧭 Navigation Architecture

* Single Activity Architecture
* Multiple Fragments
* Navigation Component (NavGraph)
* Safe Args for type-safe navigation
* Bottom Navigation (Home, Search, Favorites, Profile)
* Back button behaves correctly across all screens

---

# 💾 Offline Capabilities

* Favorites stored locally in Room
* User accounts stored locally
* Data accessible without internet connection

---

# 🚀 How to Run the Project

## 1️⃣ Clone the Repository

```bash
git clone https://github.com/YOUR_USERNAME/food-planner-app.git
```

## 2️⃣ Open in Android Studio

* Recommended: Android Studio Narwhal / Hedgehog+
* Language: Java
* Min SDK: 21+

## 3️⃣ Sync Gradle

```
File → Sync Project with Gradle Files
```

## 4️⃣ Run the App

* Use Emulator or Physical Device
* Internet required for API features

---

# 🧪 Testing Checklist (For Evaluators)

* Register a new user
* Login and verify session persistence
* View random meal
* Click meal → details screen
* Add to favorites
* Check favorites screen (offline)
* Search for meals
* Logout and login again
* Delete account and verify database cleanup

---

# 📌 Project Requirements (Satisfied)

✔ Room Database
✔ RxJava (Mandatory)
✔ MVP Design Pattern
✔ Clean Architecture
✔ Retrofit + Gson
✔ Material Design UI
✔ Lottie Splash Animation
✔ SharedPreferences (Session Management)
✔ Single Activity + Fragments Navigation

---

# 👨‍💻 Author

**Mina Wagdy**
Android Developer
Built as part of Android Development training & project evaluation.

---

# 📄 License

This project is for educational purposes and academic evaluation.
Feel free to fork and learn from the architecture.
