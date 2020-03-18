package org.colosseumer.java.fileupload.utils;

import lombok.Data;

/**
 * @Description: 文件上传成功返回对象
 * @Author: zhaoyu
 * @Date: 2019/10/19
 */
@Data
public class UploadRspBo {
    //mime类型
    private String mimeType;
    //文件路径
    private String fileUrl;
    //如果是图片，缩略图
    private String thumb;
    //
    private FileTypeEnum fileType;
}
