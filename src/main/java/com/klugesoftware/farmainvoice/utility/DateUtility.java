package com.klugesoftware.farmainvoice.utility;

import javax.swing.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtility {

	private static DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.ITALY);

	private static String converte(Date date){
		String dateString = "";
        if (date != null)
    		dateString = dateFormat.format(date);
        return dateString;
	}
	
	private static Date converte(String dateString){
		Date date = null;
		try	{
            if ((dateString != null) && (dateString.length() > 0))
                date = dateFormat.parse(dateString);
		} catch (Exception e)	{
            date = null;
		}
        return date;
	}

	public static Date getToday(){
		Calendar cal = Calendar.getInstance(Locale.ITALY);
		return cal.getTime();
	}

	/**
	 * Converte un Date in String("dd/MM/yyyy")
	 */
	public static String converteDateToGUIStringDDMMYYYY(Date data) {
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		return format.format(data);
	}
	
	/**
	 * Converte una String("dd/MM/yyyy") in Date
	 */
	public static Date converteGUIStringDDMMYYYYToDate(String data){
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Date dataRet = null;
		try {
			dataRet = format.parse(data);
		} catch (ParseException e) {
			e.printStackTrace();
		} finally{
			return dataRet;
		}
	}
	
	/**
	 * Converte una String("dd/MM/yyyy") in una String("lun 3 gen 2018")
	 */
	public static String converteGUIStringDDMMYYYYToNameDayOfWeek(String data){
		SimpleDateFormat dataFormat = new SimpleDateFormat("E d MMM yyyy");
		return dataFormat.format(converteGUIStringDDMMYYYYToDate(data));
	}
	
	
	/**
	 * 
	 * @param data
	 * @return String: converte una String "12/12/2017" in "2018-12-12"
	 */
	public static String converteGUIStringDDMMYYYYToSqlString(String data){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date temp = DateUtility.converteGUIStringDDMMYYYYToDate(data);
		String ret;
		ret = sdf.format(temp);		
		return ret;
	}
	
	/**
	 * 
	 * @param data
	 * @return String: converte una String "yyyy-mm-dd" in "dd/MM/yyyy"
	 */
	public static String converteSqlStringToGUIString(String data){
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date temp = DateUtility.converteDBStringYYYMMDDToDate(data);
		String ret;
		ret = sdf.format(temp);		
		return ret;		
	}
	
	/**
	 * Converte una String("yyyy-mm-dd") in Date
	 */
	public static Date converteDBStringYYYMMDDToDate(String data){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date dataRet = null;
		try {
			dataRet =  format.parse(data);
		} catch (ParseException e) {
			e.printStackTrace();
		} finally{
			return dataRet;
		}
	}

	/**
	 * Converte una String("yyyymmdd") in Date
	 */
	public static Date converteDBStringYYYYMMDDToDate(String data){
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		Date dataRet = null;
		try {
			dataRet =  format.parse(data);
		} catch (ParseException e) {
			e.printStackTrace();
		} finally{
			return dataRet;
		}
	}

	
	/**
	 * Converte una Date in String("yyyy-MM-dd")
	 */
	public static String converteDateToDBStringYYYYMMDD(Date data){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.format(data);
	}

	/**
	 * Confronta due date per verificare la prima sia minore della seconda:
	 * controllo da fare per le ricerche su intervallo di date.
	 */
	public static boolean confrontaDate(Date dateFrom, Date dateTo){
		if(dateFrom.after(dateTo)){
			JOptionPane.showMessageDialog(null, "La prima data deve essere maggiore o uguale alla seconda!",null, JOptionPane.ERROR_MESSAGE);
			return false;
		}else 
			return true;
	}
	
	/**
	 * Sottrae i mesi da data odierna e restituisce una Date per la query
	 * @param mesiDaSottrarre
	 * @return Date Query
	 */
	public static Date sottraeMesiFromOdierna(int mesiDaSottrarre){
		return aggiungeMesiADataOdierna(-mesiDaSottrarre);
	}
	
	/**
	 * Aggiunge i mesi da data odierna e restituisce una Date per la query
	 * @param mesiDaAggiungere
	 * @return Date Query
	 */
	public static Date aggiungeMesiADataOdierna(int mesiDaAggiungere){
		Date toDay = new Date();
		Calendar data = Calendar.getInstance(Locale.ITALY);
		data.setTime(toDay);
		data.add(Calendar.MONTH, mesiDaAggiungere);
		return (data.getTime());
	}
	
	/**
	 * Sottrare giorni dalla data odierna e restuisce una Date per la query
	 * @param giorniDaSottrarre
	 * @return Date query
	 */
	public static Date sottraeGiorniADataOdierna(int giorniDaSottrarre){
		return aggiungeGiorniADataOdierna(-giorniDaSottrarre);
	}
	
	/**
	 * Aggiunge giorni dalla data odierna e restuisce una Date per la query
	 * @param giorniDaAggiungere
	 * @return Date query
	 */
	public static Date aggiungeGiorniADataOdierna(int giorniDaAggiungere){
		Date toDay = new Date();
		Calendar data = Calendar.getInstance(Locale.ITALY);
		data.setTime(toDay);
		data.add(Calendar.DAY_OF_YEAR, giorniDaAggiungere);
		return (data.getTime());
	}
	
	public static Date aggiungeGiorniAData(int giorniDaAggiungere, Date myDate){
		Calendar data = Calendar.getInstance(Locale.ITALY);
		data.setTime(myDate);
		data.add(Calendar.DAY_OF_YEAR, giorniDaAggiungere);
		return (data.getTime());
	}
	
	public static Date primoGiornoDelMeseCorrente(Date toDay){
		Calendar data = Calendar.getInstance(Locale.ITALY);
		data.setTime(toDay); 
		int currentYear =  data.get(Calendar.YEAR);
		int currentMonth = data.get(Calendar.MONTH);
		data.set(currentYear, currentMonth, 1);
		return (data.getTime());
	}

	public static Date ultimoGiornoDelMeseCorrente(Date toDay){
		Calendar data = Calendar.getInstance(Locale.ITALY);
		data.setTime(toDay);
		int currentYear =  data.get(Calendar.YEAR);
		int currentMonth = data.get(Calendar.MONTH);
		data.set(currentYear, currentMonth, 1);
		data.set(Calendar.DAY_OF_MONTH,data.getActualMaximum(Calendar.DAY_OF_MONTH));
		return (data.getTime());
	}

	public static Date primoGiornoAnnoCorrente(){
		Calendar myData = Calendar.getInstance(Locale.ITALY);
		myData.set(Calendar.DAY_OF_MONTH,1);
		myData.set(Calendar.MONTH,0);
		return myData.getTime();
	}

	public static Date ultimoGiornoAnnoCorrente(){
		Calendar myData = Calendar.getInstance(Locale.ITALY);
		myData.set(Calendar.DAY_OF_MONTH,31);
		myData.set(Calendar.MONTH,11);
		return myData.getTime();
	}

	public static Date primoGiornoAnnoPrecedente(){
		Calendar myData = Calendar.getInstance(Locale.ITALY);
		myData.set(Calendar.DAY_OF_MONTH,1);
		myData.set(Calendar.MONTH,0);
		myData.set(Calendar.YEAR,myData.get(Calendar.YEAR)-1);
		return myData.getTime();
	}

	public static Date ultimoGiornoAnnoPrecedente(){
		Calendar myData = Calendar.getInstance(Locale.ITALY);
		myData.set(Calendar.DAY_OF_MONTH,31);
		myData.set(Calendar.MONTH,11);
		myData.set(Calendar.YEAR,myData.get(Calendar.YEAR)-1);
		return myData.getTime();
	}


	/**
	 * 
	 * @param mese
	 * @param precedente
	 * @param periodoConfronto
	 * @return ArrayList<Date>
	 * Ritorna un array di 2 elementi Date( anno corrente): fromDate e toDate in funzione del mese selezionato; 
	 * se precedete=true allora  ritorna le date riferite al periodo precedente.
	 * Il periodo precedente può essere Mese oppure Anno in base al valore della String periodoConfronto:
	 * periodoConfronto = MESE oppure periodoConfronto = ANNO  
	 */
	public static ArrayList<Date> convertiMeseinIntervallo(Object mese, boolean precedente, String periodoConfronto){
		Date tempFrom = null;
		Date tempTo = null;
		boolean bisestile = false;
		boolean mesePrecedente = false;
		Calendar myCalendar = Calendar.getInstance(Locale.ITALY);
		if (myCalendar.getActualMaximum(Calendar.DAY_OF_YEAR) > 365){
			bisestile = true;
		}
		if (precedente){
			switch(periodoConfronto){
			case "ANNO":
					myCalendar.add(Calendar.YEAR, -1);
					break;
			case "MESE":
				mesePrecedente = true;
				break;
			}
		}
		switch ((String)mese){
		case "GENNAIO": 
			if(mesePrecedente){
				myCalendar.set(Calendar.MONTH, 11);
				myCalendar.set(Calendar.DATE, 01);
				myCalendar.add(Calendar.YEAR, -1);
			}else{
				myCalendar.set(Calendar.MONTH, 0);
				myCalendar.set(Calendar.DATE, 01);
			}
			tempFrom = myCalendar.getTime();
			myCalendar.set(Calendar.DATE,31);
			tempTo = myCalendar.getTime();
			break;
		case "FEBBRAIO":
			if(mesePrecedente){
				myCalendar.set(Calendar.MONTH, 0);
			}else{
				myCalendar.set(Calendar.MONTH, 1);
			}
			myCalendar.set(Calendar.DATE, 01);
			tempFrom = myCalendar.getTime();

			if(mesePrecedente){
				myCalendar.set(Calendar.DATE, 31);
			}else{
				if(bisestile)
					myCalendar.set(Calendar.DATE,29);
				else
					myCalendar.set(Calendar.DATE,28);
			}
			tempTo = myCalendar.getTime();
			break;
		case "MARZO":
			if(mesePrecedente){
				myCalendar.set(Calendar.MONTH, 1);
			}else{
				myCalendar.set(Calendar.MONTH, 2);
			}
			myCalendar.set(Calendar.DATE, 01);
			tempFrom = myCalendar.getTime();
			
			if(mesePrecedente){
				if(bisestile)
					myCalendar.set(Calendar.DATE,29);
				else
					myCalendar.set(Calendar.DATE,28);
			}else{
				myCalendar.set(Calendar.DATE,31);
			}
			tempTo = myCalendar.getTime();
			break;
		case "APRILE": 
			if(mesePrecedente){
				myCalendar.set(Calendar.MONTH, 2);
			}else{
				myCalendar.set(Calendar.MONTH, 3);
			}
			myCalendar.set(Calendar.DATE, 01);
			tempFrom = myCalendar.getTime();
			
			if(mesePrecedente){
				myCalendar.set(Calendar.DATE,31);
			}else{
				myCalendar.set(Calendar.DATE,30);
			}
			tempTo = myCalendar.getTime();
			break;
		case "MAGGIO":
			if(mesePrecedente){
				myCalendar.set(Calendar.MONTH, 3);
			}else{
				myCalendar.set(Calendar.MONTH, 4);
			}
			myCalendar.set(Calendar.DATE, 01);
			tempFrom = myCalendar.getTime();
			
			if(mesePrecedente){
				myCalendar.set(Calendar.DATE,30);
			}else{
				myCalendar.set(Calendar.DATE,31);
			}
			tempTo = myCalendar.getTime();
			break;
		case "GIUGNO":	
			if(mesePrecedente){
				myCalendar.set(Calendar.MONTH, 4);
			}else{
				myCalendar.set(Calendar.MONTH, 5);
			}
			myCalendar.set(Calendar.DATE, 01);
			tempFrom = myCalendar.getTime();
			
			if(mesePrecedente){
				myCalendar.set(Calendar.DATE,31);
			}else{
				myCalendar.set(Calendar.DATE,30);
			}
			tempTo = myCalendar.getTime();
			break;
		case "LUGLIO":
			if(mesePrecedente){
				myCalendar.set(Calendar.MONTH, 5);
			}else{
				myCalendar.set(Calendar.MONTH, 6);
			}
			myCalendar.set(Calendar.DATE, 01);
			tempFrom = myCalendar.getTime();
			
			if(mesePrecedente){
				myCalendar.set(Calendar.DATE,30);
			}else{
				myCalendar.set(Calendar.DATE,31);
			}
			tempTo = myCalendar.getTime();
			break;
		case "AGOSTO":
			if(mesePrecedente){
				myCalendar.set(Calendar.MONTH, 6);
			}else{
				myCalendar.set(Calendar.MONTH, 7);
			}
			myCalendar.set(Calendar.DATE, 01);
			tempFrom = myCalendar.getTime();
			
			if(mesePrecedente){
				myCalendar.set(Calendar.DATE,31);
			}else{
				myCalendar.set(Calendar.DATE,31);
			}
			tempTo = myCalendar.getTime();
			break;
		case "SETTEMBRE":
			if(mesePrecedente){
				myCalendar.set(Calendar.MONTH, 7);
			}else{
				myCalendar.set(Calendar.MONTH, 8);
			}
			myCalendar.set(Calendar.DATE, 01);
			tempFrom = myCalendar.getTime();
			
			if(mesePrecedente){
				myCalendar.set(Calendar.DATE,31);
			}else{
				myCalendar.set(Calendar.DATE,30);
			}
			tempTo = myCalendar.getTime();
			break;
		case "OTTOBRE":
			if(mesePrecedente){
				myCalendar.set(Calendar.MONTH, 8);
			}else{
				myCalendar.set(Calendar.MONTH, 9);
			}
			myCalendar.set(Calendar.DATE, 01);
			tempFrom = myCalendar.getTime();
			
			if(mesePrecedente){
				myCalendar.set(Calendar.DATE,30);
			}else{
				myCalendar.set(Calendar.DATE,31);
			}
			tempTo = myCalendar.getTime();
			break;
		case "NOVEMBRE":
			if(mesePrecedente){
				myCalendar.set(Calendar.MONTH, 9);
			}else{
				myCalendar.set(Calendar.MONTH, 10);
			}
			myCalendar.set(Calendar.DATE, 01);
			tempFrom = myCalendar.getTime();
			
			if(mesePrecedente){
				myCalendar.set(Calendar.DATE,31);
			}else{
				myCalendar.set(Calendar.DATE,30);
			}
			tempTo = myCalendar.getTime();
			break;
		case "DICEMBRE":
			if(mesePrecedente){
				myCalendar.set(Calendar.MONTH, 10);
			}else{
				myCalendar.set(Calendar.MONTH, 11);
			}
			myCalendar.set(Calendar.DATE, 01);
			tempFrom = myCalendar.getTime();
			
			if(mesePrecedente){
				myCalendar.set(Calendar.DATE,30);
			}else{
				myCalendar.set(Calendar.DATE,31);
			}
			tempTo = myCalendar.getTime();
			break;
	}
	ArrayList<Date> ret = new ArrayList<Date>();
	ret.add(tempFrom);
	ret.add(tempTo);
	return ret;
	}

	/**
	 * @param myDate
	 * @return int: numero del giorno nel mese
	 */
	public static int getGiornoDelMese(Date myDate) {
		Calendar myCalendar = Calendar.getInstance(Locale.ITALY);
		myCalendar.setTime(myDate);
		return (myCalendar.get(Calendar.DAY_OF_MONTH));
	}
	/**
	 * 
	 * @param myDate
	 * @return int 
	 * Ritorno il numero corrispondete al mese: 1-Gennaio;2-Febbraio etc...
	 */
	public static int getMese(Date myDate){
		Calendar myCalendar = Calendar.getInstance(Locale.ITALY);
		myCalendar.setTime(myDate);
		return (myCalendar.get(Calendar.MONTH) + 1);
	}
	
	/**
	 * 
	 * @param myDate
	 * @return int
	 * ritorna il numero corrispondete all'anno
	 */
	public static int getAnno(Date myDate){
		Calendar myCalendar = Calendar.getInstance(Locale.ITALY);
		myCalendar.setTime(myDate);
		return (myCalendar.get(Calendar.YEAR));
	}
	
	/**
	 * @param myDate
	 * @return String: gli ultimi due caratteri dell'anno di myDate, ad esempio se 
	 * myData: 12/02/2018 ritorna una string "18" 
	 */
	public static String getAnnoXX(Date myDate) {
		Calendar myCal = Calendar.getInstance(Locale.ITALY);
		myCal.setTime(myDate);
		int temp = myCal.get(Calendar.YEAR);
		String sTemp = Integer.toString(temp);
		return sTemp.substring(2);
	}
	
	public static Date getDataOdierna(){
		Calendar myCalendar = Calendar.getInstance(Locale.ITALY);
		return myCalendar.getTime();
	}
	
	/**
	 * 
	 * @param dateFrom
	 * @param dateTo
	 * @return true: se l'intervallo corrisponde ad un mese intero; altirmenti false.
	 */
	static public boolean intervalloMensile(Date dateFrom, Date dateTo){
		
		Calendar tempCal = Calendar.getInstance(Locale.ITALY);
		tempCal.setTime(dateFrom);
		int meseFrom = tempCal.get(Calendar.MONTH)+1;
		int dayFrom = tempCal.get(Calendar.DAY_OF_MONTH);
		tempCal.setTime(dateTo);
		int meseTo = tempCal.get(Calendar.MONTH)+1;
		int dayTo = tempCal.get(Calendar.DAY_OF_MONTH);
		
		if (meseFrom == meseTo){
			int differenzaDays = dayTo-dayFrom;
			if( (meseFrom==1) || (meseFrom==3) || (meseFrom==5) || (meseFrom==7) 
					|| (meseFrom==8) || (meseFrom==10) || (meseFrom==12)){
				if (differenzaDays == 30)
					return true;
				else
					return false;
			}
			else
				if((meseFrom==2) && ( (differenzaDays == 27) || (differenzaDays == 28)  ))
					return true;
				else
					if((differenzaDays == 29))
						return true;
					else 
						return false;			
		}
		else
			return false;		
	}
	
	/**
	 * 
	 * @param dateFrom
	 * @return String: ritorna una stringa della data dell'ultimo giorno del mese di dateFrom
	 */
	static public String fineMese(Date dateFrom){
		Calendar myCalTemp = Calendar.getInstance(Locale.ITALY);
		myCalTemp.setTime(dateFrom);
		Date dateTemp;
		myCalTemp.set(myCalTemp.get(Calendar.YEAR), myCalTemp.get(Calendar.MONTH), myCalTemp.getActualMaximum(Calendar.DAY_OF_MONTH));
		dateTemp  = myCalTemp.getTime();
		return converteDateToGUIStringDDMMYYYY(dateTemp);
	}

	/**
	 *
	 * @param dateFrom
	 * @return String: ritorna una stringa della data del primo giorno del mese di dateFrom
	 */
	static public String inizioMese(Date dateFrom){
		Calendar myCalTemp = Calendar.getInstance(Locale.ITALY);
		myCalTemp.setTime(dateFrom);
		Date dateTemp;
		myCalTemp.set(myCalTemp.get(Calendar.YEAR), myCalTemp.get(Calendar.MONTH), myCalTemp.getActualMinimum(Calendar.DAY_OF_MONTH));
		dateTemp  = myCalTemp.getTime();
		return converteDateToGUIStringDDMMYYYY(dateTemp);
	}
	
	/**
	 * 
	 * @param dataFrom
	 * @param dataTo
	 * @param periodoConfronto
	 * @return ArrayList<Date>: ritorna un arrayList con due Date, la prima è la dateFromPrecedente mentre 
	 * la seconda è dateToPrecedente; il periodo precedente si riferisce ad ANNO oppure MESE in base al valore 
	 * del periodoConfronto.
	 */
	static public ArrayList<Date> intervalloDiConfronto(Date dataFrom, Date dataTo, String periodoConfronto){
	
		ArrayList<Date> ret = new ArrayList<Date>();
		Calendar myCalFrom = Calendar.getInstance(Locale.ITALY);
		Calendar myCalTo = Calendar.getInstance(Locale.ITALY);
		boolean meseIntero = false;
		myCalTo.setTime(dataTo);
		myCalFrom.setTime(dataFrom);
		int dayTo = myCalTo.get(Calendar.DAY_OF_MONTH);
		
		if(dayTo == myCalTo.getActualMaximum(Calendar.DAY_OF_MONTH))
			meseIntero = true;
		
		switch(periodoConfronto){
			case "ANNO":
				myCalFrom.add(Calendar.YEAR, -1);
				myCalTo.add(Calendar.YEAR, -1);
				break;
			case "MESE":
				myCalFrom.add(Calendar.MONTH, -1);
				myCalTo.add(Calendar.MONTH, -1);
				break;
		}
		ret.add(myCalFrom.getTime());
		if(meseIntero){
			myCalTo.set(Calendar.DAY_OF_MONTH, myCalFrom.getActualMaximum(Calendar.DAY_OF_MONTH));
			ret.add(myCalTo.getTime());
		}else
			ret.add(myCalTo.getTime());
		
		return ret;
	}
	
	/**
	 * 
	 * @param dateFrom
	 * @param dateTo
	 * @return boolean: ritorna true se l'intervallo è all'interno del mese altrimenti false.
	 */
	static public boolean isMese(Date dateFrom, Date dateTo){
		Calendar myCalFrom = Calendar.getInstance(Locale.ITALY);
		Calendar myCalTo = Calendar.getInstance(Locale.ITALY);
		myCalFrom.setTime(dateFrom);
		myCalTo.setTime(dateTo);
		if (myCalTo.get(Calendar.MONTH) != myCalFrom.get(Calendar.MONTH)){
			return false;
		}else 
			return true;
	}

	/**
	 *
	 * @param firstDate
	 * @param lastDate
	 * @param annoPrecedente
	 * @return Date firstDateBefore, Date lastDateBefore
	 *
	 * Se anno precedente = true ritorna il relativo intervallo riferito all'anno precedente
	 * altrimenti ritorna il relativo intervallo riferito al mese precedente
	 */
	static public Date[] datesBefore(Date firstDate, Date lastDate, boolean annoPrecedente){

		Date firstDateBefore;
		Date lastDateBefore;
		Calendar myCal = Calendar.getInstance(Locale.ITALY);
		myCal.setTime(firstDate);
		if (annoPrecedente){
			myCal.set(Calendar.YEAR,myCal.get(Calendar.YEAR)-1);
			firstDateBefore = myCal.getTime();
			myCal.setTime(lastDate);
			myCal.set(Calendar.YEAR,myCal.get(Calendar.YEAR)-1);
			lastDateBefore = myCal.getTime();
		}else{
			myCal.set(Calendar.MONTH,myCal.get(Calendar.MONTH)-1);
			firstDateBefore = myCal.getTime();
			myCal.setTime(lastDate);
			myCal.set(Calendar.MONTH,myCal.get(Calendar.MONTH)-1);
			lastDateBefore = myCal.getTime();
		}

		Date[] dates = {firstDateBefore,lastDateBefore};
		return  dates;
	}

	/**
	 *
	 * @return Date of first day of current month
	 */
	public static Date getDateFromMeseCorrente(){
		Calendar cal = Calendar.getInstance(Locale.ITALY);
		cal.set(Calendar.DAY_OF_MONTH,1);
		return cal.getTime();
	}

	/**
	 *
	 * @return Date of today
	 */
	public static Date getDateToMeseCorrente(){
		Calendar cal = Calendar.getInstance(Locale.ITALY);
		return  cal.getTime();
	}

	/**
	 *
	 * @return Date La prima data dell'intervallo di date fra il primo giorno dell'anno corrente ed oggi
	 */
	public static Date getDateFromTuttiMesi(){
		Calendar cal = Calendar.getInstance(Locale.ITALY);
		cal.set(cal.get(Calendar.YEAR),0,1);
		return  cal.getTime();
	}

	/**
	 *
	 * @return Date La seconda data dell'intervallo fra il primo giorno dell'anno corrente ed oggi
	 */
	public static Date getDateToTuttiMesi(){
		Calendar cal = Calendar.getInstance(Locale.ITALY);
		return cal.getTime();
	}

	/**
	 *
	 * @return Date La prima data dell'intervallo di date fra il primo giorno dell'anno corrente ed oggi
	 */
	public static Date getDateFromUltimi3Mesi(){
		Calendar cal = Calendar.getInstance(Locale.ITALY);
		cal.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH)-2,1);
		return  cal.getTime();
	}

	/**
	 *
	 * @return Date La seconda data dell'intervallo fra il primo giorno dell'anno corrente ed oggi
	 */
	public static Date getDateToUltimi3Mesi(){
		Calendar cal = Calendar.getInstance(Locale.ITALY);
		return cal.getTime();
	}

}
