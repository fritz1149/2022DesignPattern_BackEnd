package com.dp.common.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface ExistMapper {
    @Select("select exists (select * from user where id = #{userId})")
    public Long userExists(Long userId);
    @Select("select exists (select * from `group` where id = #{groupId})")
    public Long groupExists(Long groupId);
    @Select("select exists (select * from `member` where user_id = #{userId} and group_id = #{groupId})")
    public Long memberExists(Long userId, Long groupId);
    @Select("select exists (select * from `contact` where user_id = #{userId} and contact_id = #{contactId})")
    public Long contactExists(Long userId, Long contactId);
    @Select("select exists (select * from `file` where saved_path = #{path})")
    public Long fileExists(String path);
}
