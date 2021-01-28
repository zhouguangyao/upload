package com.inodes.third;

import com.inodes.third.config.UploadConfig;
import com.inodes.third.common.ResumableInfoStorage;
import com.inodes.third.entity.ResumableInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Component
public class Resumable {
    @Autowired
    UploadConfig config;

    public ResumableInfo upload(String category, HttpServletRequest request) {
        int resumableChunkNumber = getResumableChunkNumber(request);

        ResumableInfo info = null;
        try {
            info = getResumableInfo(category, request);

            RandomAccessFile raf = new RandomAccessFile(info.resumableFilePath, "rw");

            //Seek to position
            raf.seek((resumableChunkNumber - 1) * (long) info.resumableChunkSize);

            //Save to file
            InputStream is = request.getInputStream();
            long readed = 0;
            long content_length = request.getContentLength();
            byte[] bytes = new byte[1024 * 100];
            while (readed < content_length) {
                int r = is.read(bytes);
                if (r < 0) {
                    break;
                }
                raf.write(bytes, 0, r);
                readed += r;
            }
            raf.close();
            //Mark as uploaded.
            info.uploadedChunks.add(new ResumableInfo.ResumableChunkNumber(resumableChunkNumber));
            if (info.checkIfUploadFinished()) { //Check if all chunks uploaded, and change filename
                ResumableInfoStorage.getInstance().remove(info);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return info;
    }

    private int getResumableChunkNumber(HttpServletRequest request) {
        return toInt(request.getParameter(config.getChunkNumberParameterName()), -1);
    }

    private ResumableInfo getResumableInfo(String category, HttpServletRequest request) throws Exception {
        String baseDir = config.getRootPath();

        long resumableChunkSize = toLong(request.getParameter(config.getCurrentChunkSizeParameterName()), -1);
        long resumableTotalSize = toLong(request.getParameter(config.getTotalSizeParameterName()), -1);
        String resumableIdentifier = request.getParameter(config.getIdentifierParameterName());
        String resumableFilename = request.getParameter(config.getFileNameParameterName());
        String resumableRelativePath = request.getParameter(config.getRelativePathParameterName());
        //Here we add a ".temp" to every upload file to indicate NON-FINISHED

        String originalFilename = resumableFilename;
        String suffixName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        //文件类型目录
        String suffixPath = suffixName;
        //日期目录
        String datePath = new StringBuffer()
                .append(new SimpleDateFormat("yyyy").format(new Date()))
                .append("/")
                .append(new SimpleDateFormat("MM").format(new Date()))
                .append("/")
                .append(new SimpleDateFormat("dd").format(new Date()))
                .toString();
        String fullPath = config.getRootPath() + "/" + category + "/" + suffixPath + "/" + datePath;
        String visitPath = config.getVisitPrefix() + "/" + category + "/" + suffixPath + "/" + datePath;
        //文件名
        String fileName = UUID.randomUUID() + "." + suffixName;

        File filepathFile = new File(fullPath);
        if (!filepathFile.exists()) filepathFile.mkdirs();
        String resumableFilePath = new File(fullPath, fileName).getAbsolutePath() + ".temp";

        ResumableInfoStorage storage = ResumableInfoStorage.getInstance();

        ResumableInfo info = storage.get(resumableChunkSize, resumableTotalSize,
                resumableIdentifier, resumableFilename, resumableRelativePath, resumableFilePath);
        if (!info.vaild()) {
            storage.remove(info);
            throw new Exception("Invalid request params.");
        }
        info.url = getVisitPath(info.resumableFilePath);
        return info;
    }

    /**
     * 校验是否图片文件
     *
     * @param filename
     * @return
     */
    private static boolean verifyImages(String filename) {
        Boolean flag = false;
        //图片格式
        String[] FILETYPES = new String[]{
                ".jpg", ".bmp", ".jpeg", ".png", ".gif",
                ".JPG", ".BMP", ".JPEG", ".PNG", ".GIF"
        };
        if (!StringUtils.isEmpty(filename)) {
            for (int i = 0; i < FILETYPES.length; i++) {
                String fileType = FILETYPES[i];
                if (filename.endsWith(fileType)) {
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }

    /**
     * Convert String to long
     *
     * @param value
     * @param def   default value
     * @return
     */
    public long toLong(String value, long def) {
        if (StringUtils.isEmpty(value)) {
            return def;
        }

        try {
            return Long.valueOf(value);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return def;
        }
    }

    /**
     * Convert String to int
     *
     * @param value
     * @param def   default value
     * @return
     */
    public int toInt(String value, int def) {
        if (StringUtils.isEmpty(value)) {
            return def;
        }
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return def;
        }
    }

    /**
     * 获取访问路径
     *
     * @param resumableRelativePath
     * @return
     */
    public String getVisitPath(String resumableRelativePath) {
        if (resumableRelativePath.indexOf(":") > -1) {
            String[] split = resumableRelativePath.split(":");
            if (split.length == 2) {
                resumableRelativePath = split[1];
            }
        }
        resumableRelativePath = resumableRelativePath.replace("\\", "/").replace(config.getRootPath(), config.getVisitPrefix()).replace(".temp", "");
        return resumableRelativePath;
    }
}
