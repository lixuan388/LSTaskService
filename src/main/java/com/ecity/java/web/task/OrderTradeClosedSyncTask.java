package com.ecity.java.web.task;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import com.ecity.java.sql.db.DBTable;
import com.ecity.java.web.ls.system.fun.GFunction;
import com.ecity.java.web.taobao.api.alitripTravelTradeQueryClass;
import com.ecity.java.web.taobao.service.TaobaoService;
import com.java.sql.SQLCon;
import com.taobao.api.ApiException;
import com.taobao.api.request.AlitripTravelTradesSearchRequest;
import com.taobao.api.response.AlitripTravelTradesSearchResponse;

public class OrderTradeClosedSyncTask extends TimerTask {

  @Override
  public void run() {
    // TODO Auto-generated method stub
    GFunction.TimeTaskLog("TaskService OrderTradeClosedSyncTask","同步发起退款的订单状态","1小时");
    try {
     
      Date EndDate = new Date();
  
      Calendar cal = Calendar.getInstance();
      cal.setTime(EndDate);
      cal.add(Calendar.DATE, -30);
      Date StartDate = cal.getTime();
  
      SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  
      Long PageSize = 20l;
      // 订单状态
      // 过滤。1-等待买家付款，2-等待卖家发货（买家已付款），3-等待买家确认收货，4-交易关闭（买家发起的退款），6-交易成功，8-交易关闭（订单超时
      // 自动关单）
  //		Long order_status=2l;
      Long order_status = 4l;
      Long CurrentPage = 1l;
      Long TotalOrders = PageSize + 1l;
      AlitripTravelTradesSearchRequest req = new AlitripTravelTradesSearchRequest();
      req.setOrderStatus(order_status);
      req.setPageSize(PageSize);
      req.setStartCreatedTime(StartDate);
      req.setEndCreatedTime(EndDate);
  
      AlitripTravelTradesSearchResponse rsp = null;
      System.out.println("------------GetAlitripTravelTradeInfoTask begin-----------------------");
      System.out.println("Date:"+fmt.format(new Date()));
  
      int OrderListNum = 1;
      while (OrderListNum > 0) {
        try {
          req.setCurrentPage(CurrentPage);
          rsp = TaobaoService.Client().execute(req, TaobaoService.Sessionkey);
        } catch (ApiException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        if (rsp.getErrorCode() == null) {
          TotalOrders = rsp.getTotalOrders();
          CurrentPage = CurrentPage + 1;
  
          List<String> OrderList = rsp.getOrderStringList();
          if (OrderList != null) {
            OrderListNum = OrderList.size();
            System.out.println("OrderListNum:"+OrderListNum);
            for (String OrderID : OrderList) {
              DBTable table=new DBTable(SQLCon.GetConnect(), "select isnull(Ebo_OrderState,'') as Ebo_OrderState from Ebo_BookingOrder where ebo_sourceorderno='"+OrderID+"'");
              try {
                table.Open();
                if (table.next()) {
                  if (!table.getString("Ebo_OrderState").equals("TRADE_CLOSED")) {
//                    System.out.println("OrderID:"+OrderID+"/Ebo_OrderState:"+table.getString("Ebo_OrderState"));
                    //同步关闭状态至本地记录
                    alitripTravelTradeQueryClass.OrderInfo(OrderID, true);
                  }
                }
              }catch (SQLException e) {
                // TODO: handle exception
              } finally {
                table.CloseAndFree();
              }
              //System.out.println("OrderID:"+OrderID);
              //alitripTravelTradeQueryClass.OrderInfo(OrderID, false);
            }
          } else {
            OrderListNum = 0;
          }
        } else {
          return;
        }
      }
      System.out.println("------------GetAlitripTravelTradeInfoTask end-----------------------");
      
    } catch (Exception e) {
      // TODO: handle exception
      e.printStackTrace();
    }
  }

}
