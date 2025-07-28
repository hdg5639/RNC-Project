package serv.rnc.rncserver.serivce;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import serv.rnc.rncserver.dto.user.SignInDTO;
import serv.rnc.rncserver.dto.user.UserDTO;
import serv.rnc.rncserver.dto.user.UserDetailDTO;
import serv.rnc.rncserver.entity.User;
import serv.rnc.rncserver.repository.UserRepository;
import serv.rnc.rncserver.serivce.impl.UserService;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public void signup(UserDetailDTO userDetailDTO) {
        if (userRepository.existsByEmail(userDetailDTO.getEmail())) {
            throw new IllegalStateException("동일한 이메일이 이미 존재합니다.");
        }
        if (userDetailDTO.getUsername() == null || userDetailDTO.getUsername().isEmpty()) {
            throw new IllegalArgumentException("User name cannot be null or empty");
        }
        if (userDetailDTO.getEmail() == null || userDetailDTO.getEmail().isEmpty()) {
            throw new IllegalArgumentException("User email cannot be null or empty");
        }
        if (userDetailDTO.getPassword() == null || userDetailDTO.getPassword().isEmpty()) {
            throw new IllegalArgumentException("User password cannot be null or empty");
        }

        userRepository.save(User.builder()
                        .email(userDetailDTO.getEmail())
                        .username(userDetailDTO.getUsername())
                        .password(bCryptPasswordEncoder.encode(userDetailDTO.getPassword()))
                        .role(userDetailDTO.getRole())
                        .build());
    }
}
