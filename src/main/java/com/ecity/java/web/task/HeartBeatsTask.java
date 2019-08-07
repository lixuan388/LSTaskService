package com.ecity.java.web.task;

import java.util.TimerTask;

import com.ecity.java.web.ls.system.fun.GFunction;



public class HeartBeatsTask extends TimerTask {
  

//  public static  ScheduledExecutorService timer = Executors.newScheduledThreadPool(1);  
  @Override
  public void run() {
    GFunction.TimeTaskLog("TaskService HeartBeatsTask","心跳服务，验证主服务状态是否正常","1分钟");
    System.err.println("TaskService HeartBeatsTask");
  }
//  
//  public static void scheduleWithFixedDelay(long arg1, long arg2, TimeUnit arg3) {
//    timer.scheduleWithFixedDelay(new HeartBeatsTask(), arg1, arg1,arg3);
//  }
  
}
