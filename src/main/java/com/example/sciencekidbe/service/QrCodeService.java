package com.example.sciencekidbe.service;

import com.beust.jcommander.internal.Nullable;
import com.example.sciencekidbe.dto.QrCodeRequest;
import com.example.sciencekidbe.enums.ContainerName;
import com.example.sciencekidbe.repository.QrCodeRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static javax.imageio.ImageIO.write;
import static org.springframework.util.StringUtils.hasText;

@Service
public class QrCodeService {
    private final QrCodeRepository qrCodeRepository;
    private final FileService fileService;

    private static final int QR_PX = 512;
    private static final long MAX_VIDEO_SIZE_MB = 512;
    private static final Set<String> ALLOWED_VIDEO_EXT = Set.of("mp4","mov","mkv","webm","avi");

    public QrCodeService(QrCodeRepository qrCodeRepository,
                         FileService fileService) {
        this.qrCodeRepository = qrCodeRepository;
        this.fileService = fileService;
    }

    @Transactional
    public void generateQrCode(QrCodeRequest qrCodeRequest, @Nullable MultipartFile file) {

        if (hasText(qrCodeRequest.getUrl())) {
            String targetUrl = normalizeAndValidateUrl(qrCodeRequest.getUrl());

            Path qrPng = null;
            try {
                qrPng = generateQrPngTemp(targetUrl);

                fileService.uploadFileToBlob(qrCodeRequest.getLectureId(), ContainerName.LECTURE_VIDEO, qrPng.toString());
            } finally {
                deleteQuietly(qrPng);
            }
            return;
        }

        if (file != null && !file.isEmpty()) {
            validateMultipartVideo(file);

            Path tempVideo = null;
            Path tempQr = null;

            try {
            tempVideo = toTempFile(file);
            String videoBlobUrl = fileService.uploadFileToBlob(
                    qrCodeRequest.getLectureId(),
                    ContainerName.ACTUAL_VIDEO,
                    tempVideo.toString()
            );

            String targetUrl = fileService.generateUrlWithSasToken(videoBlobUrl);

            tempQr = generateQrPngTemp(targetUrl);
            fileService.uploadFileToBlob(qrCodeRequest.getLectureId(), ContainerName.LECTURE_VIDEO, tempQr.toString());
            } catch (IOException e) {
                throw new RuntimeException("Eroare IO la procesarea fișierului.", e);
            } finally {
                deleteQuietly(tempVideo);
                deleteQuietly(tempQr);
            }
            return;
        }

        throw new IllegalArgumentException("Trebuie să furnizezi fie 'url', fie 'path'.");
    }

    private Path toTempFile(MultipartFile file) throws IOException {
        String base = Optional.ofNullable(file.getOriginalFilename()).orElse("upload");
        String ext = getExtLower(base);
        Path temp = Files.createTempFile("upload_", ext.isEmpty() ? null : "." + ext);
        file.transferTo(temp.toFile());
        return temp;
    }

    private String normalizeAndValidateUrl(String raw) {
        String url = raw.trim();

        if (!(url.startsWith("http://") || url.startsWith("https://"))) {
            throw new IllegalArgumentException("URL trebuie să înceapă cu http:// sau https://");
        }
        if (url.length() > 2048) {
            throw new IllegalArgumentException("URL prea lung.");
        }
        return url;
    }

    private Path generateQrPngTemp(String content) {
        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 1);

            BitMatrix matrix =
                    new MultiFormatWriter()
                            .encode(content, BarcodeFormat.QR_CODE, QrCodeService.QR_PX, QrCodeService.QR_PX, hints);

            BufferedImage image = MatrixToImageWriter.toBufferedImage(matrix);

            Path temp = Files.createTempFile("qr_", ".png");
            try (OutputStream os = Files.newOutputStream(temp, StandardOpenOption.TRUNCATE_EXISTING)) {
                write(image, "png", os);
            }
            return temp;
        } catch (Exception e) {
            throw new RuntimeException("Generarea codului QR a eșuat.", e);
        }
    }

    private void deleteQuietly(Path p) {
        if (p == null) return;
        try { Files.deleteIfExists(p); } catch (Exception ignore) {}
    }

    private void validateMultipartVideo(MultipartFile file) {
        long maxBytes = MAX_VIDEO_SIZE_MB * 1024 * 1024;
        if (file.getSize() > maxBytes) {
            throw new IllegalArgumentException("Fișier prea mare: " + (file.getSize() / (1024 * 1024)) + " MB. " +
                    "Max permis: " + MAX_VIDEO_SIZE_MB + " MB");
        }

        String original = Optional.ofNullable(file.getOriginalFilename()).orElse("");
        String ext = getExtLower(original);
        if (!ALLOWED_VIDEO_EXT.contains(ext)) {
            throw new IllegalArgumentException("Extensie video neacceptată: ." + ext +
                    " (acceptate: " + String.join(", ", ALLOWED_VIDEO_EXT) + ")");
        }
    }

    private static String getExtLower(String filename) {
        int dot = filename.lastIndexOf('.');
        return (dot >= 0 && dot < filename.length() - 1)
                ? filename.substring(dot + 1).toLowerCase(Locale.ROOT)
                : "";
    }

}
