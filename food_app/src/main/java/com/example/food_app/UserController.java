package com.example.food_app;

import com.example.food_app.User;
import com.example.food_app.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    private User loggedInUser = null;

//    // Registration Endpoint
//    @PostMapping("/register")
//    public String register(@RequestBody User user) {
//        userRepository.save(user);
//        return "User registered successfully!";
//    }


    @PostMapping("/register")
    public String register(
            @RequestBody User user,
            @RequestParam(required = false) MultipartFile profilePicture
    ) {
        // Save the profile picture
        String picturePath = null;
        if (profilePicture != null && !profilePicture.isEmpty()) {
            picturePath = saveProfilePicture(profilePicture);
        }

        // Set profile picture if uploaded
        user.setProfilePicture(picturePath);

        // Save the user to the repository
        userRepository.save(user);

        return "User registered successfully!";
    }

    private String saveProfilePicture(MultipartFile file) {
        try {
            // Save the file to a specific directory
            String filePath = "/uploads/" + file.getOriginalFilename();
            file.transferTo(new java.io.File(filePath));
            return filePath; // Return the file path
        } catch (Exception e) {
            e.printStackTrace();
            return "Error uploading file.";
        }
    }


    @PostMapping("/login")
    public Object login(@RequestParam String phoneNumber, @RequestParam String password) {
        Optional<User> user = userRepository.findByPhoneNumberAndPassword(phoneNumber, password);
        if (user.isPresent()) {
            String token = JwtUtil.generateToken(phoneNumber); // Generate JWT
            return "Login successful! Your token: " + token;
        } else {
            return "Invalid phone number or password.";
        }
    }

    // Home Endpoint
    @GetMapping("/home")
    public Object home(@RequestHeader("Authorization") String token) {
        try {
            String phoneNumber = JwtUtil.validateToken(token.replace("Bearer ", "")); // Validate token
            Optional<User> user = userRepository.findByPhoneNumber(phoneNumber);
            if (user.isPresent()) {
                return user.get(); // Return the User object if found
            } else {
                return "User not found."; // Return a message if the user is not found
            }
        } catch (Exception e) {
            return "Invalid or expired token. Please login again.";
        }
    }
}
