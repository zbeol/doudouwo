package com.ddw.services;

import com.ddw.beans.*;
import com.ddw.enums.*;
import com.ddw.util.BusinessCodeUtil;
import com.ddw.util.IMApiUtil;
import com.gen.common.exception.GenException;
import com.gen.common.services.CommonService;
import com.gen.common.util.BeanToMapUtil;
import com.gen.common.util.CacheUtil;
import com.gen.common.util.Page;
import com.gen.common.vo.ResponseVO;
import com.tls.sigcheck.tls_sigcheck;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 审批
 */
@Service
public class ReviewService extends CommonService {
    @Autowired
    private CommonReviewService commonReviewService;

    @Autowired
    private LiveRadioService liveRadioService;

    /**
     * 判断是否未审核直播
     * @param userid
     * @return
     */
    public boolean hasLiveRadioReviewFromGoddess(Integer userid,Integer storeId){
        return this.hasReview(userid,storeId,ReviewBusinessTypeEnum.ReviewBusinessType3,ReviewBusinessStatusEnum.liveRadio10,ReviewReviewerTypeEnum.ReviewReviewerType1);
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseApiVO applyLiveRadio(String userOpenId, Integer storeId)throws Exception{
        if(storeId==null){
            return new ResponseApiVO(-2,"请选择一个门店",null);

        }
        Map upo=this.commonObjectBySingleParam("ddw_userinfo","openid",userOpenId);
        if(upo==null){
            return new ResponseApiVO(-2000,"用户不存在",null);
        }
        if(!GoddessFlagEnum.goddessFlag1.getCode().equals((Integer) upo.get("goddessFlag"))){
            return new ResponseApiVO(-2001,"请先申请当女神",null);
        }
        Integer userid=(Integer) upo.get("id");
        LiveRadioPO liveRadioPO=liveRadioService.getLiveRadio(userid,storeId);
        if(liveRadioPO!=null && liveRadioPO.getLiveStatus()==LiveStatusEnum.liveStatus0.getCode()) {
            return new ResponseApiVO(1,"直播等待中，请前往直播房间",null);

        }
        if(liveRadioPO!=null && liveRadioPO.getLiveStatus()==LiveStatusEnum.liveStatus1.getCode()){
            return new ResponseApiVO(-2002,"直播房间已开，请关闭再申请",null);
        }
        boolean hasReview=this.hasLiveRadioReviewFromGoddess(userid,storeId);
        if(hasReview){
            return new ResponseApiVO(-2003,"正在审核中，请耐心等待",null);

        }
        ReviewPO reviewPO=new ReviewPO();
        reviewPO.setDrBusinessCode(BusinessCodeUtil.createLiveRadioCode(userid,storeId));
        reviewPO.setDrBelongToStoreId(storeId);
        reviewPO.setCreateTime(new Date());
        reviewPO.setDrProposerName((String) upo.get("userName"));
        reviewPO.setDrBusinessType(ReviewBusinessTypeEnum.ReviewBusinessType3.getCode());
        reviewPO.setDrReviewStatus(ReviewStatusEnum.ReviewStatus0.getCode());
        reviewPO.setDrProposerType(ReviewProposerTypeEnum.ReviewProposerType1.getCode());
        reviewPO.setDrReviewerType(ReviewReviewerTypeEnum.ReviewReviewerType1.getCode());
        reviewPO.setDrBelongToStoreId(storeId);
        reviewPO.setDrProposer(userid);
        reviewPO.setDrApplyDesc("申请开通直播空间");
        reviewPO.setDrBusinessStatus(ReviewBusinessStatusEnum.liveRadio10.getCode());
        return new ResponseApiVO(this.commonReviewService.submitAppl(reviewPO));

    }
    /**
     * 判断是否未审核
     * @param proposerId 业务表的流水号
     * @param storeId 审核单位的ID，空即是总店
     * @param businessType 业务类型
     * @param businessStatus 业务状态
     * @param reviewReviewerTypeEnum 审核人类型
     * @return
     */
    public boolean hasReview(Integer proposerId,Integer storeId, ReviewBusinessTypeEnum businessType, ReviewBusinessStatusEnum businessStatus,ReviewReviewerTypeEnum reviewReviewerTypeEnum){
        Map condition=new HashMap();
        condition.put("drProposer",proposerId);
        condition.put("drBusinessType",businessType.getCode());
        condition.put("drBusinessStatus",businessStatus.getCode());
        condition.put("drReviewStatus", ReviewStatusEnum.ReviewStatus0.getCode());
        condition.put("drProposerType", ReviewProposerTypeEnum.ReviewProposerType1.getCode());
        condition.put("drReviewerType", reviewReviewerTypeEnum.getCode());
        if(storeId!=null && reviewReviewerTypeEnum.getCode()==ReviewReviewerTypeEnum.ReviewReviewerType1.getCode()){
            condition.put("drBelongToStoreId", storeId);
        }
        return this.commonCountBySearchCondition("ddw_review",condition)>0?true:false;
    }



}