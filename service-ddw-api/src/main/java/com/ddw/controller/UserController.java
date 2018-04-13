package com.ddw.controller;

import com.ddw.beans.ResponseVO;
import com.ddw.beans.UserInfoDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 访问地址：/swagger-ui.html
 */
@Api(description = "会员用户用例")
@RestController
@RequestMapping("/ddwapp/user")
public class UserController {

    @ApiOperation(value = "会员注册用例")
   // @ApiImplicitParam(name = "args", value = "参数", required = true, dataType = "UserInfoDTO")
    @PostMapping("/save")
    public ResponseVO save(@RequestBody @ApiParam(name="参数",value="传入json格式",required=true)UserInfoDTO args){
        System.out.println(args+"---");
        return new ResponseVO();
    }

    @ApiOperation(value = "会员修改资料用例")
    @PostMapping("/update")
    public ResponseVO update( @RequestBody @ApiParam(name="参数",value="传入json格式",required=true)UserInfoDTO args){
        System.out.println(args+"---");
        return new ResponseVO();
    }

    @ApiOperation(value = "会员查询资料用例")
    @PostMapping("/query")
    public ResponseVO query(@ApiParam(value = "账号", required = true) UserInfoDTO args){
        System.out.println(args+"---");
        return new ResponseVO();
    }
}
