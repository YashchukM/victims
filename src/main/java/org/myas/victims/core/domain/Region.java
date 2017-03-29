package org.myas.victims.core.domain;

/**
 * Created by Mykhailo Yashchuk on 29.03.2017.
 */
public enum Region {
    VOLYN("волинська"),
    RIVNE("рівненська"),
    VINNYTSYA("вінницька"),
    KYIV("київська"),
    LVIV("львівська"),
    ZAKARPATTIA("закарпатська"),
    IVANO_FRANKIVSK("івано-франківська"),
    CHERNIVTSI("чернівецька"),
    TERNOPIL("тернопільська"),
    KMELNYTSKII("хмельницька"),
    ZHYTOMYR("житомирська"),
    CHERNIHIV("чернігівська"),
    CHERKASY("черкаська"),
    SUMY("сумська"),
    POLTAVA("полтавська"),
    KHARKIV("харківська"),
    LUHANSK("луганська"),
    KIROVOHRAD("кіровоградська"),
    MYKOLAIV("миколаївська"),
    ODESSA("одеська"),
    KHERSON("херсонська"),
    DNIPRO("дніпровська"),
    DONETSK("донецька"),
    ZAPORIZHIA("запорізька"),
    CRIMEA("АРК");

    private String name;

    Region(String name) {
        this.name = name;
    }
}
