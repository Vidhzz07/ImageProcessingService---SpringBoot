package com.ImageService.ImageProcessing.utils;

import com.ImageService.ImageProcessing.dataModels.TransformationRequest;
import org.apache.coyote.BadRequestException;
import org.imgscalr.Scalr;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;


@Component
public class ImageUtil {

    public  BufferedImage resize(BufferedImage img, int width, int height) {
        return Scalr.resize(img, Scalr.Method.QUALITY, width, height);
    }

    public BufferedImage crop(BufferedImage img, int x, int y, int width, int height) {
        return Scalr.crop(img, x, y, width, height);
    }

    public BufferedImage rotate(BufferedImage img, Scalr.Rotation type) {
        return Scalr.rotate(img, type);
    }

    public BufferedImage applyGrayscale(BufferedImage img) {
        return Scalr.apply(img, Scalr.OP_GRAYSCALE);
    }


    public void convertImage(String imageUrl, TransformationRequest.Transformations transformations) throws Exception {

        String filePath = imageUrl.substring(imageUrl.indexOf("src"));

        BufferedImage image = ImageIO.read(new File(filePath));


        if(transformations.getResize()!=null)
        {
            TransformationRequest.Resize resize = transformations.getResize();
            image = resize(image,resize.getWidth(),resize.getHeight());
        }
        if(transformations.getCrop()!=null)
        {
            TransformationRequest.Crop crop = transformations.getCrop();
            image = crop(image,crop.getX(),crop.getY(),crop.getWidth(),crop.getHeight());
        }
        if(transformations.getRotate()!=null)
        {
            Integer rotateNo = transformations.getRotate();
             switch (rotateNo){
                 case 0 : {image= rotate(image, Scalr.Rotation.CW_90);break;}
                 case 1 : {image= rotate(image, Scalr.Rotation.CW_180);break;}
                 case 2 : {image= rotate(image, Scalr.Rotation.CW_270);break;}
                 case 3 : {image= rotate(image, Scalr.Rotation.FLIP_HORZ);break;}
                 case 4 : {image= rotate(image, Scalr.Rotation.FLIP_VERT);break;}
             }
        }
        if(transformations.getFilters()!=null)
        {
            if(transformations.getFilters().getGrayscale()==true)
            {
                image=applyGrayscale(image);
            }
        }

        ImageIO.write(image,"png",new File(filePath));

    }
}
