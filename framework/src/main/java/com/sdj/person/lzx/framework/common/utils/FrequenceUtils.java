package com.sdj.person.lzx.framework.common.utils;

import org.apache.commons.lang3.time.StopWatch;

public final class FrequenceUtils {
	public static void limit(final long limitSplitTime , final int limitCount) throws InterruptedException{
		FrequenceUnit unit = threadLocal.get();
		unit.limitSplitTime = limitSplitTime;
		unit.limitCount = limitCount;
		unit.watch.split();
		long diffTime = unit.limitSplitTime - unit.watch.getSplitTime();
		if(diffTime >= 0){
			if(unit.realCount >= unit.limitCount){
				unit.watch.suspend();
				Thread.sleep(diffTime);
				unit.watch.resume();
				unit.realCount = 0;
			}
			unit.realCount++;
		}
	}
	
	private static class FrequenceUnit{
		public FrequenceUnit() {
			this.watch = new StopWatch();
		}
		long limitSplitTime;
		int limitCount;
		StopWatch watch;
		int realCount = 0;
	}
	
	private static ThreadLocal<FrequenceUnit> threadLocal = new ThreadLocal<FrequenceUnit>(){
		protected synchronized FrequenceUnit initialValue() {
			FrequenceUnit unit = new FrequenceUnit();
			unit.watch.start();
			return unit;
			
		}
	};
}
