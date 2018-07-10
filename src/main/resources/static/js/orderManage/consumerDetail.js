$(function(){
    if(window.userInfo.roleType==3){
        $(".form-inline").children().eq(0).hide();
    }

    ConsumeDetail.loadTable();



})

var ConsumeDetail= {
    loadTable : function () {
        var url=commonJs.root() + '/orderManage/consumeDetailTable?tt='+new Date().getTime();
        var options={
            tableId:"consumeDetailTable",
            url:url,toolbarId:"consumeDetailToolbar",
            queryParams:ConsumeDetail.queryParams,
            showExport:true,
            // buttonsAlign:'left',
            exportDataType:'all',
            exportTypes:['excel','doc','xlsx'],
            Icons:'glyphicon-export',
            exportOptions:{
                fileName: '消费明细表',  //文件名称设置
                worksheetName: 'sheet1',  //表格工作区名称
                tableName: '消费明细表'
            }

        };
        _LoadTableInit(options);
        //优化导出按钮样式
        $(".export").children("button").eq(0).prepend("导出");
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
        ConsumeDetail.loadTable();
        var opt = {
            url:commonJs.root() + '/orderManage/consumeDetailTable?tt='+new Date().getTime(),
            silent: true,
            query:ConsumeDetail.queryParams
        };
        $("#"+tableId).bootstrapTable('refresh',opt);
    }

}