package by.baby.usermicroservice.controller.rest;

import by.baby.usermicroservice.dto.UserDto;
import by.baby.usermicroservice.exception.UnableToDeleteUserException;
import by.baby.usermicroservice.exception.UserNotFoundException;
import by.baby.usermicroservice.persistence.repository.UserRepository;
import by.baby.usermicroservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserRestController {

    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Unable to find user by id: " + id)));
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.save(userDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id,
                                              @RequestBody UserDto userDto) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("Unable to find user by id: " + id);
        }
        return ResponseEntity.ok(userService.update(userDto, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable Long id) {
        if (userRepository.existsById(id)) {
            userService.deleteById(id);
        } else {
            throw new UserNotFoundException("Unable to delete user by id: " + id);
        }
        if (userRepository.existsById(id)) throw new UnableToDeleteUserException("Unable to delete user by id: " + id);
        return ResponseEntity.noContent().build();
    }

}
