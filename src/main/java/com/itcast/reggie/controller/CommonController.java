package com.itcast.reggie.controller;

import com.itcast.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.print.attribute.standard.PresentationDirection;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.UUID;

/*
文件的上传和下载

所谓上传,是将网络上的信息存储到我们的电脑上,我们为其创建一个目录或者存放的文件夹
而下载,则是由我们传输回客户端(即网页中),这两个动作的主语都是网页而不是电脑!!!

这里的控制器都是通用的,即只要是有关上传下载的操作都会在这里执行

 */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {
    @Value("${reggie.path}")
    private String basePath;
    @PostMapping("/upload")
    //这里的参数名必须与前端页面中调教表单的name属性一致!!!否则是接收不到文件的
    public R<String> upload(MultipartFile file){
        //当前的file是一个临时文件,如果我们不对其进行转存,则在请求结束后file文件会自动删除
        log.info(file.toString());
        //获得原始文件名
        String filename = file.getOriginalFilename();

        //使用UUID重新取名,防止文件覆盖
        UUID uuid = UUID.randomUUID();
        //获得后缀!
        String suffix = filename.substring(filename.lastIndexOf("."));
        filename=uuid+suffix;

        //创建一个目录对象,并判断当前目录是否存在

        File dir=new File(basePath);
        if(!dir.exists()){
            //目录不存在,需要创建
            dir.mkdir();
        }
        try {
            //将这个临时文件转存到指定位置
            file.transferTo(new File(basePath+filename));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //将存储的文件名称返回给前端
        return R.success(filename);
    }
    /*
    文件下载

     */
    //通过输出流的形式返回数据
    //这一步我们需要两个操作,即以便读取本地的信息,一边向网页传输数据,所以要有两个流,一个input,一个output
    @GetMapping("/download")
    public void downLoad(String name, HttpServletResponse response)  {
        //输入流,通过输入流读取文件内容
        try {
            FileInputStream fileInputStream=new FileInputStream(new File(basePath+name));
            //输出流,通过输出流的方式将文件协会浏览器,在浏览器展示图片
            ServletOutputStream outputStream = response.getOutputStream();
            byte[] bytes=new byte[1024];
            int len=0;

            response.setContentType("imag/jpeg");

            while ((len=fileInputStream.read(bytes))!=-1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }
            fileInputStream.close();
            outputStream.close();

            outputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }





    }

}
