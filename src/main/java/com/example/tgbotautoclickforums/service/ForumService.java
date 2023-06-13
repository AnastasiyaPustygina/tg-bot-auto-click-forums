package com.example.tgbotautoclickforums.service;

import com.example.tgbotautoclickforums.dao.ForumDao;
import com.example.tgbotautoclickforums.domain.Forum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ForumService {
    private final ForumDao forumDao;

    public List<Forum> getForums(){
        return forumDao.getForums();
    }
    public void updateForums(){
        forumDao.updateForums();
    }

}
