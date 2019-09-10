package tw.ga.workshop.model;

import java.util.Date;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.boolex.OnErrorEvaluator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.boolex.EvaluationException;

public class MyErrorEvaluator extends OnErrorEvaluator{
	
	public static long TEMP_TIME = new Date().getTime();
	public static final long TEMP_TIME_INTERVAL = 60*1000;
	public static int MAX_ERROR_COUNT = 5;
	public static int CURRENT_ERROR_COUNT = 0;
	
	@Override
	public boolean evaluate(ILoggingEvent event) throws NullPointerException, EvaluationException {
		if(TEMP_TIME + TEMP_TIME_INTERVAL > new Date().getTime()){
			MAX_ERROR_COUNT++;
			if(MAX_ERROR_COUNT >= 10){
				MAX_ERROR_COUNT = 10;
			}
		}else {
			MAX_ERROR_COUNT = 5;
		}
		TEMP_TIME = new Date().getTime();
		CURRENT_ERROR_COUNT++;
		if(CURRENT_ERROR_COUNT >= MAX_ERROR_COUNT && event.getLevel().levelInt >= Level.ERROR_INT){
			CURRENT_ERROR_COUNT = 0;
			return true;
		}
		return false;
	}
	
}
