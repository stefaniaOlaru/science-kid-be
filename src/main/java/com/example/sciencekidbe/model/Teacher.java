package com.example.sciencekidbe.model;

import com.example.sciencekidbe.enums.ERole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "Teacher")
public class Teacher implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long teacherId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private ERole role;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "email",  unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "subject")
    private String subject;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "image_path")
    private String imagePath;

    @Column(name = "pdf_path")
    private String pdfPath;

    @ManyToMany
    @JoinTable(
            name = "teacher_class",
            joinColumns = @JoinColumn(name = "teacher_id"),
            inverseJoinColumns = @JoinColumn(name = "class_id")
    )
    private List<ClassEntity> classEntities;

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
