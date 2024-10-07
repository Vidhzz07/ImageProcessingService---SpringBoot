package com.ImageService.ImageProcessing.services;


import com.ImageService.ImageProcessing.Repos.ImageRepo;
import com.ImageService.ImageProcessing.Repos.UserRepo;
import com.ImageService.ImageProcessing.dataModels.TransformationRequest;
import com.ImageService.ImageProcessing.entites.ImageEntity;
import com.ImageService.ImageProcessing.entites.ImageResponse;
import com.ImageService.ImageProcessing.entites.User;
import com.ImageService.ImageProcessing.utils.ImageUtil;
import com.ImageService.ImageProcessing.utils.SecurityUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ImageService {

    @Value("${image.upload.url}")
    private  String uploadDir;
    @Value("${server.port}")
    private  String serverPort;

    @Autowired
    private ImageRepo imageRepo;

    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private ImageUtil imageUtil;

    public static String[] allowedFormat={"JPG", "JPEG", "PNG", "BMP", "WBMP" , "GIF"};



    public ImageResponse saveImageInDB(MultipartFile file,String url) throws IOException {


            //Making imageEntity object
            ImageEntity image =  ImageEntity.builder()
                    .type(file.getContentType())
                    .fileName(file.getOriginalFilename())
                    .fileSize(file.getSize())
                    .url(url)
                    .build();

            //Saving in db
            image =   imageRepo.save(image);

            ImageResponse response = this.convertToImageResponse(image);

            return response;
        }


    public ImageResponse saveImageInDB(File file,String url) throws IOException {


        Path filePath = file.toPath();
        //Making imageEntity object
        ImageEntity image =  ImageEntity.builder()
                .fileName(file.getName())
                .fileSize(file.length())
                .type(Files.probeContentType(filePath))
                .url(url)
                .build();



        //Saving in db
        image =   imageRepo.save(image);

        ImageResponse response = this.convertToImageResponse(image);

        return response;
    }

    //Removed user dependency in ImageEntity

//    public ImageResponse saveImageInDB(MultipartFile file,String url) throws IOException {
//        String username = securityUtil.getUsernameFromAuth();
//
//        if(username!=null)
//        {
//
//            //Getting the user
//            User user = userRepo.findByUsername(username)
//                    .orElse(null);
//
//
//            //Making imageEntity object
//            ImageEntity image =  ImageEntity.builder()
//                    .type(file.getContentType())
//                    .user(user)
//                    .fileName(file.getOriginalFilename())
//                    .fileSize(file.getSize())
//                    .url(url)
//                    .build();
//
//            //Saving in db
//            image =   imageRepo.save(image);
//
//            ImageResponse response = this.convertToImageResponse(image);
//
//            return response;
//
//        }
//        else {
//            log.error("Failed to load username");
//            return null;
//        }
//
//    }



    private ImageResponse convertToImageResponse(ImageEntity image)
    {

        ImageResponse response = ImageResponse.builder()
                .imageId(image.getId())
                .url(image.getUrl())
                .fileSize(image.getFileSize())
                .type(image.getType())
                .build();

        return response;
    }

    public ImageResponse saveImage(MultipartFile file) throws IOException {
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdir();
        }

        //Writing image to server

        String systemFileName = System.currentTimeMillis()+"_"+file.getOriginalFilename();
        //File serverImageFile  = new File(uploadDir+systemFileName);
        Path filePath = Paths.get(uploadDir+systemFileName);
        //Files.write(filePath,file.getBytes());
        file.transferTo(filePath);

        String fileUrl = "http://localhost:"+serverPort+"/"+uploadDir+ systemFileName;

        ImageResponse response = this.saveImageInDB(file,fileUrl);

        return response;

    }

    public ImageResponse getImageFromId(Long id) throws Exception {


        try{
            ImageEntity image = imageRepo.findById(id).orElse(null);
            if(image!=null)
            {
                ImageResponse response = convertToImageResponse(image);
                return response;
            }
            else {
                throw new EntityNotFoundException("Image is not found");
            }

        }catch (Exception e)
        {
            e.printStackTrace();
            throw  new Exception(e.getMessage());
        }

    }


    public List<ImageResponse> getServerImages(int pageNo,int pageSize) throws Exception
    {

        try{
            Pageable pageable =  PageRequest.of(pageNo,pageSize);
            List<ImageEntity> userImages = imageRepo.findAll(pageable).toList();
            List<ImageResponse>responseList = userImages.stream().map(this::convertToImageResponse).toList();

            return responseList;

        }catch (Exception e)
        {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    public ImageResponse changeFormatAndSaveImage(BufferedImage image,String format,String url) throws Exception {

        int counter = 1;
        if(format!=null)
        {
            if(Arrays.stream(allowedFormat).anyMatch(f -> f.equals(format)))
            {
                String filename = System.currentTimeMillis()+"_Transformed_"+ counter+++"."+format.toLowerCase();
                String path = uploadDir+filename;
                ImageIO.write(image,format,new File(path));

                File newFile = new File(path);

                String fileUrl = "http://localhost:"+serverPort+"/"+uploadDir+ filename;

                ImageResponse response = this.saveImageInDB(newFile,fileUrl);

                return response;

            }
            else {
                throw new Exception("Incorrect format type");
            }



        }
        else {
            String filename = System.currentTimeMillis()+"_Transformed_"+ counter++ +".png";
            String path = uploadDir+filename;
            ImageIO.write(image,"PNG",new File(path));

            File newFile = new File(path);

            String fileUrl = "http://localhost:"+serverPort+"/"+uploadDir+ filename;

            ImageResponse response = this.saveImageInDB(newFile,fileUrl);

            return response;

        }
    }

    public ImageResponse transformImage(ImageResponse image, TransformationRequest.Transformations transformations) throws Exception {
      BufferedImage transformedImage =  imageUtil.convertImage(image.getUrl(),transformations);

       ImageResponse newImageResponse = this.changeFormatAndSaveImage(transformedImage,transformations.getFormat(),image.getUrl());

        return newImageResponse;

    }

}
