# Image Management System

This project is an Image Management System built with Java Spring Boot, featuring user authentication and various image processing functionalities. User data and image metadata are stored in a MySQL database, while images are stored on the server. The project utilizes Java's Graphics2D and the Imgscalr library for image transformations.

## Features

### User Authentication
- **Sign-Up**: Users can create an account.
- **Log-In**: Users can log into their account.
- **JWT Authentication**: Secure endpoints using JWT tokens for authenticated access.

### Image Management
- **Upload Image**: Users can upload images to the server.
- **Transform Image**: Users can perform various transformations on uploaded images, including:
  - Resize
  - Crop
  - Rotate
  - Watermark
  - Flip
  - Mirror
  - Compress
  - Change format (JPEG, PNG, etc.)
  - Apply filters (grayscale, sepia, etc.)
- **Retrieve Image**: Users can retrieve a saved image in different formats.
- **List Images**: Users can view a list of all uploaded images along with metadata.

## Requirements
- Java 11 or higher
- Spring Boot
- MySQL Database
- Imgscalr library

## Usage

- Access the API endpoints for user authentication and image management.
- Use tools like Postman to test the endpoints.

## Contributing

Contributions are welcome! Please open an issue or submit a pull request to improve the project.

## Project Idea 
This project is solution to the following project idea https://roadmap.sh/projects/image-processing-service
