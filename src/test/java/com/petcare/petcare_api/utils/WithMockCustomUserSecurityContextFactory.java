package com.petcare.petcare_api.utils;

import com.petcare.petcare_api.coredomain.model.user.User;
import com.petcare.petcare_api.coredomain.model.user.enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.stereotype.Component;

@Component
public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

    @Autowired
    private UserTestFactory userTestFactory;

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
        User user = userTestFactory.persistUser(UserRole.USER, customUser.email(), customUser.cpf());

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                user, user.getPassword(), user.getAuthorities());
        context.setAuthentication(authentication);

        return context;
    }
}