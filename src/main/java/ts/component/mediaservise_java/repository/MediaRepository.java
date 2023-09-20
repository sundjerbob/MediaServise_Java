package ts.component.mediaservise_java.repository;

import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import ts.component.mediaservise_java.dto.MediaDataDTO;
import ts.component.mediaservise_java.dto.MediaObjectDTO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Component
public class MediaRepository implements RepositoryAPI {

    private final MinioClient minioClient;

    @Autowired
    public MediaRepository(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @Override
    public List<String> allBuckets() {
        List<String> bucketsData = new ArrayList<>();
        try {
            List<Bucket> bucketList = minioClient.listBuckets();

            for (Bucket curr : bucketList) {
                bucketsData.add("bucket name: " + curr.name() + " creation date: " + curr.creationDate());
            }

            return bucketsData;

        } catch (MinioException e) {
            System.out.println("Connection failed" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bucketsData;
    }

    @Override
    public boolean makeBucket(String bucketName) {
        try {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            return true;
        } catch (MinioException e) {
            System.out.println("Error creating bucket: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteBucket(String bucketName) {
        try {
            minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
            return true;
        } catch (MinioException e) {
            System.out.println("Error deleting bucket: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Uploads an object to the specified bucket with the given objectName
    public boolean uploadObject(String bucketName, String objectName, InputStream inputStream, long size, String contentType) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(inputStream, size, -1)
                            .contentType(contentType)
                            .build()
            );
            return true;
        } catch (MinioException | IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public boolean deleteObject(String bucketName, String objectName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
            return true;
        } catch (MinioException | IOException | InvalidKeyException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return false;
    }


    public MediaDataDTO getMedia(String bucketName, String objectName) {
        try {
            GetObjectResponse response = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );

            String contentType = determineContentType(objectName);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;

            while ((length = response.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }

            InputStream dataStream = new ByteArrayInputStream(outputStream.toByteArray());

            return MediaDataDTO
                    .builder()
                    .contentType(contentType)
                    .dataStream(dataStream)
                    .build();

        } catch (MinioException | IOException | InvalidKeyException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String determineContentType(String fileName) {
        // Determine the content type based on the file extension
        String extension = getExtension(fileName);

        // Map file extensions to content types for images and videos
        if (isImageExtension(extension)) {
            return MediaType.IMAGE_JPEG_VALUE;
        } else if (isVideoExtension(extension)) {
            return "video/mp4"; // Adjust this based on the actual content type
        } else {
            return MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
    }

    private String getExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex != -1 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1).toLowerCase();
        }
        return "";
    }


    public List<MediaObjectDTO> listObjects(String bucketName) {
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .build()
        );
        List<MediaObjectDTO> objects = new ArrayList<>();
        results.forEach(result -> {
            try {
                Item item = result.get();
                MediaObjectDTO dto = MediaObjectDTO.builder()
                        .name(item.objectName())
                        .size(item.size())
                        .build();
                // Set other DTO properties as needed
                objects.add(dto);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return objects;
    }

    private boolean isImageExtension(String extension) {
        // Add the list of image extensions you want to support (e.g., jpg, jpeg, png, svg)
        return List.of("jpg", "jpeg", "png", "svg").contains(extension.toLowerCase());
    }

    private boolean isVideoExtension(String extension) {
        // Add the list of video extensions you want to support (e.g., mp4, avi, mkv)
        return List.of("mp4", "avi", "mkv").contains(extension.toLowerCase());
    }
}
