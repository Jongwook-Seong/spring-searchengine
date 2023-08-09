package sjw.spring.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sjw.spring.domain.user.User;
import sjw.spring.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;

    /**
     * @return null 로그인 실패
     */
    public User login(String loginId, String password) {

        List<User> loginIdList = userRepository.findUserByLoginId(loginId);
        for (User user : loginIdList) {
            if (user.getPassword().equals(password))
                return user;
        }
        return null;
    }
}
