package org.microcloud.manager.core.placer.solution;

import org.microcloud.manager.core.placer.solution.view.OrderNumber;

public abstract class OrderableSolutionElement implements OrderNumber {
	private int orderNumber;
	
	@Override
	public int getOrderNumber() {
		return this.orderNumber;
	}

	@Override
	public void setOrderNumber(int number) {
		this.orderNumber = number;
		
	}
	
	@Override
	public String toString() {
		return "NO. " + orderNumber + ": ";
	}

}
