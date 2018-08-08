package uk.gov.companieshouse.accountsDates;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountsDates {

    private static final String PERIOD_START = "periodStart";
    private static final String PERIOD_END = "periodEnd";
    private static final String DATE_FORMAT_YYYYMMDD = "yyyy-MM-dd";
    private static final String DATE_FORMAT_D_MMMM_YYYY = "d MMMM yyyy";

    /**
     * 
     * Takes a {@link String} and converts it to a Java 8 {@link LocalDate}
     * 
     * @param stringDate
     * @return
     * 
     */
    public LocalDate convertStringToDate(String stringDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT_YYYYMMDD);
        return LocalDate.parse(stringDate, formatter);
    }

    /**
     * Takes a Java 8 {@link LocalDate} and converts it to a {@link String}
     * 
     * @param date
     * @return
     */
    public String convertDateToString(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT_YYYYMMDD);
        return date.format(formatter);
    }

    /**
     * Formats a {@link String} date from format '2017-03-05' to '3 March 2005'
     * 
     * @param stringDate
     * @return
     */
    public String convertLocalDateToDisplayDate(LocalDate stringDate) {

   
        DateTimeFormatter dateFormatDesired = DateTimeFormatter.ofPattern(DATE_FORMAT_D_MMMM_YYYY);
      

        return stringDate.format(dateFormatDesired);
    }

    /**
     * Returns a date and time object from given date/time string in the format 'D_MMMM_YYYY' 'h:mm a'
     * 
     * @param dateString
     * @return
     * 
     */
    public Map<String, String> getDateAndTime(String dateString) {
        Map<String, String> timeObject = new HashMap<>();

        Instant instant = Instant.parse(dateString);

        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.of(ZoneOffset.UTC.getId()));
        LocalDate localDate = localDateTime.toLocalDate();

        DateTimeFormatter dateFormatDesired = DateTimeFormatter.ofPattern(DATE_FORMAT_D_MMMM_YYYY);
        DateTimeFormatter timeFormatDesired = DateTimeFormatter.ofPattern("h:mm a");

        String date = localDate.format(dateFormatDesired);
        String time = localDateTime.format(timeFormatDesired).toLowerCase();

        timeObject.put("date", date);
        timeObject.put("time", time);

        return timeObject;
    }

    /**
     * 
     * Generate balance sheet header string to display on web and ixbrl templates based on period start and end
     * dates
     * 
     * @param periodStartString accounting period start date 
     * @param periodEndString accounting period end date 
     * @param isSameYear
     * @return
     * 
     */
    public String generateBalanceSheetHeading(String periodStartString, String periodEndString, boolean isSameYear) {

        Instant instantPeriodStart = Instant.parse(periodStartString);
        Instant instantPeriodEnd = Instant.parse(periodEndString);
        
        LocalDate localDatePeriodStart = LocalDateTime.ofInstant(instantPeriodStart, ZoneId.of(ZoneOffset.UTC.getId())).toLocalDate();
        LocalDate localDatePeriodEnd = LocalDateTime.ofInstant(instantPeriodEnd, ZoneId.of(ZoneOffset.UTC.getId())).toLocalDate();

        Map<String, String> resultDates = calculatePeriodRange(localDatePeriodStart, localDatePeriodEnd, isSameYear);

        if (!resultDates.containsKey(PERIOD_START)) {
            return resultDates.get(PERIOD_END);
        } else {
            return resultDates.get(PERIOD_START) + " to " + resultDates.get(PERIOD_END);
        }
    }

    /**
     *Calculate balance sheet dates display format depending on range between period start and end dates
     * 
     * 
     * @param periodStart accounting period start date
     * @param periodEnd accounting period end date 
     * @param isSameYear
     * @return
     * 
     * 
     */
    public Map<String, String> calculatePeriodRange(LocalDate periodStart, LocalDate periodEnd, boolean isSameYear) {

        int allowance = 15; // We allow year +/-15 days difference between dates to treat them as full year
                            // difference
        int yearRangeMax = 365 + allowance;
        int yearRangeMin = 365 - allowance;

        Map<String, String> periodObject = new HashMap<>();

        // period returns years, hours and days between dates eg 1 year, 3 months and 4
        // days
        Period accountsPeriod = Period.between(periodStart, periodEnd);

        long totalDaysDiff = ChronoUnit.DAYS.between(periodStart, periodEnd) + 1; // + 1 as they have time till the end
                                                                                  // of the day
        long totalMonthsDiff = ChronoUnit.MONTHS.between(periodStart, periodEnd);

        // If the days over an exact month are 1 to 15, then round down (unless
        // the period is less than 1 month, in which case round up).
        // If the days over are 16 to 31, then round up. e.g. 15 months and 4 days is
        // shown as 15 months; 15 months and 21 days is shown as 16 months.
        totalMonthsDiff = (accountsPeriod.getDays() >= 15) ? totalMonthsDiff + 1 : totalMonthsDiff; // + 1 to days to
                                                                                                    // include end date

        // If the previous and current periods both end in the same year, then the
        // heading is output as a full date e.g. ‘5 January 2015’ ’31 December 2015’.
        if (isSameYear) {
            periodObject.put(PERIOD_END, convertLocalDateToDisplayDate(periodEnd));

            return periodObject;
        }

        // If the accounting period is twelve months (+/- 15 days - period start to period end)
        // Then the heading for the balance sheet figures is: ccyy e.g. 2015 2016
        else if (totalDaysDiff >= yearRangeMin && totalDaysDiff <= yearRangeMax) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");
            periodObject.put(PERIOD_END, periodEnd.format(formatter));

            return periodObject;
        }

        // If the accounting period is less than twelve months (<350 days - period start to period end) or
        // more than twelve months (>380 days - period start to period end)
        // Then the heading for the balance sheet figures is 'x' months to dd/mm/yyyy
        // e.g. 15 months to 31/12/2016
        else {
            String monthsDiffString = (totalMonthsDiff == 1) ? " month" : " months";
            periodObject.put(PERIOD_START, totalMonthsDiff + monthsDiffString);
            periodObject.put(PERIOD_END, convertLocalDateToDisplayDate(periodEnd));

            return periodObject;
        }
    }

    /**
     * 
     * Returns true if given Java 8 {@link LocalDate} dates are in the same calendar year
     * 
     * @param date1
     * @param date2
     * @return
     * 
     */
    public boolean isSameYear(LocalDate date1, LocalDate date2) {

        return date1.getYear() == date2.getYear();
    }

    /**
     * Takes a {@link Date} and converts it to a Java 8 {@link LocalDate}
     * 
     * @param date
     * @return {@link LocalDate}
     */

    public LocalDate convertDateToLocalDate(Date date) {
        return date != null ? date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;
    }

    /**
     * Returns numDays number of LocalDates prior to currentDate
     * @param currentDate
     * @param numDays
     * @return
     */
    public List<LocalDate> getPreviousDays(LocalDate currentDate, int numDays) {
        List<LocalDate> previousDays = new ArrayList<>();

        for (int i = 1; i <= numDays; i++) {
            previousDays.add(currentDate.minusDays(i));
        }

        return previousDays;
    }

    /**
     * Returns numDays number of LocalDates in an ArrayList after currentDate 
     * or future dates up to and including todays date
     * 
     * @param currentDate - date which to calculate future dates from
     * @param numDays - number of days after of currentDate to add to arrayList
     * @param now - today's date
     * @return
     */
    public List<LocalDate> getFutureDays(LocalDate currentDate, int numDays) {
        List<LocalDate> futureDays = new ArrayList<>();
        LocalDate now = LocalDate.now();
        LocalDate nextDate;

        for (int i = 1; i <= numDays; i++) {
            nextDate = currentDate.plusDays(i);
            if (nextDate.isAfter(now)) {
                break;
            }
            futureDays.add(nextDate);
        }

        return futureDays;
    }

}
