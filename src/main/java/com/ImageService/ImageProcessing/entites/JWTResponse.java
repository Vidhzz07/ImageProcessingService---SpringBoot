package com.ImageService.ImageProcessing.entites;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JWTResponse {

    private String username;
    private String atToken;
}
