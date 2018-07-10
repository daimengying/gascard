package com.lirong.gascard.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class Tree<T> {
    /**
     * 节点ID
     */
    @Getter
    @Setter
    private String id;
    /**
     * 显示节点文本
     */
    @Getter
    @Setter
    private String text;
    /**
     * 节点的子节点
     */
    @Getter
    @Setter
    private List<Tree<T>> children = new ArrayList<Tree<T>>();

    /**
     * 父ID
     */
    @Getter
    @Setter
    private String parentId;

    /**
     * 指向的链接
     */
    @Getter
    @Setter
    private String href;

    /**
     * 样式图标
     */
    @Getter
    @Setter
    private String icon;


}
