package com.dp.chat.service.searchService;


import com.dp.chat.entity.Group;

import java.util.List;

public interface SearchService {
    List<Group> getGroupByName(String name, Long userId);

}
