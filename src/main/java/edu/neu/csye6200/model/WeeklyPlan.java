package edu.neu.csye6200.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WeeklyPlan {
    private Student student;
    private List<DayPlan> days; // 7 days
    
    public WeeklyPlan(Student student) {
        this.student = student;
        this.days = new ArrayList<>();
        initializeDays();
    }
    
    private void initializeDays() {
        String[] dayNames = {"Monday", "Tuesday", "Wednesday", 
                            "Thursday", "Friday", "Saturday", "Sunday"};
        for (String day : dayNames) {
            days.add(new DayPlan(day));
        }
    }
    
    public Optional<DayPlan> getDayPlan(String dayName) {
        return days.stream()
            .filter(d -> d.getDayName().equalsIgnoreCase(dayName))
            .findFirst();
    }
    
    // Getters and setters
    public Student getStudent() {
        return student;
    }
    
    public void setStudent(Student student) {
        this.student = student;
    }
    
    public List<DayPlan> getDays() {
        return days;
    }
    
    public void setDays(List<DayPlan> days) {
        this.days = days;
    }
}