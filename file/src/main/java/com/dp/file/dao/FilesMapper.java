package com.dp.file.dao;

import com.dp.file.entity.MyFile;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface FilesMapper {

    @Insert("insert into `files` (fromUserId, realName, savedPath, createTime, size, type) " +
            "values (#{fromUserId}, #{realName}, #{savedPath}, #{createTime}, #{size}, #{type})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(MyFile file);

    @Select("select * from `files` where savedPath = #{savedPath}")
    List<MyFile> selectByFilePath(String savedPath);

    @Select("select * from `files` where id = #{id}")
    MyFile selectById(Long id);

    @Select("select * from `files` where realName like  CONCAT('%',#{name},'%')")
    List<MyFile> selectByLikeName(String name);
}
