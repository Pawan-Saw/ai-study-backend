package ai_study_backend.controller;

import ai_study_backend.model.User;
import ai_study_backend.repository.UserRepository;
import ai_study_backend.util.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    private final UserRepository userRepo;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthController(UserRepository userRepo, JwtUtil jwtUtil) {
        this.userRepo = userRepo;
        this.jwtUtil = jwtUtil;
    }

    // ✅ SIGNUP
    @PostMapping("/signup")
    public Map<String, String> signup(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");
        String name = body.get("name");

        if (userRepo.findByEmail(email).isPresent()) {
            return Map.of("error", "Email already exists!");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(encoder.encode(password));
        user.setName(name);
        userRepo.save(user);

        String token = jwtUtil.generateToken(email);
        return Map.of("token", token, "name", name, "email", email);
    }

    // ✅ LOGIN
    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");

        Optional<User> userOpt = userRepo.findByEmail(email);
        if (userOpt.isEmpty()) {
            return Map.of("error", "User not found!");
        }

        User user = userOpt.get();
        if (!encoder.matches(password, user.getPassword())) {
            return Map.of("error", "Wrong password!");
        }

        String token = jwtUtil.generateToken(email);
        return Map.of("token", token, "name", user.getName(), "email", email);
    }
}