package com.commercehub.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User extends BaseEntity {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(length=50, nullable=false, unique=true)
	private String username;
	
	@Column(nullable=false, unique=true)
	private String email;
	
	@Column(nullable=false)
	private String password;
	
	@Column(length=50, nullable=false)
	private String firstName;
	
	@Column(length=50, nullable=false)
	private String lastName;
	
	@Column(length=15)
	private String phoneNumber;
	
	@Column(nullable=false)
	private Boolean isEnable;
	
	@Column(nullable=false)
	private Boolean isLocked;	
	
	@Column(nullable=false)
	private Boolean emailVerified;
	
	@Column(nullable=false)
	@Builder.Default
	private LocalDateTime lastLoginAt = LocalDateTime.now();
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private Set<Order> orders = new HashSet<>();
	
	@ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
            )
	@Builder.Default
	private Set<Role> roles = new HashSet<>();
	
	public void addOrder(Order order)
	{
		this.orders.add(order);
		order.setUser(this);
	}
	
	public void removeOrder(Order order)
	{
		this.orders.remove(order);
		order.setUser(null);
	}	
	
	public void addRole(Role role)
	{
		this.roles.add(role);
		role.getUsers().add(this);
	}
	
	public void removeRole(Role role)
	{
		this.roles.remove(role);
		role.getUsers().remove(this);
	}
	
}
