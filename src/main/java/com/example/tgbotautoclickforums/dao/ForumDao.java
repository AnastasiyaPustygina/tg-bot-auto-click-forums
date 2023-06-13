package com.example.tgbotautoclickforums.dao;

import com.example.tgbotautoclickforums.domain.Forum;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ForumDao {
    private List<Forum> forums = new ArrayList<>();

    public void updateForums(){
        RestTemplate restTemplate = new RestTemplate();
        List<?> response = restTemplate.getForObject("http://192.168.1.56:8081/forums", List.class);
        forums = (List<Forum>) response;
    }
    public List<Forum> getForums(){
        return forums;
    }

}
