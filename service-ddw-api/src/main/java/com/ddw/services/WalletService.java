package com.ddw.services;

import com.ddw.beans.*;
import com.ddw.config.DDWGlobals;
import com.ddw.dao.WalletErrorLogMapper;
import com.ddw.enums.IncomeTypeEnum;
import com.ddw.enums.OrderTypeEnum;
import com.ddw.enums.PayStatusEnum;
import com.ddw.token.TokenUtil;
import com.ddw.util.MsgUtil;
import com.gen.common.beans.CommonChildBean;
import com.gen.common.beans.CommonSearchBean;
import com.gen.common.services.CommonService;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 钱包
 */
@Service
@Transactional(readOnly = true)
public class WalletService extends CommonService {

    @Autowired
    private DDWGlobals ddwGlobals;
    @Autowired
    private WalletErrorLogMapper walletErrorLogMapper;
    @Autowired
    private UserInfoService userInfoService;

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseApiVO transferMoney(String token ,WalletTransferMoneyDTO dto)throws Exception{
        if(StringUtils.isBlank(IncomeTypeEnum.getName(dto.getIncomeType()))){
            return new ResponseApiVO(-2,"收益类型异常",null);
        }
        Integer userId=TokenUtil.getUserId(token);
        ResponseVO vo=null;
        if(IncomeTypeEnum.IncomeType1.getCode().equals(dto.getIncomeType())){
            ResponseApiVO<WalletGoddessInVO> gvo=this.getGoddessIn(userId);
            if(gvo.getReCode()!=1){
                return new ResponseApiVO(-2,"失败",null);

            }else{
                WalletGoddessInVO wg=gvo.getData();
                if(wg.getGoddessIncome()==null || wg.getGoddessIncome()<dto.getMoney()){
                    return new ResponseApiVO(-2,"女神收益金额不足",null);

                }
                Map setMap=new HashMap();
                setMap.put("money",dto.getMoney());
                setMap.put("goddessIncome",-dto.getMoney());
                Map searchMap=new HashMap();
                searchMap.put("userId",userId);
                vo= this.commonCalculateOptimisticLockUpdateByParam("ddw_my_wallet",setMap,searchMap,"version",new String[]{"money","goddessIncome"});
            }
        }else if(IncomeTypeEnum.IncomeType2.getCode().equals(dto.getIncomeType())){
            ResponseApiVO<WalletPracticeInVO> gvo=this.getPracticeIn(userId);
            if(gvo.getReCode()!=1){
                return new ResponseApiVO(-2,"失败",null);

            }else{
                WalletPracticeInVO wg=gvo.getData();
                if(wg.getPracticeIncome()==null || wg.getPracticeIncome()<dto.getMoney()){
                    return new ResponseApiVO(-2,"代练收益金额不足",null);

                }
                Map setMap=new HashMap();
                setMap.put("money",dto.getMoney());
                setMap.put("practiceIncome",-dto.getMoney());
                Map searchMap=new HashMap();
                searchMap.put("userId",userId);
                vo=this.commonCalculateOptimisticLockUpdateByParam("ddw_my_wallet",setMap,searchMap,"version",new String[]{"money","practiceIncome"});
            }
        }
        if(vo==null ||  vo.getReCode()!=1){
            return new ResponseApiVO(-2,"转入钱包失败",null);

        }else{
            return new ResponseApiVO(1,"成功",null);

        }
    }

    public ResponseApiVO getIncome(Integer incomeType,Integer pageNo,String token)throws Exception{
        if(StringUtils.isBlank(IncomeTypeEnum.getName(incomeType))){
            return new ResponseApiVO(-2,"参数异常",null);
        }
        if(pageNo==null){
            pageNo=1;
        }
        Map search=new HashMap();
        search.put("diType",incomeType);
        search.put("userId",TokenUtil.getUserId(token));

        List list=this.commonList("ddw_income_record","createTime desc","t1.diMoney money,t1.diType type,t1.createTime,t1.orderNo,t1.orderType",pageNo,10,search);
        if(list==null || list.isEmpty()){
            return new ResponseApiVO(2,"成功",new ListVO(new ArrayList()));

        }
        List dataList=new ArrayList();

        list.forEach(a->{
            IncomeVO incomeVO=new IncomeVO();
            try {
                PropertyUtils.copyProperties(incomeVO,a);
                incomeVO.setOrderTypeName(OrderTypeEnum.getName(incomeVO.getOrderType()));
                dataList.add(incomeVO);
            } catch (Exception e) {
              e.printStackTrace();
            }
        });

        return new ResponseApiVO(1,"成功",new ListVO(dataList));
    }

    /**
     * 获取余额
     * @param userid
     * @return
     * @throws Exception
     */
    public ResponseApiVO getBalance(Integer userid)throws Exception{
        WalletBalanceVO balanceVO=this.commonObjectBySingleParam("ddw_my_wallet","userId",userid, WalletBalanceVO.class);

        return new ResponseApiVO(1,"成功",balanceVO);
    }
    /**
     * 获取逗币
     * @param userid
     * @return
     * @throws Exception
     */
    public ResponseApiVO getCoin(Integer userid)throws Exception{
        WalletDoubiVO balanceVO=this.commonObjectBySingleParam("ddw_my_wallet","userId",userid, WalletDoubiVO.class);
        return new ResponseApiVO(1,"成功",balanceVO);
    }
    public ResponseApiVO getCoinAndExpenseCoin(Integer userid)throws Exception{
        ResponseApiVO<WalletDoubiVO> vo=getCoin(userid);
        WalletDoubiVO balanceVO=vo.getData();
        Map searchMap=new HashMap();
        searchMap.put("userId",userid);
        searchMap.put("orderType",OrderTypeEnum.OrderType6.getCode());
        balanceVO.setExpenseCoin((int)this.commonSumByBySingleSearchMap("ddw_order_view","price",searchMap));
        return new ResponseApiVO(1,"成功",balanceVO);
    }
    public CouponPO getCoupon(Integer couponId,Integer userId,Integer storeId)throws Exception{
        Map search=new HashMap();
        search.put("id",couponId);
        Map csearch=new HashMap();
        csearch.put("userId",userId);
        csearch.put("used",0);
        csearch.put("storeId",storeId);
        CommonSearchBean csb=new CommonSearchBean("ddw_coupon",null,"t1.*",0,1,search,
                new CommonChildBean("ddw_userinfo_coupon","couponId","id",csearch));
        List couponlist=this.getCommonMapper().selectObjects(csb);
        if(couponlist!=null && !couponlist.isEmpty()){
            CouponPO po=new CouponPO();
            PropertyUtils.copyProperties(po,couponlist.get(0));
            return po;
        }
        return null;
    }
    public ResponseApiVO getGiftPackge(Integer userid){
        Map search=new HashMap();
        search.put("used",0);
        search.put("userId",userid);
        Map csearch=new HashMap();
        csearch.put("doPayStatus",PayStatusEnum.PayStatus1.getCode());
        CommonSearchBean csb=new CommonSearchBean("ddw_order_gift",null,"ct1.id giftCode,ct1.dgName name,ct1.dgImgPath imgUrl",null,null,search,
                new CommonChildBean("ddw_order","id","orderId",csearch),
                new CommonChildBean("ddw_gift","id","giftId",null));
        List giftPacketlist=this.getCommonMapper().selectObjects(csb);
        if(giftPacketlist==null){
            return new ResponseApiVO(2,"空背包",new ListVO(new ArrayList()));
        }
        return new ResponseApiVO(1,"成功",new ListVO(giftPacketlist));
    }
    public ResponseApiVO getCouponList(Integer userid){
        Map csearch=new HashMap();
        csearch.put("userId",userid);
        csearch.put("used",0);
        CommonSearchBean csb=new CommonSearchBean("ddw_coupon",null,"t1.id couponCode,t1.dcName name,t1.dcType type,t1.dcMoney mop,t1.dcStartTime startTime,t1.dcEndTime endTime,t1.dcDesc 'desc',ct1.dsName storeName,t1.dcMinPrice minPrice,ct0.used",1,1,null,
                new CommonChildBean("ddw_userinfo_coupon","couponId","id",csearch),
                new CommonChildBean("ddw_store","id","storeId",null));
        List couponlist=this.getCommonMapper().selectObjects(csb);
        List list=new ArrayList();
        Date now=new Date();
        if(couponlist!=null){
            couponlist.forEach(a->{
                CouponVO vo=new CouponVO();
                try {

                    PropertyUtils.copyProperties(vo,a);
                    if(vo.getEndTime().after(now)){
                        vo.setExpire(1);
                    }else{
                        vo.setExpire(-1);
                    }
                    list.add(vo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        if(couponlist==null){
            return new ResponseApiVO(2,"没有优惠卷",new ListVO(list));
        }
        return new ResponseApiVO(1,"成功",new ListVO(list));
    }
    /**
     * 获取总资产
     * @param userid
     * @return
     * @throws Exception
     */
    public ResponseApiVO getAsset(Integer userid)throws Exception{
        WalletAssetVO balanceVO=this.commonObjectBySingleParam("ddw_my_wallet","userId",userid, WalletAssetVO.class);
        ResponseApiVO<ListVO<List>> couponVo=this.getCouponList(userid);
        ResponseApiVO<ListVO<List>> giftPackVo=this.getGiftPackge(userid);
        List couponlist=couponVo.getData().getList();
        if(couponlist!=null && !couponlist.isEmpty()){
            balanceVO.setCouponList(couponlist);
        }
        List giftPacketlist=giftPackVo.getData().getList();
        if(giftPacketlist!=null && !giftPacketlist.isEmpty()){
            balanceVO.setPackList(giftPacketlist);
        }

        return new ResponseApiVO(1,"成功",balanceVO);
    }


    /**
     * 获取女神资产
     * @param userid
     * @return
     * @throws Exception
     */
    public ResponseApiVO getGoddessIn(Integer userid)throws Exception{
        WalletGoddessInVO balanceVO=this.commonObjectBySingleParam("ddw_my_wallet","userId",userid, WalletGoddessInVO.class);

        return new ResponseApiVO(1,"成功",balanceVO);
    }


    /**
     * 获取代练资产
     * @param userid
     * @return
     * @throws Exception
     */
    public ResponseApiVO getPracticeIn(Integer userid)throws Exception{
        WalletPracticeInVO balanceVO=this.commonObjectBySingleParam("ddw_my_wallet","userId",userid, WalletPracticeInVO.class);

        return new ResponseApiVO(1,"成功",balanceVO);
    }

    /**
     * 忘记支付密码
     * @param userId
     * @param walletForgetPayPwdDTO
     * @return
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseApiVO forgetPayPwd(Integer userId,WalletForgetPayPwdDTO walletForgetPayPwdDTO)throws Exception{
        UserInfoPO userInfoPO = userInfoService.querySimple(userId);
        if(!StringUtils.isBlank(userInfoPO.getPhone())){
            if(!userInfoPO.getPhone().equals(walletForgetPayPwdDTO.getTelphone())){
                return new ResponseApiVO(-2,"与绑定手机不一致",null);
            }
            if(StringUtils.isBlank(walletForgetPayPwdDTO.getNewPwd())){
                return new ResponseApiVO(-3,"密码不能为空",null);
            }
            if(!MsgUtil.verifyCode(walletForgetPayPwdDTO.getTelphone(),walletForgetPayPwdDTO.getCode())){
                return new ResponseApiVO(-4,"失败,验证码不对",null);
            }
            //修改钱包密码
            Map setParams = new HashMap();
            setParams.put("payPwd",walletForgetPayPwdDTO.getNewPwd());
            Map searchCondition = new HashMap();
            searchCondition.put("userId",userId);
            ResponseVO vo = this.commonOptimisticLockUpdateByParam("ddw_my_wallet",setParams,searchCondition,"version");
            return new ResponseApiVO(vo.getReCode(),vo.getReMsg(),null);
        }else {
            return new ResponseApiVO(-1,"没绑定手机号码",null);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseApiVO createWallet(Integer userid){
        Map wallet=new HashMap();
        wallet.put("userId",userid);
        wallet.put("money",0);
        wallet.put("coin",0);
        wallet.put("version",1);
        wallet.put("goddessIncome",0);
        wallet.put("practiceIncome",0);
        wallet.put("createTime",new Date());
        wallet.put("updateTime",new Date());
        ResponseVO vo=this.commonInsertMap("ddw_my_wallet",wallet);
        return new ResponseApiVO(vo.getReCode(),vo.getReMsg(),null);
    }

    public WalletPO getWallet(Integer userId)throws Exception{
        return this.commonObjectBySingleParam("ddw_my_wallet","userId",userId,WalletPO.class);
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseApiVO updatePayPwd(Integer userId,String oldPwd,String newPwd)throws Exception{
        WalletPO walletPO = this.getWallet(userId);
        //校验密码
        if(walletPO.getPayPwd() == null){
            if(StringUtils.isBlank(newPwd)){
                return new ResponseApiVO(-1,"密码不能为空",null);
            }
        }else {
            if(StringUtils.isBlank(oldPwd) || StringUtils.isBlank(newPwd)){
                return new ResponseApiVO(-1,"密码不能为空",null);
            }
            if(!oldPwd.equals(walletPO.getPayPwd())){
                return new ResponseApiVO(-2,"原密码不正确",null);
            }
        }
        //修改钱包密码
        Map setParams = new HashMap();
        setParams.put("payPwd",newPwd);
        Map searchCondition = new HashMap();
        searchCondition.put("userId",userId);
        ResponseVO vo = this.commonOptimisticLockUpdateByParam("ddw_my_wallet",setParams,searchCondition,"version");
        return new ResponseApiVO(vo.getReCode(),vo.getReMsg(),null);
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO insertPayPwdErrorLog(Integer userId,String payPwd)throws Exception{
        Map payPwdErrorLog = new HashMap<>();
        payPwdErrorLog.put("userId",userId);
        payPwdErrorLog.put("payPwd",payPwd);
        payPwdErrorLog.put("createTime",new Date());
        return this.commonInsertMap("ddw_my_wallet_error_log",payPwdErrorLog);
    }
    /**
     * 校验支付密码
     * @param userId
     * @param payPwd
     * @return
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseApiVO verifyPayPwd(Integer userId,String payPwd)throws Exception{
        WalletPO walletPO = this.getWallet(userId);
        int errorCount = walletErrorLogMapper.errorTodayCount(userId);
        if(errorCount >= 5){
            return new ResponseApiVO(-2,"今日密码输入错误达5次,请明日再试",null);
        }
        if(payPwd.equals(walletPO.getPayPwd())){
            return new ResponseApiVO(1,"成功",null);
        }else{
            //插入失败记录
            this.insertPayPwdErrorLog(userId,payPwd);
            return new ResponseApiVO(-1,"支付密码错误",null);
        }
    }

    /**
     * 生成随机码值，包含数字、大小写字母
     * @param number 生成的随机码位数
     * @return
     */
    public static String getRandomCode(int number){
        String codeNum = "";
        int [] code = new int[3];
        Random random = new Random();
        for (int i = 0; i < number; i++) {
            int num = random.nextInt(10) + 48;
            int uppercase = random.nextInt(26) + 65;
            int lowercase = random.nextInt(26) + 97;
            code[0] = num;
            code[1] = uppercase;
            code[2] = lowercase;
            codeNum+=(char)code[random.nextInt(3)];
        }
        return codeNum;
    }

}
