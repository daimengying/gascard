package com.lirong.gascard.utils;


import com.lirong.gascard.vo.Tree;

import java.util.ArrayList;
import java.util.List;

public class BuildTree {
    public static <T> List<Tree<T>> build(List<Tree<T>> nodes) {

        if (nodes == null) {
            return null;
        }
        List<Tree<T>> topNodes = new ArrayList<>();

        for (Tree<T> children : nodes) {

            String pid = children.getParentId();
            if (pid.equals(children.getId() ) || "0".equals(pid)) {
                topNodes.add(children);
                continue;
            }
            for (Tree<T> parent : nodes) {
                String id = parent.getId();
                if (id != null && id.equals(pid)) {
                    parent.getChildren().add(children);
                }
            }
        }

        return topNodes;
    }

}
