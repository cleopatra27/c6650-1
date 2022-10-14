package org.example;

import java.util.Random;

public class SkierBean {

    private int skierID;
    private int resortID;
    private int liftID;
    private int seasonID;
    private int dayID;
    private int time;

    public int getSkierID() {
        return skierID;
    }

    public void setSkierID(int skierID) {
        this.skierID = skierID;
    }

    public int getResortID() {
        return resortID;
    }

    public void setResortID(int resortID) {
        this.resortID = resortID;
    }

    public int getLiftID() {
        return liftID;
    }

    public void setLiftID(int liftID) {
        this.liftID = liftID;
    }

    public int getSeasonID() {
        return seasonID;
    }

    public void setSeasonID(int seasonID) {
        this.seasonID = seasonID;
    }

    public int getDayID() {
        return dayID;
    }

    public void setDayID(int dayID) {
        this.dayID = dayID;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
    public static int generateResortID() {
        return new Random().nextInt(10 - 1 + 1) + 1;
    }

    public static int generateSkierID() {
        return new Random().nextInt(100000 - 1 + 1) + 1;
    }

    public static int generateLiftID() {
        return new Random().nextInt(40 - 1 + 1) + 1;
    }

    public static int generateTime() {
        return new Random().nextInt(360 - 1 + 1) + 1;
    }

    public static SkierBean generate(){
        SkierBean skierBean = new SkierBean();
        skierBean.setSkierID(generateSkierID());
        skierBean.setDayID(1);
        skierBean.setLiftID(generateLiftID());
        skierBean.setTime(generateTime());
        skierBean.setSeasonID(2022);
        skierBean.setResortID(generateResortID());
        return skierBean;
    }
}
