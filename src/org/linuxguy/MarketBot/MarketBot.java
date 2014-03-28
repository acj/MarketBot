package org.linuxguy.MarketBot;

import com.gc.android.market.api.MarketSession;
import com.gc.android.market.api.MarketSession.Callback;
import com.gc.android.market.api.model.Market.CommentsRequest;
import com.gc.android.market.api.model.Market.CommentsResponse;
import com.gc.android.market.api.model.Market.ResponseContext;


public class MarketBot {
   public static void main(String[] args) {
      if (args.length < 3) {
         printUsage();
         System.exit(-1);
      }
      
      String username = args[0];
      String password = args[1];
      String app_id   = args[2];
      
      MarketSession session = new MarketSession();
      session.login(username, password);

      CommentsRequest commentsRequest = CommentsRequest.newBuilder()
            .setAppId(app_id)
            .setStartIndex(0)
            .setEntriesCount(10)
            .build();

      session.append(commentsRequest, new Callback<CommentsResponse>() {
         @Override
         public void onResult(ResponseContext context, CommentsResponse response) {
            System.out.println("Response : " + response);
         }
      });

      session.flush();
   }

   private static void printUsage() {
      System.out.println(String.format("Usage: %s <user> <password> <app package name>", MarketBot.class.getName()));
   }
}
