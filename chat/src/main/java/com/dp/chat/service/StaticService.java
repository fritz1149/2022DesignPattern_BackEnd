package com.dp.chat.service;

import com.alibaba.fastjson.JSONObject;
import com.dp.chat.dao.CacheDao;
import com.dp.chat.dao.ContactMapper;
import com.dp.chat.dao.GroupMapper;
import com.dp.chat.dao.StorageDao;
import com.dp.chat.entity.Group;
import com.dp.chat.entity.Message.MessageFactory;
import com.dp.chat.entity.UserInfo;
import com.dp.common.Name;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
@Service
public class StaticService {
    @Autowired
    StorageDao storageDao;
    @Autowired
    GroupMapper groupMapper;
    @Autowired
    ContactMapper contactMapper;
    @Autowired
    CacheDao cacheDao;
    @Autowired
    AppendService appendService;
    @Autowired
    MessageFactory messageFactory;

    public String claimStorage(Long userId1, Long userId2){
        Assert.notNull(userId1, "userId null");
        Assert.notNull(userId2, "userId null");
        String pairName = Name.pairName(userId1, userId2);
        return storageDao.claimStorage(pairName);
    }

    public JSONObject getChatLog(String pairName, Integer pageSize, Integer pageNum){
        Integer offset = (pageNum - 1) * pageSize;
        Integer limit = pageSize;
        return storageDao.getLog(pairName, offset, limit);
    }

    public Long claimGroup(String groupName, Long userId){
        Long groupId = storageDao.claimGroup(groupName, userId);
        cacheDao.addOnlineToGroup(userId, groupId);
        return groupId;
    }

    public Group getGroup(Long groupId){
        Assert.notNull(groupId, "groupId null");
        return groupMapper.getGroupById(groupId);
    }

    public String joinGroup(Long userId, Long groupId){
        Assert.notNull(userId, "userId null");
        Assert.notNull(groupId, "groupId null");
        groupMapper.addMember(userId, groupId);
        cacheDao.addOnlineToGroup(userId, groupId);
        return "OK";
    }

    public Boolean leaveGroup(Long userId, Long groupId){
        Assert.notNull(userId, "userId null");
        Assert.notNull(groupId, "groupId null");
        Long linesAffected = groupMapper.deleteMember(userId, groupId);
        cacheDao.removeOnlineToGroup(userId, groupId);
        return linesAffected == 1L;
    }

    public String addContact(Long userId, Long contactId){
        Assert.notNull(userId, "userId null");
        Assert.notNull(contactId, "contactId null");
        Long linesAffected = contactMapper.addContact(userId, contactId);
        if(linesAffected == 1L || linesAffected == 2L)
            return "OK";
        else if(linesAffected == 0L)
            return "contact already exists";
        else
            return "error";
    }

    public Boolean removeContact(Long userId, Long contactId){
        Assert.notNull(userId, "userId null");
        Assert.notNull(contactId, "contactId null");
        Long linesAffected = contactMapper.deleteContact(userId, contactId);
        return linesAffected == 1L;
    }

    public List<UserInfo> getContacts(Long userId){
        Assert.notNull(userId, "userId null");
        return contactMapper.getContacts(userId);
    }

    public List<Group> getMyGroups(Long userId){
        Assert.notNull(userId, "userId null");
        return groupMapper.getMyGroups(userId);
    }

    public Long setRemark(Long userId, Long contactId, String remark){
        Assert.notNull(userId, "userId null");
        Assert.notNull(contactId, "contactId null");
        Assert.notNull(remark, "remark null");
        return contactMapper.setRemark(userId, contactId, remark);
    }

    public Long setGroup(Long userId, Long contactId, String group){
        Assert.notNull(userId, "userId null");
        Assert.notNull(contactId, "contactId null");
        Assert.notNull(group, "group null");
        return contactMapper.setGroup(userId, contactId, group);
    }

    public Boolean beContact(Long userId, Long contactId, String type){
        Assert.notNull(userId, "userId null");
        Assert.notNull(contactId, "contactId null");
        Long relation = contactMapper.checkContact(userId, contactId);
        if(relation == 2L)
            return false; // 已经是好友关系
        else{
            if(type.equals("response")) {
                addContact(userId, contactId);
                claimStorage(userId, contactId);
            }
            appendService.appendMessage(messageFactory.contactRequest(userId, contactId, type));
            return true;
        }
    }

    public Boolean updateGroupAvatar(Long groupId, String avatar){
        Assert.notNull(groupId, "groupId null");
        return groupMapper.uploadAvatar(groupId, avatar) == 1L;
    }
}
