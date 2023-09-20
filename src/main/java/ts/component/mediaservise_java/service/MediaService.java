package ts.component.mediaservise_java.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ts.component.mediaservise_java.dto.MediaDataDTO;
import ts.component.mediaservise_java.dto.MediaObjectDTO;
import ts.component.mediaservise_java.repository.RepositoryAPI;

import java.io.InputStream;
import java.util.List;


@Service
public class MediaService implements MediaServiceAPI {

    private final RepositoryAPI mediaRepository;

    @Autowired
    public MediaService(RepositoryAPI mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    @Override
    public List<String> getAllBuckets() {
        return mediaRepository.allBuckets();
    }

    @Override
    public boolean makeBucket(String bucketName) {
        return mediaRepository.makeBucket(bucketName);
    }

    @Override
    public boolean deleteBucket(String bucketName) {
        return mediaRepository.deleteBucket(bucketName);
    }

    public MediaDataDTO getMedia(String bucketName, String imageName) {
        return mediaRepository.getMedia(bucketName, imageName);
    }


    @Override
    public List<MediaObjectDTO> getObjectsInBucket(String bucketName) {
        return mediaRepository.listObjects(bucketName);
    }

    @Override
    public boolean uploadObject(String bucketName, String objectName, InputStream inputStream, long size, String contentType) {
        return mediaRepository.uploadObject(bucketName, objectName, inputStream, size, contentType);
    }

    @Override
    public boolean deleteObject(String bucketName, String objectName) {
        return mediaRepository.deleteObject(bucketName, objectName);
    }
}
