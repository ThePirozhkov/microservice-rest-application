package by.baby.usermicroservice.service;

import by.baby.dto.UserDto;
import by.baby.usermicroservice.exception.UnableToUpdateUserException;
import by.baby.spring.components.mapper.UserDtoMapper;
import by.baby.spring.components.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService implements by.baby.usermicroservice.service.Service<UserDto, Long> {

    private final UserRepository userRepository;
    private final UserDtoMapper userDtoMapper;

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll()
                .stream()
                .map(userDtoMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UserDto> findById(Long id) {
        return userRepository.findById(id)
                .map(userDtoMapper::mapToDto);
    }

    @Override
    public UserDto save(UserDto dto) {
        return userDtoMapper.mapToDto(userRepository.save(userDtoMapper.mapToEntity(dto)));
    }

    @Override
    public UserDto update(UserDto dto, Long id) {
        return userRepository.findById(id)
                .map(userEntity -> {
                    userEntity.setUsername(dto.getUsername());
                    userEntity.setAuthToken(dto.getAuthToken());
                    userEntity.setMoney(dto.getMoney());
                    userEntity.setCreatedAt(dto.getCreatedAt());
                    return userEntity;
                })
                .map(userRepository::save)
                .map(userDtoMapper::mapToDto)
                .orElseThrow(() -> new UnableToUpdateUserException("Unable to update user"));
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
}
