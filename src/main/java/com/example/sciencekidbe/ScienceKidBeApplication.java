package com.example.sciencekidbe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ScienceKidBeApplication {

        public static void main(String[] args) {
            SpringApplication.run(ScienceKidBeApplication.class, args);
        }

//    @Bean
//    CommandLineRunner init(TeacherRepository teacherRepository, PasswordEncoder passwordEncoder) {
//        return args -> {
//            Teacher admin = new Teacher();
//            admin.setFullName("Alina Gheorghe");
//            admin.setEmail("alinagheorghe@gmail.com");
//            admin.setPassword(passwordEncoder.encode("password"));
//            admin.setRole(ERole.valueOf("ROLE_ADMIN"));
//
//            Teacher userInserted = teacherRepository.save(admin);
//        };
//    }

}
