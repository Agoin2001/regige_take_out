package com.wpx.renggie.controller;

import com.wpx.renggie.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件的上传与下载
 */
@RestController
@Slf4j
@RequestMapping("/common")
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;

    /**
     * 上传图片
     * @param file 文件名称，和前台传来的文件名称是一致的
     * @return
     */
    @PostMapping("/upload")
    public Result<String> upLoadFile(MultipartFile file)  {

        //这里的file只是一个临时的文件存储，临时存储到某一个位置，然后待接收完毕后再转存到目标位置上，然后再把这个临时文件删除
        //截取文件后缀
        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.'));
        //生成UUID
        String randomUUID = UUID.randomUUID().toString();
        //拼接文件最后名称，结果为文件本体名字+UUID+后缀
        String fileName = file.getOriginalFilename() + randomUUID + suffix;


        //创建一个目录对象
        File dir = new File(basePath);
        //判断当前目录是否存在
        if(!dir.exists()){
            //目录不存在
            dir.mkdirs();
        }


        try {
            file.transferTo(new File(basePath+fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //把文件的名字上传回去，方便后续回显读取路径
        return Result.success(fileName);
    }


    /**
     * 文件回显接口
     * @param response 响应对象
     * @param name 上传的文件名称
     * @throws IOException IO异常
     */
    @GetMapping("/download")
    public void downLoad(String name, HttpServletResponse response) throws IOException {

        try {
            //输入流，通过输入流读取文件内容
            FileInputStream fileInputStream=new FileInputStream(new File(basePath+name));
            //输出流，通过输出流将文件写回浏览器，在浏览器中展示图片
            ServletOutputStream outputStream = response.getOutputStream();

            int len=0;
            byte[] bytes = new byte[1024];

            //可选项，选择响应类型
            response.setContentType("image/jpeg/png/jpg");

            while ((len=fileInputStream.read(bytes))!=-1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }
            outputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

//        //输入流，通过输入流读取文件内容
//        FileInputStream fileInputStream =new FileInputStream("basePath"+name);
//
//        //输出流，通过输出流将文件写回浏览器，在浏览器中展示图片
//        ServletOutputStream outputStream = response.getOutputStream();
//
//        //可选项，选择响应类型
//        response.setContentType("image/jpeg/png/jpg");
//
//        //用byte数组写入，注意是小写b，不是大写，大写就是包装类了
//        byte[] fileArray = new byte[1024];
//        int length=0;
//        try {
//            //只要没读到数组的尾部就一直读下去，这部分是IO的内容
//            while ((length=fileInputStream.read(fileArray))!=-1) {
//                //写入响应流，从0开始，写入到数组末尾长度
//                outputStream.write(fileArray, 0, length);
//                //把流里的东西挤出来
//                outputStream.flush();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }finally {
//            //关闭流
//            fileInputStream.close();
//            outputStream.close();
//        }
//        return;
    }
}
