# ComPair - Product Comparison App

ComPair is an Android application that allows users to compare products from various e-commerce platforms like Amazon, Flipkart, and Meesho. Users can search for products, view detailed comparisons, save items to their wishlist, and navigate seamlessly within the app.

## Features

✅ **Product Search** - Search for products from different e-commerce platforms.
✅ **Product Comparison** - Compare specifications and prices using extracted product details.
✅ **Wishlist (Cart)** - Save products for future reference, synced with Firestore.
✅ **User Authentication** - Secure user login and authentication.
✅ **Navigation Drawer** - Access different sections of the app via a right-side navigation drawer.
✅ **Bottom Navigation** - Quick access to Home, Search, and Profile.
✅ **Dark Mode Support** - Adapts to system-wide dark mode settings.

## Tech Stack

- **Android Studio** - Development environment
- **Java** - Primary programming language
- **XML** - UI Design
- **Firestore** - Database for product links and user data
- **DeepSeek API** - Fetching product details from e-commerce websites
- **SFML 3.0.0** - Used for additional processing

## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/your-repo/ComPair.git
   ```
2. Open the project in **Android Studio**.
3. Sync Gradle dependencies.
4. Configure Firebase and Firestore.
5. Run the application on an emulator or a physical device.

## Folder Structure
```
ComPair/
│── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/compair/
│   │   │   │   ├── activities/  # Activities like Home, Login, Settings
│   │   │   │   ├── fragments/   # Fragments like Profile, Search, Wishlist
│   │   │   │   ├── adapters/    # RecyclerView adapters
│   │   │   │   ├── models/      # Data models for products and users
│   │   │   ├── res/
│   │   │   │   ├── layout/      # XML layouts
│   │   │   │   ├── values/      # Colors, Strings, Themes
```

## Usage

1. **Login/Register** - Create an account or log in with existing credentials.
2. **Search Products** - Enter keywords to search for products.
3. **Compare Products** - Select two products to compare their specifications.
4. **Save to Wishlist** - Bookmark products for later reference.
5. **Navigate Easily** - Use bottom navigation and drawer menu for quick access.

## Contributing

1. Fork the repository.
2. Create a new branch: `git checkout -b feature-branch`
3. Make your changes and commit: `git commit -m 'Add new feature'`
4. Push to the branch: `git push origin feature-branch`
5. Open a pull request.

## License

This project is licensed under the MIT License. Feel free to use and modify it as needed.

## Contact

For any queries or collaboration, reach out at: **your-email@example.com**

