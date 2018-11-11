package com.proj.zyr.bean;

/**
 * Created by IrisZYR on 2018/10/11
 */
public class TodayWeather {
    private String city;
    private String updatetime;
    private String wendu;
    private String shidu;
    private String pm25;
    private String quality;
    private String fengxiang;
    private String fengli;
    private String date;
    private String high;
    private String low;
    private String type;

    private String[] futurefengxiang;
    private String[] futuredate;
    private String[] futurehigh;
    private String[] futurelow;
    private String[] futuretype;


    public String getCity() {
        return city;
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public String getShidu() {
        return shidu;
    }

    public String getQuality() {
        return quality;
    }

    public String getPm25() {
        return pm25;
    }

    public String getDate() {
        return date;
    }

    public String getHigh() {
        return high;
    }

    public String getLow() {
        return low;
    }

    public String getType() {
        return type;
    }

    public String getFengli() {
        return fengli;
    }

    public String getFengxiang() {
        return fengxiang;
    }

    public String getWendu() {
        return wendu;
    }

    public String[] getFuturedate() {
        return futuredate;
    }

    public String[] getFuturefengxiang() {
        return futurefengxiang;
    }

    public String[] getFuturehigh() {
        return futurehigh;
    }

    public String[] getFuturelow() {
        return futurelow;
    }

    public String[] getFuturetype() {
        return futuretype;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }

    public void setWendu(String wendu) {
        this.wendu = wendu;
    }

    public void setShidu(String shidu) {
        this.shidu = shidu;
    }

    public void setPm25(String pm25) {
        this.pm25 = pm25;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public void setFengxiang(String fengxiang) {
        this.fengxiang = fengxiang;
    }

    public void setFengli(String fengli) {
        this.fengli = fengli;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public void setType(String type) {
        this.type = type;
    }


    public void setFuturefengxiang(String[] futurefengxiang) {
        this.futurefengxiang = futurefengxiang;
    }

    public void setFuturedate(String[] futuredate) {
        this.futuredate = futuredate;
    }

    public void setFuturehigh(String[] futurehigh) {
        this.futurehigh = futurehigh;
    }

    public void setFuturelow(String[] futurelow) {
        this.futurelow = futurelow;
    }

    public void setFuturetype(String[] futuretype) {
        this.futuretype = futuretype;
    }

    @Override
    public String toString() {
        return "TodayWeather{" +
                "city='" + city + '\'' +
                ",wendu='" + wendu + '\'' +
                ",shidu='" + shidu + '\'' +
                ",pm25='" + pm25 + '\'' +
                ",quality='" + quality + '\'' +
                ",fengxiang='" + fengxiang + '\'' +
                ",fengli='" + fengli + '\'' +
                ",date='" + date + '\'' +
                ",high='" + high + '\'' +
                ",low='" + low + '\'' +
                ",type='" + type + '\'' +
                '}';
    }
}
