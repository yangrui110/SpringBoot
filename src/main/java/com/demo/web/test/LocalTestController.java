package com.demo.web.test;

import com.demo.config.advice.BaseException;
import com.demo.web.dao.mysql.ZhiyuDao;
import com.demo.web.service.UserService;
import com.demo.web.util.file.xls.ExcelWriter;
import com.demo.web.util.http.HeaderType;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @autor 杨瑞
 * @date 2019/5/11 13:52
 */
@Controller
@RequestMapping("localTest")
public class LocalTestController {

    //@Autowired
    private UserService userService;

    //@Autowired
    private ZhiyuDao zhiyuDao;
    @ResponseBody
    @GetMapping("ok")
    public Object ok(){
        try{
            int l=1/0;
        }catch (Exception e){
            throw new BaseException(500,"除以0错误");
        }
        return userService.finds(new HashMap());
    }

    //
    @ResponseBody
    @GetMapping("exportZhiyuUser")
    public Object export(HttpServletResponse response) throws IOException {
        List<Map<String,Object>> ls=zhiyuDao.findZhiyuUser();
        Map map=new HashMap();
        map.put("id", "编号");
        map.put("username", "用户名");
        map.put("nickname", "微信昵称");
        map.put("realname", "真实姓名");
        map.put("mobile", "手机号码");
        map.put("certificate_no", "证件号码");
        map.put("risk_score", "最新风险测评得分");
        map.put("sex", "性别");
        map.put("source", "客户来源");
        map.put("certificate", "证件类型ID");
        map.put("riskLevel", "最新用户风险类型");
        map.put("email", "电子邮箱");
        map.put("birthday", "生日");
        map.put("company", "公司名称");

        map.put("telephone", "固定电话");
        map.put("address", "通讯地址");
        map.put("postcode", "邮政编码");
        map.put("remarks", "备注");
        map.put("qudao", "渠道");
        map.put("manageName", "客户经理");
        HSSFWorkbook workbook=ExcelWriter.writeIntoOneSheet("用户数据表", ls, map);
        String name="用户数据表.xls";
        HeaderType.setResponseFile(name, response);
        OutputStream os = response.getOutputStream();
        workbook.write(os);
        os.flush();
        os.close();
        return "";
    }

    @ResponseBody
    @GetMapping("exeportProducts")
    public Object exportProducts(HttpServletResponse response) throws IOException {
        List<Map<String,Object>> ls=zhiyuDao.findProducts();
        Map map=new HashMap();
        map.put("id", "产品编号");
        map.put("push_channel", "推送渠道");
        map.put("proAuth", "产品权限");
        map.put("netValueAuth", "净值权限");
        map.put("reportAuth", "报告权限");
        map.put("rasiLevel", "风险等级");
        map.put("filing_code", "基金编码");
        map.put("raise_status", "募集状态");
        map.put("receive", "接受预约");
        map.put("pro_name", "产品名称");
        map.put("investmentType", "投资类型");
        map.put("investment_adviser", "投资顾问");
        map.put("threshold", "认购门槛");
        map.put("pro_admin", "管理人");
        map.put("trusteeship_mechanism", "托管机构");
        map.put("add_threshold", "追加门槛");
        map.put("issue_start_date", "发行开始日");
        map.put("issue_end_date", "发行结束日");
        map.put("pro_establish_date", "产品成立日");
        map.put("bizhong", "币种");
        map.put("closure_period", "封闭期");
        map.put("pro_term", "产品期限");
        map.put("promotion_start_date", "推介起始日");
        map.put("promotion_end_date", "推介结束日");
        map.put("open_subscription_date", "开放申购日");
        map.put("open_redemption_date", "开放赎回日");
        map.put("baseName", "业绩比较基准");
        map.put("jijinType", "基金类型");
        map.put("subscription_fee", "认购费");
        map.put("redemption_fee", "赎回费");
        map.put("establish_fee", "设立费");
        map.put("fixed_management_fee", "固定管理费");
        map.put("floating_management_fee", "浮动管理费");
        map.put("custodian_fee", "托管费");
        map.put("operation_service_fee", "运营服务费");
        map.put("other_fee", "其他");
        map.put("investment_direction", "投资方向");
        map.put("investment_scope", "投资范围");
        map.put("investment_strategy", "投资策略");
        map.put("risk_management", "风险控制");
        map.put("recommended_reason", "推荐理由");
        map.put("sharing_mode", "收益亏损分担方式");
        map.put("abnormal_liquidation", "非正常清盘");
        map.put("performance_compensation", "业绩报酬");
        HSSFWorkbook workbook=ExcelWriter.writeIntoOneSheet("产品信息表", ls, map);
        String name="产品信息表.xls";
        HeaderType.setResponseFile(name, response);
        OutputStream os = response.getOutputStream();
        workbook.write(os);
        os.flush();
        os.close();
        return "";
    }

    /**
     * 分组获取净值数据
     * */
    @ResponseBody
    @GetMapping("exportNetValue")
    public Object getNetValue(HttpServletResponse response) throws IOException {

        //获取产品分组
        List<Map<String,Object>> lists=zhiyuDao.findNetValue();
        Map map=new HashMap();
        map.put("release_date", "创建时间");
        map.put("net_value", "单位净值");
        map.put("base_net_value", "基准净值");
        map.put("accumulated_net_value", "累计净值");
        HSSFWorkbook workbook=new HSSFWorkbook();
        lists.forEach((k)->{
            Object os=k.get("pro_name");
            if(os!=null){
                String name= (String) os;
                List<Map<String,Object>> results=zhiyuDao.findNetValueByName(name);

                ExcelWriter.makeNewSheet(name, results,map , workbook);
            }
        });
        String name="产品净值表.xls";
        HeaderType.setResponseFile(name, response);
        OutputStream os = response.getOutputStream();
        workbook.write(os);
        os.flush();
        os.close();
        return "";
    }

    /**
     * 报告文件地址
     * */

    @ResponseBody
    @GetMapping("exportReport")
    public Object getProFile(HttpServletResponse response) throws IOException {
        List<Map<String,Object>> lists=zhiyuDao.findProFile();
        Map map=new HashMap();
        map.put("report_name","报告名称");
        map.put("report_release_date","发布日期");
        map.put("download_url","文件下载地址");
        HSSFWorkbook workbook=new HSSFWorkbook();
        lists.forEach((k)->{
            Object os=k.get("pro_name");
            if(os!=null){
                String name= (String) os;
                List<Map<String,Object>> results=zhiyuDao.findProFileByName(name);

                ExcelWriter.makeNewSheet(name, results,map , workbook);
            }
        });
        String name="产品文件表.xls";
        HeaderType.setResponseFile(name, response);
        OutputStream os = response.getOutputStream();
        workbook.write(os);
        os.flush();
        os.close();
        return "";
    }

    /**
     * 导出订单信息
     * */
    @ResponseBody
    @GetMapping("exportOrder")
    public Object exportOrder(HttpServletResponse response) throws IOException {
        List<Map<String,Object>> ls=zhiyuDao.findOrder();
        Map map=new HashMap();
        map.put("id", "订单编号");
        map.put("reservation_id", "预约订单");
        map.put("pro_name", "产品名称");
        map.put("orderStatus", "订单状态");
        map.put("username", "客户姓名");
        map.put("initial_amount", "初始认购金额");
        map.put("initial_pay_time", "初始打款时间");
        map.put("subscription_fee", "认购费");
        map.put("initial_share", "初始认购份额");
        map.put("remarks", "备注");
        map.put("qudao", "渠道");
        map.put("manageName", "客户经理");
        map.put("tuisong", "是否推送");
        map.put("custSource", "客户来源");
        map.put("orderSource", "订单来源");
        map.put("moneyUnit", "初始认购金额单位");

        HSSFWorkbook workbook=ExcelWriter.writeIntoOneSheet("订单信息表", ls, map);
        String name="订单信息表.xls";
        HeaderType.setResponseFile(name, response);
        OutputStream os = response.getOutputStream();
        workbook.write(os);
        os.flush();
        os.close();
        return "";
    }

    /**
     * 导出订单记录表
     * */
    @ResponseBody
    @GetMapping("exportOrderRecord")
    public Object exportOrderRecord(HttpServletResponse response) throws IOException {
        List<Map<String,Object>> ls=zhiyuDao.findOrderRecard();
        Map map=new HashMap();
        map.put("order_id","对应的订单编号" );
        map.put("recordType","记录类型" );
        map.put("share","记录份额" );
        map.put("amount","记录金额" );
        map.put("moneyUnit","金额单位" );
        map.put("date","记录时间" );

        HSSFWorkbook workbook=ExcelWriter.writeIntoOneSheet("订单记录表", ls, map);
        String name="订单记录表.xls";
        HeaderType.setResponseFile(name, response);
        OutputStream os = response.getOutputStream();
        workbook.write(os);
        os.flush();
        os.close();
        return "";
    }

    @ResponseBody
    @RequestMapping("dow")
    public void dow(HttpServletResponse response) throws IOException {
        HeaderType.setResponseFile("123.txt", response);
        OutputStream os = response.getOutputStream();
        os.write("ls".getBytes());
        //workbook.write(os);
        os.flush();
        os.close();
    }
}
