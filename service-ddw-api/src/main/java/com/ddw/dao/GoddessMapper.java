package com.ddw.dao;

import com.ddw.beans.vo.AppIndexGoddessVO;
import com.ddw.beans.vo.LiveRadioListVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Jacky on 2018/6/3.
 */
public interface GoddessMapper {
    List<AppIndexGoddessVO> getGoddessList(@Param("storeId") Integer storeId, @Param("start") Integer start, @Param("end") Integer end);
    List<AppIndexGoddessVO> getGoddessListByIds(@Param("userIdList") List<Integer> userIdList, @Param("userId") Integer userId,
                                                @Param("start") Integer start, @Param("end") Integer end,@Param("weekList") Integer weekList);
    List<AppIndexGoddessVO> getGoddessListByNotInIds(@Param("userIdList") List<Integer> userIdList, @Param("userId") Integer userId,
                                                     @Param("start") Integer start, @Param("end") Integer end,@Param("weekList") Integer weekList);
    List<AppIndexGoddessVO> getGoddessListByNotInIdsNoFans(@Param("userIdList") List<Integer> userIdList, @Param("userId") Integer userId,
                                                     @Param("start") Integer start, @Param("end") Integer end);
    List<LiveRadioListVO> liveGoddess(@Param("startNum") Integer startNum, @Param("endNum") Integer endNum,@Param("storeId") Integer storeId,@Param("userId") Integer userId);
}
