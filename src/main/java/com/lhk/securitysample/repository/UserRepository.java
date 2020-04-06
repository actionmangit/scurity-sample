package com.lhk.securitysample.repository;

import com.lhk.securitysample.model.SocialProvider;
import com.lhk.securitysample.model.UserEntity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    UserEntity findByUsernameAndSns(String id, SocialProvider sns);
}