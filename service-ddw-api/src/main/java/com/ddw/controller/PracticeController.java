package com.ddw.controller;


import com.alibaba.fastjson.JSONObject;
import com.ddw.beans.*;
import com.ddw.services.*;
import com.ddw.token.Idemp;
import com.ddw.token.Token;
import com.ddw.token.TokenUtil;
import com.gen.common.util.CacheUtil;
import com.gen.common.vo.ResponseVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 访问地址：/swagger-ui.html
 */
@Api(value = "代练用例",description = "代练用例",tags = {"代练用例"})
@RestController
@RequestMapping("/ddwapp/practice")
public class PracticeController {
    private final Logger logger = Logger.getLogger(PracticeController.class);
    @Autowired
    private ReviewPracticeService reviewPracticeService;
    @Autowired
    private MyAttentionService myAttentionService;
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private GameService gameService;
    @Autowired
    private ReviewService reviewService;
    @Idemp("applyPractice")
    @Token
    @ApiOperation(value = "代练认证申请用例")
    @PostMapping("/apply/{token}")
    public ResponseApiVO apply(@PathVariable String token,
                               @RequestParam(value = "gameId") @ApiParam(name = "gameId",value="游戏表对应id", required = true) String gameId,
                               @RequestParam(value = "rankId") @ApiParam(name = "rankId",value="段位表对应id", required = true) String rankId,
                               @RequestParam(value = "photograph1") @ApiParam(name = "photograph1",value="游戏截图", required = true) MultipartFile photograph1,
                               @RequestParam(value = "photograph2") @ApiParam(name = "photograph2",value="游戏截图", required = true) MultipartFile photograph2,
                               @RequestParam(value = "photograph3") @ApiParam(name = "photograph3",value="游戏截图", required = true) MultipartFile photograph3){
        try {
            return reviewPracticeService.apply(TokenUtil.getUserId(token),TokenUtil.getStoreId(token),TokenUtil.getUserName(token),gameId,rankId,photograph1,photograph2,photograph3);
        }catch (Exception e){
            logger.error("PracticeController->apply",e);
            return new ResponseApiVO(-1,"提交失败",null);
        }
    }

    @Token
    @ApiOperation(value = "游戏列表")
    @PostMapping("/gameList/{token}")
    public ResponseVO gameList(@PathVariable String token){
        try {
            JSONObject json = new JSONObject();
            json.put("gameList",gameService.getGameList());
            return new ResponseVO(1,"成功",json);
        }catch (Exception e){
            logger.error("PracticeController->gameList",e);
            return new ResponseVO(-1,"提交失败",null);
        }
    }

    @Token
    @ApiOperation(value = "代练排行榜")
    @PostMapping("/queryList/{token}")
    public ResponseVO queryList(@PathVariable String token,@RequestBody @ApiParam(name="args",value="传入json格式",required=true)PracticeQueryListDTO practiceQueryListDTO){
        try {
            JSONObject json = new JSONObject();
            json.put("list",reviewPracticeService.practiceList(token,practiceQueryListDTO.getPageNo(),null,practiceQueryListDTO.getWeekList()));
            return new ResponseVO(1,"成功",json);
        }catch (Exception e){
            logger.error("PracticeController->queryList",e);
            return new ResponseVO(-1,"提交失败",null);
        }
    }

    @Token
    @ApiOperation(value = "代练动态")
    @PostMapping("/practiceDynamic/{token}")
    public ResponseVO getPracticeDynamic(@PathVariable String token,@RequestBody @ApiParam(name="args",value="传入json格式",required=true)PracticeDynamicDTO practiceDynamicDTO){
        try {
            return reviewPracticeService.getPracticeDynamic(practiceDynamicDTO);
        }catch (Exception e){
            logger.error("PracticeController->getPracticeDynamic",e);
            return new ResponseVO(-1,"提交失败",null);
        }
    }

    @ApiOperation(value = "代练资料查询用例")
    @PostMapping("/query/{token}")
    public ResponseApiVO<PracticeVO> query(@PathVariable String token,
                                           @RequestBody @ApiParam(name = "id",value="代练id,传入json格式,如:{\"id\":\"1\"}", required = false) JSONObject json){
        try {
            //代练编号
            Integer practiceId = 0;
            boolean focus = false;
            if(json !=null && !json.isEmpty() && json.containsKey("id") && !StringUtils.isBlank(json.get("id").toString())){
                practiceId = Integer.valueOf(json.getString("id"));
                //会员编号
                Integer userId = TokenUtil.getUserId(token);
                focus = myAttentionService.isFocusPractice(userId,practiceId);
            }else{
                practiceId = TokenUtil.getUserId(token);
            }
            PracticeVO practiceVO = reviewPracticeService.getPracticeInfo(practiceId);
            //评分
            PracticeEvaluationPO practiceEvaluationPO = reviewPracticeService.getEvaluation(practiceId);
            if (practiceEvaluationPO != null) {
                practiceVO.setStar(practiceEvaluationPO.getStar());
            }
            // 查询代练信息,舍弃原本会员资料返回,返回代练游戏简历\代练资料\接单数\评分
            List<PhotographPO> photographList = userInfoService.queryPhotograph(practiceId);
            if(photographList.size()>0){
                practiceVO.setImgUrl(photographList.get(0).getImgUrl());
            }
            //接单数
            practiceVO.setOrders(reviewPracticeService.getOrderCount(practiceId));
            practiceVO.setFans(myAttentionService.getFans(practiceId));
            practiceVO.setFocus(focus);
            practiceVO.setReviewPracticeList(reviewPracticeService.getReviewPracticeList(practiceId,TokenUtil.getStoreId(token)));
            practiceVO.setPhotograph(userInfoService.queryPhotograph(practiceId));
            return new ResponseApiVO(1,"成功",practiceVO);
        }catch (Exception e){
            logger.error("PracticeController->query",e);
            return new ResponseApiVO(-1,"提交失败",null);
        }
    }

    @ApiOperation(value = "预估代练支付金额")
    @PostMapping("/estimatedAmount/{token}")
    public ResponseApiVO<PracticeGameApplyVO> estimatedAmount(@PathVariable String token,
                                                        @RequestBody @ApiParam(name = "args",value="传入json格式", required = false) PracticeGameApplyDTO practiceGameApplyDTO){
        try {
            //判断代练是否开启预约,提交游戏编号,当前段位包含几星,目标段位包含几星,代练编号,计算支付金额
            PracticeGamePO practiceGamePO = reviewPracticeService.getPracticeGamePO(practiceGameApplyDTO.getPracticeId(),practiceGameApplyDTO.getGameId());
            if (practiceGamePO != null && practiceGamePO.getAppointment() == 1) {
                PracticeEstimatedAmountVO practiceEstimatedAmountVO = new PracticeEstimatedAmountVO();
                practiceEstimatedAmountVO.setPayMoney(reviewPracticeService.payMoney(practiceGameApplyDTO.getGameId(),
                        practiceGameApplyDTO.getRankId(),practiceGameApplyDTO.getStar(),
                        practiceGameApplyDTO.getTargetRankId(),practiceGameApplyDTO.getTargetStar()));
                return new ResponseApiVO(1,"成功",practiceEstimatedAmountVO);
            }else {
                return new ResponseApiVO(-2,"代练未开启预约或在代练中",null);
            }
        }catch (Exception e){
            logger.error("PracticeController->estimatedAmount",e);
            return new ResponseApiVO(-1,"提交失败",null);
        }
    }
    @Idemp("gameApply")
    @ApiOperation(value = "约代练支付申请,返回订单编号和代练支付金额")
    @PostMapping("/gameApply/{token}")
    public ResponseApiVO<PracticeGameApplyVO> gameApply(@PathVariable String token,
                                           @RequestBody @ApiParam(name = "args",value="传入json格式", required = false) PracticeGameApplyDTO practiceGameApplyDTO){
        try {
            if(TokenUtil.getUserId(token).equals(practiceGameApplyDTO.getPracticeId())){
                return new ResponseApiVO(-3,"不可预约自己代练",null);
            }
            //判断代练是否开启预约,提交游戏编号,当前段位包含几星,目标段位包含几星,代练编号,写进订单,更新代练预约状态
            PracticeGamePO practiceGamePO = reviewPracticeService.getPracticeGamePO(practiceGameApplyDTO.getPracticeId(),practiceGameApplyDTO.getGameId());
            if(!TokenUtil.getStoreId(token).equals(practiceGamePO.getStoreId())){
                return new ResponseApiVO(-4,"预约的代练不在同一个门店",null);
            }
            if (practiceGamePO != null && practiceGamePO.getAppointment() == 1) {
                int payMoney = reviewPracticeService.payMoney(practiceGameApplyDTO.getGameId(),
                        practiceGameApplyDTO.getRankId(),practiceGameApplyDTO.getStar(),
                        practiceGameApplyDTO.getTargetRankId(),practiceGameApplyDTO.getTargetStar());
                ResponseVO rv = reviewPracticeService.updatePracticeGame(practiceGameApplyDTO.getPracticeId(),practiceGameApplyDTO.getGameId(),2,null);
                if(rv.getReCode() > 0){
                    CacheUtil.delete("publicCache","appIndexPractice"+TokenUtil.getStoreId(token));
                    ResponseVO rv2 = reviewPracticeService.insertPracticeOrder(TokenUtil.getUserId(token),TokenUtil.getStoreId(token), practiceGameApplyDTO,payMoney);
                    if(rv2.getReCode() > 0){
                        PracticeGameApplyVO practiceGameApplyVO = new PracticeGameApplyVO();
                        practiceGameApplyVO.setOrderId((Integer)rv2.getData());
                        practiceGameApplyVO.setPayMoney(payMoney);
                        return new ResponseApiVO(1,"成功",practiceGameApplyVO);
                    }else {
                        return new ResponseApiVO(rv.getReCode(),rv.getReMsg(),rv.getData());
                    }
                }else {
                    return new ResponseApiVO(rv.getReCode(),rv.getReMsg(),rv.getData());
                }
            }else {
                return new ResponseApiVO(-2,"代练未开启预约或在代练中",null);
            }
        }catch (Exception e){
            logger.error("PracticeController->gameApply",e);
            return new ResponseApiVO(-1,"提交失败",null);
        }
    }
    @Idemp("cancleOrder")
    @ApiOperation(value = "未付款,取消订单",notes="预约代练未支付,取消代练")
    @PostMapping("/cancleOrder/{token}")
    public ResponseVO cancleOrder(@PathVariable String token,
                                                          @RequestBody @ApiParam(name = "args",value="传入json格式", required = false) PracticeOrderDTO practiceOrderDTO){
        try {
            return reviewPracticeService.cancleOrder(TokenUtil.getUserId(token),practiceOrderDTO);
        }catch (Exception e){
            logger.error("PracticeController->cancleOrder",e);
            return new ResponseVO(-1,"提交失败",null);
        }
    }
    @Idemp("settlement")
    @ApiOperation(value = "提交结算申请",notes="客户违约（用户先提出结束），按照下单时间来定，客户一小时内要走，就扣罚违约金（总金额*30%），代练掉星也不用赔偿，超过一小时，则客户提前走就不用罚违约金了，按照实际代练结果结算。代练未完成要线下双倍赔付，退全款，返回需要线下双倍退款金额。")
    @PostMapping("/settlement/{token}")
    public ResponseApiVO<PracticeSettlementVO> settlement(@PathVariable String token,
                                               @RequestBody @ApiParam(name = "args",value="传入json格式", required = false) PracticeSettlementDTO practiceSettlementDTO){
        try {
            // 原段位包含几星(不可修改),目前段位包含几星,代练编号,返回需要支付金额,如果段位星级比原段位星级低,则走赔付流程
            return reviewPracticeService.settlement(TokenUtil.getUserId(token),practiceSettlementDTO);
        }catch (Exception e){
            logger.error("PracticeController->settlement",e);
            return new ResponseApiVO(-1,"提交失败",null);
        }
    }
    @Idemp("endThePractice")
    @ApiOperation(value = "代练结束上传凭证,结束代练")
    @PostMapping("/endThePractice/{token}")
    public ResponseVO endThePractice(@PathVariable String token,
                                                              @RequestParam(value = "orderId") @ApiParam(name = "orderId",value="订单编号ID", required = true) Integer orderId,
                                                              @RequestParam(value = "photograph") @ApiParam(name = "photograph",value="游戏截图", required = true) MultipartFile photograph){
        try {
            // 原段位包含几星(不可修改),目前段位包含几星,代练编号,返回需要支付金额,如果段位星级比原段位星级低,则走赔付流程
            return reviewPracticeService.endThePractice(orderId,photograph);
        }catch (Exception e){
            logger.error("PracticeController->endThePractice",e);
            return new ResponseVO(-1,"提交失败",null);
        }
    }
    @Idemp("evaluation")
    @ApiOperation(value = "评价")
    @PostMapping("/evaluation/{token}")
    public ResponseVO evaluation(@PathVariable String token,
                                                @RequestBody @ApiParam(name = "args",value="传入json格式", required = false) PracticeEvaluationDetailDTO practiceEvaluationDetailDTO){
        try {
            long count = reviewPracticeService.countEvaluationDetai(TokenUtil.getUserId(token),practiceEvaluationDetailDTO.getOrderId());
            if(count > 0){
                return new ResponseVO(-2,"抱歉，已评价过",null);
            }
            PracticeOrderPO practiceOrderPO = reviewPracticeService.getOrder(practiceEvaluationDetailDTO.getOrderId());
            //写入ddw_practice_evaluation_detail代练评分明细表,以及更新ddw_practice_evaluation代练平均评分表
            ResponseVO responseVO = reviewPracticeService.insertPracticeEvaluationDetail(practiceEvaluationDetailDTO,practiceOrderPO);
            if(responseVO.getReCode() == 1){
                reviewPracticeService.insertPracticeEvaluation(practiceEvaluationDetailDTO,practiceOrderPO.getPracticeId());
            }
            CacheUtil.delete("publicCache","appIndexPractice"+TokenUtil.getStoreId(token));
            return responseVO;
        }catch (Exception e){
            logger.error("PracticeController->evaluation",e);
            return new ResponseVO(-1,"提交失败",null);
        }
    }

    @Token
    @ApiOperation(value = "代练评价详情")
    @PostMapping("/evaluationDetailList/{token}")
    public ResponseVO getPracticeEvaluationDetailList(@PathVariable String token,@RequestBody @ApiParam(name="args",value="传入json格式",required=true)PracticePubTaskDTO practicePubTaskDTO){
        try {
            JSONObject json = new JSONObject();
            json.put("list",reviewPracticeService.getPracticeEvaluationDetailList(practicePubTaskDTO));
            return new ResponseVO(1,"成功",json);
        }catch (Exception e){
            logger.error("PracticeController->getPracticeEvaluationDetailList",e);
            return new ResponseVO(-1,"提交失败",null);
        }
    }
    @Idemp("release")
    @ApiOperation(value = "发布代练,同时只允许发布一个")
    @PostMapping("/release/{token}")
    public ResponseVO release(@PathVariable String token,
                                 @RequestBody @ApiParam(name = "args",value="传入json格式", required = false) PracticeReleaseDTO practiceReleaseDTO){
        try {
            List<Map> list = reviewPracticeService.practiceGame(TokenUtil.getUserId(token));
            if(list.size()>0){
                for(Map map:list){
                    if(map.get("appointment").toString().equals("2")){
                        return new ResponseVO(-3,"有代练任务在进行中,请先结束再重新发布",null);
                    }
                    if(map.get("appointment").toString().equals("1")){
                        return new ResponseVO(-2,"不允许同时发布多个任务",null);
                    }
                }
            }
//            if(reviewPracticeService.countPracticeOrder(BaseTokenUtil.getUserId(token),practiceReleaseDTO.getGameId())>0){
//                return new ResponseVO(-3,"请先结束订单再发布",null);
//            }
            CacheUtil.delete("publicCache","appIndexPractice"+TokenUtil.getStoreId(token));
            return reviewPracticeService.updatePracticeGame(TokenUtil.getUserId(token),practiceReleaseDTO.getGameId(),1,TokenUtil.getStoreId(token));
        }catch (Exception e){
            logger.error("PracticeController->release",e);
            return new ResponseVO(-1,"提交失败",null);
        }
    }
    @Idemp("cancle")
    @ApiOperation(value = "取消发布代练")
    @PostMapping("/cancle/{token}")
    public ResponseVO cancle(@PathVariable String token,
                             @RequestBody @ApiParam(name = "args",value="传入json格式", required = false) PracticeReleaseDTO practiceReleaseDTO){
        try {
            PracticeOrderPO practiceOrderPO = reviewPracticeService.getOrderInProgress(TokenUtil.getUserId(token),practiceReleaseDTO.getGameId());
            //判断无接单,可取消发布
            if (practiceOrderPO == null) {
                CacheUtil.delete("publicCache","appIndexPractice"+TokenUtil.getStoreId(token));
                return reviewPracticeService.updatePracticeGame(TokenUtil.getUserId(token),practiceReleaseDTO.getGameId(),0,null);
            }else {
                return new ResponseVO(-2,"有正在进行的订单,不可取消,请先结算",null);
            }
        }catch (Exception e){
            logger.error("PracticeController->cancle",e);
            return new ResponseVO(-1,"提交失败",null);
        }
    }

    @Token
    @ApiOperation(value = "代练订单列表,倒序")
    @PostMapping("/orderPracticeList/{token}")
    public ResponseVO getOrderPracticeList(@PathVariable String token,@RequestBody @ApiParam(name="args",value="传入json格式",required=true)PageNoDTO page){
        try {
            return reviewPracticeService.getOrderPracticeList(TokenUtil.getUserId(token),page);
        }catch (Exception e){
            logger.error("PracticeController->getOrderPracticeList",e);
            return new ResponseVO(-1,"提交失败",null);
        }
    }

    @Token
    @ApiOperation(value = "会员代练订单列表,倒序")
    @PostMapping("/orderUserList/{token}")
    public ResponseVO getOrderUserList(@PathVariable String token,@RequestBody @ApiParam(name="args",value="传入json格式",required=true)PageNoDTO page){
        try {
            return reviewPracticeService.getOrderUserList(TokenUtil.getUserId(token),page);
        }catch (Exception e){
            logger.error("PracticeController->getOrderUserList",e);
            return new ResponseVO(-1,"提交失败",null);
        }
    }
    @Token
    @ApiOperation(value = "订单详情查询")
    @PostMapping("/practiceOrder/{token}")
    public ResponseVO getPracticeOrder(@PathVariable String token,@RequestBody @ApiParam(name="args",value="传入json格式",required=true)PracticeOrderDTO practiceOrderDTO){
        try {
            return reviewPracticeService.getPracticeOrder(practiceOrderDTO.getId());
        }catch (Exception e){
            logger.error("PracticeController->getPracticeOrder",e);
            return new ResponseVO(-1,"提交失败",null);
        }
    }

    @Token
    @ApiOperation(value = "代练任务发布列表",notes="不传practiceId,查询所有代练任务,传practiceId,只查询practiceId的代练任务")
    @PostMapping("/pubTaskList/{token}")
    public ResponseVO getPubTaskList(@PathVariable String token,@RequestBody @ApiParam(name="args",value="传入json格式",required=false)PracticePubTaskDTO practicePubTaskDTO){
        try {
            return reviewPracticeService.getPubTaskList(TokenUtil.getUserId(token),practicePubTaskDTO);
        }catch (Exception e){
            logger.error("PracticeController->getPubTaskList",e);
            return new ResponseVO(-1,"提交失败",null);
        }
    }
    @Idemp("refundPractice")
    @Token
    @ApiOperation(value = "退款申请",notes = "订单24小时内可申请,退款通过后台审核发放退款,退款金额=约代练支付金额-结算金额")
    @PostMapping("/refund/{token}")
    public ResponseVO refund(@PathVariable String token,
                             @RequestParam(value = "orderId") @ApiParam(name = "orderId",value="订单编号", required = true) Integer orderId,
                             @RequestParam(value = "reason") @ApiParam(name = "reason",value="退款原因", required = true) String reason,
                             @RequestParam(value = "describe") @ApiParam(name = "describe",value="描述", required = true) String describe,
                             @RequestParam(value = "pic") @ApiParam(name = "pic",value="凭证", required = true) MultipartFile pic){
        try {
            return reviewPracticeService.refund(TokenUtil.getUserId(token),TokenUtil.getStoreId(token),orderId,TokenUtil.getUserName(token),reason,describe,pic);
        }catch (Exception e){
            logger.error("PracticeController->refund",e);
            return new ResponseVO(-1,"提交失败",null);
        }
    }
    @Idemp("refundReason")
    @Token
    @ApiOperation(value = "退款拒绝反馈")
    @PostMapping("/refundReason/{token}")
    public ResponseVO refundReason(@PathVariable String token,@RequestBody @ApiParam(name="args",value="传入json格式",required=true)PracticeOrderDTO practiceOrderDTO){
        try {
            return new ResponseVO(1,"成功",reviewPracticeService.refundReason(practiceOrderDTO.getId()));
        }catch (Exception e){
            logger.error("PracticeController->refund",e);
            return new ResponseVO(-1,"提交失败",null);
        }
    }
    @Token
    @ApiOperation(value = "获取申请代练状态",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/queryReviewStatus/{token}")
    @ResponseBody
    public ResponseApiVO<PracticeReviewStatusVO> queryReviewStatus(@PathVariable String token){
        try {
            return this.reviewService.getPracticeReviewStatus(token);
        }catch (Exception e){
            logger.error("PracticeController->queryReviewStatus",e);
            return new ResponseApiVO(-1,"提交失败",null);
        }
    }
}
