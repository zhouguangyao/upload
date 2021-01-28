package com.inodes.third.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "upload",ignoreUnknownFields = false)
@Component
public class UploadConfig {
    private String rootPath = "/upload";//上传根目录
    private String visitPrefix = "/upload";//上传根目录
    private Long chunkSize = 3 * 1024 *1024L;//单片大小
    private Integer simultaneousUploads = 5;//每次提交片数

    private String chunkNumberParameterName = "resumableChunkNumber";
    private String chunkSizeParameterName = "resumableChunkSize";
    private String currentChunkSizeParameterName = "resumableCurrentChunkSize";
    private String totalSizeParameterName = "resumableTotalSize";
    private String typeParameterName = "resumableType";
    private String identifierParameterName = "resumableIdentifier";
    private String fileNameParameterName = "resumableFilename";
    private String relativePathParameterName = "resumableRelativePath";
    private String totalChunksParameterName = "resumableTotalChunks";
}
