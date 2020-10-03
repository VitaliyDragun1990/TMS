package org.vdragun.tms.util.generator;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import org.vdragun.tms.core.domain.Group;

/**
 * Creates {@link Group} instances with randomly generated names.
 * 
 * @author Vitaliy Dragun
 *
 */
public class GroupGenerator {

    private static final List<Integer> NUMBERS = IntStream.rangeClosed(1, 9)
            .boxed()
            .collect(toList());

    private static final List<Character> LETTERS = IntStream.rangeClosed('a', 'z')
            .mapToObj(c -> (char) c)
            .collect(toList());

    private static final String HYPHEN = "-";

    /**
     * Generates specified number of groups
     */
    public List<Group> generate(int number) {
        return IntStream.rangeClosed(1, number)
                .mapToObj(n -> generateGroup())
                .collect(toList());
    }

    private Group generateGroup() {
        StringBuilder name = new StringBuilder(5);
        name.append(LETTERS.get(position(LETTERS.size())));
        name.append(LETTERS.get(position(LETTERS.size())));
        name.append(HYPHEN);
        name.append(NUMBERS.get(position(NUMBERS.size())));
        name.append(NUMBERS.get(position(NUMBERS.size())));

        return new Group(name.toString());
    }

    private int position(int bound) {
        return ThreadLocalRandom.current().nextInt(0, bound);
    }

}
