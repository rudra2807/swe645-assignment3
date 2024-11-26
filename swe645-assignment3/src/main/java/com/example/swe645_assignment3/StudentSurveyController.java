package com.example.swe645_assignment3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/surveys")
public class StudentSurveyController {
    @Autowired
    private StudentSurveyService surveyService;

    @PostMapping
    public ResponseEntity<StudentSurvey> createSurvey(@RequestBody StudentSurvey survey) {
        return new ResponseEntity<StudentSurvey>(surveyService.saveSurvey(survey), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentSurvey> updateSurvey(@PathVariable Long id, @RequestBody StudentSurvey survey) {
        return new ResponseEntity<StudentSurvey>(surveyService.updateSurvey(survey, id), HttpStatus.OK);
    }

    @GetMapping
    public List<StudentSurvey> getAllSurveys() {
        return surveyService.getAllSurveys();
    }

    @GetMapping("/{id}")
    public StudentSurvey getSurveyById(@PathVariable Long id) {
        return surveyService.getSurveyById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSurvey(@PathVariable Long id) {
        surveyService.deleteSurvey(id);
        return new ResponseEntity<String>("Entry Successfully Deleted", HttpStatus.OK);
    }
}
