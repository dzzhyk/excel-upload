package com.yankaizhang.excel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yankaizhang.excel.entity.FileInfo;

public interface FileInfoMapper extends BaseMapper<FileInfo> {

    void deleteBySaveName(String filename);

    FileInfo selectBySaveName(String filename);

    FileInfo selectByFileMd5(String fileMd5);
}
