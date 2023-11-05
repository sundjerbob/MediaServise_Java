package ts.component.mediaservise_java.controller;

import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ts.component.mediaservise_java.dto.MediaDataDTO;
import ts.component.mediaservise_java.dto.MediaObjectDTO;
import ts.component.mediaservise_java.service.MediaServiceAPI;

import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/media")
public class MediaController {

    private final MediaServiceAPI mediaService;

    @Autowired
    public MediaController(MediaServiceAPI mediaService) {
        this.mediaService = mediaService;
    }

    @PostMapping("/uploadToBucket")
    public ResponseEntity<String> uploadFile(
            @RequestParam MultipartFile file,
            @RequestParam String bucketName,
            @RequestParam String objectName

    ) {
        try {
            if (!file.isEmpty()) {
                // Get the file data and content type
                InputStream inputStream = file.getInputStream();
                long fileSize = file.getSize();
                String contentType = file.getContentType();

                // Upload the file to MinIO
                boolean uploadResult = mediaService.uploadObject(bucketName, objectName, inputStream, fileSize, contentType);

                if (uploadResult) {
                    return ResponseEntity.status(HttpStatus.OK).body("File uploaded successfully");
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed");
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Empty file");
            }
        } catch (Exception e) {

            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed");
        }
    }

    @GetMapping("/{bucketName}")
    public List<MediaObjectDTO> getObjectsInBucket(@PathVariable String bucketName) {
        return mediaService.getObjectsInBucket(bucketName);
    }

    @GetMapping("/{bucketName}/{mediaName}")
    public ResponseEntity<byte[]> getMedia(
            @PathVariable String bucketName,
            @PathVariable String mediaName
    ) {
        try {
            MediaDataDTO mediaData = mediaService.getMedia(bucketName, mediaName);

            if (mediaData != null) {
                String contentType = mediaData.getContentType();

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.parseMediaType(contentType));

                return new ResponseEntity<>(IOUtils.toByteArray(mediaData.getDataStream()), headers, HttpStatus.OK);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
