$(function(){
    UserManager.loadTable();

    //添加管理员模态框
    $("#addAdmin").bind("click", function(){
        UserManager.showModel('',2);
    });
    //编辑管理员模态框
    $("#editAdmin").bind("click", function(){
        // UserManager.editValidate("adminTable",2);
        var userid=isOnlyCheck("adminTable");
        if(userid!=null){
            UserManager.showModel(userid,2);
        }


    });

})

var nodeIds=[];

var UserManager= {
    /**
     * 加载表格
     */
    loadTable : function () {
        var url=commonJs.root() + '/userManager/userTable?tt='+new Date().getTime();
        var options={
            tableId:"adminTable",
            sortable:true,
            sortOrder:"desc",
            roleType:2,
            url:url,toolbarId:"adminTableToolbar",
            queryParams:UserManager.queryParams
        };
        _LoadTableInit(options);
        $(".search").remove();//去掉自带搜索框
    },

    queryParams : function (params) {
        var temp = {   //这里的键的名字和控制器的变量名必须一直，这边改动，控制器也需要改成一样的
            username: $(".form-inline").find("#username").val(),
            name: $(".form-inline").find("#name").val(),
            roleType:$("#roleType").val(),
            pageSize: params.pageSize,
            pageNo: params.pageNumber,
            searchText : params.searchText,
            sortName : params.sortName,
            sortOrder : params.sortOrder,
        };
        return temp;
    },
    
    search : function (tableId) {
        $("#"+tableId).bootstrapTable('destroy');//先要将table销毁，否则会保留上次加载的内容
        if($("#roleType").val()==2){
            UserManager.loadTable();
        }else if($("#roleType").val()==3){
            AgentManager.loadTable();
        }

        var opt = {
            url:commonJs.root() + '/userManager/userTable',
            silent: true,
            query:UserManager.queryParams
        };
        $("#"+tableId).bootstrapTable('refresh',opt);
    },

    /**
     * 表单数据校验
     * @param data
     * @returns {boolean}
     */
    formVerify : function(){
        var username=$("input[name='username']").val();
        var name=$("input[name='name']").val();
        var password = $("input[name='password']").val();
        // var phone=$("input[name='phone']").val();
        // var reg=/^1[3|4|5|7|8][0-9]\d{4,8}$/; //匹配手机号
        if(username.trim().length==0){
            layer.msg('请输入管理员账号');
            return false;
        }else if(password.trim().length==0){
            layer.msg('请输入管理员密码');
            return false;
        }else if(name.trim().length==0){
            layer.msg('请输入管理员名称');
            return false;
        }else{
            return true;
        }
    },

    addOrEditAdmin : function () {

        if(UserManager.formVerify()){
            if($("#password").val()!=""){
                $("#password").val($.md5($("#password").val()));
            }
            if($("#valiTime").val()!=null){
                $("#validateTime").val($.timeStrToDate($("#valiTime").val()));
            }
             $("#nodeIds").val(nodeIds);//勾选的菜单Id

            $.ajax({
                type: "POST",
                url: commonJs.root() +"/userManager/addOrEditAdmin?tt="+new Date().getTime(),
                data:$("#addOrEditAdminForm").serialize(),
                dataType:'json',
                success: function(data){
                    if( data && data.success){
                        layer.msg("保存成功");
                        if($("#roleType").val()==2){
                            window.location.href = commonJs.root()+"/userManager/toAdminManager";
                        }else{
                            window.location.href = commonJs.root()+"/userManager/toAgentManager";
                        }

                    }else{
                        layer.msg("保存失败");
                    }
                },
                error : function(){
                    layer.msg("连接服务器失败！");
                }
            });
        }
    },

    /**显示模态框*/
    showModel : function (userId,roleType) {
        var ajaxUrl;
        if(userId!=''){
             ajaxUrl = commonJs.root() + '/userManager/toAddUser?roleType='+roleType+'&userId='+userId;
        }else {
            ajaxUrl=commonJs.root() + '/userManager/toAddUser?roleType='+roleType;
        }
        $.ajaxAlert({title:'用户信息',ajaxUrl:ajaxUrl,fun:UserManager.cleanInput});
        return false;
    },

    editValidate : function (tableId,roleType) {
        //控制复选框只能唯一
        var selections = $("#"+tableId).bootstrapTable('getSelections');
        var userIds = $.map(selections, function (row) {
            return row.id;
        });
        if(userIds.length==0){
            layer.alert("请选择要修改的用户")
        }else if(userIds.length>1){
            layer.alert("每次只能选择一行");
        }else{
            UserManager.showModel(userIds[0],roleType);
        }

    },

    cleanInput : function () {
        $("input[name='username']").val('');
        $("input[name='name']").val('');
        $("input[name='password']").val('');
        $("input[name='phone']").val('');
    },

    /**
     * 批量删除管理员
     * @param tableId
     * @param requestUrl
     * @param reLoad
     */
    deleteBatch : function (tableId, requestUrl, reLoad) {
        var selections = $("#"+tableId).bootstrapTable('getSelections');
        var userIds = $.map(selections, function (row) {
            return row.id;
        });
        if(userIds.length==0){
            layer.alert("至少选择一行！")
        }else{
            layer.confirm('确定要删除选中的内容吗？', {icon: 7, title:'提示'}, function(index) {
                //删除指定行
                $.ajax({
                    type: "POST",
                    url: commonJs.root() + requestUrl,
                    cache: false,
                    data:{userIds:userIds},
                    traditional: true,//数组格式转换 加上这个就可以了
                    dataType:'json',
                    async:false,
                    success: function(msg){
                        if(msg.resultCode==0){
                            layer.alert('删除成功'+msg.resultData+'条记录', {icon: 6},function(index){
                                if(reLoad){
                                    var opt = {
                                        url:commonJs.root() + '/userManager/userTable',
                                        silent: true,
                                        query:UserManager.queryParams
                                    };
                                    $("#"+tableId).bootstrapTable('refresh',opt);
                                    $("#username").val('');
                                    $("#name").val('');
                                }
                                layer.close(index);
                            });
                        }else{
                            layer.alert('删除失败', {icon: 5});
                        }
                    }
                });
            });
        }
    },

    /**
     * ztree 初始化菜单树
     */
    initMenuTree : function(){
        _Ztree.init({
            treeid : 'menuZtree',
            zNodes : window.allMenu,
            view : {
                expandSpeed : "normal"
                // addHoverDom :  addHoverDom, //当鼠标移动到节点上时
                // removeHoverDom : removeHoverDom//当鼠标移开节点上时
            },
            data : {
                key : {
                    title : 'name',
                    name : 'name',
                    isSimpleData : true,              //数据是否采用简单 Array 格式，默认false
                    treeNodeKey : "id",               //在isSimpleData格式下，当前节点id属性
                    treeNodeParentKey : "parentId",        //在isSimpleData格式下，当前节点的父节点id属性
                    showLine : true,                  //是否显示节点间的连线
                    checkable : true
                },
                simpleData : {
                    enable : true,
                    idKey : "id",
                    pIdKey : "parentId",
                    rootPId : 0
                }
            },
            callback:{
                beforeCheck:true,
                onCheck:UserManager.onCheck
            },
            check: {
                enable: true,
                chkStyle: "checkbox",
                chkboxType: { "Y": "p", "N": "s" }
            }
        });
    },
    
    onCheck : function () {
        var v=[];
        var treeObj = $.fn.zTree.getZTreeObj("menuZtree");
        var nodes=treeObj.getCheckedNodes(true);
        for(var i=0;i<nodes.length;i++){
            v.push(nodes[i].id);
        }
        nodeIds=v;

    }




}