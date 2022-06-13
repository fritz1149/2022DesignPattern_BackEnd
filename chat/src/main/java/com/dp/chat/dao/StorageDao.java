package com.dp.chat.dao;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dp.chat.entity.ChatTemplate;
import com.dp.chat.entity.Group;
import com.dp.chat.entity.Message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class StorageDao {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private GroupMapper groupMapper;

    public JSONObject saveToStorage(Message message, String storageName){
        Query query = new Query();
        query.addCriteria(Criteria.where("pair_name").is(storageName));
        query.fields().exclude("_id").include("number");

        Update update = new Update();
        update.push("messages", message);
        update.inc("number", 1);

        return mongoTemplate.findAndModify(query, update, JSONObject.class, "messages");
    }

    public JSONObject getLog(String pairName, Integer offset, Integer limit){
        MatchOperation match = Aggregation.match(new Criteria("pair_name").is(pairName));
        ProjectionOperation projection = Aggregation.project("messages", "number").andExclude("_id")
                .and(ArrayOperators.Slice.sliceArrayOf("messages").offset(offset).itemCount(limit))
                .as("messages");

        Aggregation aggregation = Aggregation.newAggregation(match, projection);
        List<JSONObject> results = mongoTemplate.aggregate(aggregation, "messages", JSONObject.class).getMappedResults();

        if(results.size() == 0)
            return new JSONObject().fluentPut("number", 0).fluentPut("messages", new JSONArray());
        return results.get(0);
    }

    public String claimStorage(String pairName){
        Query query = new Query();
        query.addCriteria(Criteria.where("pair_name").is(pairName));
        List<String> ret = mongoTemplate.find(query, String.class, "messages");
        if(ret.size() == 0) {
            mongoTemplate.insert(new ChatTemplate(0, pairName), "messages");
            return "OK";
        }
        return "Already exist";
    }

    public Long claimGroup(String groupName, Long userId){
        Group group = new Group(groupName);
        groupMapper.addGroup(group);
        Long groupId = group.getGroupId();
        groupMapper.addMember(userId, groupId);
        mongoTemplate.insert(new ChatTemplate(0, groupId.toString()), "messages");
        return groupId;
    }
}
