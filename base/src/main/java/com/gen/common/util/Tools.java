package com.gen.common.util;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 工具计算类
 * 
 * @author cancheung
 *
 */
public class Tools {
	private static final Logger logger = Logger.getLogger(Tools.class);

	private static ThreadLocal<String> openid=new ThreadLocal();
	private static WebApplicationContext wa;

	/**
	 * 分析值，为空或错误时使用默认值
	 * 
	 * @param value
	 * @param defValue
	 * @return
	 */
	private Double parseByDefault(Object value, Double defValue) {
		if (null != value) {
			try {
				return Double.parseDouble(value.toString());
			} catch (Exception e) {
			}
		}
		if (null != defValue) {
			return defValue;
		} else {
			return 0.0;
		}
	}

	/**
	 * 两数相加，出错使用0
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public Number add(Object a, Object b) {
		double aValue = parseByDefault(a, 0.0);
		double bValue = parseByDefault(b, 0.0);
		return aValue + bValue;
	}

	/**
	 * 两数相减，出错使用0
	 * 
	 * @param o1
	 * @param o2
	 * @return
	 */
	public Number diff(Object o1, Object o2) {
		double aValue = parseByDefault(o1, 0.0);
		double bValue = parseByDefault(o2, 0.0);
		return aValue - bValue;
	}

	public String diffDelZero(Object o1, Object o2) {
		return deleteZero(diff(o1, o2));
	}

	/**
	 * 两数相除
	 * 
	 * @param o1
	 * @param o2
	 * @return
	 */
	public Number divided(Object o1, Object o2) {
		double aValue = parseByDefault(o1, 0.0);
		double bValue = parseByDefault(o2, 0.0);
		if (bValue == 0) {
			return 0;
		}
		return aValue / bValue;
	}

	/**
	 * 
	 * @param o
	 * @return
	 */
	public String deleteZero(Object o) {
		DecimalFormat f = new DecimalFormat("0.##");
		return f.format(parseByDefault(o, 0.0));
	}

	/**
	 * 字符串转日期
	 * 
	 * @param dateTime
	 * @param format
	 * @return
	 */
	public static Date stringToDate(String dateTime, String format) {
		Date date = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			date = sdf.parse(dateTime);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;
	}

	public static Date stringToDate(String dateTime) {
		String format = "yyyy-MM-dd";
		if (dateTime.length() == "yyyy-MM-dd HH:mm:ss".length()) {
			format = "yyyy-MM-dd HH:mm:ss";
		}
//		else if (dateTime.length() == "yyyy-MM-dd".length()) {
//			format = "yyyy-MM-dd";
//		}
		return stringToDate(dateTime, format);
	}

	/**
	 * 两个date相差的毫秒
	 * 
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static long dateGetTime(Date d1, Date d2) {
		return (Math.abs(d1.getTime() - d2.getTime()));
	}

	/**
	 * 获取满标用时
	 * 
	 * @param startDate
	 * @param CompleteDate
	 * @return
	 */
	public String getFullUseTime(Date startDate, Date CompleteDate) {
		if (CompleteDate == null) {
			return "-时-分-秒";
		} else {
			long l = dateGetTime(startDate, CompleteDate);
			long day = l / (24 * 60 * 60 * 1000);
			long hour = (l / (60 * 60 * 1000) - day * 24);
			long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
			long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
			System.out.println("" + day + "天" + hour + "小时" + min + "分" + s + "秒");
			if (day > 0) {
				return day + "天" + hour + "时" + min + "分" + s + "秒";
			} else {
				return hour + "时" + min + "分" + s + "秒";
			}
		}
	}

	public String getDeadLineStr(Integer typeId, Integer deadLine, Integer deadType, Integer isRepayAd, Date addDate,
			Date returnDate) {
		int projectType = typeId == null ? 0 : typeId;
		if (projectType == 23)// 浮动项目宝
		{
			if (isRepayAd != null && isRepayAd == 1) {
				// 算是提前还款时
				long l = dateGetTime(returnDate, addDate);
				long diffDay = l / (24 * 60 * 60 * 1000) + 1;
				if (diffDay <= 31)
					return diffDay + "天";
				else {
					long diffMonth = l / (31 * 24 * 60 * 60 * 1000);
					return diffMonth + "个月" + (diffDay % 31) + "天";
				}

			} else {
				return deadLine + (deadLine == 2 ? " 天" : " 个月");
			}
		} else if (projectType == 21) { // 活期宝
			return "活期";
		} else {
			return deadLine + (deadLine == 2 ? " 天" : " 个月");
		}
	}
	
	public static String subtract(Date date,Date date2){
		if(date==null)return "-"+date2.getTime()/(24*60*60*1000)+"天";
		if(date2==null) return date.getTime()/(24*60*60*1000)+"天";
		long times = date.getTime()-date2.getTime();
		return (times>=0?"":"-")+ (times>=0?times/(24*60*60*1000):-times/(24*60*60*1000))+"天";
	}
	public static boolean isLogin(){
		ServletRequestAttributes attrs =  (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpSession session=attrs.getRequest().getSession();

		if(session.getAttribute("user")==null){
			return false;
		}
		return true;
	}

	public static Object getSession(String key){
		ServletRequestAttributes attrs =  (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpSession session=attrs.getRequest().getSession();
		return session.getAttribute(key);
	}
	public static void setSession(String key,Object obj){


		ServletRequestAttributes attrs =  (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpSession session=attrs.getRequest().getSession();

		/*HttpServletResponse response = attrs.getResponse();
		Cookie cookie = new Cookie(key, value);
		*//*if (StringUtils.isNotBlank(domain)) {
			cookie.setDomain(domain);
		}*//*

		cookie.setPath("/");
		response.addCookie(cookie); */
		session.setAttribute(key,obj);
	}
	public static boolean setParentSession(Integer id){


		setSession("parentId",id);
		return true;
	}
	public static Integer getParentId(){
		ServletRequestAttributes attrs =  (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpSession session=attrs.getRequest().getSession();
		return (Integer)session.getAttribute("parentId");


	}

	public static String getOpenidByThreadLocal(){
		String openidstr=openid.get();
		return openidstr;
	}
	public static String setOpenidByThreadLocal(String token){
		if(StringUtils.isBlank(token))return null;
		String value=MyEncryptUtil.getRealValue(token);
		if(StringUtils.isBlank(value)){
		return null;
		}
		openid.set(value);
		return value;
	}
	public static void clearLoginSession(){
		ServletRequestAttributes attrs =  (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpSession session=attrs.getRequest().getSession();
		session.removeAttribute("user");
		session.removeAttribute("userPower");
		session.invalidate();
	}

	public static String getRealAmount(String input){

		String newInput= StringUtils.isNotBlank(input)?input:"0";
		if(newInput.length()==1){
			newInput="0.0"+newInput;
		}else if(newInput.length()==2){
			newInput="0."+newInput;
		}else{
			newInput=newInput.replaceAll("^([0-9]+)([0-9][0-9])$","$1.$2");
		}
		return newInput;
	}
	public static void noCachePage(){
		ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletResponse response=attrs.getResponse();
		response.addHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
		// 设置 HTTP/1.1 no-cache 头
		response.addHeader("Cache-Control", "no-store,no-cache,must-revalidate,post-check=0,no-transform");
		// 设置 IE 扩展 HTTP/1.1 no-cache headers， 用户自己添加
		// 设置标准 HTTP/1.0 no-cache header.
		response.addHeader("Pragma", "no-cache");


	}
	/*public static String initJssdk(String url,String jsApiList){

		if(StringUtils.isBlank(url)){
			ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
			HttpServletRequest request=attrs.getRequest();
			url=request.getRequestURL().append("?").append(request.getQueryString()).toString();
		}
		return WeiXinTools.initJssdk(url,jsApiList);
	}*/
	public static void setCookie(String key,String value){
		ServletRequestAttributes attrs =  (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();


		HttpServletResponse response = attrs.getResponse();
		HttpServletRequest request=attrs.getRequest();
		try {

			Cookie cookie=new Cookie(key,value);
			cookie.setPath("/");
			response.addCookie(cookie);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static String getCookie(String key){
		ServletRequestAttributes attrs =  (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request=attrs.getRequest();
		try {
			Cookie[] cookies=request.getCookies();
			if(cookies!=null && cookies.length>0){
				for(Cookie c:cookies){
					if(c.getName().equals(key)){
						return c.getValue();


					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}
	public static String encrypt(String input){
		return MyEncryptUtil.encry(input);
	}
	public static String encryptRandom(){
		return MyEncryptUtil.encry(RandomStringUtils.randomNumeric(10));
	}

	public static boolean checked(String tagidList,String id){
        boolean res = false;
        if(StringUtils.isBlank(tagidList))return false;
        if(tagidList.indexOf(",")==-1 && tagidList.equals(id)){
            res = true;
        }else{
            for(String s:tagidList.split(",")){
                if(s.equals(id)){
                    res = true;
                    break;
                }
            }
        }
        return res;
    }


	public static boolean isNumberAndEnglishStr(String input) {
		if(NumberUtils.isNumber(input)){
			return false;
		}
		return NumberUtils.isNumber(input.replaceAll("(?i)[a-z]", ""));
	}
	public static WebApplicationContext getWebapplication(){
		if(wa==null){
			ServletRequestAttributes attrs =  (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
			wa= WebApplicationContextUtils.getWebApplicationContext(attrs.getRequest().getServletContext());
		}
		return wa;

	}
	public static <T>T getBean(Class<T> clazz){
		return getWebapplication().getBean(clazz);
	}
	public static String getIpAddr() {
		ServletRequestAttributes attrs =  (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request=attrs.getRequest();
		String ip = request.getHeader("x-forwarded-for");
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
			if(ip.equals("127.0.0.1")){
				//根据网卡取本机配置的IP
				InetAddress inet=null;
				try {
					inet = InetAddress.getLocalHost();
				} catch (Exception e) {
					e.printStackTrace();
				}
				ip= inet.getHostAddress();
			}
		}
		// 多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
		if(ip != null && ip.length() > 15){
			if(ip.indexOf(",")>0){
				ip = ip.substring(0,ip.indexOf(","));
			}
		}
		return ip;
	}
	public static <T> T xmlCastObject(String xmlStr,Class<T> tClass){
		try {
			Document doc=DocumentHelper.parseText(xmlStr);
			Element root=doc.getRootElement();

			Object obj=tClass.newInstance();
			List<Element> el=root.elements();
			Map map=new HashMap();
			for(Element e:el){
				map.put(e.getName(),e.getTextTrim());
			}

			PropertyUtils.copyProperties(obj,map);
			return (T)obj;
		}catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}
	public static Map xmlCastMap(String xmlStr){
		try {
			Document doc=DocumentHelper.parseText(xmlStr);
			Element root=doc.getRootElement();

			List<Element> el=root.elements();
			Map map=new HashMap();
			for(Element e:el){
				map.put(e.getName(),e.getTextTrim());
			}

			return map;
		}catch (Exception e){
			logger.error("xmlCastMap->"+xmlStr,e);
		}
		return null;
	}

    public static String getGameName(int id,List<Map> gameList){
        for(Map map :gameList){
            if(map.containsKey("id") && map.containsKey("gameName") && (Integer)map.get("id") == id){
                return (String)map.get("gameName");
            }
        }
        return null;
    }

    public static String getRankName(int id, List<Map>rankList){
        for(Map map :rankList){
            if(map.containsKey("id") && map.containsKey("rank") && (Integer)map.get("id") == id){
                return (String)map.get("rank");
            }
        }
        return null;
    }

	public static String getGradeName(int id, List<Map>gradeList){
		for(Map map :gradeList){
			if(map.containsKey("id") && map.containsKey("gradeName") && (Integer)map.get("id") == id){
				return (String)map.get("gradeName");
			}
		}
		return null;
	}
	public static String getSurplusTimeStr(Date currentDate,Date startTime){

		long l=currentDate.getTime()-startTime.getTime();
		BigDecimal v= BigDecimal.valueOf(l).divide(BigDecimal.valueOf(1000)).divide(BigDecimal.valueOf(60),2,BigDecimal.ROUND_DOWN);
		long part=(long)v.doubleValue();
		BigDecimal point=v.subtract(BigDecimal.valueOf(part)).multiply(BigDecimal.valueOf(60));
		StringBuilder builder=new StringBuilder();
		if(part>60){
			BigDecimal hv= BigDecimal.valueOf(part).divide(BigDecimal.valueOf(60),2,BigDecimal.ROUND_DOWN);
			long h=(long)hv.doubleValue();
			if(h>0){
				builder.append(h).append("小时");
			}
			int m=hv.subtract(BigDecimal.valueOf(h)).multiply(BigDecimal.valueOf(60)).intValue();
			if(m>0){
				builder.append(m).append("分");
			}
			builder.append(point.intValue()).append("秒");

		}else{
			if(part>0){
				builder.append(part).append("分");
			}
			builder.append(point.intValue()).append("秒");

		}
		return builder.toString();

	}

    public static void main(String[] args) {
		Map map=xmlCastMap("<xml><appid><![CDATA[wx0c0f25470c893a03]]></appid>\n" +
				"<bank_type><![CDATA[CFT]]></bank_type>\n" +
				"<cash_fee><![CDATA[1]]></cash_fee>\n" +
				"<fee_type><![CDATA[CNY]]></fee_type>\n" +
				"<is_subscribe><![CDATA[N]]></is_subscribe>\n" +
				"<mch_id><![CDATA[1503574001]]></mch_id>\n" +
				"<nonce_str><![CDATA[IcOmddqsyeLfpAUDCO2L]]></nonce_str>\n" +
				"<openid><![CDATA[omc2C0kG_Y6icQ9GAEtbzsGESbME]]></openid>\n" +
				"<out_trade_no><![CDATA[01201805172042270301000000000110]]></out_trade_no>\n" +
				"<result_code><![CDATA[SUCCESS]]></result_code>\n" +
				"<return_code><![CDATA[SUCCESS]]></return_code>\n" +
				"<sign><![CDATA[FFF35842C05A475DBF5AAA4D38329757]]></sign>\n" +
				"<time_end><![CDATA[20180517204236]]></time_end>\n" +
				"<total_fee>1</total_fee>\n" +
				"<trade_type><![CDATA[APP]]></trade_type>\n" +
				"<transaction_id><![CDATA[4200000110201805172122566582]]></transaction_id>\n" +
				"</xml>");
		System.out.println(map);
		System.out.println(map!=null && "SUCCESS".equals(map.get("return_code"))&& "SUCCESS".equals(map.get("result_code")) );
	}
}