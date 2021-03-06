package com.ddw.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


public class OrderPO implements Serializable {
    private static final long serialVersionUID = -9090941175191428874L;
    private Integer id;
    private Date createTime;
    private Date updateTime;

    /**
     * 消费者用户
     */
    private Integer doCustomerUserId;

    /**
     * 付款状态，未付款：0，已付款：1，退款：2
     */
    private Integer doPayStatus;

    /**
     * 订单类型，商品：1，原材料：2
     */
    private Integer doType;

    /**
     * 订单截止时间
     */
    private Date doEndTime;

    /**
     * 货品状态，未发货：0，已发货：1，确认签收：2，退货：3，完成：4
     */
    private Integer doShipStatus;

    /**
     * 订单日期
     */
    private String doOrderDate;

    /**
     * 支付类型，微信支付：1，支付宝：2，线下支付：3
     */
    private Integer doPayType;

    /**
     * 消费者-门店ID，此处是材料用户标识的，app订单统一是-1，其余是门店ID
     */
    private Integer doCustomerStoreId;

    /**
     * 卖家角色，总部：-1，门店：id号
     */
    private Integer doSellerId;

    /**
     * 优惠卷
     */
    private String doCouponNo;

    /**
     * 用户类型 ,普通会员：0，女神会员：1，代练会员：2，服务员：3，门店：4
     */
    private Integer doCustomerType;

    /**
     * 金额
     */
    private Integer doCost;

    /**
     * 提交订单的人名字
     */
    private String creater;

    /**
     * 卖方给买方寄出的快递号
     */
    private String doTrackingNumber;

    /**
     * 买家退还给卖家的快递号
     */
    private String doExitTrackingNumber;
    /**
     * 卖方给买方寄出的快递公司名称
     */
    private String doExpressName;

    /**
     * 买家退还给卖家的快递公司名称
     */
    private String doExitExpressName;
    /**
     * 折扣
     */
    private BigDecimal doDicount;

    private String doExtendStr;

    private Integer doStoreProportion;
    private Integer doStoreProportionCost;
    private Integer doStoreProportionCouponCost;

    /**
     * 活动原价
     */
    private Integer doOrigActCost;

    /**
     * 优惠卷所优惠的价格
     */
    private Integer doCouponCost;

    /**
     * 原价
     */
    private Integer doOrigCost;

    /**
     * 合成图片路径
     */
    private String doMergePicPath;

    public String getDoMergePicPath() {
        return doMergePicPath;
    }

    public void setDoMergePicPath(String doMergePicPath) {
        this.doMergePicPath = doMergePicPath;
    }

    public Integer getDoOrigActCost() {
        return doOrigActCost;
    }

    public void setDoOrigActCost(Integer doOrigActCost) {
        this.doOrigActCost = doOrigActCost;
    }

    public Integer getDoCouponCost() {
        return doCouponCost;
    }

    public void setDoCouponCost(Integer doCouponCost) {
        this.doCouponCost = doCouponCost;
    }


    public Integer getDoStoreProportionCouponCost() {
        return doStoreProportionCouponCost;
    }

    public void setDoStoreProportionCouponCost(Integer doStoreProportionCouponCost) {
        this.doStoreProportionCouponCost = doStoreProportionCouponCost;
    }

    public Integer getDoStoreProportion() {
        return doStoreProportion;
    }

    public void setDoStoreProportion(Integer doStoreProportion) {
        this.doStoreProportion = doStoreProportion;
    }

    public Integer getDoStoreProportionCost() {
        return doStoreProportionCost;
    }

    public void setDoStoreProportionCost(Integer doStoreProportionCost) {
        this.doStoreProportionCost = doStoreProportionCost;
    }

    public BigDecimal getDoDicount() {
        return doDicount;
    }

    public void setDoDicount(BigDecimal doDicount) {
        this.doDicount = doDicount;
    }

    public String getDoExtendStr() {
        return doExtendStr;
    }

    public void setDoExtendStr(String doExtendStr) {
        this.doExtendStr = doExtendStr;
    }

    public String getDoTrackingNumber() {
        return doTrackingNumber;
    }

    public void setDoTrackingNumber(String doTrackingNumber) {
        this.doTrackingNumber = doTrackingNumber;
    }

    public String getDoExitTrackingNumber() {
        return doExitTrackingNumber;
    }

    public void setDoExitTrackingNumber(String doExitTrackingNumber) {
        this.doExitTrackingNumber = doExitTrackingNumber;
    }

    public String getDoExpressName() {
        return doExpressName;
    }

    public void setDoExpressName(String doExpressName) {
        this.doExpressName = doExpressName;
    }

    public String getDoExitExpressName() {
        return doExitExpressName;
    }

    public void setDoExitExpressName(String doExitExpressName) {
        this.doExitExpressName = doExitExpressName;
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    @Override
    public String toString() {
        return "OrderPO{" +
                "id=" + id +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", doCustomerUserId=" + doCustomerUserId +
                ", doPayStatus=" + doPayStatus +
                ", doType=" + doType +
                ", doEndTime=" + doEndTime +
                ", doShipStatus=" + doShipStatus +
                ", doOrderDate='" + doOrderDate + '\'' +
                ", doPayType=" + doPayType +
                ", doCustomerStoreId=" + doCustomerStoreId +
                ", doSellerId=" + doSellerId +
                ", doCouponNo='" + doCouponNo + '\'' +
                ", doCustomerType=" + doCustomerType +
                ", doCost=" + doCost +
                ", creater='" + creater + '\'' +
                ", doTrackingNumber='" + doTrackingNumber + '\'' +
                ", doExitTrackingNumber='" + doExitTrackingNumber + '\'' +
                ", doExpressName='" + doExpressName + '\'' +
                ", doExitExpressName='" + doExitExpressName + '\'' +
                ", doDicount=" + doDicount +
                ", doExtendStr='" + doExtendStr + '\'' +
                ", doStoreProportion=" + doStoreProportion +
                ", doStoreProportionCost=" + doStoreProportionCost +
                ", doStoreProportionCouponCost=" + doStoreProportionCouponCost +
                ", doOrigCost=" + doOrigCost +
                '}';
    }

    public Integer getDoCost() {
        return doCost;
    }

    public void setDoCost(Integer doCost) {
        this.doCost = doCost;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getDoCustomerUserId() {
        return doCustomerUserId;
    }

    public void setDoCustomerUserId(Integer doCustomerUserId) {
        this.doCustomerUserId = doCustomerUserId;
    }

    public Integer getDoPayStatus() {
        return doPayStatus;
    }

    public void setDoPayStatus(Integer doPayStatus) {
        this.doPayStatus = doPayStatus;
    }

    public Integer getDoType() {
        return doType;
    }

    public void setDoType(Integer doType) {
        this.doType = doType;
    }

    public Date getDoEndTime() {
        return doEndTime;
    }

    public void setDoEndTime(Date doEndTime) {
        this.doEndTime = doEndTime;
    }

    public Integer getDoShipStatus() {
        return doShipStatus;
    }

    public void setDoShipStatus(Integer doShipStatus) {
        this.doShipStatus = doShipStatus;
    }

    public String getDoOrderDate() {
        return doOrderDate;
    }

    public void setDoOrderDate(String doOrderDate) {
        this.doOrderDate = doOrderDate;
    }

    public Integer getDoPayType() {
        return doPayType;
    }

    public void setDoPayType(Integer doPayType) {
        this.doPayType = doPayType;
    }

    public Integer getDoCustomerStoreId() {
        return doCustomerStoreId;
    }

    public void setDoCustomerStoreId(Integer doCustomerStoreId) {
        this.doCustomerStoreId = doCustomerStoreId;
    }

    public Integer getDoSellerId() {
        return doSellerId;
    }

    public void setDoSellerId(Integer doSellerId) {
        this.doSellerId = doSellerId;
    }

    public String getDoCouponNo() {
        return doCouponNo;
    }

    public void setDoCouponNo(String doCouponNo) {
        this.doCouponNo = doCouponNo;
    }

    public Integer getDoCustomerType() {
        return doCustomerType;
    }

    public void setDoCustomerType(Integer doCustomerType) {
        this.doCustomerType = doCustomerType;
    }

    public Integer getDoOrigCost() {
        return doOrigCost;
    }

    public void setDoOrigCost(Integer doOrigCost) {
        this.doOrigCost = doOrigCost;
    }
}
