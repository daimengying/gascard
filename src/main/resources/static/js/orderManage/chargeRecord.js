$(function(){
    if(window.userInfo.roleType==3){
        $(".form-inline").children().eq(0).hide();
    }
    ChargeRecord.loadTable();



})

var ChargeRecord= {
    loadTable : function () {
        $(".search").remove();//去掉自带搜索框
        var url=commonJs.root() + '/orderManage/chargeRecordTable?tt='+new Date().getTime();
        var options={
            tableId:"chargeRecordTable",
            url:url,
            toolbarId:"chargeRecordToolbar",
            queryParams:ChargeRecord.queryParams,
            showExport:true,
            exportDataType:'all',
            exportTypes:['excel','doc','xlsx'],
            Icons:'glyphicon-export',
            exportOptions:{
                fileName: '充值记录表',  //文件名称设置
                worksheetName: 'sheet1',  //表格工作区名称
                tableName: '充值记录表'
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
            amount:$(".form-inline").find("#amount").val(),
            type:$(".form-inline").find("#type").val(),
            chargeStatus:$(".form-inline").find("#chargeStatus").val(),
            beginTime: $(".form-inline").find("#beginTime").val(),
            endTime: $(".form-inline").find("#endTime").val(),
            cardnum : $(".form-inline").find("#cardnum").val(),
            pageSize: params.pageSize,
            pageNo: params.pageNumber,
            searchText : params.searchText
        };
        return temp;
    },
    search : function (tableId) {
        $("#"+tableId).bootstrapTable('destroy');//先要将table销毁，否则会保留上次加载的内容
        ChargeRecord.loadTable();
        var opt = {
            url:commonJs.root() + '/orderManage/chargeRecordTable?tt='+new Date().getTime(),
            silent: true,
            query:ChargeRecord.queryParams
        };
        $("#"+tableId).bootstrapTable('refresh',opt);
    }

}

/**
 * 格式化充值状态
 * @param val
 * @param row
 * @param index
 */
function formatStatus(val,row,index) {
    if(val==1){
        return "<span style='color:red;font-weight:900;'>未知</span>";
    }else if(val==2){
        return "<span style='color:#01AAED;font-weight:900;'>提交成功</span>";
    }else if(val==3){
        return "<span style='color:#FF5722;font-weight:900;'>提交失败</span>";
    }else if(val==4){
        return "<span style='color:#5cb85c;font-weight:900;'>充值成功</span>";
    }else if(val==5){
        return "<span style='color:#FF5722;font-weight:900;'>充值失败</span>";
    }
}

/**
 * 格式化油卡类型
 * @param val
 * @returns {*}
 */
function formatType(val) {
    if(val==1){
        return "中国石油";
    }else if(val==2){
        return "中国石化";
    }
}

function  formatProfit(val) {
    if(val<0){
       return "<span style='color:#FF5722;'>￥"+val+"</span>";
    }else{
        return "<span style='color:#5FB878;'>￥"+val+"</span>";
    }
}