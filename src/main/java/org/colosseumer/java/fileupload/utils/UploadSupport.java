package org.colosseumer.java.fileupload.utils;

import lombok.extern.apachecommons.CommonsLog;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.colosseumer.java.utils.DateUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 文件上传工具
 * @Author: zhaoyu
 * @Date: 2019/10/19
 */
@CommonsLog
@Component
public class UploadSupport {

    private final Map<String, FileTypeEnum> MIME_TYPE_MAP = new HashMap<>();

    private static  final String PATH_SEPARATOR="/";

    private static final String TEMP_PREFFIX = "[temp]";

    private static final String DATE_PATTERN="^[1-9][0-9]{3}(0[1-9]|1[0-2])(0[1-9]|[1-2][0-9]|3[0-1])$";

    @Value("${hesicare.maxFileNumber}")
    private Integer maxFileNumber;

    @Value("${hesicare.uploadLinuxLoc}")
    private String uploadLinuxLoc;

    @Value("${hesicare.uploadWinLoc}")
    private String uploadWinLoc;

    @Value("${hesicare.accessPath}")
    private String accessPath;

    public Integer getMaxFileNumber() {
        return maxFileNumber;
    }

    public UploadSupport() {
        //image
        MIME_TYPE_MAP.put("image/png", FileTypeEnum.img);
        MIME_TYPE_MAP.put("image/png", FileTypeEnum.img);
        MIME_TYPE_MAP.put("image/jpeg", FileTypeEnum.img);
        MIME_TYPE_MAP.put("image/x-ms-bmp", FileTypeEnum.img);
        MIME_TYPE_MAP.put("image/webp", FileTypeEnum.img);

        //pdf
        MIME_TYPE_MAP.put("application/pdf", FileTypeEnum.pdf);
    }

    /**
     * @Description 根据Mime类型，取得实际类型
     * @Author zhaoyu
     * @Date 2019/10/19
     * @Param file
     * @Return boolean
     */
    public final FileTypeEnum getUploadType(MultipartFile file) {
        String mimeType = file.getContentType();
        return MIME_TYPE_MAP.get(mimeType);
    }


    /**
     * @Description 获取文件名的后缀
     * @Author zhaoyu
     * @Date 2019/10/19
     * @Param file 表单文件
     * @Return java.lang.String
     */
    public final String getExtension(MultipartFile file) {
        String fileName=file.getOriginalFilename();
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
        return extension;
    }

    /**
     * @Description 根据os类型返回文件上传路径。
     * @Author zhaoyu
     * @Date 2019/10/21
     * @Param
     * @Return java.lang.String
     */
    public final String getUploadLoc() {
        String ret ="";
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            //若当前系统是window系统
            ret = uploadWinLoc;
        } else {
            //若当前系统是linux系统
            ret=uploadLinuxLoc;
        }
        return ret;
    }

    /**
     * @Description 获取上传文件的路径名称。绝对路径。
     * @Author zhaoyu
     * @Date 2019/10/21
     * @Param fileType 文件类型
     * @Param exTension 后缀
     * @Return java.lang.String
     */
    public final String getUploadPath(FileTypeEnum fileType,String exTension) {
        return this.getUploadLoc()+getRelativePath(fileType,exTension);
    }

    /**
     * 根据相对访问路径，获取文件的绝对路径。
     */
    public final String getUploadPath(String url) {
        String relativePath=url.replace(accessPath+PATH_SEPARATOR,"");
        return getUploadLoc()+relativePath;
    }

    /**
     * @Description 生产上传文件的路径和名称，如：img/20191021/947c8177-649b-4c2c-8293-bbf192c495eb.png
     * @Author zhaoyu
     * @Date 2019/10/21
     * @Param fileType
	 * @Param exTension
     * @Return java.lang.String
     */
    private final String getRelativePath(FileTypeEnum fileType,String exTension) {
        StringBuilder sb = new StringBuilder();
        sb.append(fileType).append(PATH_SEPARATOR)
                .append(DateUtil.dateTime()).append(PATH_SEPARATOR)
                .append(TEMP_PREFFIX)
                .append(UUID.randomUUID()).append(".").append(exTension);
        return sb.toString();
    }

    /**
     * @Description 根据上传path获取访问的url。
     * @Author zhaoyu
     * @Date 2019/10/21
     * @Param
     * @Return java.lang.String
     */
    public final  String getAccessUrl(String uploadPath){
        StringBuilder sb = new StringBuilder();
        sb.append(accessPath)
                .append(PATH_SEPARATOR)
                .append(uploadPath.replace(this.getUploadLoc(),""));
        return sb.toString();
    }

    /**
     * @Description 临时文件名更改为正式文件名。
     * @Author zhaoyu
     * @Date 2019/10/21
     * @Param
     * @Return java.lang.String
     */
    private final  String getFinalUrl(String tempUrl){
        return tempUrl.replace(TEMP_PREFFIX,"");
    }

    /**
     * @Description 临时文件名更改为正式文件名。
     * @Author zhaoyu
     * @Date 2019/10/21
     * @Param
     * @Return java.lang.String
     */
    public final  List<UploadRspBo> getFinalUrl(List<UploadRspBo> uploadRspBoList){
        return uploadRspBoList.stream().map(u -> {
            UploadRspBo bo=new UploadRspBo();
            BeanUtils.copyProperties(u, bo);
            bo.setFileUrl(getFinalUrl(u.getFileUrl()));
            if (bo.getFileType() == FileTypeEnum.img) {
                bo.setThumb(getFinalUrl(u.getThumb()));
            }
            return bo;
        }).collect(Collectors.toList());
    }

    /**
     * 将临时文件重命名为正式的文件，如果是图片，对应的压缩文件也重命名。
     * @param uploadRspBoList
     */
    public void temp2Final(List<UploadRspBo> uploadRspBoList) {
        for (UploadRspBo bo : uploadRspBoList) {
            temp2Final(bo.getFileUrl());
            if (bo.getFileType() == FileTypeEnum.img) {
                temp2Final(bo.getThumb());
            }
        }
    }

    /**
     * 根据访问url，将临时文件命名为正式文件。
     * @param tempUrl
     */
    private void temp2Final(String tempUrl) {
        String absolutePath = getUploadPath(tempUrl);
        File file = new File(absolutePath);
        File newFile = new File(getFinalUrl(absolutePath));
        file.renameTo(newFile);
    }

    /**
     * 上传文件,如果是图片，生成压缩图片。
     */
    public UploadRspBo simpleUpload(MultipartFile file, FileTypeEnum fileType) throws FileUploadException {
        String exTension = getExtension(file);
        String path = getUploadPath(fileType, exTension);
        File dest = new File(path);
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        UploadRspBo uploadRspBo = new UploadRspBo();
        try {
            file.transferTo(dest);
            // 如果是图片，生产缩略图
            if (fileType == FileTypeEnum.img) {
                String thumb = ImageThumbUtil.sacleSmallBySize(path);
                uploadRspBo.setThumb(getAccessUrl(thumb));
            }
        } catch (IOException e) {
            dest.deleteOnExit();
            throw new FileUploadException("上传文件失败");
        }
        uploadRspBo.setFileType(fileType);
        uploadRspBo.setFileUrl(getAccessUrl(path));
        return uploadRspBo;
    }

    /**
     * 清理date(包括date)以后到今天（不包含今天）的临时文件。只会清除上传目录的二级子目录（且名称是日期格式yyyyMMdd）下的临时文件。
     * 即临时文件是getUploadLoc()返回路径的三级子文件。如上传路径是d:/file/upload/
     * 那么上传临时文件的路径为d:/file/upload/pdf/20191125/[temp]xxx.pdf。
     */
    public void cleanTempFile(LocalDate date){
        LocalDate today=LocalDate.now();

        //加载上传路径的二级子目录。
        File uploadLoc = new File(getUploadLoc());
        File[] level1Children=uploadLoc.listFiles();
        List<File> level2Children = new ArrayList<>();
        for (File level1Child : level1Children) {
            level2Children.addAll(Arrays.asList(level1Child.listFiles()));
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate directoryDate;
        //清除目录下的临时文件。
        for (File level2Child : level2Children) {
            //如果是时间格式的文件夹
            if (level2Child.isDirectory()&&level2Child.getName().matches(DATE_PATTERN)) {
                directoryDate = LocalDate.parse(level2Child.getName(), formatter);
                if (date==null||(!directoryDate.isBefore(date) && directoryDate.isBefore(today))) {
                    log.info("扫描文件夹"+level2Child.getAbsolutePath());
                    cleanTemp(level2Child);
                }
            }
        }
    }

    /**
     * 清除当前目录下的临时文件。
     * @param file
     */
    private void cleanTemp(File file) {
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                if (!child.isDirectory() && child.getName().startsWith(TEMP_PREFFIX)) {
                    child.delete();
                    log.info("清除临时文件："+child.getAbsolutePath());
                }
            }
        }
    }

}
