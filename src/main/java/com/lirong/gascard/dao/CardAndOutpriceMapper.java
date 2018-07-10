package com.lirong.gascard.dao;

import com.lirong.gascard.vo.CardAndOutprice;
import com.lirong.gascard.vo.CardAndPrice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * @Author: daimengying
 * @Date: 2018/6/4 11:17
 * @Description:油卡和外放价格关联查询
 */
@Mapper
public interface CardAndOutpriceMapper {
    @Select({"<script>",
            "SELECT * FROM (SELECT price.id,price.account,card.name,card.type,card.amount,price.gascard_id,price.out_price,price.option_time "
                    +"from g_gascard card,g_out_price price  ",
            "WHERE card.id=price.gascard_id and price.status=0 ",
            "<if test='account!=null and account!=\"\" '>AND price.account = #{account}</if>",
            "<if test='name!=null and name!=\"\"'>AND card.name like  '%'||#{name}||'%' </if>",
            "<if test='type!=null'>AND card.type = #{type}</if>",
            "<if test='amount!=null'>AND card.amount = #{amount}</if>",
            "order by price.option_time desc",
            "limit #{startNum},#{pageSize}  ",
            ")t</script>"})
    List<CardAndOutprice> getCardAndOutPricePage(Map<String, Object> params);

    @Select({"<script>",
            "SELECT  COUNT(1) "
                    +"from g_gascard card,g_gascard_price price  ",
            "WHERE card.id=price.gascard_id and price.status=0 ",
            "<if test='params.account!=null and params.account!=\"\" '>AND price.account = #{params.account}</if>",
            "<if test='params.name!=null and params.name!=\"\"'>AND card.name like  '%'||#{params.name}||'%' </if>",
            "<if test='params.type!=null'>AND card.type = #{params.type}</if>",
            "<if test='params.amount!=null'>AND card.amount = #{params.amount}</if>",
            "</script>"})
    Integer getCardAndOutPriceCount(@Param("params") Map<String, Object> params);

    @Select({"<script>",
            "SELECT * FROM (SELECT price.id,price.account,card.name,card.type,card.amount,price.gascard_id,price.out_price,price.option_time "
                    +"from g_gascard card,g_out_price price  ",
            "WHERE card.id=price.gascard_id ",
            "and price.id=#{id}",
            ")t</script>"})
    CardAndOutprice getCardAndOutPriceById(@Param("id")Integer id);

}
