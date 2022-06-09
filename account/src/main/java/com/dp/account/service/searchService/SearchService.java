package com.dp.account.service.searchService;

import com.dp.account.entity.User;

import java.util.List;

public interface SearchService {
    List<User> getUserByName(String name, Long userId);

}
