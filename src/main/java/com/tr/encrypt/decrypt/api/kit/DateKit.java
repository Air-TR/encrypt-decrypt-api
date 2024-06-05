package com.tr.encrypt.decrypt.api.kit;

import org.apache.commons.lang3.time.DateUtils;

import java.lang.management.ManagementFactory;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

/**
 * @Author TR
 */
public class DateKit extends DateUtils {

    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String TIME_FORMAT = "HH:mm:ss";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_TIME_MS_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String YEAR_FORMAT = "yyyy";
    public static final String YEAR_MONTH_FORMAT = "yyyy-MM";
    public static final String S_DATE_FORMAT = "yyyy/MM/dd";
    public static final String S_DATE_TIME_FORMAT_X = "yyyy/MM/dd HH:mm:ss";
    public static final String S_DATE_TIME_MS_FORMAT_X = "yyyy/MM/dd HH:mm:ss.SSS";
    public static final String N_DATE_FORMAT = "yyyyMMdd";
    public static final String N_DATE_FORMAT_X = "yyyyMMddHHmm";
    public static final String N_DATE_TIME_FORMAT_X = "yyyyMMdd HH:mm:ss";
    public static final String N_DATE_TIME_MS_FORMAT_X = "yyyyMMdd HH:mm:ss.SSS";

    private static String[] parsePatterns = {
            DATE_FORMAT, DATE_TIME_FORMAT, DATE_TIME_MS_FORMAT, YEAR_FORMAT, YEAR_MONTH_FORMAT,
            S_DATE_FORMAT, S_DATE_TIME_FORMAT_X, S_DATE_TIME_MS_FORMAT_X,
            N_DATE_FORMAT, N_DATE_TIME_FORMAT_X, N_DATE_TIME_MS_FORMAT_X};

    public static Date parse(String dateStr) {
        if (StringKit.isBlank(dateStr)) return null;
        try {
            return parseDate(dateStr, parsePatterns);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static String format(Date date, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

    public static String getDateTime() {
        return format(new Date(), DateKit.DATE_TIME_FORMAT);
    }

    public static String getDate() {
        return format(new Date(), DateKit.DATE_FORMAT);
    }

    public static String getTime() {
        return format(new Date(), DateKit.TIME_FORMAT);
    }

    /**
     * 获取本周的第一天
     *
     * @return Date
     */
    public static Date startOfWeek() {
        LocalDate now = LocalDate.now();
        // 获取本周的起始时间（周一）
        LocalDate startOfWeek = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        return java.sql.Date.valueOf(startOfWeek);
    }

    /**
     * 获取本周的最后一天
     *
     * @return Date
     */
    public static Date endOfWeek() {
        LocalDate now = LocalDate.now();
        // 获取本周的起始时间（周一）
        LocalDate endOfWeek = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        return java.sql.Date.valueOf(endOfWeek);
    }

    /**
     * 获取当天的零分零秒
     *
     * @return Date
     */
    public static Date startOfDay() {
        LocalDate today = LocalDate.now();
        // 获取本周的起始时间（周一）
        LocalDateTime startOfDay = today.atStartOfDay();
        return Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 获取当天的零分零秒
     *
     * @return Date
     */
    public static Date startOfTomorrow() {
        // 获取明天的日期
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        // 将明天日期转换为明天的零分零秒
        LocalDateTime startOfTomorrow = tomorrow.atStartOfDay();
        return Date.from(startOfTomorrow.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 判断时间是否属于当天
     *
     * @param date
     * @return true/false
     */
    public static boolean isCurrentDate(Date date) {
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate currentDate = LocalDate.now();
        if (localDate.isEqual(currentDate)) return true;
        return false;
    }

    /**
     * 获取服务器启动时间
     */
    public static Date getServerStartDate() {
        long time = ManagementFactory.getRuntimeMXBean().getStartTime();
        return new Date(time);
    }

    /**
     * 计算两个时间差
     */
    public static String getDatePoor(Date endDate, Date nowDate) {
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - nowDate.getTime();
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        // 计算差多少秒//输出结果
        return day + "天" + hour + "小时" + min + "分钟";
    }
}
