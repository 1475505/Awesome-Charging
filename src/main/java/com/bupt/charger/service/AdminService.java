package com.bupt.charger.service;
import com.bupt.charger.entity.Admin;
import com.bupt.charger.response.AdminLoginResponse;
import com.bupt.charger.response.UserLoginResponse;
import com.bupt.charger.entity.User;
import com.bupt.charger.exception.ApiException;
import com.bupt.charger.repository.AdminRepository;
import com.bupt.charger.request.UserRegistrationRequest;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.security.auth.login.LoginException;

/**
 * @author wyf （ created: 2023-05-26 13:27 )
 */
@Service
@Log
public class AdminService {
    @Autowired
    private AdminRepository adminRepository;

    public AdminLoginResponse login(String adminName, String password) throws LoginException {
        log.info("Admin try to login: " + adminName);
        Admin admin = adminRepository.findByAdminNameAndPassword(adminName,password);
        if (admin == null) {
            throw new LoginException("用户名或密码错误");
        }

        AdminLoginResponse loginResponse = new AdminLoginResponse();
        loginResponse.setAdminName(admin.getAdminName());

        return loginResponse;
    }
}
