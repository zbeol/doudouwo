package com.ddw.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@ApiModel
public class PayDTO {
    @ApiModelProperty(name="money",value="金额,单位分",example="1000")
    private Integer money;

    @ApiModelProperty(name="orderType",value="订单类型,商品：1，充值：3，竞价定金：4，竞价金额：5，门票：7，逗币充值：8，约玩续费：9,代练支付:10",example="3")
    private Integer orderType;


    @ApiModelProperty(name="codes",value="所购买的多个物品编号（充值卷的ID，商品ID，礼物ID，竞价ID，定金ID,代练订单ID等）,",example="[1]")
    private Integer codes[];

    @ApiModelProperty(name="couponCode",value="优惠卷code",example="")
    private Integer couponCode;

    @ApiModelProperty(name="groupId",value="群ID（此参数用在房间购买礼物时候传，在商城购买礼物的不用传这个参数）",example="")
    @JsonIgnore
    private String groupId;

    @ApiModelProperty(name="tableNo",value="桌号",example="")
    private String tableNo;

    @JsonIgnore
    private Map douBiDtos;

    public Map getDouBiDtos() {
        return douBiDtos;
    }

    public void setDouBiDtos(Map douBiDtos) {
        this.douBiDtos = douBiDtos;
    }

    public String getTableNo() {
        return tableNo;
    }

    public void setTableNo(String tableNo) {
        this.tableNo = tableNo;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Integer getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(Integer couponCode) {
        this.couponCode = couponCode;
    }

    public Integer[] getCodes() {
        return codes;
    }

    public void setCodes(Integer[] codes) {
        this.codes = codes;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    @Override
    public String toString() {
        return "PayDTO{" +
                "money=" + money +
                ", orderType=" + orderType +
                ", codes=" + Arrays.toString(codes) +
                '}';
    }

    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }
}
