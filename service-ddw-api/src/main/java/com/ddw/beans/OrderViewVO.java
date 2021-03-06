package com.ddw.beans;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

@ApiModel
public class OrderViewVO {


    @ApiModelProperty(name="name",value="标题",example="奶茶")
    private String name;
    @ApiModelProperty(name="headImg",value="图像地址,除了商品和礼物，其它都是空",example="http://")
    private String headImg;
    @ApiModelProperty(name="price",value="价格",example="1000")
    private Integer price;

    @ApiModelProperty(name="createTime",value="时间",example="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(name="orderNo",value="订单号",example="xxxxx")

    private String orderNo;
    @ApiModelProperty(name="num",value="数量",example="1")
    private Integer num;
    @ApiModelProperty(name="orderType",value="订单类型",example="1")
    private Integer orderType;

    @ApiModelProperty(name="orderTypeName",value="订单类型名称",example="1")
    private String orderTypeName;
    @ApiModelProperty(name="shipStatus",value="发货状态,未发货：0，已接单：1，完成：5",example="1")
    private Integer shipStatus;

    @ApiModelProperty(name="shipStatusName",value="发货状态名称",example="1")
    private String shipStatusName;
    @ApiModelProperty(name="isEvaluate",value="是否已评价",example="是否已评价,未评价：0，已评价：1")
    private Integer isEvaluate;
    @ApiModelProperty(name="描述",value="描述",example="描述")
    private String desc;
    @ApiModelProperty(name="type",value="是否聚合订单，否：0，是：1",example="是否聚合订单，否：0，是：1")
    private Integer type=0;
    @ApiModelProperty(name="code",value="编号",example="编号")
    @JsonProperty("code")
    private Integer id;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIsEvaluate() {
        return isEvaluate;
    }

    public void setIsEvaluate(Integer isEvaluate) {
        this.isEvaluate = isEvaluate;
    }

    public String getOrderTypeName() {
        return orderTypeName;
    }

    public void setOrderTypeName(String orderTypeName) {
        this.orderTypeName = orderTypeName;
    }

    public String getShipStatusName() {
        return shipStatusName;
    }

    public void setShipStatusName(String shipStatusName) {
        this.shipStatusName = shipStatusName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public Integer getShipStatus() {
        return shipStatus;
    }

    public void setShipStatus(Integer shipStatus) {
        this.shipStatus = shipStatus;
    }
}
