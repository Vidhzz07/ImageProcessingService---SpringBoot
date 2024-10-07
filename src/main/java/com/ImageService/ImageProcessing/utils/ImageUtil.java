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

    public BufferedImage applySepia(BufferedImage image)
    {
        int width = image.getWidth(),height = image.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int p = image.getRGB(x, y);

                int a = (p >> 24) & 0xff;
                int R = (p >> 16) & 0xff;
                int G = (p >> 8) & 0xff;
                int B = p & 0xff;

                // calculate newRed, newGreen, newBlue using formula for sepia RGB values


                int newRed = (int) (0.393 * R + 0.769 * G + 0.189 * B);
                int newGreen = (int) (0.349 * R + 0.686 * G + 0.168 * B);
                int newBlue = (int) (0.272 * R + 0.534 * G + 0.131 * B);

                // check condition for max 255 value

                newRed = newRed<=255?newRed:255;
                newBlue = newBlue<=255?newBlue:255;
                newGreen = newGreen<=255?newGreen:255;

                // set new RGB value
                p = (a << 24) | (R << 16) | (G << 8) | B;

                image.setRGB(x, y, p);
            }
        }
            return image;
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

    public BufferedImage flipImage(BufferedImage image)
    {
        Graphics2D g2d = (Graphics2D) image.getGraphics();

        int width = image.getWidth(),height = image.getHeight();
        int x =image.getMinX() ,y = image.getMinY();

        g2d.drawImage(image, x + width, y, -width, height, null);

        return image;

    }

    public BufferedImage mirrorImage(BufferedImage image)
    {
        int width = image.getWidth(),height = image.getHeight();
        BufferedImage mirrorImage = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < height; y++) {
            for (int lx = 0, rx = width - 1; lx < width; lx++, rx--) {

                int p = image.getRGB(lx, y);

                // set mirror image pixel value
                mirrorImage.setRGB(rx, y, p);
            }
        }

        return mirrorImage;
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
        if(transformations.getMirrorImage()==true)
        {
            image = this.mirrorImage(image);
        }
        if(transformations.getFlipImage()==true)
        {
            image=this.flipImage(image);
        }


        if(transformations.getFilters()!=null)
        {
            if(transformations.getFilters().getGrayscale()==true)
            {
                image=applyGrayscale(image);
            }
            if(transformations.getFilters().getSepia()==true)
            {
                image=this.applySepia(image);
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



    }
}
