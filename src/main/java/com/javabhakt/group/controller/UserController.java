package com.javabhakt.group.controller;

import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.javabhakt.group.common.UserContants;
import com.javabhakt.group.entity.User;
import com.javabhakt.group.repository.UserRepository;

@RestController
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserRepository repository;
	
	@Autowired
	private BCryptPasswordEncoder encoder;
	
	@PostMapping("/join")
	public String joinGroup (@RequestBody User user) {
		user.setRoles(UserContants.DEFAULT_ROLE);
		String encryptedPassword = encoder.encode(user.getPassword());
		user.setPassword(encryptedPassword);
		repository.save(user);
		return "Hi " + user.getUserName() + " welcome to the group !!";
		
	}
	//If logged-in user is admin then admin or moderator access
	//If logged-in user is moderator then moderator access
	@GetMapping("/access/{userId}/{userRole}")
	@Secured("ROLE_ADMIN")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_MODERATOR')")
	public String giveAccessToUser (@PathVariable int userId, @PathVariable String userRole, Principal principal) {
		User user = repository.findById(userId).get();
		List<String> activeRoles = getRolesByLoggedInUser(principal);
		String newRole = "";
		if(activeRoles.contains(userRole)) {
			newRole = user.getRoles()+ "," +userRole;
			user.setRoles(newRole);
		}
		repository.save(user);
		return "Hi "+user.getUserName()+"New Role assigned to you by "+principal.getName();
		
	}
	
	@GetMapping
	@Secured("ROLE_ADMIN")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public List<User> loadUsers () {
		return repository.findAll();
	}
	
	@PreAuthorize("hasAuthority('ROLE_USER')")
	@GetMapping("/test")
	public String testUserAccess () {
		return "User can access only this !!";
	}
	
	private User getLoggedInUser (Principal principal) {
		return repository.findByUserName(principal.getName()).get();
		
	}
	
	private List<String> getRolesByLoggedInUser (Principal principal) {
		String roles = getLoggedInUser(principal).getRoles();
		List<String> assignedRoles = Arrays.stream(roles.split(",")).collect(Collectors.toList());
		if(assignedRoles.contains("ROLE_ADMIN")) {
			return Arrays.stream(UserContants.ADMIN_ACCESS).collect(Collectors.toList());
		} 
		if(assignedRoles.contains("ROLE_MODERATOR")) {
			return Arrays.stream(UserContants.MODERATOR_ACCESS).collect(Collectors.toList());
		}
		return Collections.emptyList();
		
	}
	
}
