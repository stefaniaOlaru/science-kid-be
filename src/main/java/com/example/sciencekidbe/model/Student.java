package com.example.sciencekidbe.model;

import com.example.sciencekidbe.enums.ERole;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Student")
@Entity
@Getter
@Setter
public class Student implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private ERole role;

    @NonNull
    @Column(name = "full_name", nullable = false)
    private String fullName;

    @NonNull
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password")
    private String password;

    @NonNull
    @Column(name = "birthdate", nullable = false)
    private LocalDate birthdate;

    @NonNull
    @Column(name = "joinDate")
    private LocalDate joinDate;

    @Column(name = "image_path")
    private String imagePath;

    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    @JsonManagedReference
    private ClassEntity classEntity;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentAnswer> answers;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
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
}
