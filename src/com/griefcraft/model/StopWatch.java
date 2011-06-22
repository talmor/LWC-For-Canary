package com.griefcraft.model;

public class StopWatch
{
  private long startTime = 0L;
  private long stopTime = 0L;
  private boolean running = false;

  public long getElapsedTime()
  {
    long l;
    if (this.running)
      l = System.currentTimeMillis() - this.startTime;
    else
      l = this.stopTime - this.startTime;
    return l;
  }

  public long getElapsedTimeSecs()
  {
    long l;
    if (this.running)
      l = (System.currentTimeMillis() - this.startTime) / 1000L;
    else
      l = (this.stopTime - this.startTime) / 1000L;
    return l;
  }

  public void start()
  {
    this.startTime = System.currentTimeMillis();
    this.running = true;
  }

  public void stop()
  {
    this.stopTime = System.currentTimeMillis();
    this.running = false;
  }
}

/* Location:           D:\dev\Minecraft Mods\server_1.6.Crow_b1.1.7\plugins\LWC.jar
 * Qualified Name:     com.griefcraft.model.StopWatch
 * JD-Core Version:    0.6.0
 */