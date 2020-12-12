package com.yankaizhang.excel.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.crypto.digest.MD5;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.yankaizhang.excel.constant.FileStatusConstant;
import com.yankaizhang.excel.entity.ChunkInfo;
import com.yankaizhang.excel.entity.FileInfo;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.channels.FileChannel;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 文件上传工具类
 */
public class FileUploadUtil {

    /**
     * 上传单个文件
     */
    public static FileInfo uploadSingleFile(MultipartFile file, HttpServletRequest request, String uploadUrl) throws Exception {

        // 获取绝对路径
        String path = request.getSession().getServletContext().getRealPath(uploadUrl);
        File dir = new File(path);
        if(!dir.exists()){
            boolean mkdirs = dir.mkdirs();
            if (!mkdirs){
                throw new Exception("创建存放文件夹失败");
            }
        }
        //原始文件名
        String trueName = file.getOriginalFilename();
        //文件类型
        String fileType = getFileExt(trueName);

        if(StringUtils.isBlank(fileType)){
            throw new Exception("文件后缀不能为空！");
        }

        String fileName = UUID.randomUUID().toString();
        String filePath = path + File.separator + fileName + "." + fileType;
        File newFile = new File(filePath);
        file.transferTo(newFile);


        FileInfo fileInfo = new FileInfo();

        fileInfo.setTrueName(trueName);
        fileInfo.setSaveName(fileName);
        fileInfo.setPath(filePath);
        fileInfo.setSize(file.getSize());
        fileInfo.setType(fileType);
        fileInfo.setCreated(new Date());
        fileInfo.setUrl(uploadUrl + "/" + fileName + "." + fileType);
        fileInfo.setStatus(FileStatusConstant.WAITING_INSERT);
        fileInfo.setDelFlag(-1);

        // 获取文件MD5
        String md5 = SecureUtil.md5(newFile);
        fileInfo.setFileMd5(md5);

        return  fileInfo;
    }

    /**
     * 获取文件拓展名
     * @param trueName
     * @return
     */
    private static String getFileExt(String trueName) {
        if(StringUtils.isBlank(trueName)){
            return "";
        }
        return trueName.substring(trueName.lastIndexOf(".") + 1).toLowerCase().trim();
    }

    /**
     * 上传分块
     */
    public static boolean uploadChunk(MultipartFile file, String chunkPath, String fileMd5, int chunk){

        //得到分块文件路径
        String chunkFilePath = genChunkFilePath(chunkPath, fileMd5);
        File chunkFileFolder = new File(chunkFilePath);
        if(!chunkFileFolder.exists()){
            boolean mkdirs = chunkFileFolder.mkdirs();
            if (!mkdirs){
                return false;
            }
        }

        File target = new File(chunkFileFolder.getAbsolutePath() + File.separator + chunk);

        try {
            file.transferTo(target);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 检查合并后文件MD5是否正确
     * @param mergeFile 合并后文件
     * @param fileMd5 目标文件md5
     */
    public static boolean checkFileMd5(File mergeFile, String trueName, String fileMd5) {
        String md5 = SecureUtil.md5(mergeFile) + SecureUtil.md5(trueName);
        System.out.println("md5 " + md5);
        System.out.println("fileMd5 " + fileMd5);
        return md5.equals(fileMd5);
    }

    /**
     * 合并文件
     * @param fileList 文件块列表
     * @param mergeFile 合并目标文件
     */
    public static File mergeFile(List<File> fileList, File mergeFile) {
        try {

            if (mergeFile.exists()) {
                mergeFile.delete();
            } else {
                mergeFile.createNewFile();
            }
            // 对块文件进行排序
            fileList.sort(Comparator.comparingInt(o -> Integer.parseInt(o.getName())));
            final FileOutputStream fileOutputStream = new FileOutputStream(mergeFile);
            FileChannel resultFileChannel = fileOutputStream.getChannel();

            for(File chunkFile : fileList){
                FileChannel blk = new FileInputStream(chunkFile).getChannel();
                resultFileChannel.transferFrom(blk, resultFileChannel.size(), blk.size());
                blk.close();
            }

            resultFileChannel.close();
            fileOutputStream.close();

            return mergeFile;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据MD5和路径生成chunkPath
     * 例如： /chunk/fileMd5前两位/fileMd5前两位/fileMd5
     */
    public static String genChunkFilePath(String chunkPath, String fileMd5){
        String temp =
                chunkPath + File.separator + fileMd5.substring(1, 3) + File.separator + fileMd5.substring(3, 5) + File.separator + fileMd5 + File.separator;
        return temp.replaceAll("/+", "/");
    }
}
