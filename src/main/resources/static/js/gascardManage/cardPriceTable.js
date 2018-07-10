$(function(){
    PriceManage.loadTable();
    //编辑油卡报价信息模态框
    $("#editCardPrice").bind("click", function(){
        // PriceManage.editValidate("adminTable",2);
        var priceId=isOnlyCheck('priceTable');
        if(priceId!=null){
            PriceManage.showModel(priceId);
        }
    });


})

var PriceManage= {
    loadTable : function () {
        var url=commonJs.root() + '/gascardManage/priceTable?tt='+new Date().getTime();
        var options={
            tableId:"priceTable",
            url:url,toolbarId:"priceToolbar",
            queryParams:PriceManage.queryParams
        };
        _LoadTableInit(options);
        $(".search").remove();//去掉自带搜索框
    },
    queryParams : function (params) {
        var temp = {
            amount: $(".form-inline").find("#amount").val(),
            type : $(".form-inline").find("#type option:selected").val(),
            name: $(".form-inline").find("#name").val(),
            account : $(".form-inline").find("#account").val(),
            pageSize: params.pageSize,
            pageNo: params.pageNumber,
            searchText : params.searchText
        };
        return temp;
    },
    search : function (tableId) {
        $("#"+tableId).bootstrapTable('destroy');//先要将table销毁，否则会保留上次加载的内容
        PriceManage.loadTable();
        var opt = {
            url:commonJs.root() + '/gascardManage/priceTable?tt='+new Date().getTime(),
            silent: true,
            query:PriceManage.queryParams
        };
        $("#"+tableId).bootstrapTable('refresh',opt);
    },
    deletePrices : function (tableId, requestUrl, reLoad) {
        var selections = $("#"+tableId).bootstrapTable('getSelections');
        var priceIds = $.map(selections, function (row) {
            return row.id;
        });
        if(priceIds.length==0){
            layer.alert("至少选择一行！")
        }else{
            layer.confirm('确定要删除选中的内容吗？', {icon: 7, title:'提示'}, function(index) {
                //删除指定行
                $.ajax({
                    type: "POST",
                    url: commonJs.root() + requestUrl,
                    cache: false,
                    data:{priceIds:priceIds},
                    traditional: true,//数组格式转换 加上这个就可以了
                    dataType:'json',
                    async:false,
                    success: function(msg){
                        if(msg.success){
                            layer.alert('删除成功'+msg.data+'条记录', {icon: 6},function(index){
                                if(reLoad){
                                    var opt = {
                                        url:commonJs.root() + '/gascardManage/priceTable',
                                        silent: true,
                                        query:PriceManage.queryParams
                                    };
                                    $("#"+tableId).bootstrapTable('refresh',opt);
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
    showModel : function (priceId) {
        var ajaxUrl=commonJs.root() + '/gascardManage/toAddCardPrice?cardPriceId='+priceId;
        $.ajaxAlert({title:'油卡报价',ajaxUrl:ajaxUrl});
        return false;
    },
    changeType : function () {
        var type=$("#type").val();
        if(type==null||type==""){
            type=1;
        }
        $.ajax({
            type: "POST",
            url: commonJs.root() +"/gascardManage/getPriceListByType?tt="+new Date().getTime(),
            data:{type:type},
            dataType:'json',
            success: function(data){
                $("#amount option").remove();
                $("#amount").append("<option value='0'>请选择</option>");
                var list=data.cardList;
                if( list!=null&&list.length>0){
                    for ( var i = 0; i <list.length; i++) {
                        // var cardId = list[i].id;
                        var cardAmount= list[i].amount;
                        $("#amount").append("<option value="+cardAmount+">"+ cardAmount + "</option>");
                    }
                }
            },error : function(){
                layer.msg("连接服务器失败！");
            }
        });

    },

    formVerify : function(){
        var amount=$("#amount").val();
        var account=$("#account").val();
        var price=$("#price").val();
        if(account.trim().length==0){
            layer.msg('请输入通道名称');
            return false;
        }
        if(amount=="0"){
            layer.msg('请选择油卡面值');
            return false;
        }
        if(price.trim().length==0){
            layer.msg('请输入油卡报价');
            return false;
        }
        return true;

    },
    addOrEditCardPrice : function () {
        if(PriceManage.formVerify()){
            $.ajax({
                type: "POST",
                url: commonJs.root() +"/gascardManage/addOrEditCardPrice?tt="+new Date().getTime(),
                data:$("#addPriceForm").serialize(),
                dataType:'json',
                success: function(data){
                    if( data && data.success){
                        layer.msg("保存成功");
                        window.location.href = commonJs.root()+"/gascardManage/toCardPrice";
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