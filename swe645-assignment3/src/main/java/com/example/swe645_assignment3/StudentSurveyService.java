package com.example.swe645_assignment3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.MissingResourceException;

@Service
public class StudentSurveyService {
    @Autowired
    private StudentSurveyRepository repository;

    public StudentSurvey saveSurvey(StudentSurvey survey) {
        return repository.save(survey);
    }

    public StudentSurvey updateSurvey(StudentSurvey survey, Long id) {
        StudentSurvey existingSurvey = repository.findById(id).orElseThrow(
                () -> new MissingResourceException("Survey Not Found", "Id", id.toString()));

        existingSurvey.setFirstName(survey.getFirstName());
        existingSurvey.setLastName(survey.getLastName());
        existingSurvey.setEmail(survey.getEmail());

        repository.save(existingSurvey);
        return existingSurvey;
    }

    public List<StudentSurvey> getAllSurveys() {
        return repository.findAll();
    }

    public StudentSurvey getSurveyById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public void deleteSurvey(Long id) {
        repository.deleteById(id);
    }
}
