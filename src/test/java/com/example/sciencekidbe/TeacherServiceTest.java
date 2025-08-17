package com.example.sciencekidbe;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.sciencekidbe.dto.ClassSummaryDTO;
import com.example.sciencekidbe.dto.TeacherDTO;
import com.example.sciencekidbe.enums.ERole;
import com.example.sciencekidbe.model.ClassEntity;
import com.example.sciencekidbe.model.Teacher;
import com.example.sciencekidbe.repository.ClassEntityRepository;
import com.example.sciencekidbe.repository.TeacherRepository;
import com.example.sciencekidbe.service.FileService;
import com.example.sciencekidbe.service.TeacherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

public class TeacherServiceTest {

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private ClassEntityRepository classEntityRepository;

    @Mock
    private FileService fileService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private TeacherService teacherService;

    private Teacher teacher;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        ClassEntity class1 = new ClassEntity();
        class1.setClassId(1L);
        class1.setClassName("Math 101");
        class1.setGradeLevel(10);

        teacher = new Teacher();
        teacher.setTeacherId(1L);
        teacher.setFullName("John Doe");
        teacher.setEmail("john.doe@example.com");
        teacher.setSubject("Math");
        teacher.setPhoneNumber("0123456789");
        teacher.setPassword("encodedPassword");
        teacher.setClassEntities(List.of(class1));
        teacher.setImagePath("image.png");
        teacher.setPdfPath("file.pdf");
    }

    @Test
    public void testChangePassword_Success() {
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
        when(passwordEncoder.matches("oldPassword", teacher.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("newEncodedPassword");

        teacherService.changePassword(1L, "oldPassword", "newPassword");

        assertEquals("newEncodedPassword", teacher.getPassword());
        verify(teacherRepository, times(1)).save(teacher);
    }

    @Test
    public void testChangePassword_WrongOldPassword() {
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
        when(passwordEncoder.matches("wrongOld", teacher.getPassword())).thenReturn(false);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                teacherService.changePassword(1L, "wrongOld", "newPassword"));

        assertEquals("Parola veche este incorectă", exception.getMessage());
    }

    @Test
    public void testGetTeacherById_ReturnsDTOWithUrls() {
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
        when(fileService.generateUrlWithSasToken("image.png")).thenReturn("imageUrl");
        when(fileService.generateUrlWithSasToken("file.pdf")).thenReturn("pdfUrl");

        TeacherDTO dto = teacherService.getTeacherById(1L);

        assertEquals("imageUrl", dto.getImagePath());
        assertEquals("pdfUrl", dto.getPdfPath());
        assertEquals("John Doe", dto.getFullName());
    }

    @Test
    public void testGetAllTeacher_EmptyList() {
        when(teacherRepository.findByRole(ERole.ROLE_PROFESOR)).thenReturn(Collections.emptyList());

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                teacherService.getAllTeacher());

        assertEquals("Nu exista profesori inregistrati", exception.getMessage());
    }

    @Test
    public void testDeleteTeacher_Success() {
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));

        teacherService.deleteTeacher(1L);

        verify(teacherRepository, times(1)).delete(teacher);
    }

    @Test
    public void testUpdateTeacher_Success() {
        TeacherDTO dto = new TeacherDTO();
        dto.setFullName("Jane Doe");
        dto.setEmail("jane.doe@example.com");
        dto.setPhoneNumber("0987654321");
        dto.setSubject("Science");
        dto.setClassEntityIds(List.of(1L));

        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
        when(classEntityRepository.findById(1L)).thenReturn(Optional.of(teacher.getClassEntities().get(0)));
        when(teacherRepository.save(any())).thenReturn(teacher);

        Teacher updated = teacherService.updateTeacher(1L, dto);

        assertEquals("Jane Doe", updated.getFullName());
        assertEquals("jane.doe@example.com", updated.getEmail());
    }

    @Test
    public void testGetAllTeacher_Success() {
        when(teacherRepository.findByRole(ERole.ROLE_PROFESOR)).thenReturn(List.of(teacher));
        when(fileService.generateUrlWithSasToken("image.png")).thenReturn("imageUrl");
        when(fileService.generateUrlWithSasToken("file.pdf")).thenReturn("pdfUrl");

        List<TeacherDTO> result = teacherService.getAllTeacher();

        assertEquals(1, result.size());
        TeacherDTO dto = result.get(0);
        assertEquals("John Doe", dto.getFullName());
        assertEquals("imageUrl", dto.getImagePath());
        assertEquals("pdfUrl", dto.getPdfPath());
    }

    @Test
    public void testDeleteTeacher_NotFound() {
        when(teacherRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                teacherService.deleteTeacher(1L));

        assertEquals("Profesorul nu există!", exception.getMessage());
    }

    @Test
    public void testUpdateTeacher_InvalidEmail() {
        TeacherDTO dto = new TeacherDTO();
        dto.setFullName("Jane Doe");
        dto.setEmail("invalidEmail");
        dto.setPhoneNumber("0123456789");
        dto.setSubject("Science");
        dto.setClassEntityIds(List.of(1L));

        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                teacherService.updateTeacher(1L, dto));

        assertEquals("Emailul introdus nu este valid.", exception.getMessage());
    }

    @Test
    public void testUpdateTeacher_InvalidPhoneNumber() {
        TeacherDTO dto = new TeacherDTO();
        dto.setFullName("Jane Doe");
        dto.setEmail("jane.doe@example.com");
        dto.setPhoneNumber("123"); // invalid
        dto.setSubject("Science");
        dto.setClassEntityIds(List.of(1L));

        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                teacherService.updateTeacher(1L, dto));

        assertEquals("Numărul de telefon trebuie să aibă exact 10 cifre și să înceapă cu 0.", exception.getMessage());
    }

    @Test
    public void testUpdateTeacher_ClassNotFound() {
        TeacherDTO dto = new TeacherDTO();
        dto.setFullName("Jane Doe");
        dto.setEmail("jane.doe@example.com");
        dto.setPhoneNumber("0123456789");
        dto.setSubject("Science");
        dto.setClassEntityIds(List.of(999L)); // non-existent class

        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
        when(classEntityRepository.findById(999L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                teacherService.updateTeacher(1L, dto));

        assertEquals("Clasa cu ID-ul 999 nu a fost găsită.", exception.getMessage());
    }

    @Test
    public void testGetTeacherById_NotFound() {
        when(teacherRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                teacherService.getTeacherById(1L));

        assertEquals("Profesorul cu acest id nu a fost gasit", exception.getMessage());
    }

    @Test
    public void testGetClassesForTeacher_Success() {
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));

        List<ClassSummaryDTO> classes = teacherService.getClassesForTeacher(1L);

        assertEquals(1, classes.size());
        ClassSummaryDTO dto = classes.get(0);
        assertEquals(1L, dto.getClassId());
        assertEquals("Math 101", dto.getClassName());
        assertEquals(10, dto.getGradeLevel());
    }

    @Test
    public void testGetClassesForTeacher_NotFound() {
        when(teacherRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                teacherService.getClassesForTeacher(1L));

        assertEquals("Profesorul nu exista", exception.getMessage());
    }
}

