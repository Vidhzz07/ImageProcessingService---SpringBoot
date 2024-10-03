package com.ImageService.ImageProcessing.dataModels;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.web.multipart.MultipartFile;

@Data
public class TransformationRequest {

    private Transformations transformations;

    @Data
    public static class Transformations {
        private Resize resize;
        private Crop crop;
        private Integer rotate;
        private String format;
        private String watermarkText;
        private MultipartFile watermarkImageFile = null;
        private Filters filters;
    }

    @Data
    public static class Resize {
        @NonNull
        private Integer width;
        @NonNull
        private Integer height;
    }

    @Data
    public static class Crop {
        @NonNull
        private Integer width;
        @NonNull
        private Integer height;
        @NonNull
        private Integer x;
        @NonNull
        private Integer y;
    }

    @Data
    @NoArgsConstructor
    public static class Filters {
        @NonNull
        private Boolean grayscale;
    }
}
