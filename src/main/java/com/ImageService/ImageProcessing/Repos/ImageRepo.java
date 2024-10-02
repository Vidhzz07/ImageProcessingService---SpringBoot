package com.ImageService.ImageProcessing.Repos;

import com.ImageService.ImageProcessing.entites.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepo  extends JpaRepository<ImageEntity,Long> {

}
