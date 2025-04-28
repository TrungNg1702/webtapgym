package com.project.WebTapGym.models;

import com.project.WebTapGym.enums.Gender;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User extends BaseEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 50)
    private String username;

    @Column(length = 100)
    private String email;

    @Column(name = "password_hash", length = 255, nullable = false)
    private String password;

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(length = 20, nullable = false)
    private String phone;

    private String address;

    @Column(name = "is_active")
    private boolean active;

    @Column(name = "date_of_birth")
    private Date dateOfBirth;

    @Column(name = "height_cm")
    private Float heightCm;

    @Column(name = "weight_kg")
    private Float weightKg;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @Column(name = "registration_date")
    private LocalDateTime registrationDate;

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;

    @Column(name = "subscription_months")
    private int subscriptionMonths;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(this.getRole().getRoleName()));
        return authorities;
    }

    @Override
    public String getUsername() {
        return phone;
    }

    public String getUsername2() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "sex")
    private Gender sex;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Cart cart;

}
