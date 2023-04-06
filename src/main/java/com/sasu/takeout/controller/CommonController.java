package com.sasu.takeout.controller;

import com.sasu.takeout.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 文件上传下载
 */
@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${takeOutFile.fileLocaltion}")
    String pathName;

    /**
     * 文件上传：底层实现原理前端from表单必须采用post请求
     * @param file 与前端名字相同
     * @return
     */
    @RequestMapping("/upload")
    public Result<String> upload(MultipartFile file){
        //file文件是一个临时文件，需要转存

        //获取源文件的文件名
        String originalFilename = file.getOriginalFilename();
        //截取源文件的后缀名
        String substring = originalFilename.substring(originalFilename.lastIndexOf("."));

        //通过uuid获取随机名字
        String randomName = UUID.randomUUID().toString();
        //String randomName = LocalDateTime.now().toString();

        //拼接新文件名
        String fileName = randomName+substring;

        //判断目录结构 是否存在
        File dir = new File(pathName);
        //当前目录结构不存在的时候创建
        if (!dir.exists()){
            dir.mkdirs();
        }
        try {
            file.transferTo(new File(pathName+fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Result.success(fileName);
    }

    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){

        try {

            FileInputStream fileInputStream = new FileInputStream(new File(pathName+name));

            ServletOutputStream outputStream = response.getOutputStream();

            response.setContentType("image/jpeg");

            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }

            fileInputStream.close();
            outputStream.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
