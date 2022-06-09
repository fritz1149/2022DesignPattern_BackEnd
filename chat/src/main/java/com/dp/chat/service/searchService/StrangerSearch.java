package com.dp.chat.service.searchService;

import com.dp.chat.dao.GroupMapper;
import com.dp.chat.entity.Group;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

@Service
public class StrangerSearch implements SearchService {
    @Autowired
    GroupMapper groupMapper;
    public List<Group> getGroupByName(String name, Long userId){
        Assert.notNull(name, "name null");
        Assert.notNull(userId, "userId null");
        return groupMapper.getStrangerGroupByName(name, userId);
    }
}
