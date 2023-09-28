package ts.component.mediaservise_java.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ts.component.mediaservise_java.service.MediaService;

import java.util.List;

@RestController
@RequestMapping("/buckets")
public class BucketController {

    private final MediaService bucketService;

    @Autowired
    public BucketController(MediaService bucketService) {
        this.bucketService = bucketService;
    }
    @GetMapping("/list")
    public List<String> listBuckets() {
        return bucketService.getAllBuckets();
    }

    @PostMapping("/makeBucket")
    public boolean makeBucket(@RequestBody  String bucketName) {
        return bucketService.makeBucket(bucketName);
    }

    @DeleteMapping("/deleteBucket")
    public boolean deleteBucket(@RequestBody String bucketName) {
        return bucketService.deleteBucket(bucketName);
    }



}
