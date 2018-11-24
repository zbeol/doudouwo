package com.ddw;

import com.ApiApplication;
import com.ddw.beans.BiddingDTO;
import com.ddw.services.BiddingService;
import com.ddw.services.IncomeService;
import com.ddw.token.TokenUtil;
import com.gen.common.util.CacheUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;


@RunWith(SpringRunner.class)
@SpringBootTest(classes= ApiApplication.class)
public class BiddingServiceTest {

    @Autowired
    private BiddingService biddingService;
    @Autowired
    private IncomeService incomeService;

    @Test
    public void testBidOrderInfo(){
        String token= TokenUtil.createToken("openid");
        TokenUtil.putStoreid(token,1);
        TokenUtil.putUseridAndName(token,8,"test123123");
        TokenUtil.putStreamId(token,"23115_1_8_180516180728");

        Map paymap=new HashMap();
        paymap.put("name","haha");
        paymap.put("openid","haha");
        paymap.put("time",60);
        paymap.put("bidPrice",60);

        String useridBid="8-19";
         CacheUtil.put("pay","bidding-success-1_8_180516180728",useridBid);
        CacheUtil.put("pay","bidding-pay-"+useridBid,paymap);

        //String retStr=(String) CacheUtil.get("pay","bidding-finish-pay-"+TokenUtil.getUserId(token));
      //  System.out.println(this.biddingService.getBidOrderInfoByGoddess(token,"1_8_180516180728"));;
    }
    @Test
    public void testPutPrice()throws Exception{
        String token= TokenUtil.createToken("openid");
        TokenUtil.putStoreid(token,1);
        TokenUtil.putUseridAndName(token,26,"test123");
        TokenUtil.putGroupId(token,"1_8_180530232503");
        BiddingDTO dto=new BiddingDTO();
        dto.setPrice(10000);
        dto.setTime(60);
        System.out.println(this.biddingService.putPrice(token,dto));;
    }
    @Test
    public void testIncomeService()throws Exception{
        this.incomeService.handleGoddessIncome(1);
    }
}
