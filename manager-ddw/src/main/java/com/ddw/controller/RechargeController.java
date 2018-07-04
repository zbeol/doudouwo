package com.ddw.controller;

import com.ddw.beans.RechargeDTO;
import com.ddw.beans.StorePO;
import com.ddw.enums.ShipStatusEnum;
import com.ddw.servies.OrderService;
import com.ddw.servies.RechargeManagerService;
import com.ddw.servies.StoreService;
import com.ddw.util.Toolsddw;
import com.gen.common.util.MyEncryptUtil;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/manager/recharge")
public class RechargeController {

    private final Logger logger = Logger.getLogger(RechargeController.class);

    @Autowired
    private RechargeManagerService rechargeManagerService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private StoreService storeService;


    @GetMapping("to-paging")
    public String toPaging(@RequestParam(defaultValue = "1") Integer pageNo, Model model){
        try {
            model.addAttribute("sPage",rechargeManagerService.findPage(pageNo));
        }catch (Exception e){
            logger.error("RechargeController->toPaging",e);
        }
        return "pages/manager/headquarters/recharge/list";
    }
    @GetMapping("to-edit")
    public String toEdtitPage(String idStr,Model model){
        try {

            String id= MyEncryptUtil.getRealValue(idStr);
            if(StringUtils.isNotBlank(id)){
                model.addAttribute("ds",this.rechargeManagerService.getById(Integer.parseInt(id)));
            }

        }catch (Exception e){
            logger.error("RechargeController->toEdtitPage",e);
        }
        return "pages/manager/headquarters/recharge/edit";
    }

    @PostMapping("do-edit")
    @ResponseBody
    public ResponseVO doEditPage(RechargeDTO dto){
        try {
            return this.rechargeManagerService.save(dto);
        }catch (Exception e){
            logger.error("RechargeController->doEditPage",e);
            return new ResponseVO(-1,"提交失败",null);
        }

    }

    @PostMapping("do-makesure")
    @ResponseBody
    public ResponseVO doMakeSure(String orderStr){
        try {
            StorePO spo=this.storeService.getStoreBySysUserid(Toolsddw.getCurrentUserId());
            if(spo!=null){
                return this.orderService.updateOrderStatus(null, ShipStatusEnum.ShipStatus5.getCode(),spo.getId(),orderStr);

            }
        }catch (Exception e){
            logger.error("RechargeController->makesure",e);
        }
        return new ResponseVO(-1,"操作失败",null);

    }

}