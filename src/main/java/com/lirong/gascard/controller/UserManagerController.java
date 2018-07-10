package com.lirong.gascard.controller;

import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lirong.gascard.domain.*;
import com.lirong.gascard.service.LoginService;
import com.lirong.gascard.service.MenuService;
import com.lirong.gascard.service.PayService;
import com.lirong.gascard.service.UserManagerService;
import com.lirong.gascard.vo.PageResultForBootstrap;
import net.sf.json.JSONArray;
import org.apache.catalina.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: daimengying
 * @Date: 2018/5/21 18:43
 * @Description:用户管理，包括管理员管理和代理商管理
 */
@EnableAsync
@Controller
@RequestMapping("/userManager")
public class UserManagerController extends BaseController{
    private Logger _logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserManagerService userManagerService;

    @Autowired
    MenuService menuService;

    @Autowired
    LoginService loginService;

    @Autowired
    PayService payService;

    /**
     * 管理员管理首页
     *  @param req
     * @return
     */
    @RequestMapping("/toAdminManager")
    public String toAdminManager(HttpServletRequest req){
        menuTreeToSession(req);
        return "/userManager/adminManager";
    }

    /**
     * 代理商管理首页
     * @return
     */
    @RequestMapping("/toAgentManager")
    public String toAgentManager(HttpServletRequest request){
        menuTreeToSession(request);
        return "/userManager/agentManager";
    }

    @RequestMapping(value="/genApiKey",method= RequestMethod.POST)
    @ResponseBody
    public JSONObject genApiKey(){
        JSONObject result = new JSONObject();

        byte[] encoded = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue()).getEncoded();
        String apikey = HexUtil.encodeHexStr(encoded);
        if(!StringUtils.isEmpty(apikey)){
            result.put("success", true);
            result.put("key", apikey);
        }else{
            result.put("success", false);
        }
        return result;
    }


    /**
     * 用户表格
     * @param params
     * @return
     */
    @RequestMapping(value="/userTable",method= RequestMethod.POST)
    @ResponseBody
    public PageResultForBootstrap userTable(@RequestBody String params){
        JSONObject parObject= JSON.parseObject(params);

        Map<String,Object>totalParam=new HashMap<>();
        totalParam.put("roleType",parObject.getString("roleType"));
        totalParam.put("username",parObject.getString("username"));
        totalParam.put("name",parObject.getString("name"));

        List<Users>adminTable=userManagerService.getUserListByExampleAndPage(parObject);
        PageResultForBootstrap page = new PageResultForBootstrap();
//        if(adminTable!=null&&adminTable.size()>0){
            //Gson把list中的Date格式转为时间
//            Gson gson =new GsonBuilder().setDateFormat("yyyy-MM-dd hh:mm:ss").create();
//            String adminTableString=gson.toJson(adminTable);
//            List<Map<String,String>> adminTableList = gson.fromJson(adminTableString, new TypeToken<List<Map<String,String>>>() {}.getType());
//            page.setRows(adminTableList);
//            page.setTotal(userManagerService.getCountByExample(totalParam));
//        }
        page.setRows(adminTable);
        page.setTotal(userManagerService.getCountByExample(totalParam));
        return page;
    }

    /**
     * 新增用户
     * @param
     * @return
     */
    @RequestMapping("/toAddUser")
    public String toAddUser(Model model, @RequestParam(value="userId",required=false)String userId,@RequestParam(value="roleType")Integer roleType){

        if(StrUtil.isNotBlank(userId)){
            Integer userIdInt=Integer.parseInt(userId);
            try {
                Users admin=userManagerService.getUserById(userIdInt);
            }catch (Exception e){
                e.printStackTrace();
            }
            model.addAttribute("admin",userManagerService.getUserById(userIdInt));
            //通过userid获取已勾选的菜单
            List<UserMenu>getHasMenu=menuService.getMenuListByUserId(userIdInt);
            /*for(UserMenu item : getHasMenu){

            }*/
            model.addAttribute("getHasMenu", JSONArray.fromObject(getHasMenu));

        }
        //获取所有菜单列表，用作菜单树展示
        List<Menu>getAllMenu=menuService.getMenuList();
        model.addAttribute("allMenu", JSONArray.fromObject(getAllMenu));
        if(roleType==2){
            return "/userManager/addAdmin";
        }else if(roleType==3){
            return "/userManager/addAgent";
        }
        return "";
    }

    @RequestMapping(value="/addOrEditAdmin",method= RequestMethod.POST)
    @ResponseBody
    public String addOrEditAdminLogic(HttpServletRequest request, @ModelAttribute Users user, @RequestParam(value="userMenuIds",required=false)List<Integer>userMenuIds){
        JSONObject result = new JSONObject();
        if(user.getId() !=null && user.getId().intValue() > 0){
            //更新用户信息。根据username查到对应记录，做更新操作
            List<Users>users=loginService.getUserByUserName(user.getUsername());
            if(users!=null && users.size()>0){
                user.setId(users.get(0).getId());
            }
            try {
                userManagerService.updateUserByPK(user);
                /**更新用户菜单表*/
                if(userMenuIds!=null&&userMenuIds.size()>0){
                    //清空该用户的所有用户菜单关系
                    userManagerService.deleteUserMenu(user.getId() );
                    for(Integer menuId:userMenuIds){
                        //插入新的用户菜单关系
                        UserMenu userMenu=new UserMenu();
                        userMenu.setMenuId(menuId);
                        userMenu.setUserId(user.getId());
                        userManagerService.addUserMenu(userMenu);
                    }
                }
                result.put("success", true);
            }catch (Exception e){
                result.put("success", false);
                _logger.error("【权限管理模块】提交用户信息异常，错误信息：" + e.getMessage());
            }

        }else{
            Users currentUser = super.getCurrentUser(request);
            user.setAgent(currentUser.getUsername());
            user.setCreateTime(new Date());
            userManagerService.addUser(user);
            result.put("success", true);
        }
        return result.toJSONString();
    }

    /**
     *新增代理商
     * @param userId
     * @return
     */
    @RequestMapping("/toAddAgent")
    public String toAddAgent(@RequestParam(value="userId",required=false)Integer userId){
        return "/userManager/addAgent";
    }


    /**
     * 批量删除
     * @return
     */
    @RequestMapping(value="/batchDeleteUserAjax",method= RequestMethod.POST)
    @ResponseBody
    public JSONObject batchDeleteUserAjax(@RequestParam(value="userIds",required=false) String[] userIds){//
        JSONObject result=new JSONObject();
        try{
            String[] returnUserIds=new String[userIds.length];
            int j=0;
            for(int i=0;i<userIds.length;i++){
                Integer deleteUser=userManagerService.deleteUser(Integer.parseInt(userIds[i]+""));
                if(deleteUser>0){
                    returnUserIds[j]=userIds[i]+"";
                    j++;
                }
            }
            if(returnUserIds.length>0&&returnUserIds!=null){
                result.put("resultCode","0");
                result.put("resultData",returnUserIds.length);
            }else{
                result.put("resultCode","-1");
            }
        }catch (Exception e){
            result.put("resultCode","-1");
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 给代理商授信
     * @param request
     * @param id
     * @param creditFacility
     * @return
     */
    @RequestMapping("/addCredit")
    @ResponseBody
    public JSONObject addCredit( HttpServletRequest request,
                             @RequestParam(value="id") Integer id,
                             @RequestParam(value="creditFacility") Double creditFacility ){
        JSONObject result = new JSONObject();
        if(null == id || null == creditFacility){
            result.put("success",false);
        }else{
            Users user=new Users();
            user.setId(id);
            user.setCreditFacility(creditFacility);
            try {
                userManagerService.updateUserByPK(user);
                //记录管理员操作日志
                Users currentUser=super.getCurrentUser(request);
                AdminLog adminLog = new AdminLog();
                adminLog.setAccount(currentUser.getUsername());
                adminLog.setTitle("授信");
                adminLog.setDesctext("设置代理 ID:"+id+"额度"+creditFacility);
                adminLog.setIp(request.getRemoteAddr());
                adminLog.setOptionTime(new Date());
                userManagerService.addAdminLog(adminLog);
                result.put("success",true);
            }catch (Exception e){
                result.put("success",false);
                _logger.error("【权限管理模块】代理商授信异常，错误信息：" + e.getMessage());
            }

        }
        return result;
    }

    /**
     * 代理商充值页面
     * @param model
     * @param userId
     * @return
     */
    @RequestMapping("/toAgentCharge")
    public String toAgentCharge(Model model,@RequestParam(value="userId")Integer userId){
        model.addAttribute("agent",userManagerService.getUserById(userId));
        return "/userManager/agentCharge";
    }

    /**
     * 代理商充值逻辑
     * @param request
     * @param agentChargeLog
     * @return
     */
    @RequestMapping("/agentCharge")
    @ResponseBody
    public JSONObject agentCharge(HttpServletRequest request,@ModelAttribute AgentChargeLog agentChargeLog) {
        JSONObject result = new JSONObject();
        Users currentUser=super.getCurrentUser(request);
        Users agent = userManagerService.getUserById(agentChargeLog.getUserId());
        agentChargeLog.setAgent(agent.getAgent());
        agentChargeLog.setAccount(agent.getUsername());
        agentChargeLog.setOptionUser(currentUser.getUsername());
        Integer addChargeLog = payService.addAgentChargeLog(agentChargeLog);
        if(addChargeLog>0){
            result.put("success", true);
            result.put("money", agentChargeLog.getMoney());
            //异步事件。记录到消费明细表中
            PayLog payLog=new PayLog();
            payLog.setUserId(agent.getId());
            payLog.setAccount(agentChargeLog.getAccount());
            payLog.setAgent(agentChargeLog.getAgent());
            payLog.setMoney(agentChargeLog.getMoney());
            payLog.setBalance(agent.getBalance()+agentChargeLog.getMoney());
            payLog.setType(1);

            StringBuilder memo = new StringBuilder();
            memo.append("为代理商");
            memo.append(agentChargeLog.getAccount());
            memo.append("冲扣值");
            memo.append(agentChargeLog.getMoney());
            memo.append("元");
            memo.append("备注：");
            int i = agentChargeLog.getMemo().indexOf("<");
            if(i > -1){
                String mark = agentChargeLog.getMemo().substring(0, i);
                memo.append(mark);
            }else{
                memo.append(agentChargeLog.getMemo());
            }
            payLog.setMemo(memo.toString());
            payService.addPay(payLog);
            //异步事件。更新用户余额
            agent.setBalance(agent.getBalance()+agentChargeLog.getMoney());
            userManagerService.updateAgentBalance(agent);

        }else{
            result.put("success", false);
        }
        return result;
    }

}
