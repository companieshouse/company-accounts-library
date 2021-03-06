package uk.gov.companieshouse.accountsdates;

import java.time.LocalDate;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface AccountsDatesHelper {
    
    /**
     * Takes a {@link String} and converts it to a Java 8 {@link LocalDate}
     * 
     * @param stringDate
     * @return 
     */
    public LocalDate convertStringToDate(String stringDate);
    
    /**
     * Takes a {@link String} in "yyyy-MM-dd'T'HH:mm:ss.SSSZ" format and converts it to a Java 8 {@link LocalDate}
     * 
     * @param dateTimeString
     * @return
     */
    public LocalDate getLocalDatefromDateTimeString(String dateTimeString, ZoneId zoneId);
    
    
    /**
     * Takes a Java 8 {@link LocalDate} and converts it to a {@link String}
     * 
     * @param date
     * @return
     */
    public String convertDateToString(LocalDate date);

    /**
     * Formats a {@link String} date
     * 
     * @param date
     * @return
     */
    public String convertLocalDateToDisplayDate(LocalDate date) ;

    /**
     * Returns a date and time object from {@link String} date 
     * 
     * @param dateString
     * @return
     */
    public Map<String, String> getDateAndTime(String dateString, ZoneId zoneId);

    /**
     * Generate balance sheet header string to display on web and ixbrl templates based on period start and end
     * dates
     * 
     * @param periodStartString accounting period start date 
     * @param periodEndString accounting period end date 
     * @param isSameYear
     * @return
     */
    public String generateBalanceSheetHeading(String periodStartString, String periodEndString, boolean isSameYear);

    /**
     * Generate balance sheet header string to display on web and ixbrl templates based on period start and end
     * dates
     *
     * @param periodStart accounting period start date
     * @param periodEnd accounting period end date
     * @param isSameYear
     * @return
     */
    public String generateBalanceSheetHeading(LocalDate periodStart, LocalDate periodEnd, boolean isSameYear);

    /**
     *Calculate balance sheet dates display format depending on range between period start and end dates
     * 
     * @param periodStart accounting period start date
     * @param periodEnd accounting period end date 
     * @param isSameYear
     * @return
     */
    public Map<String, String> calculatePeriodRange(LocalDate periodStart, LocalDate periodEnd, boolean isSameYear);

    /**
     * Returns true if given Java 8 {@link LocalDate} dates are in the same calendar year
     * 
     * @param date1
     * @param date2
     * @return
     */
    public boolean isSameYear(LocalDate date1, LocalDate date2);
    
    
    /**
     * Takes a {@link Date} and converts it to a Java 8 {@link LocalDate}
     * 
     * @param date
     * @return {@link LocalDate}
     */
    public LocalDate convertDateToLocalDate(Date date);
    
    
    /**
     * Returns numDays number of LocalDates prior to currentDate
     * @param currentDate
     * @param numDays
     * @return
     */
    public List<LocalDate> getPreviousDays(LocalDate currentDate, int numDays);

    /**
     * Returns numDays number of LocalDates in an ArrayList after currentDate 
     * or future dates up to and including todays date
     * 
     * @param currentDate - date which to calculate future dates from
     * @param numDays - number of days after of currentDate to add to arrayList
     * @param now - today's date
     * @return
     */
    public List<LocalDate> getFutureDays(LocalDate currentDate, int numDays);

}
