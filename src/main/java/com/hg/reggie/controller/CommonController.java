package com.hg.reggie.controller;

import com.hg.reggie.common.R;
import io.lettuce.core.dynamic.annotation.Param;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

/**
 * @author hougen
 * @program Reggie
 * @description 文件上传下载
 * @create 2022-11-14 20:27
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    /**
     * 文件上传路径
     */
    @Value("${reggie.path}")
    private String filePath;

    /**
     * 文件上传
     * @param file  临时文件,需要转存到指定位置,否则本次请求完成后,临时文件会消失
     * @return
     */
    @PostMapping("/upload")
    public R<Object> upload(MultipartFile file){
        log.info("{}文件上传成功...",file.getOriginalFilename() );


        //原始文件名
        String originalFilename = file.getOriginalFilename();
        //获取源文件后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //通过uuid生成文件名称
        String fileName = UUID.randomUUID() +suffix;

        //判断存放文件路径是否存在,不存在则创建目录
        File dir = new File(filePath);
        if( !dir.exists() ){
            dir.mkdirs();
        }

        //将临时文件转存到指定位置
        try {
            file.transferTo(new File(filePath+fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success((Object)fileName);
    }

    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        try {
            //读取文件
            FileInputStream inputStream = new FileInputStream(filePath + name);
            ServletOutputStream outputStream = response.getOutputStream();
            response.setContentType("image/jpeg");

            int len;
            byte[] bytes = new byte[1024];
            while ( (len = inputStream.read(bytes)) != -1 ){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}


