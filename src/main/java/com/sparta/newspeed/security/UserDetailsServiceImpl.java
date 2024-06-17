package com.sparta.newspeed.security;

import com.sparta.newspeed.entity.User;
import com.sparta.newspeed.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository=userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(userId)
                .orElseThrow(() -> new UsernameNotFoundException("UserDetailsServiceImpl : 등록되지 않은 회원입니다."));
        return new UserDetailsImpl(user);
    }

}
