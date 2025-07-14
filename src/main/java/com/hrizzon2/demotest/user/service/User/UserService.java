package com.hrizzon2.demotest.user.service.User;

import com.hrizzon2.demotest.user.model.User;

import java.util.Optional;

public interface UserService {


    boolean existsByEmail(String email);

    User save(User user);


    boolean validateEmail(String token);

    void changePasswordFirstLogin(String email, CharSequence newPassword);

    Optional<User> findByEmail(String email);

    void requestPasswordReset(String email);

    void resetPassword(String token, String newPassword);
}
