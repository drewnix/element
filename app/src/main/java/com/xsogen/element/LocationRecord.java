package com.xsogen.element;

public class LocationRecord {
  private int id;
  private int zone_id;
  private double x;
  private double y;

  public LocationRecord(int id, double x, double y) {
    this.id = id;
    this.x = x;
    this.y = y;
  }

  public int getId() {
    return id;
  }

  public int getZoneId() {
    return zone_id;
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }
}
