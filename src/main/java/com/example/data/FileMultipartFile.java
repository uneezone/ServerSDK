package com.example.data;

import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.util.Base64;
public class FileMultipartFile implements MultipartFile {

    private final File file;
    private final String contentType;
    private final byte[] contentBytes;

    public FileMultipartFile(File file, String contentType) {
        this.file = file;
        this.contentType = contentType;
        this.contentBytes =null;
    }

    public FileMultipartFile(byte[] contentBytes, String contentType, String filename) {
        this.contentBytes = contentBytes;
        this.contentType = contentType;

        // Create a temporary file with the provided filename
        try {
            this.file = File.createTempFile(filename, null);
            try (FileOutputStream fos = new FileOutputStream(this.file)) {
                fos.write(contentBytes);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error creating temporary file", e);
        }
    }

    @Override
    public String getName() {
        return file.getName();
    }

    @Override
    public String getOriginalFilename() {
        return file.getName();
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public boolean isEmpty() {
        return file.length() == 0;
    }

    @Override
    public long getSize() {
        return file.length();
    }

    @Override
    public byte[] getBytes() throws IOException {
        try (InputStream is = new FileInputStream(file)) {
            return readBytes(is);
        }
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new FileInputStream(file);
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        try (InputStream is = new FileInputStream(file); OutputStream os = new FileOutputStream(dest)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        }
    }

    private byte[] readBytes(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int bytesRead;
        byte[] data = new byte[8192];
        while ((bytesRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, bytesRead);
        }
        return buffer.toByteArray();
    }

    public static MultipartFile[] convertToMultipartFiles(File savedImageFile, String contentType) {
        MultipartFile file = new FileMultipartFile(savedImageFile, contentType);
        return new MultipartFile[]{file};
    }

    public static MultipartFile[] convertBase64ToMultipartFile(String base64Content, String contentType, String filename) {
        byte[] contentBytes = Base64.getDecoder().decode(base64Content);
        MultipartFile file = new FileMultipartFile(contentBytes, contentType, filename);
        return new MultipartFile[]{file};
    }
}

