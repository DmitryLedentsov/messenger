package com.dimka228.messenger.controllers;

import java.security.Principal;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dimka228.messenger.entities.User;
import com.dimka228.messenger.services.UserService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping(consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
public class UserController {
    private final UserService userService;
    @DeleteMapping("user")
	public void deleteUser(Principal principal) {
		User cur = userService.getUser(principal.getName());
		userService.deleteUser(cur.getId());
	}

	@GetMapping("user")
	public User checkAuth(Principal principal) {
		return userService.getUser(principal.getName());
	}
}
