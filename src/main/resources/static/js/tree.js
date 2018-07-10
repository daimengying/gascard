var _Ztree = {
	initpar : '',
	init : function(initpar){
		var tree = $("#" + initpar.treeid);
		_Ztree.initpar = initpar;
		$.fn.zTree.init(tree, $.extend(true,{},_Ztree.defaultOpt,initpar), initpar.zNodes);
	},
	//ztree控件参数设置
	defaultOpt : {
		check: {
			enable: true   //是否显示 checkbox / radio
	    },
	    view: {
	        dblClickExpand: false,   //双击 展开/收起 父节点
	        showLine: true, //显示节点连接线
	        selectedMulti: false //ctrl多选
	    },
	    callback: {
        	// 节点点击之前调用 如果返回 false，zTree 将不会选中节点，也无法触发 onClick 事件回调函数
            beforeClick: function(treeId, treeNode) {
                var zTree = $.fn.zTree.getZTreeObj(_Ztree.initpar.treeid);
                if (treeNode.isParent) {
                	//如果是父节点 点击折叠节点
                    zTree.expandNode(treeNode);
                    return false;
                } else {
                    return true;
                }
            }
        }
	}
}
