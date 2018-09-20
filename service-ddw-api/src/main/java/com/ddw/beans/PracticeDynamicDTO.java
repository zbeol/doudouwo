package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by Jacky on 2018/7/7.
 */
@ApiModel
public class PracticeDynamicDTO{
    @ApiModelProperty(name = "practiceId",value = "代练编号",example = "1")
    private int practiceId;
    @ApiModelProperty(name="pageNo",value="页码",example="1")
    private Integer pageNo;

    public int getPracticeId() {
        return practiceId;
    }

    public void setPracticeId(int practiceId) {
        this.practiceId = practiceId;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }
}
