package by.baby.usermicroservice.controller.rest;

import by.baby.dto.UserDto;
import by.baby.exception.UnableToCreateException;
import by.baby.exception.NotFoundException;
import by.baby.exception.UnableToDeleteException;
import by.baby.exception.UnableToUpdateException;
import by.baby.spring.components.repository.UserRepository;
import by.baby.spring.components.service.UserService;
import by.baby.util.HttpResponseBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserRestController {

    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        return HttpResponseBuilder.buildResponse(HttpStatus.OK, userRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long id) {
        return HttpResponseBuilder.buildResponse(HttpStatus.OK, userService.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found")));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createUser(@Valid @RequestBody UserDto userDto,
                                                          BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return HttpResponseBuilder.buildErrorResponse(bindingResult, HttpStatus.BAD_REQUEST);
        }
        return HttpResponseBuilder.buildResponse(HttpStatus.CREATED, userService.save(userDto)
                .orElseThrow(() -> new UnableToCreateException("Unable to create user: " + userDto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable Long id,
                                                          @RequestBody UserDto userDto) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("Unable to find user by id: " + id);
        }
        userDto.setId(id);
        return HttpResponseBuilder.buildResponse(HttpStatus.OK, userService.update(userDto, id)
                .orElseThrow(() -> new UnableToUpdateException("Unable to update user: " + userDto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteUserById(@PathVariable Long id) {
        if (userService.deleteById(id)) {
            return HttpResponseBuilder.buildResponse(HttpStatus.NO_CONTENT, "User deleted successfully");
        } else {
            throw new UnableToDeleteException("Unable to delete user: " + id);
        }
    }

}
