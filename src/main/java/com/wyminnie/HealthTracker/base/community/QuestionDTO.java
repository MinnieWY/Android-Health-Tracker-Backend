package com.wyminnie.healthtracker.base.community;

import java.util.List;

import lombok.Setter;

@Setter
public class QuestionDTO {
    private Long id;
    private String question;
    private List<String> answerOptions;
}