package com.ddw.controller;

import com.ddw.beans.GoodsEditDTO;
import com.ddw.beans.GoodsTypeDTO;
import com.ddw.beans.StorePO;
import com.ddw.services.StoreGoodsTypeService;
import com.ddw.servies.StoreService;
import com.ddw.util.Toolsddw;
import com.gen.common.vo.ResponseVO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/manager/goods/type")
@Controller
public class StoreGoodsTypeContoller {

    private final Logger logger = Logger.getLogger(StoreGoodsTypeContoller.class);

    @Autowired
    private StoreGoodsTypeService storeGoodsTypeService;

    @Autowired
    private StoreService storeService;

    @GetMapping("/to-paging")
    public String  toPaging(@RequestParam(defaultValue = "1") Integer pageNo, Model model){
        try{
            StorePO spo=this.storeService.getStoreBySysUserid(Toolsddw.getCurrentUserId());
            if(spo!=null){
                model.addAttribute("gPage",storeGoodsTypeService.page(pageNo,spo.getId()));

            }
        }catch (Exception e){
            logger.error("StoreGoodsTypeContoller->toPaging->系统异常",e);
        }
        return "pages/manager/store/goods/storeGoodsTypeList";
    }

    @GetMapping("to-edit")
    public String  toEdit(Integer id, Model model){
        try{
            StorePO spo=this.storeService.getStoreBySysUserid(Toolsddw.getCurrentUserId());
            if(spo!=null){
                model.addAttribute("goodsType",this.storeGoodsTypeService.getGoodsType(id,spo.getId()));

            }
        }catch (Exception e){
            logger.error("StoreGoodsTypeContoller->toEdit->系统异常",e);
        }
        return "pages/manager/store/goods/storeGoodsTypeEdit";
    }

    @PostMapping("do-save")
    @ResponseBody
    public ResponseVO doSave(GoodsTypeDTO dto, Model model){
        try{
            StorePO spo=this.storeService.getStoreBySysUserid(Toolsddw.getCurrentUserId());
            if(spo!=null){
                return this.storeGoodsTypeService.saveGoodsType(dto,spo.getId());
            }
        }catch (Exception e){
            logger.error("StoreGoodsTypeContoller->doSave->系统异常",e);
        }
        return new ResponseVO(-1,"提交失败",null);
    }

    @PostMapping("do-update-status")
    @ResponseBody
    public ResponseVO doUpdateStatus(String idStr,Integer status){
        try {
            StorePO spo=this.storeService.getStoreBySysUserid(Toolsddw.getCurrentUserId());
            if(spo!=null){
                return this.storeGoodsTypeService.updateStatus(idStr,status,spo.getId());

            }
        }catch (Exception e){
            logger.error("StoreGoodsTypeContoller->doUpdateStatus",e);
        }
        return new ResponseVO(-1,"操作失败",null);

    }
}
