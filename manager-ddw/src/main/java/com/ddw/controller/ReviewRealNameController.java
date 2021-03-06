package com.ddw.controller;


import com.ddw.beans.ReviewPO;
import com.ddw.services.ReviewRealNameService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/manager/reviewRealName")
public class ReviewRealNameController {

    private final Logger logger = Logger.getLogger(ReviewRealNameController.class);

    @Autowired
    private ReviewRealNameService reviewRealNameService;

    /**
     * 总店-会员实名认证列表
     * @param pageNo
     * @param model
     * @return
     */
    @GetMapping("/to-review-page")
    public String toReviewPageByHq(@RequestParam(defaultValue = "1") Integer pageNo,Model model){
       try {
           model.addAttribute("rPage",this.reviewRealNameService.findRealNamePageByHq(pageNo));
       }catch (Exception e){
           logger.error("ReviewRealNameController->toReviewPage",e);
       }
       return "pages/manager/reviewRealName/list";

    }

    /**
     * 根据ID审批情况
     * @return
     */
    @GetMapping("/to-review-info-by-id-html")
    public String  toReviewInfoByIdHtml(Integer id,Model model){
        try {
            ReviewPO reviewPO = this.reviewRealNameService.getReviewById(id);
            model.addAttribute("review",reviewPO);
            if (reviewPO != null) {
                model.addAttribute("reviewRealName",this.reviewRealNameService.getReviewRealNameByCode(reviewPO.getDrBusinessCode()));
            }
            return "pages/manager/reviewRealName/reviewInfo";


        }catch (Exception e){
            logger.error("ReviewRealNameController->toReviewInfoByIdHtml",e);
        }

        return "pages/manager/reviewRealName/reviewInfo";
    }

}
