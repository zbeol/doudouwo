package com.ddw.services;

import com.ddw.beans.*;
import com.ddw.beans.vo.GoodFriendPlayChatCenterVO;
import com.ddw.beans.vo.GoodFriendPlayRoomListVO;
import com.ddw.config.DDWGlobals;
import com.ddw.dao.GoodFriendPlayMapper;
import com.ddw.enums.*;
import com.ddw.token.Token;
import com.ddw.token.TokenUtil;
import com.ddw.util.IMApiUtil;
import com.gen.common.beans.CommonBeanFiles;
import com.gen.common.beans.CommonChildBean;
import com.gen.common.beans.CommonSearchBean;
import com.gen.common.config.MainGlobals;
import com.gen.common.exception.GenException;
import com.gen.common.services.CommonService;
import com.gen.common.services.FileService;
import com.gen.common.util.*;
import com.gen.common.vo.FileInfoVo;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(readOnly = true)
public class GoodFriendPlayService extends CommonService {

    @Autowired
    private GoodFriendPlayMapper goodFriendPlayMapper;

    @Autowired
    private MainGlobals mainGlobals;

    @Autowired
    private DDWGlobals ddwGlobals;
    @Autowired
    private FileService fileService;

    @Autowired
    private CommonReviewService commonReviewService;
    public ResponseApiVO getChatCenter(String token)throws Exception{
        Page page=new Page(1,10);
        Integer storeId= TokenUtil.getStoreId(token);
        GoodFriendPlayChatCenterVO gfp=this.getChatCenterBean(storeId);
        if(gfp==null){
            return new ResponseApiVO(-2,"大聊天房还没建",null);
        }

       /* Map search=new HashMap();
        search.put("centerId",gfp.getId());
        search.put("disabled", DisabledEnum.disabled0.getCode());
        search.put("status",GoodFriendPlayRoomStatusEnum.status0.getCode());
        CommonChildBean ccb=new CommonChildBean("ddw_goodfriendplay_tables","id","tableCode",null);
        CommonSearchBean csb=new CommonSearchBean("ddw_goodfriendplay_room","t1.updateTime desc","t1.id code,t1.roomImgIcon,t1.name,ct0.tableNumber,t1.status,t1.peopleMaxNum",page.getStartRow(),page.getEndRow(),search,ccb);
        List onlist=this.getCommonMapper().selectObjects(csb);
        if(onlist==null || onlist.isEmpty()){
            gfp.setOnLineList(new ArrayList());
        }else{
            gfp.setOnLineList(onlist);
        }*/
        List<GoodFriendPlayRoomListVO> offlist=this.goodFriendPlayMapper.getRoomList(gfp.getId(),DisabledEnum.disabled0.getCode(),null,"<"+GoodFriendPlayRoomStatusEnum.status21.getCode(),null,page.getStartRow(),page.getEndRow());
        if(offlist==null || offlist.isEmpty()){
            gfp.setRoomList(new ArrayList());
        }else{

            gfp.setRoomList(offlist);
        }
        return new ResponseApiVO(1,"成功",gfp);
    }
    public GoodFriendPlayChatCenterVO getChatCenterBean(Integer storeId)throws Exception{
        GoodFriendPlayChatCenterVO gfp=(GoodFriendPlayChatCenterVO)CacheUtil.get("publicCache","gfplay-chatcenter-"+storeId);
        if(gfp==null){
            gfp=this.commonObjectBySingleParam("ddw_goodfriendplay_center","storeId",storeId,GoodFriendPlayChatCenterVO.class);
            if(gfp==null)return null;
            CacheUtil.put("publicCache","gfplay-chatcenter-"+storeId,gfp);
        }
        return gfp;
    }
    public ResponseApiVO getRoom(String token,CodeDTO dto)throws Exception{
        if(dto.getCode()==null || dto.getCode()<=0){
            return new ResponseApiVO<>(-2,"请选择聊天房间",null);
        }
        Map search=new HashMap();
        search.put("id",dto.getCode());
        search.put("disabled",DisabledEnum.disabled0.getCode());
        Map chid1=new HashMap();
        chid1.put("disabled",DisabledEnum.disabled0.getCode());
        chid1.put("roomId",dto.getCode());
        CommonSearchBean csb=new CommonSearchBean("ddw_goodfriendplay_room",null,"t1.roomOwnerOpenId,t1.des `describe`,t1.groupId,t1.id code,t1.roomImg,t1.name,t1.status,ct0.tableNumber,t1.peopleMaxNum,count(ct1.id) peopleNum",null,null,search,
                new CommonChildBean("ddw_goodfriendplay_tables","id","tableCode",null),
                new CommonChildBean("ddw_goodfriendplay_room_member","roomId","id",chid1));
        List<Map> list=this.getCommonMapper().selectObjects(csb);
        if(list==null || list.isEmpty() || !list.get(0).containsKey("code")){
            return new ResponseApiVO<>(-2,"房间不存在或者已过期",null);
        }
        Map dataMap=list.get(0);
        Integer roomId=(Integer) dataMap.get("code");
        Map searchMap=new HashMap();
        searchMap.put("roomId",roomId);
        searchMap.put("joinOffLine", JoinOffLineStatusEnum.Status1.getCode());
        CommonSearchBean memberCsb=new CommonSearchBean("ddw_goodfriendplay_room_member",null,"t1.openId,ct0.nickName,ct0.headImgUrl",null,null,searchMap,
                new CommonChildBean("ddw_userinfo","id","userId",null));
        List<Map> memberList=this.getCommonMapper().selectObjects(memberCsb);
        if(memberList!=null && !memberList.isEmpty()){
            dataMap.put("memberList",memberList);
        }else{
            dataMap.put("memberList",new ArrayList());
        }

        return new ResponseApiVO(1,"成功",dataMap);
    }
    public List getIndexRoomRecord(String token)throws Exception{
        Page page=new Page(1,4);

        Integer storeId= TokenUtil.getStoreId(token);
        GoodFriendPlayChatCenterVO gfp=this.getChatCenterBean(storeId);
        if(gfp==null)return new ArrayList<>();
        List<GoodFriendPlayRoomListVO> offlist=this.goodFriendPlayMapper.getIndexRoomList(gfp.getId(),DisabledEnum.disabled0.getCode(),page.getStartRow(),page.getEndRow());
        if(offlist==null || offlist.isEmpty()){
            new ArrayList<>();
        }

        return offlist;
    }
    public ResponseApiVO getRoomRecord(String token,PageNoDTO dto)throws Exception{
        Page page=new Page(dto.getPageNo()==null?1:dto.getPageNo(),10);

        List<GoodFriendPlayRoomListVO> offlist=this.goodFriendPlayMapper.getRoomList(null,null,null,null,TokenUtil.getUserId(token),page.getStartRow(),page.getEndRow());

        if(offlist==null || offlist.isEmpty()){
            return new ResponseApiVO(1,"成功",new ListVO<>(new ArrayList<>()));
        }else{
            offlist.forEach(a->{
                if(a.getChatRoomEndTime()!=null){
                    a.setChatRoomUseTime(Tools.getSurplusTimeStr(a.getChatRoomEndTime(),a.getCreateDate()));
                }
                a.setCreateTime(DateFormatUtils.format(a.getCreateDate(),"yyyy-MM-dd HH:mm"));
                if(a.getStartTime()!=null){
                    a.setPlayUseTime(Tools.getSurplusTimeStr(a.getEndDate(),a.getCreateDate()));
                }else{
                    a.setPlayUseTime("未开始");
                }
                if(DisabledEnum.disabled1.getCode().equals(a.getDisabled()) && !GoodFriendPlayRoomStatusEnum.status21.getCode().equals(a.getStatus())){
                    a.setStatus(23);
                }
            });
        }
        return new ResponseApiVO(1,"成功",new ListVO<>(offlist));
    }
    public ResponseApiVO getRoomList(String token ,GoodFriendPlayRoomListDTO dto)throws Exception{
        Page page=new Page(dto.getPageNo()==null?1:dto.getPageNo(),10);
        Integer storeId= TokenUtil.getStoreId(token);
        GoodFriendPlayChatCenterVO gfp=this.getChatCenterBean(storeId);

        List<GoodFriendPlayRoomListVO> offlist=this.goodFriendPlayMapper.getRoomList(gfp.getId(),DisabledEnum.disabled0.getCode(),dto.getType(),dto.getStatus()==null?"<"+GoodFriendPlayRoomStatusEnum.status21.getCode():"= "+dto.getStatus(),null,page.getStartRow(),page.getEndRow());
        if(offlist==null || offlist.isEmpty()){
            return new ResponseApiVO(1,"成功",new ListVO<>(new ArrayList<>()));
        }
        return new ResponseApiVO(1,"成功",new ListVO<>(offlist));
    }
    public ResponseApiVO getHistoryFriend(String token,GoodFriendPlayHistoryDTO dto){
        Integer userId=TokenUtil.getUserId(token);
        Page page=new Page(dto.getPageNo()==null?1:dto.getPageNo(),15);
        List list=this.goodFriendPlayMapper.selecrHistoryFriend(dto.getFilterRoomId(),userId,page.getStartRow(),page.getEndRow());
        if(list==null || list.isEmpty()){
            return new ResponseApiVO(1,"成功",new ListVO<>(new ArrayList<>()));
        }
        return new ResponseApiVO(1,"成功",new ListVO<>(list));

    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseApiVO createRoom(String token ,GoodFriendPlayCreateRoomDTO dto)throws Exception{

        Integer storeId= TokenUtil.getStoreId(token);
        if(StringUtils.isBlank(dto.getName())){
            return new ResponseApiVO(-2,"请填写主题名",null);

        }
        if(dto.getRoomImg()==null || dto.getRoomImg().isEmpty()){
            return new ResponseApiVO(-2,"请上传图片",null);

        }
        if(dto.getTableCode()==null || dto.getTableCode()<=0){
            return new ResponseApiVO(-2,"请选择桌号",null);

        }

        if(StringUtils.isBlank(dto.getChatRoomEndTime())){
            return new ResponseApiVO(-2,"请填写房间结束时间",null);

        }else if(new Date().after(DateUtils.parseDate(dto.getChatRoomEndTime(),"yyyy-MM-dd HH:mm","yyyy-MM-dd HH:mm:ss","yyyy-MM-dd HH"))){
            return new ResponseApiVO(-2,"结束时间不能小于当前时间",null);

        }
        Map tableMap=getTableById(token,dto.getTableCode());
        if(tableMap==null || tableMap.isEmpty()){
            return new ResponseApiVO(-2,"请选择正确的桌号",null);

        }
        if(GoodFriendPlayRoomStatusEnum.status1.getCode().equals(tableMap.get("status"))){
            return new ResponseApiVO(-2,"当前桌号已被使用",null);

        }
        Integer peopleMaxNum=(Integer) tableMap.get("peopleMaxNum");
        if(dto.getPeopleMaxNum()==null || dto.getPeopleMaxNum()<=0){
            dto.setPeopleMaxNum(peopleMaxNum);
        }else if(dto.getPeopleMaxNum()>peopleMaxNum){
            return new ResponseApiVO(-2,"抱歉，人数设定不能超过座位数",null);

        }


        ResponseVO res=this.isOkCreateRoom(token);
        if(res.getReCode()!=1){
            return new ResponseApiVO(-2,res.getReMsg(),null);
        }
        Map map= BeanToMapUtil.beanToMap(dto,true);
        map.put("createTime",new Date());
        map.put("updateTime",new Date());
        if(!dto.getRoomImg().isEmpty()){
            String dmImgName= DateFormatUtils.format(new Date(),"yyyyMMddHHmmssSSS")+"."+ FilenameUtils.getExtension( dto.getRoomImg().getOriginalFilename());
            FileInfoVo fileInfoVo= UploadFileMoveUtil.move(dto.getRoomImg(),mainGlobals.getRsDir(), dmImgName);
            map.put("roomImg",ddwGlobals.getCallBackHost()+fileInfoVo.getUrlPath());
            map.put("roomImgIcon",map.get("roomImg"));
            CommonBeanFiles f=this.fileService.createCommonBeanFiles(fileInfoVo);
            this.fileService.saveFile(f);

        }

        GoodFriendPlayChatCenterVO gfp=this.getChatCenterBean(storeId);
        map.put("centerId",gfp.getId());
        map.put("storeId",storeId);
        map.put("status",GoodFriendPlayRoomStatusEnum.status0.getCode());
        map.put("roomOwner",TokenUtil.getUserId(token));
        map.put("roomOwnerOpenId",TokenUtil.getUserObject(token));
        String groupId=storeId+"_"+TokenUtil.getUserId(token)+"_"+ RandomStringUtils.randomNumeric(5);
        map.put("groupId",IMApiUtil.createGroup((String)TokenUtil.getUserObject(token),groupId,dto.getName()));
        ResponseVO<Integer> vo=this.commonInsertMap("ddw_goodfriendplay_room",map);
        Map paramMap=new HashMap();
        paramMap.put("userId",TokenUtil.getUserId(token));
        paramMap.put("roomId",vo.getData());
        paramMap.put("disabled",DisabledEnum.disabled0.getCode());
        paramMap.put("createTime",new Date());
        paramMap.put("updateTime",new Date());
        paramMap.put("roomOwner",TokenUtil.getUserId(token));
        paramMap.put("joinOffLine",JoinOffLineStatusEnum.Status1.getCode());
        paramMap.put("openId",TokenUtil.getUserObject(token));
        this.commonInsertMap("ddw_goodfriendplay_room_member",paramMap);
        Map mapret=new HashMap();
        mapret.put("roomCode",vo.getData());
        mapret.put("groupId",groupId);
        TokenUtil.putRoomId(token,vo.getData());
        return new ResponseApiVO(1,"成功",mapret);
    }
    public ResponseApiVO goIntoMyRoom(String token){

       Map map= getCurrentRoom(token);
       if(map==null){
           return new ResponseApiVO(-2,"抱歉，没找到你所在的房间",null);

       }

        return new ResponseApiVO(1,"成功",map);
    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseApiVO createOffLinePlay(String token,GoodFriendPlayTableDTO dto)throws Exception{
        Map callMap=checkRoom(token);
        if(callMap==null){
            return new ResponseApiVO(-2,"小房不存在",null);
        }
        Integer status=(Integer) callMap.get("status");
        //Integer roomId=BaseTokenUtil.getRoomId(token);

        if(GoodFriendPlayRoomStatusEnum.status1.getCode().equals(status)){
            return new ResponseApiVO(-2,"已经约玩开桌中",null);
        }else if(GoodFriendPlayRoomStatusEnum.status20.getCode().equals(status)){
            return new ResponseApiVO(-2,"审核中",null);
        }
        Map tableMap=null;
        if(dto.getTableCode()!=null && dto.getTableCode()>0){
            tableMap=getTableById(token,dto.getTableCode());
        }else{
            tableMap=getTableById(token,(Integer) callMap.get("tableCode"));

        }
        if(tableMap==null || tableMap.isEmpty()){
            return new ResponseApiVO(-2,"桌号code异常",null);
        }
        Integer tableStatus=(Integer) tableMap.get("status");
        if(TableStatusEnum.status1.getCode().equals(tableStatus)){
            return new ResponseApiVO(-2,"抱歉，桌号已经被使用",null);

        }
        ReviewPO reviewPO=new ReviewPO();
        reviewPO.setDrProposerName(TokenUtil.getUserName(token));
        reviewPO.setDrProposer(TokenUtil.getUserId(token));
        reviewPO.setDrBelongToStoreId(TokenUtil.getStoreId(token));
        reviewPO.setDrBusinessCode(callMap.get("id").toString());
        reviewPO.setDrBusinessType(ReviewBusinessTypeEnum.ReviewBusinessType10.getCode());
        reviewPO.setDrReviewStatus(ReviewStatusEnum.ReviewStatus0.getCode());
        reviewPO.setDrProposerType(ReviewProposerTypeEnum.ReviewProposerType1.getCode());
        reviewPO.setDrReviewerType(ReviewReviewerTypeEnum.ReviewReviewerType1.getCode());
        reviewPO.setDrApplyDesc("申请约玩开桌");
        reviewPO.setDrBusinessStatus(ReviewBusinessStatusEnum.goodFriendPlay20.getCode());
        reviewPO.setDrExtend("主题名-"+callMap.get("name")+",桌号-"+tableMap.get("tableNumber"));
        reviewPO.setCreateTime(new Date());
        this.commonReviewService.submitAppl(reviewPO);
        Map updateMap=new HashMap();
        updateMap.put("updateTime",new Date());
        updateMap.put("status",GoodFriendPlayRoomStatusEnum.status20.getCode());
        if(dto.getPeopleMaxNum()!=null && dto.getPeopleMaxNum()>0){
            updateMap.put("peopleMaxNum",dto.getPeopleMaxNum());
        }
        this.commonUpdateBySingleSearchParam("ddw_goodfriendplay_room",updateMap,"id",(Integer)callMap.get("id"));

        TokenUtil.putRoomId(token,(Integer)callMap.get("id"));
        return new ResponseApiVO(1,"成功",null);
    }
    private ResponseApiVO commonHandleUser(Integer userId,GoodFriendPlayOutUserDTO dto){
        if(dto==null || dto.getCode()==null || dto.getCode()<=0){
            return new ResponseApiVO(-2,"房间code异常",null);
        }
        if(StringUtils.isBlank(dto.getOpenId())){
            return new ResponseApiVO(-2,"请选择一个用户",null);
        }

        Map searchMap=new HashMap();
        searchMap.put("id",dto.getCode());
        searchMap.put("roomOwner",userId);
        Map map=this.commonObjectBySearchCondition("ddw_goodfriendplay_room",searchMap);
        if(map==null || map.isEmpty()){
            return new ResponseApiVO(-2,"房间还没建",null);
        }
        if(DisabledEnum.disabled1.getCode( ).equals(map.get("disabled"))){
            return new ResponseApiVO(-2,"房间已停用",null);
        }
        return new ResponseApiVO(1,"成功",null);
    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseApiVO disabledUserJoin(String token,GoodFriendPlayOutUserDTO dto)throws Exception{
        Integer userId=TokenUtil.getUserId(token);
        ResponseApiVO r=commonHandleUser(userId,dto);
        if(r.getReCode()!=1){
            return r;
        }
        Map search=new HashMap();
        search.put("roomOwner",userId);
        search.put("roomId",dto.getCode());
        search.put("openId",dto.getOpenId());
        Map update=new HashMap();
        update.put("disabled",DisabledEnum.disabled1.getCode());
        ResponseVO res=this.commonUpdateByParams("ddw_goodfriendplay_room_member",update,search);
        if(res.getReCode()==1){
            return new ResponseApiVO(1,"成功",null);
        }
        return new ResponseApiVO(-2,"失败",null);

    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseApiVO outUser(String token,GoodFriendPlayOutUserDTO dto)throws Exception{
        Integer userId=TokenUtil.getUserId(token);
        ResponseApiVO r=commonHandleUser(userId,dto);
        if(r.getReCode()!=1){
            return r;
        }
        Map deleteMap=new HashMap();
        deleteMap.put("roomOwner",userId);
        deleteMap.put("roomId",dto.getCode());
        deleteMap.put("openId",dto.getOpenId());
        deleteMap.put("openId,!=",TokenUtil.getUserObject(token).toString());
        ResponseVO res=this.commonDeleteByParams("ddw_goodfriendplay_room_member",deleteMap);
        if(res.getReCode()==1){
           return new ResponseApiVO(1,"成功",null);
        }
        return new ResponseApiVO(-2,"失败",null);

    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseApiVO join(String token,CodeDTO dto)throws Exception{
        if(dto==null || dto.getCode()==null || dto.getCode()<=0){
            return new ResponseApiVO(-2,"参数异常",null);
        }


        Integer userId=TokenUtil.getUserId(token);
        Map map=null;

        /*if(roomId==null){
            searchRoom.put("id",dto.getCode());

        }else{
            searchRoom.put("id,in","("+dto.getCode()+","+roomId+")");
        }*/
        Map searchRoom=new HashMap();
        searchRoom.put("status,in","(1,20)");
        Map userMap=new HashMap();
        userMap.put("userId",userId);
        CommonSearchBean csb=new CommonSearchBean("ddw_goodfriendplay_room",null,"t1.*,ct0.userId,ct0.disabled memDisabled",null,null,searchRoom,
                new CommonChildBean("ddw_goodfriendplay_room_member","roomId","id",userMap));
        csb.setJointName("left");
        List<Map> list=this.getCommonMapper().selectObjects(csb);

        if(list==null || list.isEmpty()){
            return new ResponseApiVO(-2,"小房间不存在",null);
        }
        Map oldRoom=null;
        for(Map m:list){
            if(m.get("id").equals(dto.getCode())){
                map=m;
            }else if(m.containsKey("userId") && userId.equals(m.get("userId"))){
                oldRoom=m;
            }
        }
        if(oldRoom!=null){
            if(!DisabledEnum.disabled1.getCode().equals(oldRoom.get("disabled")) && !GoodFriendPlayRoomStatusEnum.status22.getCode().equals(oldRoom.get("status")) && !DisabledEnum.disabled1.getCode().equals(oldRoom.get("memDisabled"))){
                return new ResponseApiVO(-2,"没法加入，你已经参与到别的开桌！",null);
            }
        }

        if(map==null ||  map.isEmpty()){
            return new ResponseApiVO(-2,"小房间不存在",null);
        }
        Integer status=(Integer) map.get("status");
        Integer disabled=(Integer) map.get("disabled");
        Integer memDisabled=(Integer) map.get("memDisabled");
        Integer peopleMaxNum=(Integer) map.get("peopleMaxNum");
        Integer memberUserId=(Integer) map.get("userId");
        if(DisabledEnum.disabled1.getCode().equals(disabled)){
            return new ResponseApiVO(-2,"抱歉，房间已解散",null);

        }
        if(DisabledEnum.disabled1.getCode().equals(memDisabled)){
            return new ResponseApiVO(-2,"抱歉，你被禁止加入开桌",null);
        }
        if(GoodFriendPlayRoomStatusEnum.status20.getCode().equals(status)){


            Map countSearch=new HashMap();
            countSearch.put("disabled",DisabledEnum.disabled0.getCode());
            countSearch.put("roomId",dto.getCode());

            Long menberNum=this.commonCountBySearchCondition("ddw_goodfriendplay_room_member",countSearch);
            if(menberNum>=peopleMaxNum){
                return new ResponseApiVO(-2,"抱歉，人数已满",null);
            }
            if(memberUserId==null){
                Map paramMap=new HashMap();
                paramMap.put("userId",TokenUtil.getUserId(token));
                paramMap.put("roomId",dto.getCode());
                paramMap.put("disabled",DisabledEnum.disabled0.getCode());
                paramMap.put("createTime",new Date());
                paramMap.put("updateTime",new Date());
                paramMap.put("roomOwner",map.get("roomOwner"));
                paramMap.put("joinOffLine",JoinOffLineStatusEnum.Status1.getCode());
                paramMap.put("openId",TokenUtil.getUserObject(token));
                this.commonInsertMap("ddw_goodfriendplay_room_member",paramMap);
                TokenUtil.putRoomId(token,dto.getCode());
                return new ResponseApiVO(1,"成功",null);
            }else{

                return new ResponseApiVO(-2,"你已经加入开桌了，无需再加入",null);

            }
        }else if(GoodFriendPlayRoomStatusEnum.status1.getCode().equals(status)){
            return new ResponseApiVO(-2,"房间已开桌",null);
        }
        return new ResponseApiVO(-2,"房主还没申请开桌",null);
    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseApiVO dismissRoom(String token,CodeDTO dto)throws Exception{

        Map search=new HashMap();
        search.put("roomOwner",TokenUtil.getUserId(token));
        search.put("id",dto.getCode());
        search.put("disabled",DisabledEnum.disabled0.getCode());
        Map map=this.commonObjectBySearchCondition("ddw_goodfriendplay_room",search);
        if(map==null || map.isEmpty()){
            return new ResponseApiVO(-2,"抱歉，房间不存在或已经被解散了",null);

        }
        Integer status=(Integer) map.get("status");
        if(GoodFriendPlayRoomStatusEnum.status1.getCode().equals(status) || GoodFriendPlayRoomStatusEnum.status22.getCode().equals(status)){
            return new ResponseApiVO(-2,"抱歉，约战过的房间没法解散",null);

        }

        Map update=new HashMap();
        update.put("disabled",DisabledEnum.disabled1.getCode());
        update.put("updateTime",new Date());
        update.put("chatRoomEndTime",new Date());
        ResponseVO res=this.commonUpdateBySingleSearchParam("ddw_goodfriendplay_room",update,"id",dto.getCode());
        if(res.getReCode()!=1){
            return new ResponseApiVO(-2,"解散失败",null);
        }
        boolean flag=IMApiUtil.destoryGroup(map.get("groupId").toString());
        if(!flag){
            throw new GenException("解散群失败");
        }
        return new ResponseApiVO(1,"成功",null);


    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public  ResponseApiVO leave(String token,CodeDTO dto)throws Exception{
        Integer userId=TokenUtil.getUserId(token);
        if(dto.getCode()==null || dto.getCode()<=0){
            return new ResponseApiVO(-2,"缺少房间code参数",null);
        }
        Map map=this.commonObjectBySingleParam("ddw_goodfriendplay_room","id",dto.getCode());
        if(map==null || map.isEmpty()){
            return new ResponseApiVO(-2,"抱歉，房间不存在或已经被解散了",null);

        }
        Integer status=(Integer) map.get("status");
        if(GoodFriendPlayRoomStatusEnum.status1.getCode().equals(status) || GoodFriendPlayRoomStatusEnum.status22.getCode().equals(status)){
            return new ResponseApiVO(-2,"抱歉，约战过的开桌没法离开",null);

        }
        Map search=new HashMap();
        search.put("userId",userId);
        search.put("roomId",dto.getCode());
        search.put("disabled",DisabledEnum.disabled0.getCode());
        search.put("roomOwner,!=",userId);
        ResponseVO res=this.commonDeleteByParams("ddw_goodfriendplay_room_member",search);
        if(res.getReCode()!=1){
            return new ResponseApiVO(-2,"离开开桌失败",null);
        }
        TokenUtil.putRoomId(token,null);
        return new ResponseApiVO(1,"成功",null);

    }
    private Map checkRoom(String token)throws Exception{
        Integer userId=TokenUtil.getUserId(token);
        Map search=new HashMap();
        search.put("roomOwner",userId);
        search.put("status,<=",GoodFriendPlayRoomStatusEnum.status20.getCode());
        search.put("disabled",DisabledEnum.disabled0.getCode());
        List dataList=this.commonObjectsBySearchCondition("ddw_goodfriendplay_room",search);
        if(dataList!=null && dataList.size()>0){
            return (Map)dataList.get(0);
        }
        return null;
    }
    private boolean checkRoomMember(String token){
        Map map=getCurrentRoom(token);
        return map==null?false:true;
    }
    public Map getCurrentRoom(String token){
        Map search=new HashMap();
        search.put("disabled",DisabledEnum.disabled0.getCode());
        search.put("userId",TokenUtil.getUserId(token));
        Map roomS=new HashMap();
        roomS.put("status,<=",GoodFriendPlayRoomStatusEnum.status20.getCode());
        roomS.put("disabled",DisabledEnum.disabled0.getCode());
        CommonSearchBean csb=new CommonSearchBean("ddw_goodfriendplay_room_member",null,"ct0.id roomCode",null,null,search,
                new CommonChildBean("ddw_goodfriendplay_room","id","roomId",roomS));
        List list=this.getCommonMapper().selectObjects(csb);
        if(list==null || list.isEmpty() || list.size()==0){
            return null;
        }
        return (Map)list.get(0);
    }
    public Map getTableById(String token,Integer tableCode )throws  Exception{
        Integer storeId= TokenUtil.getStoreId(token);
        Map search=new HashMap();
        search.put("storeId",storeId);
        search.put("id",tableCode);
        Map m=this.commonObjectBySearchCondition("ddw_goodfriendplay_tables",search);
        return m;
    }
    public ResponseApiVO getTables(String token )throws  Exception{
        Integer storeId= TokenUtil.getStoreId(token);
        List list=this.commonObjectsBySingleParam("ddw_goodfriendplay_tables","storeId",storeId);
        if(list==null || list.isEmpty()){
            return new ResponseApiVO(2,"成功",new ArrayList<>());
        }
        return new ResponseApiVO(1,"成功",new ListVO<>(list));
    }
    public ResponseVO isOkCreateRoom(String token ){

        Integer userId=TokenUtil.getUserId(token);

        List<Map> list=this.goodFriendPlayMapper.selectIsCanCreateRoom(userId);
        if(list!=null && !list.isEmpty()){
            for(Map m:list){
                if(DisabledEnum.disabled0.getCode().equals(m.get("memDisabled"))){
                    if(userId.equals(m.get("roomOwner"))){
                        return new ResponseVO(-3,"抱歉，不能创建多个房间",null);
                    }else{
                        return new ResponseVO(-2,"抱歉，请离开别的开桌再创建",null);

                    }

                }
            }

        }
        return new ResponseVO(1,"成功",null);



    }
}
