package org.myas.victims.core.domain;

/**
 * Created by Mykhailo Yashchuk on 12.03.2017.
 */
public class Victim {
    private String name;
    private String village;
    private String district;
    private String region;

    private int bookNumber;
    private int pageNumber;

    private String fullRecord;

    public Victim() {
        this.bookNumber = 1;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    public String getFullRecord() {
        return fullRecord;
    }

    public void setFullRecord(String fullRecord) {
        this.fullRecord = fullRecord;
    }

    public int getBookNumber() {
        return bookNumber;
    }

    public void setBookNumber(int bookNumber) {
        this.bookNumber = bookNumber;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("name:").append(name).append(',');
        sb.append("village:").append(village).append(',');
        sb.append("district:").append(district).append(',');
        sb.append("region:").append(region).append(',');
        sb.append("bookNumber:").append(bookNumber).append(',');
        sb.append("pageNumber:").append(pageNumber).append(',');
        sb.append("fullRecord:").append(fullRecord);
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Victim)) return false;

        Victim victim = (Victim) o;

        if (bookNumber != victim.bookNumber) return false;
        if (pageNumber != victim.pageNumber) return false;
        if (!name.equals(victim.name)) return false;
        if (!village.equals(victim.village)) return false;
        if (!district.equals(victim.district)) return false;
        if (!region.equals(victim.region)) return false;
        return fullRecord.equals(victim.fullRecord);

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + village.hashCode();
        result = 31 * result + district.hashCode();
        result = 31 * result + region.hashCode();
        result = 31 * result + bookNumber;
        result = 31 * result + pageNumber;
        result = 31 * result + fullRecord.hashCode();
        return result;
    }
}
