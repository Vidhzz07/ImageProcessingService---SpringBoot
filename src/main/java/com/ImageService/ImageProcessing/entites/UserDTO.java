package com.ImageService.ImageProcessing.entites;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class UserDTO {

    private String username;
    private String password;


}
