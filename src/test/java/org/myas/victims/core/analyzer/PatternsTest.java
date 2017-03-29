package org.myas.victims.core.analyzer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.myas.victims.core.helper.Patterns.NAMED_RECORD_PATTERN;

import java.util.regex.Matcher;

import org.junit.Test;

/**
 * Created by Mykhailo Yashchuk on 28.03.2017.
 */
public class PatternsTest {
    @Test
    public void testNamePatternSimple() {
        String inputGood = "Вовк Марія Яківна, селянка-колгоспниця (хлібороб), 1933, від голоду, свідчення очевидців";
        String inputShortGood = "Вовк Марія, селянка-колгоспниця (хлібороб), від голоду, свідчення очевидців";
        String inputBad = "Вовк Марія Які вна, селянка-колгоспниця (хлібороб), 1933, від голоду, свідчення очевидців";

        Matcher matcherGood = NAMED_RECORD_PATTERN.matcher(inputGood);
        Matcher matcherShortGood = NAMED_RECORD_PATTERN.matcher(inputShortGood);
        Matcher matcherBad = NAMED_RECORD_PATTERN.matcher(inputBad);

        assertTrue(matcherGood.matches());
        assertTrue(matcherShortGood.matches());
        assertFalse(matcherBad.matches());
    }

    @Test
    public void testNamePatternAdditional() {
        String inputShortened = "Катрін Гріша Петр., (хлібороб)";
        String inputShortenedBad = "Катрін Гріша Петр.., (хлібороб)";

        Matcher matcherShortened = NAMED_RECORD_PATTERN.matcher(inputShortened);
        Matcher matcherShortenedBad = NAMED_RECORD_PATTERN.matcher(inputShortenedBad);

        assertTrue(matcherShortened.matches());
        assertFalse(matcherShortenedBad.matches());
    }

    @Test
    public void testNamePatternRelations() {
        String inputSon = "син Катрін Гріша Петр., (хлібороб)";
        String inputDaughter = "дочка Катрін Гріша Петр., (хлібороб)";
        String inputSecondSon = "другий син Мотрі, (хлібороб)";
        String inputSecondDaughter = "друга дочка Катрін Гріши Петровича, (хлібороб)";

        Matcher matcherSon = NAMED_RECORD_PATTERN.matcher(inputSon);
        Matcher matcherDaughter = NAMED_RECORD_PATTERN.matcher(inputDaughter);
        Matcher matcherSecondSon = NAMED_RECORD_PATTERN.matcher(inputSecondSon);
        Matcher matcherSecondDaughter = NAMED_RECORD_PATTERN.matcher(inputSecondDaughter);

        assertTrue(matcherSon.matches());
        assertTrue(matcherDaughter.matches());
        assertTrue(matcherSecondSon.matches());
        assertTrue(matcherSecondDaughter.matches());
    }
}
