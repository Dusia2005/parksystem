package com.project.parksystem.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class PlantFileService {

    private final Path uploadDir;
    private final long maxSizeBytes;
    private final int maxWidth;
    private final int maxHeight;

    public PlantFileService(
            @Value("${app.upload.dir:uploads/plants}") String uploadDir,
            @Value("${app.upload.max-size-bytes:20480}") long maxSizeBytes,
            @Value("${app.upload.max-width:64}") int maxWidth,
            @Value("${app.upload.max-height:64}") int maxHeight
    ) throws IOException {
        this.uploadDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.maxSizeBytes = maxSizeBytes;
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        Files.createDirectories(this.uploadDir);
    }

    public String store(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) return null;

        if (file.getSize() > maxSizeBytes) {
            throw new IllegalStateException("Файл слишком большой. Максимум " + (maxSizeBytes/1024) + " KB.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !(contentType.equals("image/png") ||
                contentType.equals("image/jpeg") || contentType.equals("image/jpg") ||
                contentType.equals("image/webp"))) {
            throw new IllegalStateException("Недопустимый формат. Разрешены: PNG, JPG, WEBP.");
        }

        // Проверка размеров изображения через ImageIO
        try (InputStream is = file.getInputStream()) {
            BufferedImage img = ImageIO.read(is);
            if (img == null) throw new IllegalStateException("Не удалось прочитать изображение.");
            if (img.getWidth() > maxWidth || img.getHeight() > maxHeight) {
                throw new IllegalStateException("Изображение больше " + maxWidth + "x" + maxHeight + " пикселей.");
            }
        }

        String original = StringUtils.cleanPath(file.getOriginalFilename());
        String ext = "";
        int i = original.lastIndexOf('.');
        if (i > 0) ext = original.substring(i);

        String filename = "plant-" + UUID.randomUUID() + ext;
        Path target = this.uploadDir.resolve(filename);

        try (InputStream is = file.getInputStream()) {
            Files.copy(is, target, StandardCopyOption.REPLACE_EXISTING);
        }

        return filename;
    }

    public Resource loadAsResource(String filename) throws MalformedURLException {
        if (filename == null) return null;
        Path file = uploadDir.resolve(filename).normalize();
        Resource resource = new UrlResource(file.toUri());
        if (resource.exists() && resource.isReadable()) {
            return resource;
        } else {
            return null;
        }
    }

    public boolean delete(String filename) {
        if (filename == null) return true;
        try {
            Path file = uploadDir.resolve(filename).normalize();
            return Files.deleteIfExists(file);
        } catch (IOException e) {
            return false;
        }
    }
    public String copyDefaultImage() {
        try {
            InputStream is = getClass().getResourceAsStream("/static/assets/plants/dyb.png");
            if (is == null) {
                throw new IllegalStateException("Файл dyb.png не найден в resources/static/assets!");
            }

            String filename = "plant-default-" + UUID.randomUUID() + ".png";
            Path target = uploadDir.resolve(filename);
            Files.copy(is, target, StandardCopyOption.REPLACE_EXISTING);

            return filename;
        } catch (IOException e) {
            throw new RuntimeException("Не удалось скопировать изображение по умолчанию", e);
        }
    }

}
