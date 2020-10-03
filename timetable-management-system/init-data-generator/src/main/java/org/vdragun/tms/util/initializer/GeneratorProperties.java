package org.vdragun.tms.util.initializer;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("generator")
@Component
public class GeneratorProperties {

    private List<String> categories;
    private List<String> firstNames;
    private List<String> lastNames;
    private LocalDate baseDate;
    private int deviationDays;
    private int numberOfStudents;
    private int numberOfTeachers;
    private int numberOfClassrooms;
    private int classroomMinCapacity;
    private int classroomMaxCapacity;
    private List<String> coursePrefixes;
    private int numberOfCourses;
    private int numberOfGroups;
    private int minStudentsPerGroup;
    private int maxStudentsPerGroup;
    private int maxCoursesPerStudent;
    private LocalTime timetableStartTime;
    private LocalTime timetableEndTime;
    private int timetablePeriodOfMonths;
    private int timetableDurationInMinutes;
    private int timetableMaxClassesPerWeek;

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public List<String> getFirstNames() {
        return firstNames;
    }

    public void setFirstNames(List<String> firstNames) {
        this.firstNames = firstNames;
    }

    public List<String> getLastNames() {
        return lastNames;
    }

    public void setLastNames(List<String> lastNames) {
        this.lastNames = lastNames;
    }

    public LocalDate getBaseDate() {
        return baseDate;
    }

    public void setBaseDate(LocalDate baseDate) {
        this.baseDate = baseDate;
    }

    public int getDeviationDays() {
        return deviationDays;
    }

    public void setDeviationDays(int deviationDays) {
        this.deviationDays = deviationDays;
    }

    public int getNumberOfStudents() {
        return numberOfStudents;
    }

    public void setNumberOfStudents(int numberOfStudents) {
        this.numberOfStudents = numberOfStudents;
    }

    public int getNumberOfTeachers() {
        return numberOfTeachers;
    }

    public void setNumberOfTeachers(int numberOfTeachers) {
        this.numberOfTeachers = numberOfTeachers;
    }

    public int getNumberOfClassrooms() {
        return numberOfClassrooms;
    }

    public void setNumberOfClassrooms(int numberOfClassrooms) {
        this.numberOfClassrooms = numberOfClassrooms;
    }

    public int getClassroomMinCapacity() {
        return classroomMinCapacity;
    }

    public void setClassroomMinCapacity(int classroomMinCapacity) {
        this.classroomMinCapacity = classroomMinCapacity;
    }

    public int getClassroomMaxCapacity() {
        return classroomMaxCapacity;
    }

    public void setClassroomMaxCapacity(int classroomMaxCapacity) {
        this.classroomMaxCapacity = classroomMaxCapacity;
    }

    public List<String> getCoursePrefixes() {
        return coursePrefixes;
    }

    public void setCoursePrefixes(List<String> coursePrefixes) {
        this.coursePrefixes = coursePrefixes;
    }

    public int getNumberOfCourses() {
        return numberOfCourses;
    }

    public void setNumberOfCourses(int numberOfCourses) {
        this.numberOfCourses = numberOfCourses;
    }

    public int getNumberOfGroups() {
        return numberOfGroups;
    }

    public void setNumberOfGroups(int numberOfGroups) {
        this.numberOfGroups = numberOfGroups;
    }

    public int getMinStudentsPerGroup() {
        return minStudentsPerGroup;
    }

    public void setMinStudentsPerGroup(int minStudentsPerGroup) {
        this.minStudentsPerGroup = minStudentsPerGroup;
    }

    public int getMaxStudentsPerGroup() {
        return maxStudentsPerGroup;
    }

    public void setMaxStudentsPerGroup(int maxStudentsPerGroup) {
        this.maxStudentsPerGroup = maxStudentsPerGroup;
    }

    public int getMaxCoursesPerStudent() {
        return maxCoursesPerStudent;
    }

    public void setMaxCoursesPerStudent(int maxCoursesPerStudent) {
        this.maxCoursesPerStudent = maxCoursesPerStudent;
    }

    public LocalTime getTimetableStartTime() {
        return timetableStartTime;
    }

    public void setTimetableStartTime(LocalTime timetableStartTime) {
        this.timetableStartTime = timetableStartTime;
    }

    public LocalTime getTimetableEndTime() {
        return timetableEndTime;
    }

    public void setTimetableEndTime(LocalTime timetableEndTime) {
        this.timetableEndTime = timetableEndTime;
    }

    public int getTimetablePeriodOfMonths() {
        return timetablePeriodOfMonths;
    }

    public void setTimetablePeriodOfMonths(int timetablePeriodOfMonths) {
        this.timetablePeriodOfMonths = timetablePeriodOfMonths;
    }

    public int getTimetableDurationInMinutes() {
        return timetableDurationInMinutes;
    }

    public void setTimetableDurationInMinutes(int timetableDurationInMinutes) {
        this.timetableDurationInMinutes = timetableDurationInMinutes;
    }

    public int getTimetableMaxClassesPerWeek() {
        return timetableMaxClassesPerWeek;
    }

    public void setTimetableMaxClassesPerWeek(int timetableMaxClassesPerWeek) {
        this.timetableMaxClassesPerWeek = timetableMaxClassesPerWeek;
    }

}
