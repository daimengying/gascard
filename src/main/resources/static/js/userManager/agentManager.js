//QQ截图粘贴事件
window.addEventListener('load', function (e) {
    document.body.onpaste = function (e) {
        var items = e.clipboardData.items;
        for (var i = 0; i < items.length; ++i) {
            var item = items[i];
            if (item && item.kind === 'file' && item.type.match(/^image\//i)) {
                AgentManager.imgReader(item);
                break;
            }
        }
    };
});

$(function(){
    AgentManager.loadTable();
    //日期控件
    $("#validateTime").click(function(){
        WdatePicker({el:'validateTime'});
    });
    //编辑代理商模态框
    $("#editAgent").bind("click", function(){
        var userid=isOnlyCheck("agentTable");
        if(userid!=null){
            UserManager.showModel(userid,3);
        }

    });
})

var AgentManager= {
    /**
     * 加载表格
     */
    loadTable : function () {
        var url=commonJs.root() + '/userManager/userTable?tt='+new Date().getTime();
        var options={
            tableId:"agentTable",
            sortable:true,sortOrder:"desc",
            url:url,
            roleType:2,
            toolbarId:"agentTableToolbar",
            queryParams:UserManager.queryParams
        };
        _LoadTableInit(options);
        $(".search").remove();//去掉自带搜索框
    },

    /**
     * 授信
     */
    credit : function () {
        var userId=isOnlyCheck("agentTable");

        if(userId!=null){
            layer.prompt({
                title: '设置代理编号'+userIds[0]+'授信额度',
                formType: 0 //prompt风格，支持0-2
            }, function(creditFacility){
                if (isNaN(creditFacility)){
                    layer.msg("额度必须为数字！")
                    return;
                }else{
                    $.ajax({
                        url :  commonJs.root()+'/userManager/addCredit',
                        type : 'post',
                        data : {"id":userIds[0],"creditFacility":creditFacility},
                        dataType : 'json',
                        cache : false,
                        success : function(data){
                            if(data.success){
                                layer.msg("设置成功");
                                var opt = {
                                    url:commonJs.root() + '/userManager/userTable',
                                    silent: true,
                                    query:UserManager.queryParams
                                };
                                $("#agentTable").bootstrapTable('refresh',opt);
                            }else{
                                layer.msg("设置失败");
                            }
                        },
                        error : function(){

                        }
                    })
                }
                layer.closeAll();
            });
        }

    },

    /**
     * 代理商充值
     */
    charge : function () {
        var userId=isOnlyCheck("agentTable");
        if( userId!=null){
           var ajaxUrl=commonJs.root() + '/userManager/toAgentCharge?userId='+userId;
           $.ajaxAlert({title:'代理商充值',ajaxUrl:ajaxUrl});
        }
    },

    genApiKey: function () {
        layer.confirm('是否确定重新生成密钥', {icon: 7, title:'提示'}, function(index) {
            //删除指定行
            $.ajax({
                type: "POST",
                url: commonJs.root() + '/userManager/genApiKey',
                cache: false,
                dataType:'json',
                async:false,
                success: function(msg){
                    if(msg.success){
                        $('#apiKey').val (msg.key);
                    }else{
                        layer.msg("生成失败");
                    }

                },
                error : function(){
                    layer.msg("连接服务器失败");
                }
            });
            layer.close(index);
        });
    },
    /**
     * 提交充值
     */
    commitCharge : function () {
        if(!$("#money").val()){
            layer.msg("请输入充值金额");
            return;
        }
        layer.closeAll();
        layer.confirm('确定充值吗？',{
            btn: ['确定','取消']
        },function(){
            $("#memo").val($('#memoDiv').html());
            $.ajax({
                url : commonJs.root()+'/userManager/agentCharge',
                type : 'post',
                data : $("#agentCharge").serialize(),
                dataType : 'json',
                success : function(data){
                    if( data && data.success){
                        var html=[];
                        html.push('<div>充值金额：'+data.money+'</div>');
                        layer.alert(html.join(""),{icon: 1}, function(index){
                            layer.close(index);
                            window.location.href= commonJs.root()+"/userManager/toAgentManager";
                        });
                    }else{
                        layer.msg("保存失败");
                    }
                },
                error : function(){
                    layer.msg("连接服务器失败！");
                }
            })
        })
    },

    imgReader : function (item) {
        var blob = item.getAsFile(),
            reader = new FileReader();

        reader.onload = function (e) {
            var img = new Image();

            img.src = e.target.result;
            var logBox = document.getElementById('memoDiv');
            logBox.appendChild(img);
        };

        reader.readAsDataURL(blob);
    }

}
