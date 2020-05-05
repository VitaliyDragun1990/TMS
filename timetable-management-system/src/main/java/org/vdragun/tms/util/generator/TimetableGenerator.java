package org.vdragun.tms.util.generator;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.vdragun.tms.core.domain.Classroom;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.core.domain.Timetable;

/**
 * Randomly generates {@link Timetable} instances using provided data
 * 
 * @author Vitaliy Dragun
 *
 */
public class TimetableGenerator {

    private static final List<DayOfWeek> WEEKDAYS = Arrays.asList(
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY
    );

    private LocalTime timetableStartTime;
    private LocalTime timetableEndTime;
    private int classDurationMinutes;
    private int maxClassesPerWeek;
    private LocalDate fromDate;
    private LocalDate toDate;

    public TimetableGenerator(
            LocalTime timetableStartTime,
            LocalTime timetableEndTime,
            int classDurationMinutes,
            int numberOfMonths,
            int maxClassesPerWeek) {
        this.timetableStartTime = timetableStartTime;
        this.timetableEndTime = timetableEndTime;
        this.classDurationMinutes = classDurationMinutes;
        this.maxClassesPerWeek = maxClassesPerWeek;

        LocalDate now = LocalDate.now();
        fromDate = now.withDayOfMonth(1);
        LocalDate toMonth = now.plusMonths(numberOfMonths - 1);
        toDate = toMonth.withDayOfMonth(toMonth.lengthOfMonth());
    }

    public List<Timetable> generate(List<Course> courses, List<Classroom> classrooms) {
        List<LocalDate> targetDates = prepareTargetDates();
        Map<LocalDate, List<ClassroomReservation>> reservations = prepareClassroomReservations(targetDates, classrooms);

        List<Timetable> result = new ArrayList<>();

        for (Course course : courses) {
            List<LocalDate> courseDates = randomCourseDates(targetDates);
            for (LocalDate courseDate : courseDates) {
                ClassroomReservation reservation = findAvailableClassroom(reservations.get(courseDate));
                LocalTime courseTime = reservation.reserveFor(classDurationMinutes);
                LocalDateTime courseDateTime = LocalDateTime.of(courseDate, courseTime);

                Timetable timetable = new Timetable(
                        courseDateTime,
                        classDurationMinutes,
                        course,
                        reservation.classroom(),
                        course.getTeacher());
                result.add(timetable);
            }
        }
        return result;
    }

    private ClassroomReservation findAvailableClassroom(List<ClassroomReservation> reservations) {
        Collections.shuffle(reservations);
        return reservations.stream()
                .filter(reservation -> reservation.isAvailableFor(
                        classDurationMinutes))
                .findAny()
                .orElseThrow(() -> new GenerationException("Fail to find available classroom for course"));
    }

    private List<LocalDate> prepareTargetDates() {
        List<LocalDate> result = new ArrayList<>();
        LocalDate current = fromDate;
        LocalDate upperBound = toDate.plusDays(1);

        while (current.isBefore(upperBound)) {
            if (WEEKDAYS.contains(current.getDayOfWeek())) {
                result.add(current);
            }
            current = current.plusDays(1);
        }
        return result;
    }

    private List<LocalDate> randomCourseDates(List<LocalDate> targetDates) {
        Set<DayOfWeek> courseDays = getRandomDaysOfWeek();
        return targetDates.stream()
                .filter(date -> courseDays.contains(date.getDayOfWeek()))
                .collect(toList());
    }

    private Set<DayOfWeek> getRandomDaysOfWeek() {
        Set<DayOfWeek> result = new HashSet<>();

        int daysPerWeek = randomDaysPerWeek();
        while (result.size() != daysPerWeek) {
            DayOfWeek randomDay = WEEKDAYS.get(position(WEEKDAYS.size()));
            result.add(randomDay);
        }

        return result;
    }

    private int randomDaysPerWeek() {
        return ThreadLocalRandom.current().nextInt(1, maxClassesPerWeek + 1);
    }

    private int position(int bound) {
        return ThreadLocalRandom.current().nextInt(bound);
    }

    private Map<LocalDate, List<ClassroomReservation>> prepareClassroomReservations(
            List<LocalDate> dates,
            List<Classroom> classrooms) {
        return dates.stream()
                .collect(toMap(identity(), date -> buildReservationsFrom(classrooms)));
    }

    private List<ClassroomReservation> buildReservationsFrom(List<Classroom> classrooms) {
        return classrooms.stream()
                .map(classroom -> new ClassroomReservation(classroom, timetableStartTime,
                        timetableEndTime))
                .collect(toList());
    }

    private static class ClassroomReservation {
        // Interval between two consecutive classes
        private static final int INTERVAL_MINUTES = 10;
        private Classroom classroom;
        private LocalTime fromTime;
        private LocalTime toTime;
        private int reservedMinutes;

        public ClassroomReservation(Classroom classroom, LocalTime fromTime, LocalTime toTime) {
            this.classroom = classroom;
            this.fromTime = fromTime;
            this.toTime = toTime;
            reservedMinutes = 0;
        }

        public LocalTime reserveFor(int durationMinutes) {
            LocalTime reservedTime = fromTime.plusMinutes(reservedMinutes);
            reservedMinutes += durationMinutes + INTERVAL_MINUTES;
            return reservedTime;
        }

        public boolean isAvailableFor(int durationInMinutes) {
            return fromTime
                    .plusMinutes(reservedMinutes)
                    .plusMinutes(durationInMinutes)
                    .isBefore(toTime);
        }

        public Classroom classroom() {
            return classroom;
        }

    }
}
