package org.microcloud.manager.core.model.clientquery;

import java.util.Date;

import org.microcloud.manager.core.model.workeralgorithm.WorkerAlgorithmType;

public class ClientQuery {
	
	private Integer time;
	private Integer price;
	private WorkerAlgorithmType workerAlgorighmType;
	
	private Date startTime;
	
///////////////////////////////////////////////////////////////
// Constructors
///////////////////////////////////////////////////////////////
	
	public ClientQuery(Integer time, Integer price, WorkerAlgorithmType workerAlgorithmType) {
		this.time = time;
		this.price = price;
		this.workerAlgorighmType = workerAlgorithmType;
		this.startTime = null;
	}
	
	public ClientQuery(Integer time, Integer price, WorkerAlgorithmType workerAlgorithmType, Date startTime) {
		this.time = time;
		this.price = price;
		this.workerAlgorighmType = workerAlgorithmType;
		this.startTime = startTime;
	}
	
///////////////////////////////////////////////////////////////
// Getters and setters
///////////////////////////////////////////////////////////////
	
	/**
	 * needed time of query execution in minutes
	 * null if not defined
	 * 
	 * if real-time sources are considered, this should be the time of their work
	 */
	public Integer getTime() {
		return time;
	}
	
	/**
	 * (maximum) price in dollars that client is ready to pay
	 * null if not defined
	 */
	public Integer getPrice() {
		return price;
	}
	
	/**
	 * type of algorithm;
	 */
	public WorkerAlgorithmType getWorkerAlgorighmType() {
		return workerAlgorighmType;
	}
	
	
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date date) {
		this.startTime = date;
	}
}
