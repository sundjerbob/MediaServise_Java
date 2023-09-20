package ts.component.mediaservise_java.service;

import ts.component.mediaservise_java.dto.MediaDataDTO;
import ts.component.mediaservise_java.dto.MediaObjectDTO;

import java.io.InputStream;
import java.util.List;

public interface MediaServiceAPI {
    List<String> getAllBuckets();

    boolean makeBucket(String bucketName);

    boolean deleteBucket(String bucketName);

    MediaDataDTO getMedia(String bucketName, String imageName);

    List<MediaObjectDTO> getObjectsInBucket(String bucketName);

    boolean uploadObject(String bucketName, String objectName, InputStream inputStream, long size, String contentType);

    boolean deleteObject(String bucketName, String objectName);


}
