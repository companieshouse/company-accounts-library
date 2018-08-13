package uk.gov.companieshouse.accountsDates;

import java.text.ParseException;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import uk.gov.companieshouse.accountsDates.AccountsDates;

public class AccountsDatesTest {

	private static final String YYYY_MM_DD = "yyyy-MM-dd";
	private static final String PERIOD_START = "periodStart";
	private static final String PERIOD_END = "periodEnd";

	private SimpleDateFormat simpleDateFormat;
	private AccountsDates datesHelper = new AccountsDates();

	@Test
	public void convertStringtoDate() throws ParseException {

		LocalDate testDate = LocalDate.parse("2017-03-05");
		LocalDate date = datesHelper.convertStringToDate("2017-03-05");

		assertEquals(testDate, date);

		testDate = LocalDate.parse("2013-01-22");
		date = datesHelper.convertStringToDate("2013-01-22");
		assertEquals(testDate, date);

	}

	@Test
	public void convertLocalDatetoDisplayDate() {

		String date = datesHelper.convertLocalDateToDisplayDate(LocalDate.parse("2017-03-05"));
		assertEquals("5 March 2017", date);

		date = datesHelper.convertLocalDateToDisplayDate(LocalDate.parse("2010-11-12"));
		assertEquals("12 November 2010", date);

		date = datesHelper.convertLocalDateToDisplayDate(LocalDate.parse("2015-02-27"));
		assertEquals("27 February 2015", date);

	}

	@Test
	public void getDateAndTime() {

		String dateString = "2017-12-31T18:15:00.000Z";
		Map dateMap = datesHelper.getDateAndTime(dateString);
		assertEquals("31 December 2017", dateMap.get("date"));
		assertEquals("6:15 pm", dateMap.get("time"));

		dateString = "2016-12-02T03:15:22Z";
		dateMap = datesHelper.getDateAndTime(dateString);
		assertEquals("2 December 2016", dateMap.get("date"));
		assertEquals("3:15 am", dateMap.get("time"));

	}

	@Test
	public void convertDatetoString() throws ParseException {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YYYY_MM_DD);

		LocalDate testDate = LocalDate.parse("2017-03-05", formatter);
		String stringDate = datesHelper.convertDateToString(testDate);
		assertEquals("2017-03-05", stringDate);

		LocalDate testDate2 = LocalDate.parse("2015-12-17", formatter);
		String stringDate2 = datesHelper.convertDateToString(testDate2);
		assertEquals("2015-12-17", stringDate2);

	}

	@Test
	public void calculatePeriodRangeOver2Years() throws ParseException {

		// Days difference over 2 years
		LocalDate testDateStart = LocalDate.parse("2013-03-07");
		LocalDate testDateEnd = LocalDate.parse("2017-12-17");
		Map<String, String> periodMap = datesHelper.calculatePeriodRange(testDateStart, testDateEnd, false);
		assertEquals("57 months", periodMap.get(PERIOD_START));
		assertEquals("17 December 2017", periodMap.get(PERIOD_END));
	}

	@Test
	public void calculatePeriodRangeOver380() throws ParseException {

		// Days difference over 380
		LocalDate testDateStart = LocalDate.parse("2015-03-07");
		LocalDate testDateEnd = LocalDate.parse("2017-12-17");
		Map<String, String> periodMap = datesHelper.calculatePeriodRange(testDateStart, testDateEnd, false);
		assertEquals("33 months", periodMap.get(PERIOD_START));
		assertEquals("17 December 2017", periodMap.get(PERIOD_END));
	}

	@Test
	public void calculatePeriodRangeBelow350() throws ParseException {

		// Days difference below 350
		LocalDate testDateStart = LocalDate.parse("2016-08-03");
		LocalDate testDateEnd = LocalDate.parse("2017-02-16");
		Map<String, String> periodMap = datesHelper.calculatePeriodRange(testDateStart, testDateEnd, false);
		assertEquals("6 months", periodMap.get(PERIOD_START));
		assertEquals("16 February 2017", periodMap.get(PERIOD_END));
	}

	@Test
	public void calculatePeriodLessThanOneMonth() throws ParseException {

		// Month difference less than one month
		LocalDate testDateStart = LocalDate.parse("2015-03-07");
		LocalDate testDateEnd = LocalDate.parse("2015-04-01");
		Map<String, String> periodMap = datesHelper.calculatePeriodRange(testDateStart, testDateEnd, false);
		assertEquals("1 month", periodMap.get(PERIOD_START));
		assertEquals("1 April 2015", periodMap.get(PERIOD_END));
	}

	@Test
	public void calculatePeriodRangeBelow350RoundUp() throws ParseException {

		// Days difference below 350 with month round up ( remaining days > 15 )
		LocalDate testDateStart = LocalDate.parse("2016-08-01");
		LocalDate testDateEnd = LocalDate.parse("2017-02-17");
		Map<String, String> periodMap = datesHelper.calculatePeriodRange(testDateStart, testDateEnd, false);
		assertEquals("7 months", periodMap.get(PERIOD_START));
		assertEquals("17 February 2017", periodMap.get(PERIOD_END));
	}

	@Test
	public void calculatePeriodRangeSameYear() throws ParseException {
		// Same year
		LocalDate testDateEndLast = LocalDate.parse("2017-08-12");
		LocalDate testDateEndNext = LocalDate.parse("2017-11-23");
		Map<String, String> periodMap = datesHelper.calculatePeriodRange(testDateEndLast, testDateEndNext, true);
		assertNull(periodMap.get(PERIOD_START));
		assertEquals("23 November 2017", periodMap.get(PERIOD_END));
	}

	@Test
	public void calculatePeriod12Months() throws ParseException {
		// 12 months acc period ( +/- 15 days )
		LocalDate testDateStart = LocalDate.parse("2016-08-12");

		System.out.println("periodStartDate: " + testDateStart);

		LocalDate testDateEnd = LocalDate.parse("2017-08-23");

		System.out.println("periodEndDate: " + testDateStart);
		Map<String, String> periodMap = datesHelper.calculatePeriodRange(testDateStart, testDateEnd, false);
		assertNull(periodMap.get(PERIOD_START));
		assertEquals("2017", periodMap.get(PERIOD_END));

	}

	@Test
	public void convertDateToLocalDate() throws ParseException {

		simpleDateFormat = new SimpleDateFormat(YYYY_MM_DD);

		Date date = simpleDateFormat.parse("2017-03-05");
		LocalDate localDate = datesHelper.convertDateToLocalDate(date);
		assertEquals("2017-03-05", localDate.toString());
	}

	@Test
	public void convertStringToLocalDate() {

		String date = "2017-03-05";
		LocalDate localDate = datesHelper.convertStringToDate(date);
		assertEquals("2017-03-05", localDate.toString());
	}

	@Test
	public void getPreviousDays() {

		int numDays = 3;

		LocalDate localDate = LocalDate.of(2017, 1, 1);
		List<LocalDate> previousDays = datesHelper.getPreviousDays(localDate, numDays);
		List<LocalDate> testPreviousDays = new ArrayList<>();

		for (int i = 1; i <= numDays; i++) {
			testPreviousDays.add(localDate.minusDays(i));
		}

		assertEquals(testPreviousDays, previousDays);
	}

	@Test
	public void getFutureDays() {

		final int numDays = 7;

		for (int i = 0; i < 9; i++) {
			getFutureDays(numDays, i);
		}
	}

	private void getFutureDays(int numberOfDays, int daysDifference) {
		LocalDate now = LocalDate.now();
		LocalDate localDate = now.minusDays(daysDifference);
		List<LocalDate> futureDays = datesHelper.getFutureDays(localDate, numberOfDays);
		List<LocalDate> testFutureDays = new ArrayList<>();

		int numberOfFutureDays = Math.min(daysDifference, numberOfDays);

		for (int i = 1; i <= numberOfFutureDays; i++) {
			testFutureDays.add(localDate.plusDays(i));
		}

		assertEquals(testFutureDays, futureDays);
	}

	@Test
	public void getFutureDatesLeapYear() {

		final LocalDate startDate = LocalDate.of(2016, 02, 26);
		final int numberOfDays = 7;

		List<LocalDate> futureDays = datesHelper.getFutureDays(startDate, numberOfDays);

		assertTrue(futureDays.contains(LocalDate.of(2016, 02, 29)));
	}

	@Test
	public void generateCorrectBalanceSheetHeader() {

		// test yyyy returned for 12 month period
		assertEquals("2017",
				datesHelper.generateBalanceSheetHeading("2016-01-01T00:00:00.000Z", "2017-01-14T00:00:00.000Z", false));

		// Test 381 days shows month (more than 12 month period)
		assertEquals("13 months to 16 February 2016",
				datesHelper.generateBalanceSheetHeading("2015-02-01T00:00:00.000Z", "2016-02-16T00:00:00.000Z", false));

		// Test 349 days shows month (less than 12 month period)
		assertEquals("11 months to 1 January 2017",
				datesHelper.generateBalanceSheetHeading("2016-01-19T00:00:00.000Z", "2017-01-01T00:00:00.000Z", false));

		// Test exactly 381 days shows months leap year
		assertEquals("13 months to 16 February 2015",
				datesHelper.generateBalanceSheetHeading("2014-02-01T00:00:00.000Z", "2015-02-16T00:00:00.000Z", false));
		
		// Test 336 days shows 'month' rather than 'months'
		assertEquals("1 month to 1 April 2015",
				datesHelper.generateBalanceSheetHeading("2015-03-07T00:00:00.000Z", "2015-04-01T00:00:00.000Z", false));

		// "Test exactly 351 days show yyyy"
		assertEquals("2015",
				datesHelper.generateBalanceSheetHeading("2014-04-01T00:00:00.000Z", "2015-03-16T00:00:00.000Z", false));
		
		// Test exactly 351 days show years leap year
		assertEquals("2016",
				datesHelper.generateBalanceSheetHeading("2015-04-01T00:00:00.000Z", "2016-03-16T00:00:00.000Z", false));

		// Test 1st year filing within 15 days either side of 1 year period with same
		// year is just year
		assertEquals("30 June 2015",
				datesHelper.generateBalanceSheetHeading("2014-06-01T00:00:00.000Z", "2015-06-30T00:00:00.000Z", true));

	}
}
