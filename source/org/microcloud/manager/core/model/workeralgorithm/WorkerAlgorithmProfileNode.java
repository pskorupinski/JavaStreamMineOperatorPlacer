package org.microcloud.manager.core.model.workeralgorithm;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity(name="workeralgorithm_profilenode")
public class WorkerAlgorithmProfileNode implements Comparable<WorkerAlgorithmProfileNode> {
	
	@Embeddable
	public static class WorkerAlgorithmProfileNodePk implements Serializable {
		public WorkerAlgorithmProfileNodePk() {};
		private static final long serialVersionUID = 1062833372676492281L;
		@ManyToOne
		@JoinColumn(name = "wa_id")
		private WorkerAlgorithm workerAlgorithm;
		@Column(name = "wapn_slicesno")
		private int slicesNo;
	}
	
	@Id
	private WorkerAlgorithmProfileNodePk pk = new WorkerAlgorithmProfileNodePk();
	
	/**
	 * how many MB/s can be processed
	 */
	@Column(name = "wapn_velocity")
	private double velocity;	

////////////////////////////////////////////////////////
// getters and setters
////////////////////////////////////////////////////////	
	
	public WorkerAlgorithm getWorkerAlgorithm() {
		return pk.workerAlgorithm;
	}
	public void setWorkerAlgorithm(WorkerAlgorithm workerAlgorithm) {
		pk.workerAlgorithm = workerAlgorithm;
	}
	
	public int getSlicesNo() {
		return pk.slicesNo;
	}
	public void setSlicesNo(int slicesNo) {
		pk.slicesNo = slicesNo;
	}
	
	public double getVelocity() {
		return velocity;
	}
	public void setVelocity(double velocity) {
		this.velocity = velocity;
	}
	@Override
	public int compareTo(WorkerAlgorithmProfileNode o) {
		return this.getSlicesNo() - o.getSlicesNo();
	}
}
