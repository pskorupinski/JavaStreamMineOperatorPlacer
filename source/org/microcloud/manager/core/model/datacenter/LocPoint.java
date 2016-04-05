package org.microcloud.manager.core.model.datacenter;

import java.awt.Point;
import java.awt.geom.Point2D;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;


@Embeddable
@Access(value = AccessType.PROPERTY)
public class LocPoint {
	
	private Point2D point;

	public LocPoint() {
		this.point = new Point2D.Double();
	}
	
	public LocPoint(double x, double y) {
		this.point = new Point2D.Double(x,y);
	}


	@Column(name = "mc_posx", nullable = false)
	public double getX() {
		return point.getX();
	}
	public void setX(double x) {
		double y = point.getY();
		this.point.setLocation(x, y);
	}

	@Column(name = "mc_posy", nullable = false)
	public double getY() {
		return point.getY();
	}
	public void setY(double y) {
		double x = point.getX();
		this.point.setLocation(x, y);
	}
	
}
