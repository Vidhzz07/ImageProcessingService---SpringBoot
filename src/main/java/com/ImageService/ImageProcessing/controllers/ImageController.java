package com.ImageService.ImageProcessing.controllers;


import com.ImageService.ImageProcessing.dataModels.TransformationRequest;
import com.ImageService.ImageProcessing.entites.ImageResponse;
import com.ImageService.ImageProcessing.services.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.List;

@RestController
@RequestMapping("/image")
public class ImageController {


    @Autowired
    private ImageService imageService;


    @PostMapping("/postImage")
    public ResponseEntity<?> postImage(@RequestParam("image") MultipartFile file)
    {
        if(file==null || file.isEmpty())
        {
            return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
        }
        try{

            ImageResponse response = imageService.saveImage(file);
            return new ResponseEntity<>(response,HttpStatus.CREATED);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getImageFromID(@PathVariable Long id)
    {
        try{
            ImageResponse response = imageService.getImageFromId(id);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }catch (Exception e)
        {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getImages")
    public ResponseEntity<?> getUserImage(@RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10")int limit)
    {
        try {

            List<ImageResponse> responseList = imageService.getServerImages(page,limit);

            return ResponseEntity.ok(responseList);
        }
        catch (Exception e)
        {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping("/{id}/transform")
    public ResponseEntity<?> transformImage(@PathVariable Long id, @RequestBody TransformationRequest transformationRequest)
    {
        try{
            ImageResponse response = imageService.getImageFromId(id);

          response =   imageService.transformImage(response,transformationRequest.getTransformations());

            return new ResponseEntity<>(response,HttpStatus.OK);

        }catch (Exception e)
        {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

}
