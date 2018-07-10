$(function(){
    OutPriceManage.loadTable();
    //编辑油卡报价信息模态框
    $("#editOutPrice").bind("click", function(){
        var priceId=isOnlyCheck('outPriceTable');
        if(priceId!=null){
            OutPriceManage.showModel(priceId);
        }
    });


})

var OutPriceManage= {
    loadTable : function () {
        var url=commonJs.root() + '/gascardManage/outPriceTable?tt='+new Date().getTime();
        var options={
            tableId:"outPriceTable",
            url:url,toolbarId:"outPriceToolbar",
            queryParams:OutPriceManage.queryParams
        };
        _LoadTableInit(options);
        $(".search").remove();//去掉自带搜索框
    },
    queryParams : function (params) {
        var temp = {
            account: $(".form-inline").find("#account").val(),
            amount: $(".form-inline").find("#amount").val(),
            type : $(".form-inline").find("#type option:selected").val(),
            pageSize: params.pageSize,
            pageNo: params.pageNumber,
            searchText : params.searchText
        };
        return temp;
    },
    search : function (tableId) {
        $("#"+tableId).bootstrapTable('destroy');//先要将table销毁，否则会保留上次加载的内容
        OutPriceManage.loadTable();
        var opt = {
            url:commonJs.root() + '/gascardManage/outPriceTable?tt='+new Date().getTime(),
            silent: true,
            query:OutPriceManage.queryParams
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
                    data:{outPrices:priceIds},
                    traditional: true,//数组格式转换 加上这个就可以了
                    dataType:'json',
                    async:false,
                    success: function(msg){
                        if(msg.success){
                            layer.alert('删除成功'+msg.data+'条记录', {icon: 6},function(index){
                                if(reLoad){
                                    var opt = {
                                        url:commonJs.root() + '/gascardManage/outPriceTable',
                                        silent: true,
                                        query:OutPriceManage.queryParams
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
        var ajaxUrl=commonJs.root() + '/gascardManage/toAddOutPrice?outPriceId='+priceId;
        $.ajaxAlert({title:'外放价格',ajaxUrl:ajaxUrl});
        return false;
    },
    changeAmount : function () {
        var type=$("#type").val();
        var amount=$("#amount").val();
        if(amount==0){
           return false;
        }
        $.ajax({
            type: "POST",
            url: commonJs.root() +"/gascardManage/getLowestPrice?tt="+new Date().getTime(),
            data:{type:type,amount:amount},
            dataType:'json',
            success: function(data){
                $("#price").val(data.price);
            },error : function(){
                layer.msg("连接服务器失败！");
            }
        });

    },

    formVerify : function(){
        var amount=$("#amount").val();
        var account=$("#account").val();
        var outPrice=$("#outPrice").val();
        if(account.trim().length==0){
            layer.msg('请输入通道名称');
            return false;
        }
        if(amount=="0"){
            layer.msg('请选择油卡面值');
            return false;
        }
        if(outPrice.trim().length==0){
            layer.msg('请输入油卡外放价格');
            return false;
        }
        if(Number(outPrice) < Number($("#price").val())){
            layer.msg('外放价格比成本小');
            return false;
        }
        return true;

    },
    addOrEditOutPrice : function () {
        if(OutPriceManage.formVerify()){
            $.ajax({
                type: "POST",
                url: commonJs.root() +"/gascardManage/addOrEditOutPrice?tt="+new Date().getTime(),
                data:$("#addPriceForm").serialize(),
                dataType:'json',
                success: function(data){
                    if( data && data.success){
                        layer.msg("保存成功");
                        window.location.href = commonJs.root()+"/gascardManage/toOutPrice";
                    }else{
                        /*if(data.code=="-1001"){
                            layer.msg("每位代理商每张卡只能配置一次");
                        }else {
                            layer.msg("保存失败");
                        }*/
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