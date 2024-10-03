package com.ImageService.ImageProcessing.utils;

import com.ImageService.ImageProcessing.dataModels.TransformationRequest;
import org.apache.coyote.BadRequestException;
import org.imgscalr.Scalr;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;


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


    public BufferedImage addTextWatermark(BufferedImage image, String text)
    {
        Graphics2D g2d = (Graphics2D) image.getGraphics();

        // initializes necessary graphic properties
        AlphaComposite alphaChannel = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f);
        g2d.setComposite(alphaChannel);
        g2d.setColor(Color.RED);
        g2d.setFont(new Font("Arial", Font.BOLD, 100));
        FontMetrics fontMetrics = g2d.getFontMetrics();
        Rectangle2D rect = fontMetrics.getStringBounds(text, g2d);

        // calculates the centre coordinates to paint water mark
        int centerX = (image.getWidth() - (int) rect.getWidth()) / 2;
        int centerY = image.getHeight() / 2;

        g2d.drawString(text, centerX, centerY);

        return image;
    }

    public BufferedImage addImageWatermark(BufferedImage image, MultipartFile watermarkImageFile) throws IOException {
        BufferedImage watermarkImage = ImageIO.read(watermarkImageFile.getInputStream());
        watermarkImage = resize(watermarkImage,500,500);

        Graphics2D g2d = (Graphics2D) image.getGraphics();
        AlphaComposite alphaChannel = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f);
        g2d.setComposite(alphaChannel);

        // calculates the coordinate where the image is painted
        int topLeftX = (image.getWidth() - watermarkImage.getWidth()) / 2;
        int topLeftY = (image.getHeight() - watermarkImage.getHeight()) / 2;

        // paints the image watermark
        g2d.drawImage(watermarkImage, topLeftX, topLeftY, null);

        return image;
    }



    public BufferedImage convertImage(String imageUrl, TransformationRequest.Transformations transformations) throws Exception {

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
        if(transformations.getWatermarkText()!=null)
        {
            image = addTextWatermark(image, transformations.getWatermarkText());
        }
        if(transformations.getWatermarkImageFile()!=null)
        {
            image = addImageWatermark(image,transformations.getWatermarkImageFile());
        }

        return image;


        //Formating does not work ,need to check

//        String format = transformations.getFormat();
//        if(format!=null)
//        {
//            if(Arrays.stream(allowedFormat).anyMatch(f -> f.equals(format)))
//            {
//
//                try{
//                    ImageIO.write(image,format,new File(filePath));
//                }
//                catch (Exception e)
//                {
//                    e.printStackTrace();
//                }
//
//            }
//            else throw new Exception("This format is not supported");
//        }
//        else {
//            ImageIO.write(image,"png",new File(filePath));
//        }



    }
}
