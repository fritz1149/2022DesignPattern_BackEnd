package com.dp.file.controller;

import com.dp.file.entity.MyFile;
import com.dp.file.service.FileService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.websocket.server.PathParam;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
//@RequestMapping("/file")
public class FileController {
    @Autowired
    FileService fileService;
    String serverAddress = "http://82.156.59.244/";
    @Value("${file-path}")
    private String FilePath;

    @ApiOperation("上传文件")
    @PostMapping("/upload")
    public Object uploadFile(@ApiParam("文件本体") @RequestParam("file") MultipartFile file,
                             @ApiParam("用户id") @RequestParam("userId") String uID,
                             @ApiParam("文件类型") @RequestParam("type") String type) {
        Map<String, Object> result = new HashMap<>(), data = new HashMap<>();
        result.put("data", data);
        try {
//            File path = new File(System.getProperty("user.dir"));
            File path = new File(FilePath);
            String curDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
//            String pathPrefix = "dpFiles/dpUid_" + uID + "/FileStorage/" + curDate + "/";
            LocalDateTime currentDateTime = LocalDateTime.now();
            String curTime = currentDateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String fileName = file.getOriginalFilename();
            assert fileName != null;
            int split = fileName.lastIndexOf(".");
            String suffix = fileName.substring(split);
            String prefix = fileName.substring(0, Math.min(split, 200));
            String fileStoredName = prefix + "_" + curTime + "_" + uID + suffix;
            if (type.equals("image") && !(".jpg".equals(suffix) || ".jpeg".equals(suffix) || ".png".equals(suffix) || ".gif".equals(suffix))) {
                result.put("msg", "Pictures in this format are not supported.");
                result.put("code", 500);
            } else {
                try {
                    File upload = new File(path.getAbsolutePath());
                    if (!upload.exists()) {
                        upload.mkdirs();
                    }
                    File savedFile = new File(upload + "/" + fileStoredName);
                    System.out.println("fsn: " + fileStoredName);
                    file.transferTo(savedFile);
                    String absolutePath = savedFile.getAbsolutePath();

                    result.put("msg", "上传成功");
                    result.put("code", 200);
//                    data.put("savedPath", absolutePath);
                    data.put("savedPath", fileStoredName);
                    MyFile myFile = new MyFile(
                            Long.valueOf(uID),
                            fileName,
//                            absolutePath,
                            fileStoredName,
                            currentDateTime,
                            file.getSize(),
                            type
                    );
                    Long fileId = fileService.addFileRecord(myFile);
                    data.put("fileId", fileId);
                } catch (IOException e) {
                    e.printStackTrace();
                    result.put("msg", "上传失败" + e.getMessage());
                    result.put("code", 500);
                }
            }
        } catch (Exception e) {
            result.put("msg", "上传失败" + e.getMessage());
            result.put("code", 500);
        }
        return result;
    }

    @ApiOperation("下载文件")
    @GetMapping("/download/{filePath}")
    public ResponseEntity<FileSystemResource> downloadFile(@ApiParam("文件名") @PathVariable String filePath) {
        File file = new File(FilePath, filePath);
        System.out.println(file.getAbsolutePath());
        if (!file.exists()) {
            return null;
        } else {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            System.out.println(file.getName());
            headers.add("Content-Disposition", "attachment; filename=" + file.getName());
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");
            headers.add("Last-Modified", new Date().toString());
            headers.add("ETag", String.valueOf(System.currentTimeMillis()));
            return ResponseEntity.ok().headers(headers)
                    .contentLength(file.length())
                    .contentType(MediaType.parseMediaType("application/octet-stream"))
                    .body(new FileSystemResource(file));
        }
    }

    @ApiOperation("文件信息")
    @PostMapping("/info")
    public Object getFileInfo(@RequestParam("type") String type, @RequestParam("key") String key) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (type.equals("fileAbsolutePath")) {//根据绝对地址查找单个文件
                File file = new File(key);
                if (!file.exists()) {
                    result.put("msg", "文件不存在！");
                    result.put("code", 500);
                } else {
                    List<MyFile> files = fileService.getFileRecordByFilePath(key);
                    if (files.size() == 1) {
                        result.put("data", files.get(0));
                        result.put("msg", "查询成功");
                        result.put("code", 200);
                    } else {
                        result.put("msg", "找到了" + files.size() + "个文件记录");
                        result.put("code", 200);
                    }
                }
            } else if (type.equals("fileId")) {//根据id查找单个文件
                result.put("data", fileService.getFileById(Long.valueOf(key)));
                result.put("msg", "查询成功");
                result.put("code", 200);
            } else if (type.equals("searchName")) {
//                List<MyFile> myFiles = fileService.searchFilesRecordByName(key);
                result.put("data", fileService.searchFilesRecordByName(key));
                result.put("msg", "查询成功");
                result.put("code", 200);
            }
        } catch (Exception e) {
            result.put("msg", "查询失败！" + e.getMessage());
            result.put("code", 500);
        }
        return result;
    }


}
