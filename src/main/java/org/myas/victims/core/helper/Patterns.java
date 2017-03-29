package org.myas.victims.core.helper;

import java.util.regex.Pattern;

/**
 * Created by Mykhailo Yashchuk on 29.03.2017.
 */
public class Patterns {
    private static final String[] RELATIONS = {
            "син", "дочка", "донька", "чоловік", "дружина", "сестра", "брат", "дядько", "тітка", "матір", "батько",
            "другий син", "друга дочка", "друга донька",
            "третій син", "третя дочка", "третя донька"
    };

    public static final Pattern DISTRICT_PATTERN = Pattern.compile("[А-ЯЇЙІЄ]+[ ]*РАЙОН");
    public static final Pattern VILLAGE_PATTERN = Pattern.compile("С.([ ]*[А-ЯЇЙІЄ][ ]*)+");
    public static final Pattern SPLIT_RECORD_PATTERN = Pattern.compile(".*[а-яїйіє]-");
    public static final Pattern ODD_PAGE_HEADER_PATTERN = Pattern.compile("РОЗДІЛ 1. МАРТИРОЛОГ ГОЛОДОМОРУ");
    public static final Pattern NAMED_RECORD_PATTERN = getNamedRecordPattern();

    private static Pattern getNamedRecordPattern() {
        StringBuilder sb = new StringBuilder("((");
        for (int i = 0; i < RELATIONS.length; i++) {
            sb.append(RELATIONS[i]);
            if (i != RELATIONS.length - 1) {
                sb.append('|');
            }
        }
        sb.append(") {0,5})?");

        sb.append("([А-ЯЇЙІЄ][а-яїйіє']+[.]?[ ]*){1,3},.*");
        return Pattern.compile(sb.toString());
    }

    private Patterns() {}
}
