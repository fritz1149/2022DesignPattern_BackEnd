package com.dp.chat.dao;

import com.dp.chat.entity.Group;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface GroupMapper {
    Long addGroup(@Param("group") Group group);
    Long addMember(@Param("userId") Long userId, @Param("groupId") Long groupId);
    Long deleteMember(@Param("userId") Long userId, @Param("groupId") Long groupId);
}
