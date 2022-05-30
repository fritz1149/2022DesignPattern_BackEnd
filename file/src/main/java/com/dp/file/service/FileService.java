package com.dp.file.service;

import com.dp.file.dao.FilesMapper;
import com.dp.file.entity.MyFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileService {
    @Autowired
    FilesMapper filesMapper;

    public Long addFileRecord(MyFile file) {
        filesMapper.insert(file);
        return file.getId();
    }

    public List<MyFile> getFileRecordByFilePath(String filePath) {
        return filesMapper.selectByFilePath(filePath);
    }

    public MyFile getFileById(Long fileId) {
        return filesMapper.selectById(fileId);
    }

    public List<MyFile> searchFilesRecordByName(String name) {
        return filesMapper.selectByLikeName(name);
    }
}
