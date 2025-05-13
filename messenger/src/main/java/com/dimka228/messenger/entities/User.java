package com.dimka228.messenger.entities;

import java.util.Collection;
import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.dimka228.messenger.dto.UserAuthDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(name = "m_user", indexes = { @Index(name = "m_user_login_key", columnList = "login", unique = true) })
public class User implements UserDetails, Cloneable, Comparable<User> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Integer id;

	@Column(name = "password", nullable = false)
	private String password;

	@Column(name = "login", nullable = false, length = 20, unique = true)
	private String login;

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	@Override
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public User clone() throws CloneNotSupportedException {
		super.clone();
		User newUser = new User();
		newUser.setPassword(getPassword());
		newUser.setLogin(getLogin());
		newUser.setId(getId());
		return newUser;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return null;
	}

	@Override
	public String getUsername() {
		return getLogin();
	}

	@Override
	public boolean isAccountNonExpired() {
		return false;
	}

	@Override
	public boolean isAccountNonLocked() {
		return false;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return false;
	}

	@Override
	public boolean isEnabled() {
		return false;
	}

	public static User fromAuth(UserAuthDTO auth) {
		User user = new User();
		user.setLogin(auth.getLogin());
		user.setPassword(auth.getPassword());
		return user;
	}

	@Override
	public int compareTo(User user) {
		return this.getId().compareTo(user.getId());
	}
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof User)) return false;
	
		User other = (User) o;
	
		return  Objects.equals(this.getId(), other.getId());
	}
}
