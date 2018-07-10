(function($) {
    var _this = {};
    _this.root = function(){
        var curWwwPath = window.document.location.href;
        var pathName = window.document.location.pathname;
        var pos = curWwwPath.indexOf(pathName);
        var localhostPath = curWwwPath.substring(0,pos);
        var projectName = pathName.substring(0,pathName.substr(1).indexOf('/')+1);
        return (localhostPath);
    }
    commonJs=_this;
})($)

/** 拼接字符串 **/
function StringBuffer(str){
    this._string_ = new Array();
    this._string_.push(str);
};
StringBuffer.prototype.append = function(str){
    this._string_.push(str);
};
StringBuffer.prototype.toString = function(){
    return this._string_.join('');
};


/**保存按钮*/
function saveEdit(content,url){
    layer.alert(content, {icon: 6}, function(index){
        if(url!=null&&url!=""){
            window.location.href = url;
        }else{
            layer.close(index);
        }

    });
}

$.ajaxAlert = function(options){
    $.ajax({
        url: options.ajaxUrl,
        type: 'get',
        success: function (data) {
            // 清空表单
            $('.close').on('hidden.bs.modal', options.fun);
            $('#close').on('hidden.bs.modal', options.fun);
            $(".modalBody").html(data);
            $('#modal-form').modal('show');
            $('#myModalTitle').text(options.title);
        }
    });

}

_LoadTableInit = function (options) {

    $("#"+options.tableId).bootstrapTable({
        url: options.url,
        method: 'post',
        search: true,
        sortable: options.sortable,
        sortOrder: options.sortOrder,
        queryParamsType : '',//默认值为 'limit' ,在默认情况下 传给服务端的参数为：offset,limit,sort // 设置为 ''  在这种情况下传给服务器的参数为：pageSize,pageNumber
        queryParams:options.queryParams,//传递参数（*）
        sidePagination: "server",           //分页方式：client客户端分页，server服务端分页（*）
        pageNumber:1,                       //初始化加载第一页，默认第一页
        pageSize: 10,                       //每页的记录行数（*）
        pageList: [10, 25, 50, 100],   //可供选择的每页的行数（*）
        pagination: true,
        strictSearch: true,
        showRefresh: true,
        showToggle: true,  //是否显示详细视图和列表视图的切换按钮
        showColumns: true,  //是否显示所有的列
        iconSize: "outline",
        clickToSelect: true,
        toolbar: "#"+options.toolbarId,
        icons: {
            refresh: "glyphicon-repeat",
            toggle: "glyphicon-list-alt",
            columns: "glyphicon-list"
            // export:options.Icons
        },
        //导出功能相关参数
        showExport: options.showExport,  //是否显示导出按钮
        // buttonsAlign:options.buttonsAlign,  //按钮位置
        exportDataType:options.exportDataType,
        exportTypes:options.exportTypes,  //导出文件类型
        exportOptions:options.exportOptions,
        exportButton:options.exportButton
    })
    
}

/**
 * 时间字符串转日期
 * @param strDate
 * @returns {Object}
 */
$.timeStrToDate = function (strDate) {
    var date = eval('new Date(' + strDate.replace(/\d+(?=-[^-]+$)/,
        function (a) { return parseInt(a, 10) - 1; }).match(/\d+/g) + ')');
    return date;
}


function isOnlyCheck(tableId) {
    //控制复选框只能唯一
    var selections = $("#"+tableId).bootstrapTable('getSelections');
    var ids = $.map(selections, function (row) {
        return row.id;
    });
    if(ids.length==0){
        layer.alert("请选择要修改的数据");
        return null;
    }else if(ids.length>1){
        layer.alert("每次只能选择一行");
        return null;
    }else{
        return ids[0];
    }
}
