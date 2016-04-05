package org.microcloud.manager.core.model.workeralgorithm;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.OrderColumn;

import org.microcloud.manager.persistence.objectsloader.WorkerAlgorithmDao;

@Entity(name="workeralgorithm")
public class WorkerAlgorithm {
	
	/**
	 * Based on a database information on algorithm, chooses a number of worker slices that will be able to proceed such an amount of data.
	 * 
	 * @param bandwidth
	 * @param workerAlgorithmType
	 * @return
	 */
	public static Integer doFindWorkersNumber(double bandwidth, WorkerAlgorithmType workerAlgorithmType) {
		
		int workersNumber = 0;
		
		WorkerAlgorithmDao waDao = new WorkerAlgorithmDao();
		WorkerAlgorithm wa = waDao.getByType(workerAlgorithmType);
		
		Set<WorkerAlgorithmProfileNode> wapNodes = wa.getWorkerAlgorithmProfileNodes();
		
		for(WorkerAlgorithmProfileNode n : wapNodes) {
			if(n.getVelocity() > bandwidth) {
				workersNumber = n.getSlicesNo();
				break;
			}
		}
		
		return workersNumber;
	}

	@Id
	@GeneratedValue
	@Column(name = "wa_id")
	private int id;
	
	@Column(name = "wa_type")
	@Enumerated(EnumType.STRING)
	private WorkerAlgorithmType type;
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    @JoinColumn(name="wa_id")
	@OrderBy("wapn_velocity")
	private Set<WorkerAlgorithmProfileNode> workerAlgorithmProfileNodes;

////////////////////////////////////////////////////////
// getters and setters
////////////////////////////////////////////////////////
	
	public WorkerAlgorithmType getType() {
		return type;
	}

	public void setType(WorkerAlgorithmType type) {
		this.type = type;
	}

	public Set<WorkerAlgorithmProfileNode> getWorkerAlgorithmProfileNodes() {
		return workerAlgorithmProfileNodes;
	}

	public void setWorkerAlgorithmProfileNodes(Set<WorkerAlgorithmProfileNode> workerAlgorithmProfileNodes) {
		this.workerAlgorithmProfileNodes = workerAlgorithmProfileNodes;
	}	
	
}
