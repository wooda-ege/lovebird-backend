package com.ege.wooda.global.security;

import jakarta.servlet.*;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.lang.reflect.Field;

import com.ege.wooda.domain.member.domain.Member;
import com.ege.wooda.domain.member.domain.Oauth2Entity;
import com.ege.wooda.global.security.oauth.model.PrincipalUser;
import com.ege.wooda.global.security.oauth.model.enums.SocialProvider;

public class MockSecurityFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        SecurityContext context = SecurityContextHolder.getContext();

        PrincipalUser principalUser = new PrincipalUser(createMember(), null, null);
        context.setAuthentication(
                new UsernamePasswordAuthenticationToken(principalUser, principalUser.getPassword(),
                                                        principalUser.getAuthorities()));

        chain.doFilter(request, response);
    }

    private static Member createMember() {
        Oauth2Entity oauth2Entity = Oauth2Entity.builder()
                                                .accountId("su41kvy9q")
                                                .socialProvider(SocialProvider.GOOGLE)
                                                .email("test@" + SocialProvider.GOOGLE.toString().toLowerCase()
                                                       + ".com")
                                                .username("홍길동")
                                                .build();
        Member member = Member.builder()
                              .oauth2Entity(oauth2Entity)
                              .build();

        Class<Member> memberClass = Member.class;
        try {
            Field id = memberClass.getDeclaredField("id");
            id.setAccessible(true);
            id.set(member, 1L);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return member;
    }

    @Override
    public void destroy() {
        SecurityContextHolder.clearContext();
    }

    public void getFilters(MockHttpServletRequest mockHttpServletRequest) {

    }
}
