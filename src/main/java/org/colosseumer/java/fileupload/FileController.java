package org.colosseumer.java.fileupload;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.colosseumer.java.fileupload.utils.FileTypeEnum;
import org.colosseumer.java.fileupload.utils.ImageThumbUtil;
import org.colosseumer.java.fileupload.utils.UploadRspBo;
import org.colosseumer.java.fileupload.utils.UploadSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 文件上传
 * @Author: zhaoyu
 * @Date: 2019/10/17
 */

@RestController
@RequestMapping("/file")
public class FileController {

	@Autowired
	private UploadSupport uploadSupport;

	private static Logger log = LoggerFactory.getLogger(FileController.class);

	/**
	 * @Description 单文件上传。
	 * @Return String
	 */
	@PostMapping("/upload")
	public UploadRspBo upload(@RequestParam(value = "file", required = true) MultipartFile file) {
		if (file.isEmpty()) {
			throw new RuntimeException("上传文件不能为空");
		}
		UploadRspBo bo = null;
		FileTypeEnum fileType = uploadSupport.getUploadType(file);
		if (fileType == null) {
			throw new RuntimeException("错误的文件类型");
		}
		String url = "";
		try {
			log.info("upload file:" + file.getOriginalFilename());
			bo=uploadSupport.simpleUpload(file, fileType);
		} catch (FileUploadException e) {
			throw new RuntimeException("上传文件错误");
		}
		return bo;
	}



	//表单提交，文件临时名称更改为正式名称，并保存。同时对应的临时文件名重命名为正式文件名。
	@PostMapping("/add")
	public String upload(@RequestParam(value = "uploadList") String uploadList) {
		ObjectMapper mapper = new ObjectMapper();
		List<UploadRspBo> uploadRspBoList= null;
		try {
			uploadRspBoList = mapper.readValue(uploadList, new TypeReference<List<UploadRspBo>>() {});
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new RuntimeException("输入参数错误");
		}

		//finalUploadList 包含的名称是正式名称。
		List<UploadRspBo> finalUploadList = uploadSupport.getFinalUrl(uploadRspBoList);
		System.out.println(finalUploadList);

		//todo 存储 finalUploadList

		uploadSupport.temp2Final(uploadRspBoList);
		return "success";
	}
}
