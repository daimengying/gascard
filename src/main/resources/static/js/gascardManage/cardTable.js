$(function(){
    CardManage.loadTable();

})

var CardManage= {
    loadTable : function () {
        var url=commonJs.root() + '/gascardManage/cardTable?tt='+new Date().getTime();
        var options={
            tableId:"cardTable",
            url:url,toolbarId:"cardToolbar",
            queryParams:CardManage.queryParams
        };
        _LoadTableInit(options);
        $(".search").remove();//去掉自带搜索框
    },
    queryParams : function (params) {
        var temp = {
            amount: $(".form-inline").find("#amount").val(),
            type : $(".form-inline").find("#type").val(),
            pageSize: params.pageSize,
            pageNo: params.pageNumber,
            searchText : params.searchText
        };
        return temp;
    },
    search : function (tableId) {
        $("#"+tableId).bootstrapTable('destroy');//先要将table销毁，否则会保留上次加载的内容
        CardManage.loadTable();
        var opt = {
            url:commonJs.root() + '/gascardManage/cardTable?tt='+new Date().getTime(),
            silent: true,
            query:CardManage.queryParams
        };
        $("#"+tableId).bootstrapTable('refresh',opt);
    },
    deleteCards : function (tableId, requestUrl, reLoad) {
        var selections = $("#"+tableId).bootstrapTable('getSelections');
        var cardIds = $.map(selections, function (row) {
            return row.id;
        });
        if(cardIds.length==0){
            layer.alert("至少选择一行！")
        }else{
            layer.confirm('确定要删除选中的内容吗？', {icon: 7, title:'提示'}, function(index) {
                //删除指定行
                $.ajax({
                    type: "POST",
                    url: commonJs.root() + requestUrl,
                    cache: false,
                    data:{cardIds:cardIds},
                    traditional: true,//数组格式转换 加上这个就可以了
                    dataType:'json',
                    async:false,
                    success: function(msg){
                        if(msg.success){
                            layer.alert('删除成功'+msg.data+'条记录', {icon: 6},function(index){
                                if(reLoad){
                                    var opt = {
                                        url:commonJs.root() + '/gascardManage/cardTable',
                                        silent: true,
                                        query:CardManage.queryParams
                                    };
                                    $("#"+tableId).bootstrapTable('refresh',opt);
                                    $("#amount").val('');
                                    $("#type").val('');
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
    showModel : function () {
        var ajaxUrl=commonJs.root() + '/gascardManage/toAddCard';
        $.ajaxAlert({title:'新增油卡种类',ajaxUrl:ajaxUrl});
        return false;
    },

    formVerify : function(){
        var amount=$("#amount").val();
        var name=$("#name").val();
        if(amount.trim().length==0){
            layer.msg('请输入油卡面值');
            return false;
        }else if(name.trim().length==0){
            layer.msg('请输入油卡名称');
            return false;
        }else{
            return true;
        }
    },
    addCard : function () {
        if(CardManage.formVerify()){
            $.ajax({
                type: "POST",
                url: commonJs.root() +"/gascardManage/addCard?tt="+new Date().getTime(),
                data:$("#addCardForm").serialize(),
                dataType:'json',
                success: function(data){
                    if( data && data.success){
                        layer.msg("保存成功");
                        window.location.href = commonJs.root()+"/gascardManage/toGasCard";
                    }else{
                        layer.msg("保存失败");
                    }
                },
                error : function(){
                    layer.msg("连接服务器失败！");
                }
            });
        }
    }
}