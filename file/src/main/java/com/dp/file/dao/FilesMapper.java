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

    @Insert("insert into `file` (from_user_id, real_name, saved_path, create_time, size, type) " +
            "values (#{fromUserId}, #{realName}, #{savedPath}, #{createTime}, #{size}, #{type})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(MyFile file);

    @Select("select * from `file` where saved_path = #{savedPath}")
    List<MyFile> selectByFilePath(String savedPath);

    @Select("select * from `file` where id = #{id}")
    MyFile selectById(Long id);

    @Select("select * from `file` where real_name like  CONCAT('%',#{name},'%')")
    List<MyFile> selectByLikeName(String name);
}
