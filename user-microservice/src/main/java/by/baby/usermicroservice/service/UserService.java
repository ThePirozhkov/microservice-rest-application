package by.baby.usermicroservice.service;

import by.baby.usermicroservice.dto.UserDto;
import by.baby.usermicroservice.mapper.UserDtoMapper;
import by.baby.usermicroservice.persistence.entity.UserEntity;
import by.baby.usermicroservice.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements by.baby.usermicroservice.service.Service<UserDto, Long> {

    private final UserRepository userRepository;
    private final UserDtoMapper userDtoMapper;

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll()
                .stream()
                .map(userEntity -> userDtoMapper.mapToDto(userEntity)
                        .orElseThrow(() -> new RuntimeException("User not found")))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UserDto> findById(Long id) {
        return userDtoMapper.mapToDto(userRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("User not found")));
    }

    @Override
    public UserDto save(UserDto dto) {
        UserEntity userEntity = userRepository.save(userDtoMapper.mapToEntity(dto));
        return userDtoMapper.mapToDto(userEntity)
                .orElseThrow(() -> new RuntimeException("Unable to save user"));
    }

    @Override
    public UserDto update(UserDto fromDto, UserDto toDto) {
        UserEntity newDto = Optional.of(toDto)
                .map(dto -> {
                    dto.setUsername(fromDto.getUsername());
                    dto.setAuthToken(fromDto.getAuthToken());
                    dto.setMoney(fromDto.getMoney());
                    dto.setCreatedAt(fromDto.getCreatedAt());
                    return dto;
                })
                .map(userDtoMapper::mapToEntity)
                .map(userRepository::save)
                .orElseThrow(() -> new RuntimeException("Unable to update user"));
        return userDtoMapper.mapToDto(newDto)
                .orElseThrow(() -> new RuntimeException("Unable to map user"));
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
        if (userRepository.existsById(id)) {
            throw new RuntimeException("Unable to delete user");
        }
    }
}
