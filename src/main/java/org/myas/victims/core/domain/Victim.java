package org.myas.victims.core.domain;

/**
 * Created by Mykhailo Yashchuk on 12.03.2017.
 */
public class Victim {
    private String name;
    private String year;
    private String village;
    private String district;
    private String region;

    private String fullRecord;
    private int bookNumber;
    private int pageNumber;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("name:").append(name).append(',');
        sb.append("year:").append(year).append(',');
        sb.append("village:").append(village).append(',');
        sb.append("district:").append(district).append(',');
        sb.append("region:").append(region).append(',');
        sb.append("fullRecord:").append(fullRecord).append(',');
        sb.append("bookNumber:").append(bookNumber).append(',');
        sb.append("pageNumber:").append(pageNumber);
        return sb.toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
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
}
