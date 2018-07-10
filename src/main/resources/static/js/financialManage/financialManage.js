$(function(){
    FinancialManage.loadTable();


})

var FinancialManage= {
    loadTable : function () {
        var url=commonJs.root() + '/financialManage/financialManageTable?tt='+new Date().getTime();
        var options={
            tableId:"financialManageTable",
            url:url,toolbarId:"financialManageToolbar",
            queryParams:FinancialManage.queryParams
        };
        _LoadTableInit(options);
        $(".search").remove();//去掉自带搜索框
    },
    queryParams : function (params) {
        var temp = {
            account: $(".form-inline").find("#account").val(),
            beginTime: $(".form-inline").find("#beginTime").val(),
            endTime: $(".form-inline").find("#endTime").val(),
            payType : $(".form-inline").find("#payType").val(),
            pageSize: params.pageSize,
            pageNo: params.pageNumber,
            searchText : params.searchText
        };
        return temp;
    },
    search : function (tableId) {
        $("#"+tableId).bootstrapTable('destroy');//先要将table销毁，否则会保留上次加载的内容
        FinancialManage.loadTable();
        var opt = {
            url:commonJs.root() + '/financialManage/financialManageTable?tt='+new Date().getTime(),
            silent: true,
            query:FinancialManage.queryParams
        };
        $("#"+tableId).bootstrapTable('refresh',opt);
    }

}