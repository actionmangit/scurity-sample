package com.lhk.securitysample.repository;

import com.lhk.securitysample.model.SocialProvider;
import com.lhk.securitysample.model.UserEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    UserEntity findByUsernameAndSns(String id, SocialProvider sns);
    UserEntity findByUsername(String username);
}