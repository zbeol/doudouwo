package com.ddw.controller;

import com.alibaba.fastjson.JSONObject;
import com.ddw.beans.*;
import com.ddw.enums.PayTypeEnum;
import com.ddw.services.PayCenterService;
import com.ddw.services.ReviewService;
import com.ddw.services.WithdrawService;
import com.ddw.token.Idemp;
import com.ddw.token.Token;
import com.ddw.token.TokenUtil;
import com.gen.common.exception.GenException;
import com.gen.common.util.ThreadLocalUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.*;


@Api(value = "支付中心",description = "支付中心",tags = {"支付中心"})
@RestController
@RequestMapping("/ddwapp/paycenter")
public class PayCenterController {
    private final Logger logger = Logger.getLogger(PayCenterController.class);


    @Autowired
    private ReviewService reviewService;

    @Autowired
    private PayCenterService payCenterService;

    @Autowired
    private WithdrawService withdrawService;

    @Idemp("searchPayStatus")
    @Token
    @ApiOperation(value = "查询支付状态",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/query/paystatus/{token}")
    @ResponseBody
    public ResponseApiVO searchPayStatus (@PathVariable String token, @RequestBody @ApiParam(name="args",value="传入json格式",required=true)PayStatusDTO args){
        try {
            logger.info("searchPayStatus->request："+args);
            ResponseApiVO vo= this.payCenterService.searchPayStatus(token,args);
            logger.info("searchPayStatus->response："+vo);

            return vo;
        }catch (Exception e){
            logger.error("PayCenterController-searchPayStatus-》支付状态查询-》系统异常",e);
            return new ResponseApiVO(-1,"支付状态查询失败",null);
        }
    }
    @Idemp("searchPayStatus")
    @Token
    @ApiOperation(value = "查询支付状态",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/query/h5/paystatus")
    @ResponseBody
    public ResponseApiVO searchPayStatusByH5 (PayStatusDTO args){
        try {
            JSONObject jsonObj=(JSONObject)ThreadLocalUtil.get();
            if(jsonObj!=null){
                logger.info("searchPayStatusByH5->request-：jsonObj："+jsonObj+", args:"+args);
                String base64Token=jsonObj.getString("t");
                return searchPayStatus(base64Token,args);
            }

        }catch (Exception e){
            logger.error("PayCenterController-searchPayStatusByH5-》支付状态查询-》系统异常",e);

        }
        return new ResponseApiVO(-1,"支付状态查询失败",null);
    }
    @Idemp
    @Token
    @ApiOperation(value = "统一支付",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/public/pay/{token}")
    @ResponseBody
    public ResponseApiVO publicPay(@PathVariable String token, @RequestBody @ApiParam(name="args",value="传入json格式",required=true)PublicPayDTO args){
        try {
            //return this.walletService.searchPayStatus(token,args);
            logger.info("publicPay->request："+args);
            ResponseApiVO vo= this.payCenterService.prePay(token,args.getPayType(),(PayDTO)args );
            logger.info("publicPay->response:"+vo);
            return vo;
        }catch (Exception e){

            if(e instanceof GenException){
                logger.info("publicPay->response:"+((GenException)e).toString());
                return new ResponseApiVO(-2,e.getMessage(),null);

            }
            logger.error("PayCenterController-earnestMoney-》统一支付-》系统异常",e);
        }
        return new ResponseApiVO(-1,"支付失败",null);

    }
    @Idemp
    @Token
    @ApiOperation(value = "余额支付",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/blance/pay/{token}")
    @ResponseBody
    public ResponseApiVO blancePay (@PathVariable String token, @RequestBody @ApiParam(name="args",value="传入json格式",required=true)PayDTO args){
        try {
            logger.info("blancePay->request："+args);
            ResponseApiVO vo=this.payCenterService.prePay(token ,PayTypeEnum.PayType5.getCode(),args);
            logger.info("blancePay->response："+vo);

            return vo;
        }catch (Exception e){
            if(e instanceof GenException){
                logger.info("blancePay->response："+((GenException)e).toString());


                return new ResponseApiVO(-2,e.getMessage(),null);

            }
            logger.error("PayCenterController-blancePay-》余额支付-》系统异常",e);


            return new ResponseApiVO(-1,"余额支付失败",null);


        }
    }

    @Idemp
    @Token
    @ApiOperation(value = "微信支付",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/weixin/pay/{token}")
    @ResponseBody
    public ResponseApiVO<PayCenterWeixinPayVO> weixinPay (@PathVariable String token, @RequestBody @ApiParam(name="args",value="传入json格式",required=true)PayDTO args){
        try {
            logger.info("weixinPay->request："+args);
            ResponseApiVO vo=this.payCenterService.prePay(token ,PayTypeEnum.PayType1.getCode(),args);
            logger.info("weixinPay->response："+vo);

            return vo;
        }catch (Exception e){
            if(e instanceof GenException){
                logger.info("weixinPay->response："+((GenException)e).toString());
                return new ResponseApiVO(-2,e.getMessage(),null);

            }
            logger.error("PayCenterController-weixinPay-》微信支付-》系统异常",e);


            return new ResponseApiVO(-1,"微信支付失败",null);


        }
    }
    @Idemp("weixinH5Pay")
    @Token
    @ApiOperation(value = "微信-h5支付",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/weixin/h5/pay")
    @ResponseBody
    public ResponseApiVO<PayCenterWeixinPayVO> weixinH5Pay ( @RequestBody @ApiParam(name="args",value="传入json格式",required=true)PayDTO args){
        try {
            JSONObject jsonObj=(JSONObject)ThreadLocalUtil.get();
            if(jsonObj!=null){
                logger.info("weixinH5Pay->request-：jsonObj："+jsonObj+", args:"+args);

                String base64Token=jsonObj.getString("t");
                args.setTableNo(jsonObj.getString("tableNumber"));
                ResponseApiVO vo=this.payCenterService.prePay(base64Token ,PayTypeEnum.PayType6.getCode(),args);
                logger.info("weixinH5Pay->response："+vo);

                return vo;
            }

        }catch (Exception e){
            if(e instanceof GenException){
                logger.info("weixinH5Pay->response："+((GenException)e).toString());
                return new ResponseApiVO(-2,e.getMessage(),null);

            }
            logger.error("PayCenterController-weixinH5Pay-》微信-h5支付-》系统异常",e);


        }
        return new ResponseApiVO(-1,"微信-h5支付失败",null);
    }

    @Idemp
    @Token
    @ApiOperation(value = "支付宝支付",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/alipay/pay/{token}")
    @ResponseBody
    public ResponseApiVO<PayCenterAliPayVO> aliPay (@PathVariable String token, @RequestBody @ApiParam(name="args",value="传入json格式",required=true)PayDTO args){
        try {
            logger.info("aliPay->request："+args);
            ResponseApiVO vo=this.payCenterService.prePay(token, PayTypeEnum.PayType2.getCode(),args );
            logger.info("aliPay->response："+vo);

            return vo;
        }catch (Exception e){
            if(e instanceof GenException){
                logger.info("aliPay->response："+((GenException)e).toString());

                return new ResponseApiVO(-2,e.getMessage(),null);

            }
            logger.error("PayCenterController-aliPay-》支付宝支付-》系统异常",e);

            return new ResponseApiVO(-1,"支付宝支付失败",null);
        }
    }


    @Idemp
    @Token
    @ApiOperation(value = "逗币支付",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/doubi/pay/{token}")
    @ResponseBody
    public ResponseApiVO<PayCenterAliPayVO> doubiPay (@PathVariable String token, @RequestBody @ApiParam(name="args",value="传入json格式",required=true)DoubiPayDTO args){
        try {
            logger.info("doubiPay->request："+args);
            ResponseApiVO vo=this.payCenterService.doubiPay(token, args);
            logger.info("doubiPay->response："+vo);

            return vo;
        }catch (Exception e){
            if(e instanceof GenException){
                logger.info("doubiPay->response："+((GenException)e).toString());
                return new ResponseApiVO(-2,e.getMessage(),null);

            }
            logger.error("PayCenterController-doubiPay-》逗币支付-》系统异常",e);

            return new ResponseApiVO(-1,"逗币支付",null);
        }
    }
    @Idemp
    @Token
    @ApiOperation(value = "逗币支付-商城",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/doubi/pay/market/{token}")
    @ResponseBody
    public ResponseApiVO<PayCenterAliPayVO> doubiPayMarket (@PathVariable String token, @RequestBody @ApiParam(name="args",value="传入json格式",required=true)DoubiPayMarketDTO<DoubiPayMarketItemDTO> args){
        try {
            logger.info("doubiPayMarket->request："+args);
            ResponseApiVO vo=this.payCenterService.doubiPayOnMarket(token, args);
            logger.info("doubiPayMarket->response："+vo);

            return vo;
        }catch (Exception e){
            if(e instanceof GenException){
                logger.info("doubiPayMarket->response："+((GenException)e).toString());

                return new ResponseApiVO(-2,e.getMessage(),null);

            }
            logger.error("PayCenterController-doubiPayMarket-》逗币支付-商城-》系统异常",e);

            return new ResponseApiVO(-1,"逗币支付-商城",null);
        }
    }

}
