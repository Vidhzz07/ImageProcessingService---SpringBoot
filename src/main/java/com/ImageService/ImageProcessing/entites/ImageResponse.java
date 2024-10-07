package com.ImageService.ImageProcessing.entites;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ImageResponse {

    private Long imageId;
    private String url;
    private String type;
    private Long fileSize;

}
