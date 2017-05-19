package com.sdj.person.lzx.framework.common.hessian;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.remoting.RemoteLookupFailureException;
import org.springframework.remoting.caucho.HessianProxyFactoryBean;

import com.caucho.hessian.client.HessianProxyFactory;

public class MyHessionProxyFactoryBean extends HessianProxyFactoryBean{
	private static final Logger logger = LoggerFactory.getLogger(MyHessionProxyFactoryBean.class);
	private long readTimeOut = 500;
	private long connectTimeOut = 30000;
	private int poolMaxSize = 20;
	private ReentrantLock lock = new ReentrantLock();
	private Condition condition = lock.newCondition();
	private AtomicInteger invokeCount = new AtomicInteger(0);
	private String user;
	private String password;
	public long getReadTimeOut() {
		return readTimeOut;
	}
	public void setReadTimeOut(long readTimeOut) {
		this.readTimeOut = readTimeOut;
	}
	
	public long getConnectTimeOut() {
		return connectTimeOut;
	}
	public void setConnectTimeOut(long connectTimeOut) {
		this.connectTimeOut = connectTimeOut;
	}
	public int getPoolMaxSize() {
		return poolMaxSize;
	}
	public void setPoolMaxSize(int poolMaxSize) {
		this.poolMaxSize = poolMaxSize;
	}
	
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	@Override
	public void prepare() throws RemoteLookupFailureException {
		HessianProxyFactory proxyFactory = new HessianProxyFactory();
		if(this.readTimeOut > 0){
			proxyFactory.setReadTimeout(this.readTimeOut);
		}
		if(this.connectTimeOut > 0){
			proxyFactory.setConnectTimeout(this.connectTimeOut);
		}
		
		if(StringUtils.isNotBlank(this.user)){
			proxyFactory.setUser(this.user);
		}
		if(StringUtils.isNotBlank(this.password)){
			proxyFactory.setPassword(this.password);
		}
		this.setProxyFactory(proxyFactory);
		super.prepare();
	}
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		this.lock.lock();
		
		try{
			if(invokeCount.get() > this.poolMaxSize){
				logger.info("invokeCount:" + invokeCount.get());
				condition.await(this.readTimeOut, TimeUnit.MILLISECONDS);
			}
			if(invokeCount.get() > this.poolMaxSize){
				throw new IOException("wait hession pool timeout: poolSize:" + this.poolMaxSize 
						+ ", timeout:" + this.readTimeOut + ",on=" + invokeCount.get());
			}
			this.invokeCount.incrementAndGet();
		}finally {
			this.lock.unlock();
		}
		
		Object object = null;
		try{
			object = super.invoke(invocation);
		}finally {
			this.lock.lock();
			try{
				this.invokeCount.decrementAndGet();
				condition.signal();
			}finally{
				this.lock.unlock();
			}
		}
		return object;
	}
	
}
