package com.hrizzon2.demotest.security;

public interface ISecurityUtils {

    String getRole(AppUserDetails userDetails);

    String generateToken(AppUserDetails userDetails);

    String getSubjectFromJwt(String jwt);
}
