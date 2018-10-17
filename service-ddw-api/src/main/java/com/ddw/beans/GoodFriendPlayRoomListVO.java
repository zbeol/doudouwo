package com.ddw.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GoodFriendPlayRoomListVO<T> {


    @ApiModelProperty(name = "code", value = "code", example = "code")
    private Integer code;


    @ApiModelProperty(name = "bgImg", value = "背景图", example = "背景图")
    private String bgImg;

    @ApiModelProperty(name = "name", value = "主题名称", example = "主题名称")
    private String name;

    @ApiModelProperty(name = "tableNumber", value = "桌号", example = "桌号")
    private String tableNumber;
    @ApiModelProperty(name = "memberNum", value = "人数", example = "人数")
    private Integer memberNum;

    @ApiModelProperty(name = "status", value = "预约中：0，约战中：1", example = "预约中：0，约战中：1")
    private Integer status;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getBgImg() {
        return bgImg;
    }

    public void setBgImg(String bgImg) {
        this.bgImg = bgImg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }

    public Integer getMemberNum() {
        return memberNum;
    }

    public void setMemberNum(Integer memberNum) {
        this.memberNum = memberNum;
    }
}
