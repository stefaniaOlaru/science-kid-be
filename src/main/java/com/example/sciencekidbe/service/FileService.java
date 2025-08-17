package com.example.sciencekidbe.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobClientBuilder;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import com.example.sciencekidbe.enums.ContainerName;
import com.example.sciencekidbe.model.Manual;
import com.example.sciencekidbe.model.Observation;
import com.example.sciencekidbe.model.Student;
import com.example.sciencekidbe.model.Teacher;
import com.example.sciencekidbe.repository.ManualRepository;
import com.example.sciencekidbe.repository.ObservationRepository;
import com.example.sciencekidbe.repository.StudentRepository;
import com.example.sciencekidbe.repository.TeacherRepository;
import com.example.sciencekidbe.service.interfaces.IFileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

import static com.example.sciencekidbe.helpers.FileHelpers.getBlobContainerName;

@Service
public class FileService implements IFileService {
    @Value("${azure.storage.connection-string}")
    private String blobStorageConnectionString;

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final ManualRepository manualRepository;
    private final ObservationRepository observationRepository;

    public FileService(StudentRepository studentRepository,
                       TeacherRepository teacherRepository,
                       ManualRepository manualRepository,
                       ObservationRepository observationRepository) {
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.manualRepository = manualRepository;
        this.observationRepository = observationRepository;
    }

    public String uploadFileToBlob(Long entityId, ContainerName containerName, String filePath) {
        // Generate UUID as blob name with original file extension
        String fileExtension = getFileExtension(filePath);
        String blobName = containerName.name().toLowerCase() + entityId + fileExtension;

        // Create container client
        BlobContainerClient containerClient = new BlobContainerClientBuilder()
                .connectionString(blobStorageConnectionString)
                .containerName(getBlobContainerName(containerName))
                .buildClient();

        if (!containerClient.exists()) {
            throw new IllegalArgumentException("Containerul nu exista!");
        }

        // Get blob client and upload file
        BlobClient blobClient = containerClient.getBlobClient(blobName);
        blobClient.uploadFromFile(filePath, true);

        // Return full URL
        String blobUrl = blobClient.getBlobUrl();
        switch (containerName) {
            case STUDENT -> {
                Student student = studentRepository.findById(entityId).orElse(null);
                if (student == null) {
                    throw new IllegalArgumentException("Studentul cu acest id nu a fost gasit");
                }
                student.setImagePath(blobUrl);
                studentRepository.save(student);
            }
            case TEACHER -> {
                Teacher teacher = teacherRepository.findById(entityId).orElse(null);
                if (teacher == null) {
                    throw new IllegalArgumentException("Profesorul cu acest id nu a fost gasit");
                }
                teacher.setImagePath(blobUrl);
                teacherRepository.save(teacher);
            }
            case MANUAL -> {
                Manual manual = manualRepository.findById(entityId).orElse(null);
                if (manual == null) {
                    throw new IllegalArgumentException("Manualul cu acest id nu a fost gasit");
                }
                manual.setPdfPath(blobUrl);
                manualRepository.save(manual);
            }
            case PROFESSOR_CV -> {
                Teacher teacher = teacherRepository.findById(entityId).orElse(null);
                if (teacher == null) {
                    throw new IllegalArgumentException("Profesorul cu acest id nu a fost gasit");
                }
                teacher.setPdfPath(blobUrl);
                teacherRepository.save(teacher);
            }
            case AUDIO -> {
                Observation observation = observationRepository.findById(entityId).orElse(null);
                if (observation == null) {
                    throw new IllegalArgumentException("Observarea cu acest id nu a fost gasita");
                }
                observation.setAudioPath(blobUrl);
                observationRepository.save(observation);
            }
            default -> throw new IllegalArgumentException("Container name not supported: " + containerName);
        }

        System.out.println("Upload complete for blob: " + blobName);
        return blobUrl;
    }

    public String generateUrlWithSasToken(String blobUrl) {
        BlobClient blobClient = new BlobClientBuilder()
                .endpoint(blobUrl)
                .connectionString(blobStorageConnectionString)
                .buildClient();

        // Read permission is enough to download
        BlobSasPermission permissions = new BlobSasPermission()
                .setReadPermission(true);

        BlobServiceSasSignatureValues sasValues = new BlobServiceSasSignatureValues(
                OffsetDateTime.now().plusMinutes(60 * 24), // Token valid for 1 day
                permissions
        );

        String sasToken = blobClient.generateSas(sasValues);
        return blobClient.getBlobUrl() + "?" + sasToken;
    }

    private String getFileExtension(String path) {
        int lastDot = path.lastIndexOf('.');
        return (lastDot != -1) ? path.substring(lastDot) : "";
    }
}
