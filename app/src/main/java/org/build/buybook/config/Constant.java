package org.build.buybook.config;

/**
 * Created by Administrator on 2015/10/18.
 */
public class Constant {

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
    public static final String URL_GET_COURSE_LIST = "http://202.199.64.11/academic/accessModule.do?moduleId=2000&groupId=";

    /**
     *个人信息列表URL:
     */
    public static final String URL_GET_USER_MSG="http://202.199.64.11/academic/accessModule.do?moduleId=2060&groupId=";
    /**
     * 服务器地址:
     */
    public static final String URL_Base = "http://101.200.208.215";

    /**
     * 获取课程列表
     * username-学号
     * course-json编码之后的课程列表(["工数","离散"])
     */
    public static final String URL_GET_BOOKS_LIST = URL_Base + "/BookManager/core/getBooks.php";

    /**
     * 订阅图书
     * username-学号
     * book_id-书的唯一标示
     * order-1表示预定，0表示退订
     */
    public static final String URL_GET_ORDER_BOOK = URL_Base + "/BookManager/core/orderbook.php";

    /**
     * 获取已定图书列表
     * username-学号
     */
    public static final String URL_GET_ORDERED_LIST = URL_Base + "/BookManager/core/orderlist.php";
}
