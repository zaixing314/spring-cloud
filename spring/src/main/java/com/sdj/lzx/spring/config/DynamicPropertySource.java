package com.sdj.lzx.spring.config;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.MapPropertySource;

public class DynamicPropertySource extends MapPropertySource{
	private static Logger logger = LoggerFactory.getLogger(DynamicPropertySource.class);
	
	private static ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
	private static Map<String, Object> map = new ConcurrentHashMap<>();
	static{
		scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
			
			@Override
			public void run() {
				map = dynamicLoadMapInfo();
			}
		}, 1, 10, TimeUnit.SECONDS);
	}
	
	public DynamicPropertySource(String name){
		super(name, map);
	}
	
	public DynamicPropertySource(String name, Map<String, Object> source) {
		super(name, source);
	}

	private static Map<String, Object> dynamicLoadMapInfo(){
		return mockMapInfo();
	}
	
	private static Map<String, Object> mockMapInfo(){
		Map<String, Object> map = new HashMap<String, Object>();
		int randomData = new Random().nextInt();
		logger.info("random data{}; currentTime:{}", randomData, sdf.format(new Date()));
		return map;
	}
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
}
