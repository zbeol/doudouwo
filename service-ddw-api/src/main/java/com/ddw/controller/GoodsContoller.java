package com.ddw.controller;

import com.ddw.beans.*;
import com.ddw.enums.GoodsPlatePosEnum;
import com.ddw.enums.GoodsTypeEnum;
import com.ddw.services.GoodsClientService;
import com.ddw.token.Token;
import com.ddw.token.TokenUtil;
import com.gen.common.dict.DictionaryUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ddwapp/goods")
@Api(description="商品",tags = "商品")
public class GoodsContoller {
    private final Logger logger = Logger.getLogger(GoodsContoller.class);

    @Autowired
    private GoodsClientService goodsClientService;

    @Token
    @ApiOperation(value = "商品首页")
    @PostMapping("/index/{token}")
    public ResponseApiVO<ListVO> toGoodsIndex(@PathVariable String token){
        try {
            return this.goodsClientService.appIndex(token);
        }catch (Exception e){
            logger.error("GoodsContoller->toGoodsIndex-商品首页-》异常",e);
        }
        return new ResponseApiVO(-1,"失败",null);
    }

    @ApiOperation(value = "商品列表")
    @PostMapping("/list/{token}")
    public ResponseApiVO<GoodsListVO> toGoodsList(@PathVariable String token){
        try {
            return this.goodsClientService.goodsIndex(TokenUtil.getStoreId(token), GoodsPlatePosEnum.GoodsPlatePos2);
        }catch (Exception e){
            logger.error("GoodsContoller->toGoodsList-商列表-》异常",e);
        }
        return new ResponseApiVO(-1,"失败",null);
    }

    @ApiOperation(value = "商品列表(h5)")
    @PostMapping("/h5shoplist")
    public ResponseApiVO<GoodsListVO> toGoodsH5List(@RequestParam(value = "storeId") @ApiParam(name = "storeId",value="门店ID", required = false) Integer storeId,
                                                    @CookieValue(value="storeId") Integer cStoreId){
        try {
            return this.goodsClientService.goodsIndex(cStoreId!=null?cStoreId:storeId, GoodsPlatePosEnum.GoodsPlatePos2);
        }catch (Exception e){
            logger.error("GoodsContoller->toGoodsList-商列表-》异常",e);
        }
        return new ResponseApiVO(-1,"失败",null);
    }


    @ApiOperation(value = "商品详情(h5)")
    @PostMapping("/h5info")
    public ResponseApiVO<GoodsInfoVO<GoodsInfoProductVO>> toGoodsH5Info(@RequestParam(value = "storeId") @ApiParam(name = "storeId",value="门店ID", required = false) Integer storeId,
                                                                        @RequestParam(value = "code") @ApiParam(name = "code",value="商品ID", required = false) Integer code,
    @CookieValue(value="storeId") Integer cStoreId){
        try {
            CodeDTO dto=new CodeDTO();
            dto.setCode(code);
            return this.goodsClientService.getInfo(cStoreId!=null?cStoreId:storeId,dto);
        }catch (Exception e){
            logger.error("GoodsContoller->toGoodsH5Info-商品详情(h5)-》异常",e);
        }
        return new ResponseApiVO(-1,"失败",null);
    }
    @Token
    @ApiOperation(value = "商品详情")
    @PostMapping("/info/{token}")
    public ResponseApiVO<GoodsInfoVO<GoodsInfoProductVO>> toGoodsInfo(@PathVariable String token, @RequestBody @ApiParam(name="args",value="传入json格式",required=true)CodeDTO args){
        try {
            return this.goodsClientService.getInfo( TokenUtil.getStoreId(token),args);
        }catch (Exception e){
            logger.error("GoodsContoller->toGoodsInfo-商品详情-》异常",e);
        }
        return new ResponseApiVO(-1,"失败",null);
    }
}
