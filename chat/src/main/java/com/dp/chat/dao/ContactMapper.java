package com.dp.chat.dao;

import com.dp.chat.entity.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ContactMapper {
    Long addContact(@Param("userId") Long userId, @Param("contactId") Long contactId);
    Long deleteContact(@Param("userId") Long userId, @Param("contactId") Long contactId);
    List<UserInfo> getContacts(@Param("userId") Long userId);
    Long setRemark(@Param("userId") Long userId, @Param("contactId") Long contactId, @Param("remark") String remark);
    Long setGroup(@Param("userId") Long userId, @Param("contactId") Long contactId, @Param("group") String group);
}
