package org.myas.victims.core.domain;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Mykhailo Yashchuk on 23.03.2017.
 */
public class UnrecognizedRecord {
    private Victim victim;
    private String record;
    private int page;

    public Victim getVictim() {
        return victim;
    }

    public void setVictim(Victim victim) {
        this.victim = victim;
    }

    public String getRecord() {
        return record;
    }

    public void setRecord(String record) {
        this.record = record;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    @Override
    public String toString() {
        char tab = '\t', endl = '\n';
        StringBuilder sb = new StringBuilder("UnrecognizedRecord {\n");
        sb.append(tab).append("victim {").append(victim).append("}").append(endl);
        sb.append(tab).append("record {").append(record).append("}").append(endl);
        sb.append(tab).append("page {").append(page).append("}").append(endl);
        sb.append('}').append(endl);
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UnrecognizedRecord)) return false;

        UnrecognizedRecord record1 = (UnrecognizedRecord) o;

        if (page != record1.page) return false;
        if (victim != null ? !victim.equals(record1.victim) : record1.victim != null) return false;
        return record.equals(record1.record);

    }

    @Override
    public int hashCode() {
        int result = victim != null ? victim.hashCode() : 0;
        result = 31 * result + record.hashCode();
        result = 31 * result + page;
        return result;
    }
}
