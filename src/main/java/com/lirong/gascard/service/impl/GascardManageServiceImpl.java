package com.lirong.gascard.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageRowBounds;
import com.lirong.gascard.aspect.CacheRemove;
import com.lirong.gascard.dao.*;
import com.lirong.gascard.domain.Gascard;
import com.lirong.gascard.domain.GascardPrice;
import com.lirong.gascard.domain.OutPrice;
import com.lirong.gascard.service.GascardManageService;
import com.lirong.gascard.vo.CardAndOutprice;
import com.lirong.gascard.vo.CardAndPrice;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: daimengying
 * @Date: 2018/5/29 10:16
 * @Description:油卡管理
 */
@Service
public class GascardManageServiceImpl implements GascardManageService {
    private Logger _logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private GascardMapper gascardMapper;

    @Resource
    private CardPriceMapper cardPriceMapper;

    @Resource
    private CardAndPriceMapper cardAndPriceMapper;

    @Resource
    private CardAndOutpriceMapper cardAndOutpriceMapper ;

    @Resource
    private OutPriceMapper outPriceMapper;

    /**
     * 条件查询获取油卡种类，作为油卡table的数据
     * @param jsonParam
     * @return
     */
    @Override
    @Cacheable(value = "lirong_gascard_", key = "'gascardsByExampleAndPage_pageNo'+ " +
            "#jsonParam.getString('pageNo')+ '_pageSize'+#jsonParam.getString('pageSize')+'_amount'+ " +
            "#jsonParam.getString('amount')+ '_type'+#jsonParam.getString('type')" )
    public List<Gascard> getGascardsByExampleAndPage(JSONObject jsonParam) {
        String amount=jsonParam.getString("amount");
        String type=jsonParam.getString("type");
        try {
            Integer pageNo=Integer.parseInt(StrUtil.isBlank(jsonParam.getString("pageNo"))?"1":jsonParam.getString("pageNo"));//默认第一页
            Integer pageSize=Integer.parseInt(StrUtil.isBlank(jsonParam.getString("pageSize"))?"10":jsonParam.getString("pageSize"));//默认一页十行

            Example example=new Example(Gascard.class);
            Example.Criteria createCriteria = example.createCriteria();
            createCriteria.andEqualTo("status",0);

            if(StrUtil.isNotBlank(type) && Integer.parseInt(type) != 0){
                createCriteria.andEqualTo("type",type);
            }
            if(StrUtil.isNotBlank(amount) ){
                createCriteria.andEqualTo("amount",amount);
            }
            example.setOrderByClause("option_time desc");
            List<Gascard> listByEaxmpleAndPage =new ArrayList<>();
            if(StrUtil.isNotBlank(jsonParam.getString("pageNo"))&&StrUtil.isNotBlank(jsonParam.getString("pageSize"))){
                listByEaxmpleAndPage=gascardMapper.selectByExampleAndRowBounds(example,new PageRowBounds((pageNo-1)*pageSize, pageSize));
            }else{
                //条件查询不分页
                listByEaxmpleAndPage=gascardMapper.selectByExample(example);
            }
            return listByEaxmpleAndPage;
        }catch (Exception e){
            _logger.error("【油卡管理模块】查询油卡列表异常，错误信息：" + e.getMessage());
            return null;
        }
    }

    /**
     * 根据条件获取油卡数量，作为油卡table分页的total
     * @param jsonParam
     * @return
     */
    @Override
    @Cacheable(value = "lirong_gascard_", key = "'gascardCount_amount'+ " +
            "#jsonParam.getString('amount')+ '_type'+#jsonParam.getString('type')")
    public Integer getGascardCountByExample(JSONObject jsonParam) {
        String amount=jsonParam.getString("amount");
        String type=jsonParam.getString("type");
        try {
            Example example=new Example(Gascard.class);
            Example.Criteria createCriteria = example.createCriteria();
            createCriteria.andEqualTo("status",0);

            if(StrUtil.isNotBlank(type) && Integer.parseInt(type) != 0){
                createCriteria.andEqualTo("type",type);
            }
            if(StrUtil.isNotBlank(amount) ){
                createCriteria.andEqualTo("amount",amount);
            }
            return gascardMapper.selectCountByExample(example);
        }catch (Exception e){
            _logger.error("【油卡管理模块】查询油卡数量异常，错误信息：" + e.getMessage());
            return 0;
        }
    }

    /**
     * 条件查询获取油卡报价列表，作为油卡报价table的数据
     * @param jsonParam
     * @return
     */
    @Override
    @Cacheable(value = "lirong_gascard_", key = "'gascardPricesByExampleAndPage_pageNo'+ " +
            "#jsonParam.getString('pageNo')+ '_pageSize'+#jsonParam.getString('pageSize')+'_amount'+ " +
            "#jsonParam.getString('amount')+ '_type'+#jsonParam.getString('type')+ '_account'+"+
            "#jsonParam.getString('account')+ '_name'+#jsonParam.getString('name')")
    public List<CardAndPrice> getGascardPricesByExampleAndPage(JSONObject jsonParam) {
        String amount=jsonParam.getString("amount");
        String type=jsonParam.getString("type");
        String account=jsonParam.getString("account");
        String name=jsonParam.getString("name");
        try {
            Integer pageNo=Integer.parseInt(StrUtil.isBlank(jsonParam.getString("pageNo"))?"1":jsonParam.getString("pageNo"));//默认第一页
            Integer pageSize=Integer.parseInt(StrUtil.isBlank(jsonParam.getString("pageSize"))?"10":jsonParam.getString("pageSize"));//默认一页十行
            Map<String,Object>params=new HashMap<>();
            params.put("account",account);
            params.put("name",name);
            params.put("amount",StrUtil.isNotBlank(amount)?Integer.parseInt(amount):null);
            params.put("type",StrUtil.isNotBlank(type)?Integer.parseInt(type):null);
            params.put("startNum",(pageNo-1)*pageSize);
            params.put("pageSize",pageSize);
            List<CardAndPrice>list=cardAndPriceMapper.getCardAndPricePage(params);
            return list;
        }catch (Exception e){
            _logger.error("【油卡管理模块】查询油卡和报价关联表异常，错误信息：" + e.getMessage());
            return null;
        }
    }

    /**
     * 根据条件获取油卡报价数量，作为油卡报价table分页的total
     * @param jsonParam
     * @return
     */
    @Override
    @Cacheable(value = "lirong_gascard_", key = "'gascardPriceCount_amount'+ " +
            "#jsonParam.getString('amount')+ '_type'+#jsonParam.getString('type')+" +
            "'_account'+#jsonParam.getString('account')+'_name'+#jsonParam.getString('name')")
    public Integer getGascardPriceCountByExample(JSONObject jsonParam) {
        String amount=jsonParam.getString("amount");
        String type=jsonParam.getString("type");
        String account=jsonParam.getString("account");
        String name=jsonParam.getString("name");
        try {
            Map<String,Object>params=new HashMap<>();
            params.put("account",account);
            params.put("name",name);
            params.put("amount",StrUtil.isNotBlank(amount)?Integer.parseInt(amount):null);
            params.put("type",StrUtil.isNotBlank(type)?Integer.parseInt(type):null);
            return cardAndPriceMapper.getCardAndPriceCount(params);
        }catch (Exception e){
            _logger.error("【油卡管理模块】查询油卡和报价关联表数量异常，错误信息：" + e.getMessage());
            return 0;
        }
    }

    /**
     * 新增一条油卡种类记录
     * @param gascard
     * @return
     */
    @Override
    @CacheEvict(value="lirong_gascard_",key="'gascardCount_*'",beforeInvocation=true)
    @CacheRemove(value = "lirong_gascard_",key="'gascardsByExampleAndPage_*'")
    public Integer addGascard(Gascard gascard) {
        return gascardMapper.insertSelective(gascard);
    }

    /**
     * 新增一条油卡报价记录
     * @param gascardPrice
     * @return
     */
    @Override
    @CacheEvict(value="lirong_gascard_",key="'gascardPriceCount_*'",beforeInvocation=true)
    @CacheRemove(value = "lirong_gascard_",key="'gascardPricesByExampleAndPage_*'")
    public Integer addGascardPrice(GascardPrice gascardPrice) {
        return cardPriceMapper.insertSelective(gascardPrice);
    }

    /**
     * 编辑油卡报价记录
     * @param gascardPrice
     * @return
     */
    @Override
    @CacheEvict(value="lirong_gascard_",key="'gascardPriceCount_*'",beforeInvocation=true)
    @CacheRemove(value = "lirong_gascard_",key="'gascardPricesByExampleAndPage_*'")
    public Integer updateCardPriceByPK(GascardPrice gascardPrice) {
        return cardPriceMapper.updateByPrimaryKeySelective(gascardPrice);
    }

    /**
     * 删除一条油卡记录
     * @param cardId
     * @return
     */
    @Override
    @CacheEvict(value="lirong_gascard_",key="'gascardCount_*'",beforeInvocation=true)
    @CacheRemove(value = "lirong_gascard_",key="'gascardsByExampleAndPage_*'")
    public Integer deleteGascard(@NonNull  Integer cardId) {
        return gascardMapper.deleteByPrimaryKey(cardId);
    }

    /**
     * 删除一条油卡报价记录
     * @param cardPriceId
     * @return
     */
    @Override
    @CacheEvict(value="lirong_gascard_",key="'gascardPriceCount_*'",beforeInvocation=true)
    @CacheRemove(value = "lirong_gascard_",key="'gascardPricesByExampleAndPage_*'")
    public Integer deleteGascardPrice(@NonNull Integer cardPriceId) {
        return cardPriceMapper.deleteByPrimaryKey(cardPriceId);
    }

    /**
     * 根据报价表ID获取油卡和报价信息
     * @param id
     * @return
     */
    @Override
    public CardAndPrice getCardPriceById(@NonNull Integer id) {
        try {
            return cardAndPriceMapper.getCardAndPriceById(id);
        }catch (Exception e){
            _logger.error("【油卡管理模块】由报价ID获取关联信息异常，错误信息：" + e.getMessage());
            return null;
        }

    }

    /**
     * 根据条件定位到油卡信息
     * @param type
     * @param amount
     * @return
     */
    @Override
    @Cacheable(value = "lirong_gascard_", key = "'gascardsByExampleAndPage_CardInfoSelective_amount'+ " +
            "#amount+ '_type '+#type")
    public Gascard getCardInfoSelective(@NonNull Integer type, @NonNull Integer amount) {
        try {
            Example example=new Example(Gascard.class);
            Example.Criteria createCriteria = example.createCriteria();
            createCriteria.andEqualTo("status",0);
            createCriteria.andEqualTo("type",type);
            createCriteria.andEqualTo("amount",amount);
            return gascardMapper.selectOneByExample(example);
        }catch (Exception e){
            _logger.error("【油卡管理模块】条件查询油卡信息异常，错误信息：" + e.getMessage());
            return null;
        }
    }

    /**
     * 条件查询获取外放价格列表，作为外放价格table的数据
     * @param jsonParam
     * @return
     */
    @Override
    @Cacheable(value = "lirong_gascard_", key = "'outPriceByExampleAndPage_pageNo'+ " +
            "#jsonParam.getString('pageNo')+ '_pageSize'+#jsonParam.getString('pageSize')+'_amount'+ " +
            "#jsonParam.getString('amount')+ '_type'+#jsonParam.getString('type')+ '_account'+"+
            "#jsonParam.getString('account')+ '_name'+#jsonParam.getString('name')")
    public List<CardAndOutprice> getOutPriceByExampleAndPage(JSONObject jsonParam) {
        String amount=jsonParam.getString("amount");
        String type=jsonParam.getString("type");
        String account=jsonParam.getString("account");
        String name=jsonParam.getString("name");
        try {
            Integer pageNo=Integer.parseInt(StrUtil.isBlank(jsonParam.getString("pageNo"))?"1":jsonParam.getString("pageNo"));//默认第一页
            Integer pageSize=Integer.parseInt(StrUtil.isBlank(jsonParam.getString("pageSize"))?"10":jsonParam.getString("pageSize"));//默认一页十行
            Map<String,Object>params=new HashMap<>();
            params.put("account",account);
            params.put("name",name);
            params.put("amount",StrUtil.isNotBlank(amount)?Integer.parseInt(amount):null);
            params.put("type",StrUtil.isNotBlank(type)?Integer.parseInt(type):null);
            params.put("startNum",(pageNo-1)*pageSize);
            params.put("pageSize",pageSize);
            List<CardAndOutprice>list=cardAndOutpriceMapper.getCardAndOutPricePage(params);
            return list;
        }catch (Exception e){
            _logger.error("【油卡管理模块】分页查询外放价格列表异常，错误信息：" + e.getMessage());
            return null;
        }

    }

    @Override
    @Cacheable(value = "lirong_gascard_", key = "'outPriceCount_amount'+ " +
            "#jsonParam.getString('amount')+ '_type'+#jsonParam.getString('type')+ '_account'+"+
            "#jsonParam.getString('account')+ '_name'+#jsonParam.getString('name')")
    public Integer getOutPriceCountByExample(JSONObject jsonParam) {
        String amount=jsonParam.getString("amount");
        String type=jsonParam.getString("type");
        String account=jsonParam.getString("account");
        String name=jsonParam.getString("name");
        try {
            Map<String,Object>params=new HashMap<>();
            params.put("account",account);
            params.put("name",name);
            params.put("amount",StrUtil.isNotBlank(amount)?Integer.parseInt(amount):null);
            params.put("type",StrUtil.isNotBlank(type)?Integer.parseInt(type):null);
            return cardAndOutpriceMapper.getCardAndOutPriceCount(params);
        }catch (Exception e){
            _logger.error("【油卡管理模块】根据条件获取外放价格总数量异常，错误信息：" + e.getMessage());
            return 0;
        }
    }

    /**
     * 新增一条油卡外放价格信息
     * @param outPrice
     * @return
     */
    @Override
    @CacheRemove(value = "lirong_gascard_",key={"'outPriceByExampleAndPage_*'","'outPriceCount_*'"})
    public Integer addOutPrice(OutPrice outPrice) {
        return outPriceMapper.insertSelective(outPrice);
    }

    /**
     *删除一条油卡外放价格记录
     * @param id
     * @return
     */
    @Override
    @CacheRemove(value = "lirong_gascard_",key={"'outPriceByExampleAndPage_*'","'outPriceCount_*'"})
    public Integer deleteOutPrice(@NonNull Integer id) {
        return outPriceMapper.deleteByPrimaryKey(id);
    }

    /**
     * 获取一条外放价格表信息
     * @param cardId
     * @param account
     * @return
     */
    @Override
    public OutPrice getOneOutPrice(@NonNull  Integer cardId,@NonNull String account) {
        Example example=new Example(OutPrice.class);
        Example.Criteria createCriteria = example.createCriteria();
        createCriteria.andEqualTo("status",0);
        createCriteria.andEqualTo("gascardId",cardId);
        createCriteria.andEqualTo("account",account);
        return outPriceMapper.selectOneByExample(example);
    }

    /**
     * 获取一条上游报价信息
     * @param cardId
     * @param account
     * @return
     */
    @Override
    @Cacheable(value = "lirong_gascard_", key = "'gascardPricesByExampleAndPage_One_cardId_'+ " +
            "#cardId+ '_account'+#account")
    public GascardPrice getOneCardPrice(Integer cardId, String account) {
        Example example=new Example(GascardPrice.class);
        Example.Criteria createCriteria = example.createCriteria();
        createCriteria.andEqualTo("status",0);
        createCriteria.andEqualTo("gascardId",cardId);
        createCriteria.andEqualTo("account",account);
        return cardPriceMapper.selectOneByExample(example);
    }

    /**
     * 根据g_out_price表主键ID获取油卡信息
     * @param id
     * @return
     */
    @Override
    public CardAndOutprice getCardOutByOutpriceId(Integer id) {
        try {
            return cardAndOutpriceMapper.getCardAndOutPriceById(id);
        }catch (Exception e){
            _logger.error("【油卡管理模块】由外放价格ID获取关联信息异常，错误信息：" + e.getMessage());
            return null;
        }
    }

    /**
     * 根据油卡ID获取最低报价
     * @param cardId
     * @return
     */
    @Override
    @Cacheable(value = "lirong_gascard_", key = "'gascardPricesByExampleAndPage_LowestOne_cardId_'+ #cardId")
    public GascardPrice getLowestCardPrice(Integer cardId) {
        Example example=new Example(GascardPrice.class);
        Example.Criteria createCriteria = example.createCriteria();
        createCriteria.andEqualTo("status",0);
        createCriteria.andEqualTo("gascardId",cardId);
        example.setOrderByClause("price asc");
        List<GascardPrice>cardPriceList=cardPriceMapper.selectByExample(example);
        if(cardPriceList!=null && cardPriceList.size()>0){
            return cardPriceList.get(0);
        }else{
            return null;
        }

    }

    /**
     * 更新外放价格表信息
     * @param outPrice
     * @return
     */
    @Override
    @CacheRemove(value = "lirong_gascard_",key={"'outPriceByExampleAndPage_*'","'outPriceCount_*'"})
    public Integer updateOutPriceByPK(OutPrice outPrice) {
        return outPriceMapper.updateByPrimaryKeySelective(outPrice);
    }
}
