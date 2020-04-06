package com.lhk.securitysample.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.lhk.securitysample.model.UserEntity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @PersistenceContext
    private EntityManager em;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        
        // @fomatter:off
        List<UserEntity> findUsers =
                em
                    .createQuery("select v from UserEntity v where v.username = :username", UserEntity.class)
                    .setParameter("username", s)
                    .getResultList();
        // @fomatter:on

        if (findUsers.isEmpty()) {
            throw new UsernameNotFoundException("유저를 찾을 수 없습니다.");
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        GrantedAuthority role = new SimpleGrantedAuthority("ROLE_USER");
        authorities.add(role);

        if (s.equals("admin")) {
            GrantedAuthority adminRole = new SimpleGrantedAuthority("ROLE_ADMIN");
            authorities.add(adminRole);
        }

        return new User(findUsers.get(0).getUsername(), 
                        findUsers.get(0).getPassword(), 
                        authorities);
    }
}