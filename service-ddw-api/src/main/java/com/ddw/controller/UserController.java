package com.ddw.controller;


import com.alibaba.fastjson.JSONObject;
import com.ddw.beans.UserInfoDTO;
import com.ddw.services.UserInfoService;
import com.ddw.token.TokenUtil;
import com.gen.common.vo.ResponseVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 访问地址：/swagger-ui.html
 */
@Api(value = "会员用户用例",description = "会员用户用例",tags = {"会员用户用例"})
@RestController
@RequestMapping("/ddwapp/user")
public class UserController {
    private final Logger logger = Logger.getLogger(UserController.class);
    @Autowired
    private UserInfoService userInfoService;

    @ApiOperation(value = "会员注册用例")
   // @ApiImplicitParam(name = "args", value = "参数", required = true, dataType = "UserInfoDTO")
    @PostMapping("/save")
    public ResponseVO save(@RequestBody @ApiParam(name="args",value="传入json格式",required=true)UserInfoDTO userInfoDTO){
        try {
            Map userMap = userInfoService.queryByOpenid(userInfoDTO.getOpenid());
            String token = TokenUtil.createToken(userInfoDTO.getOpenid());
            JSONObject json = new JSONObject();
            json.put("token",token);
            if(userMap == null || userMap.isEmpty()){
                userInfoService.save(userInfoDTO);
                return new ResponseVO(1,"注册成功",json);
            }else{
                return new ResponseVO(2,"账号已存在",json);
            }
        }catch (Exception e){
            logger.error("UserController->save",e);
            return new ResponseVO(-1,"提交失败",null);
        }
    }

    @ApiOperation(value = "会员修改资料用例")
    @PostMapping("/update")
    public ResponseVO update( @RequestParam @ApiParam(name = "token",value="token", required = true) String token,@RequestBody @ApiParam(name="args",value="传入json格式",required=true)UserInfoDTO userInfoDTO){
        try {
            if(TokenUtil.hasToken(token)){
                return userInfoService.update(userInfoDTO);
            }else{
                return new ResponseVO(-2,"提交失败,token错误",null);
            }
        }catch (Exception e){
            logger.error("UserController->update",e);
            return new ResponseVO(-1,"提交失败",null);
        }
    }

    @ApiOperation(value = "会员查询资料用例")
    @PostMapping("/query")
    public ResponseVO query(@RequestParam @ApiParam(name = "token",value="token", required = true) String token,@RequestParam @ApiParam(name = "username",value="username", required = true) String username){
        try {
            if(TokenUtil.hasToken(token)){
                return new ResponseVO(1,"成功",userInfoService.query(username));
            }else{
                return new ResponseVO(-2,"提交失败,token错误",null);
            }
        }catch (Exception e){
            logger.error("UserInfoController->update",e);
            return new ResponseVO(-1,"提交失败",null);
        }
    }
}
