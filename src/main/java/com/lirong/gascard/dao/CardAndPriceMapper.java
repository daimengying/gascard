package com.lirong.gascard.dao;

import com.lirong.gascard.vo.CardAndPrice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.mybatis.spring.annotation.MapperScan;

import java.util.List;
import java.util.Map;

/**
 * @Author: daimengying
 * @Date: 2018/5/29 18:48
 * @Description:油卡和报价关联查询
 */
@Mapper
public interface CardAndPriceMapper {
    @Select({"<script>",
            "SELECT * FROM (SELECT price.id,price.account,card.name,card.type,card.amount,price.gascard_id,price.price,price.option_time "
                    +"from g_gascard card,g_gascard_price price  ",
            "WHERE card.id=price.gascard_id and price.status=0 ",
            "<if test='account!=null and account!=\"\" '>AND price.account = #{account}</if>",
            "<if test='name!=null and name!=\"\"'>AND card.name like  '%'||#{name}||'%' </if>",
            "<if test='type!=null'>AND card.type = #{type}</if>",
            "<if test='amount!=null'>AND card.amount = #{amount}</if>",
            "order by price.option_time desc",
            "limit #{startNum},#{pageSize}  ",
            ")t</script>"})
    List<CardAndPrice> getCardAndPricePage( Map<String, Object> params);

    @Select({"<script>",
            "SELECT  COUNT(1) "
                    +"from g_gascard card,g_gascard_price price  ",
            "WHERE card.id=price.gascard_id and price.status=0 ",
            "<if test='params.account!=null and params.account!=\"\" '>AND price.account = #{params.account}</if>",
            "<if test='params.name!=null and params.name!=\"\"'>AND card.name like  '%'||#{params.name}||'%' </if>",
            "<if test='params.type!=null'>AND card.type = #{params.type}</if>",
            "<if test='params.amount!=null'>AND card.amount = #{params.amount}</if>",
            "</script>"})
    Integer getCardAndPriceCount(@Param("params") Map<String, Object> params);

    @Select({"<script>",
            "SELECT * FROM (SELECT price.id,price.account,card.name,card.type,card.amount,price.gascard_id,price.price,price.option_time "
                    +"from g_gascard card,g_gascard_price price  ",
            "WHERE card.id=price.gascard_id ",
            "and price.id=#{id}",
            ")t</script>"})
    CardAndPrice getCardAndPriceById(@Param("id")Integer id);
}
