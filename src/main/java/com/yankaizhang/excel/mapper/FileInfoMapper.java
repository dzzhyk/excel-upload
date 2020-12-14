package com.yankaizhang.excel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yankaizhang.excel.entity.FileInfo;
import com.yankaizhang.excel.vo.FileInfoVO;


public interface FileInfoMapper extends BaseMapper<FileInfo> {

    Integer deleteBySaveName(String filename);

    FileInfo selectBySaveName(String filename);

    FileInfo selectByFileMd5(String fileMd5);

    Integer updateFileStatus(String filename, Integer status);

    // 分页
    IPage<FileInfoVO> selectFileInfoVo(Page<?> page);

}
