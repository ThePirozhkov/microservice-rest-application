package by.baby.spring.components.service;

import by.baby.dto.UserDto;
import by.baby.exception.NotFoundException;
import by.baby.spring.components.mapper.UserDtoMapper;
import by.baby.spring.components.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService implements by.baby.spring.components.service.Service<UserDto, Long> {

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
    public Optional<UserDto> save(UserDto dto) {
        return Optional.of(userDtoMapper.mapToDto(userRepository.save(userDtoMapper.mapToEntity(dto))));
    }

    @Override
    public Optional<UserDto> update(UserDto dto, Long id) {
        return userRepository.findById(id)
                .map(userEntity -> {
                    if (dto.getUsername() != null)
                        userEntity.setUsername(dto.getUsername());
                    if (dto.getAuthToken() != null)
                        userEntity.setAuthToken(dto.getAuthToken());
                    if (dto.getMoney() != null)
                        userEntity.setMoney(dto.getMoney());
                    return userEntity;
                })
                .map(userRepository::save)
                .map(userDtoMapper::mapToDto);
    }

    @Override
    public boolean deleteById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("User with id " + id + " not found");
        }
        userRepository.deleteById(id);
        return !userRepository.existsById(id);
    }
}
