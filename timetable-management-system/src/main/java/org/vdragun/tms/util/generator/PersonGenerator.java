package org.vdragun.tms.util.generator;

import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

/**
 * Randomly generates person instances using provided data
 * 
 * @author Vitaliy Dragun
 *
 */
abstract class PersonGenerator<T> {

    /**
     * Generates students using initial data
     */
    public List<T> generate(PersonGeneratorData data) {
        return IntStream.rangeClosed(1, data.getNumberOfPeople())
                .mapToObj(n -> generatePerson(
                        data))
                .collect(toList());
    }

    protected abstract T generatePerson(PersonGeneratorData data);

    protected LocalDate targetDate(LocalDate baseDate, int dateDeviationInDays) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        boolean positive = random.nextBoolean();
        int randomDeviation = random.nextInt(0, dateDeviationInDays + 1);
        if (positive) {
            return baseDate.plusDays(randomDeviation);
        } else {
            return baseDate.minusDays(randomDeviation);
        }
    }

    protected int position(int bound) {
        return ThreadLocalRandom.current().nextInt(0, bound);
    }

    public static class PersonGeneratorData {
        private int numberOfPeople;
        private List<String> firstNames;
        private List<String> lastNames;
        private LocalDate baseDate;
        private int dateDeviationInDays;

        public PersonGeneratorData(
                int numberOfPeople,
                List<String> firstNames,
                List<String> lastNames,
                LocalDate baseDate,
                int dateDeviationInDays) {
            this.numberOfPeople = numberOfPeople;
            this.firstNames = firstNames;
            this.lastNames = lastNames;
            this.baseDate = baseDate;
            this.dateDeviationInDays = dateDeviationInDays;
        }

        public static PersonGeneratorData from(
                int numberOfPeople,
                List<String> firstNames,
                List<String> lastNames,
                LocalDate baseDate,
                int dateDeviationInDays) {
            return new PersonGeneratorData(numberOfPeople, firstNames, lastNames, baseDate, dateDeviationInDays);
        }

        public int getNumberOfPeople() {
            return numberOfPeople;
        }

        public List<String> getFirstNames() {
            return firstNames;
        }

        public List<String> getLastNames() {
            return lastNames;
        }

        public LocalDate getBaseDate() {
            return baseDate;
        }

        public int getDateDeviationInDays() {
            return dateDeviationInDays;
        }

    }
}
