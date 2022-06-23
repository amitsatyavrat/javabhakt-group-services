package com.javabhakt.group.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.javabhakt.group.entity.User;
import com.javabhakt.group.repository.UserRepository;

@Service
public class GroupUserDetailService implements UserDetailsService{

	@Autowired
	private UserRepository repository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<User> user = repository.findByUserName(username);
		return user.map(GroupUserDetails::new).orElseThrow(() -> new UsernameNotFoundException(username + " doesn't exist in system"));
	}

}
