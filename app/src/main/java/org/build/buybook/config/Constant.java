package org.build.buybook.config;

/**
 * Created by Administrator on 2015/10/18.
 */
public class Constant {
    /**
     * 侧栏ListView的Item的个数
     */
    public static final int MENU_LIST_COUNT = 2;

    /**
     * 教务处ip地址:
     */
    public static final String URL_JWC = "202.199.64.11";

    /**
     * 教务处登陆界面:
     */
    public static final String URL_LOGIN_PAGE = "http://202.199.64.11/academic/index.jsp";
    /**
     * 验证码获取地址:
     */
    public static final String URL_LOGIN_VALIDATE = "http://202.199.64.11/academic/getCaptcha.do";
    /**
     * 检验账号密码以及证码是否正确的URL:
     */
    public static final String URL_LOGIN_CHECK = "http://202.199.64.11/academic/j_acegi_security_check";

    /**
     * 获取课表数据的URL:
     */
    public static final String URL_GET_COURSE_LIST="http://202.199.64.11/academic/accessModule.do?moduleId=2000&groupId=";

}
