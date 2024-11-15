package org.example.practice_platform_backend.service;

import org.example.practice_platform_backend.utils.ImageUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@Service
public class SendFileService {

    private final ImageUtils imageUtils;
    // 上传的路径
    @Value("${uploadPath}")
    private String uploadPath;
    // 用于映射视频文件路径
    private final Map<Integer, String> videoPathMap = Map.of(
            0,"community_video/community",
            1,"need_video/need",
            2,"fruit_video/fruit"
    );
    // 映射图片文件路径
    private final Map<Integer, String> imagePathMap = Map.of(
            0,"community_images/community",
            1,"need_images/need",
            2,"fruit_images/fruit"
    );

    public SendFileService(ImageUtils imageUtils) {
        this.imageUtils = imageUtils;
    }

    /**
     * 发送头像
     * @param filePath 文件路径
     * @return base64编码后的头像
     */
    public String sendAvatar(@RequestParam String filePath) throws IOException {
        String path = uploadPath + filePath;
        return imageUtils.getFileBytes(path);
    }

    /**
     * 发送高清原图
     * @param fileName 图片名字
     * @param type 类型，社区、需求、成果
     * @param id 对应的类型的 id
     * @return 图片
     */
    public String sendOriginalImage(@RequestParam String fileName, @RequestParam int type, @RequestParam int id)throws IOException{
        String path;
        if(Objects.equals(fileName, "default.jpg")){
            path = uploadPath + ImageUtils.getRealName(imagePathMap.get(type)) + "_images" + File.separator + fileName;
        }else {
            path = uploadPath + imagePathMap.get(type) + "_" + id + "/" + fileName;
        }
        return imageUtils.getFileBytes(path);
    }

    /**
     * 发送缩略图
     * @param fileName 图片名字
     * @param type 类型，社区、需求、成果
     * @param id 对应的类型的 id
     * @return 图片
     */
    public String sendImage(@RequestParam String fileName, @RequestParam int type, @RequestParam int id)throws IOException{
        String path;
        // 处理默认图片的情况
        if(Objects.equals(fileName, "default.jpg")){
            path = uploadPath + ImageUtils.getRealName(imagePathMap.get(type)) + "_images" + File.separator + fileName;
        }else {
            path = uploadPath + imagePathMap.get(type) + "_" + id + "/" + fileName;
        }
        return imageUtils.getThumbnail(path);
    }

    /**
     * 获取 m3u8 文件夹下的文件
     * @param fileName 文件名
     * @param type 类型，社区、需求、成果
     * @param id 对应的类型的 id
     * @return 处理结果
     */
    public byte[] sendM3u8File(@RequestParam String fileName, @RequestParam int type, @RequestParam int id) throws IOException {
        String path = uploadPath + videoPathMap.get(type) + "_" + id + "/m3u8/" + fileName;
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        // 读取M3U8文件内容
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        fileInputStream.read(data);
        fileInputStream.close();
        return data;
    }
}
