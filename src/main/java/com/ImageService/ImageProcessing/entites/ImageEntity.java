package com.ImageService.ImageProcessing.entites;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_Image")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String type;
    private String url;
    private String fileName;
    private Long fileSize;


    //Remove mapping

//    @ManyToOne()
//    @JoinColumn(name="user_id",nullable = false)
//    private  User user;

}
