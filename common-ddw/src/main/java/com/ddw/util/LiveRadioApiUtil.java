package com.ddw.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ddw.beans.LiveRadioUrlBean;
import com.gen.common.util.CacheUtil;
import com.gen.common.util.HttpUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;

import java.util.Date;

public class LiveRadioApiUtil {
    private static final Logger logger = Logger.getLogger(LiveRadioApiUtil.class);

    public static LiveRadioUrlBean createLiveUrl(String streamIdExt,Date endDate)throws Exception{
        String txTime=txTime= Long.toHexString(endDate.getTime()/1000).toUpperCase();
        String streamid=LiveRadioConstant.BIZID+"_"+streamIdExt;
        String md5str= DigestUtils.md5Hex(LiveRadioConstant.REFERER_KEY+streamid+txTime);
        LiveRadioUrlBean liveRadioUrlBean=new LiveRadioUrlBean();
        StringBuilder url=new StringBuilder();
        url.append("rtmp://");
        //url.append(LiveRadioConstant.BIZID);
        url.append("push.doudouwo.cn/live/");
        url.append(streamid);
        liveRadioUrlBean.setPullUrl(url.toString().replace("push","play"));

        url.append("?bizid=").append(LiveRadioConstant.BIZID).append("&");

        url.append("txSecret=").append(md5str);
        url.append("&txTime=").append(txTime);
        liveRadioUrlBean.setPushUrl(url.toString());
        liveRadioUrlBean.setStreamid(streamid);
        return liveRadioUrlBean;
    }
    public static boolean closeLoveRadio(String streamId){
        try{
            for(int i=1;i<=5;i++){
                String t=DateUtils.addHours(new Date(),12).getTime()/1000+"";
                StringBuilder builder=new StringBuilder();
                builder.append("http://fcgi.video.qcloud.com/common_access?");
                builder.append("appid=").append(LiveRadioConstant.APP_ID).append("&");
                builder.append("interface=Live_Channel_SetStatus&");
                builder.append("Param.s.channel_id=").append(streamId).append("&");
                builder.append("Param.n.status=0&");
                builder.append("t=").append(t).append("&");
                builder.append("sign=").append(DigestUtils.md5Hex(LiveRadioConstant.API_KEY+t));
                String str=HttpUtil.doGet(builder.toString());
                if(str!=null){
                    JSONObject json= JSONObject.parseObject(str);

                    if(json.getInteger("ret")==0){
                        return true;
                    }
                }
                Thread.sleep(i*200);
            }
        }catch (Exception e){
            logger.error("LiveRadioApiUtil->closeLoveRadio->关闭房间异常",e);
        }


        return false;

    }
    public static boolean isActLiveRoom(String streamId){
        //http://fcgi.video.qcloud.com/common_access
        String t=DateUtils.addHours(new Date(),12).getTime()/1000+"";
        StringBuilder builder=new StringBuilder();
        builder.append("http://fcgi.video.qcloud.com/common_access?");
        builder.append("appid=").append(LiveRadioConstant.APP_ID).append("&");
        builder.append("interface=Live_Channel_GetStatus&");
        builder.append("Param.s.channel_id=").append(streamId).append("&");
        builder.append("t=").append(t).append("&");
        builder.append("sign=").append(DigestUtils.md5Hex(LiveRadioConstant.API_KEY+t));
        String str=HttpUtil.doGet(builder.toString());

        if(str!=null){
            JSONObject json= JSONObject.parseObject(str);
            logger.info("房间状态："+json.toJSONString());

            if(json.getInteger("ret")==20601){
              return false;
            }
            JSONArray array=json.getJSONArray("output");
            if(array.isEmpty()){
                return false;
            }
            JSONObject output=array.getJSONObject(0);
            Integer status=output.getInteger("status");
            if(status==0 || status==3){
                return false;
            }

        }
        return true;
    }

    public static void main(String[] args) {
       // System.out.println(        isActLiveRoom("23115_1_41_180529215021"));
        System.out.println(        isActLiveRoom("23115_1_152_181123103415"));
        System.out.println(        closeLoveRadio("23115_1_152_181123103415"));
    }
}
