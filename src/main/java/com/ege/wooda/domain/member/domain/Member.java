package com.ege.wooda.domain.member.domain;

import com.ege.wooda.domain.member.domain.enums.Role;
import com.ege.wooda.global.audit.AuditEntity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @ElementCollection(fetch = FetchType.EAGER)

    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "member_role", joinColumns = @JoinColumn(name = "member_id"))
    @Column(name = "role")
    private List<Role> role;

    @Embedded
    private Oauth2Entity oauth2Entity;

    @Embedded
    private AuditEntity auditEntity;

    @Builder
    public Member(Oauth2Entity oauth2Entity) {
        this.oauth2Entity = oauth2Entity;
        this.role = new ArrayList<>(List.of(Role.MEMBER));
        this.auditEntity = new AuditEntity();
    }

    public List<SimpleGrantedAuthority> getRole() {
        return role.stream()
                   .map(Role::name)
                   .map(SimpleGrantedAuthority::new)
                   .toList();
    }
}
