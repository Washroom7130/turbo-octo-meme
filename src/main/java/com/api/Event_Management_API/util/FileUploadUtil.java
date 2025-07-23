package com.api.Event_Management_API.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.springframework.web.multipart.MultipartFile;

public class FileUploadUtil {
    private static final Pattern FILE_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]{1,29}\\.(png|jpg|jpeg)$");
    private static final Path SAVE_DIR = Paths.get(System.getProperty("user.dir"), "upload", "img"); 

    public static String saveImageFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null; // image field is not required so leaves null here
        }

        // check file size (max 10MB)
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("File size exceeds 10MB limit");
        }

        String originalFilename = file.getOriginalFilename();

        // check filename format
        if (originalFilename == null || !FILE_NAME_PATTERN.matcher(originalFilename).matches()) {
            throw new IllegalArgumentException("Invalid filename. Filename must be less than 30 characters and contains either .jpg, .jpeg, .png");
        }

        // check MIME type
        BufferedImage image = ImageIO.read(file.getInputStream());
        if (image == null) {
            throw new IllegalArgumentException("Invalid file type. Uploaded file must be an image");
        }

        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String randomFilename = RandomGeneratorUtil.generateToken(30) + extension;

        // create dir if not exist
        if (Files.notExists(SAVE_DIR)) {
            Files.createDirectories(SAVE_DIR);
        }

        Path savePath = SAVE_DIR.resolve(randomFilename);
        file.transferTo(savePath.toFile());

        return "/img/" + randomFilename;
    }
}
