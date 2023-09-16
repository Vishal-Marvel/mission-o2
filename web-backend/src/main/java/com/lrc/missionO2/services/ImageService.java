package com.lrc.missionO2.services;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

@Service
public class ImageService {

    public byte[] compressImages(MultipartFile file) throws IOException {
        if (file.getSize() <= 500 * 1024) {
            return file.getBytes();
        }
        int targetWidth = 650;
        int targetHeight = 650;

        double aspectRatio = (double) file.getSize() / (file.getBytes().length);

        if (aspectRatio > 1.0) {
            targetWidth = (int) Math.round(Math.sqrt(650 * 650 * aspectRatio));
        } else {
            targetHeight = (int) Math.round(650 * 650 / aspectRatio);
        }

        Thumbnails.Builder<? extends InputStream> builder = Thumbnails.of(file.getInputStream())
                .size(targetWidth, targetHeight)
                .outputQuality(0.9);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        builder.toOutputStream(outputStream);

        return outputStream.toByteArray();
    }
}
