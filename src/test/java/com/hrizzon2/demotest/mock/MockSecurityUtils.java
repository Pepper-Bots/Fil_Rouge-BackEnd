package com.hrizzon2.demotest.mock;

import com.hrizzon2.demotest.security.AppUserDetails;
import com.hrizzon2.demotest.security.ISecurityUtils;

public class MockSecurityUtils implements ISecurityUtils {


    private String nomRole;

    public MockSecurityUtils(String nomRole) {
        this.nomRole = nomRole;
    }

    @Override
    public String getRole(AppUserDetails userDetails) {
        return nomRole;
    }

    @Override
    public String generateToken(AppUserDetails userDetails) {
        return "";
    }

    @Override
    public String getSubjectFromJwt(String jwt) {
        return "";
    }
}