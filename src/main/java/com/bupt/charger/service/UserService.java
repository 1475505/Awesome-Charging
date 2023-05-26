package com.bupt.charger.service;

import com.bupt.charger.common.UserLoginResponse;
import com.bupt.charger.entity.User;
import com.bupt.charger.exception.RegistrationException;
import com.bupt.charger.repository.UserRepository;
import com.bupt.charger.request.UserRegistrationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.security.auth.login.LoginException;

/**
 * @author ll （ created: 2023-05-26 19:38 )
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void registerUser(UserRegistrationRequest registrationRequest) throws RegistrationException {
        if (userRepository.existsByUsername(registrationRequest.getUsername())) {
            throw new RegistrationException("用户名已存在");
        }

        User user = new User();
        user.setUsername(registrationRequest.getUsername());
        user.setPassword(registrationRequest.getPassword());
        user.setCarId(registrationRequest.getCarId());

        userRepository.save(user);
    }

    public UserLoginResponse login(String username, String password) throws LoginException {
        User user = userRepository.findByUsernameAndPassword(username, password);
        if (user == null) {
            throw new LoginException("用户名或密码错误");
        }

        UserLoginResponse loginResponse = new UserLoginResponse();
        loginResponse.setUsername(user.getUsername());
        loginResponse.setCarId(user.getCarId());

        return loginResponse;
    }
}
