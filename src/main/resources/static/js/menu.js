$(function(){
    _Menu.showMenu(window.allMenus,$("#mutree"));
    //菜单树样式修改
    $("#mutree li").bind("click", function(event){
        $(this).addClass("active").siblings().removeClass("active");
    });

    //修改密码模态框
    $("#modifyPass").bind("click", function(event){
        var modifyPasswordUrl = commonJs.root() + '/toModifyPassword';
        $.ajaxAlert({title:'修改密码',ajaxUrl:modifyPasswordUrl,fun:_Menu.cleanInput});
        return false;
    });

})

var _Menu= {
    showMenu : function(menuList,parentUl){
        if(menuList!=null&&menuList.length>0){
            $.each(menuList,function(index,item) {
                var li,_menuHtml= new StringBuffer();
                li=$("<li ></li>");
                _menuHtml.append("<a href='"+item.href+"' >");
                _menuHtml.append("<i class='"+item.icon+"'></i><span class='menu-text'>"+item.text+"</span></a>");
                _menuHtml.append("<b class=\"arrow\"></b>");
                $(li).append(_menuHtml.toString());
                if (item.children.length > 0) {
                    var nextParent=$("<ul class=\"submenu\"></ul>");
                    $(nextParent).appendTo(li);
                    $(li).appendTo(parentUl);
                    $(li).find('span').after("<b class='arrow fa fa-angle-down'></b>");
                    $(li).find("a").addClass("dropdown-toggle");
                    _Menu.showMenu(item.children, nextParent);

                }else {
                    $(li).appendTo(parentUl);
                }
            });
        }
    },

    /**
     * 修改密码逻辑
     */
    modifyPassword : function () {
        var param={
            'password': $.md5($("input[name='password']").val()),
            'newPassword':$.md5($("input[name='newPassword']").val()),
            'repeatPassword': $.md5($("input[name='repeatPassword']").val())
        };
        if(!$("#password").val()||!$("#newPassword").val()||!$("#repeatPassword").val()){
            layer.msg("信息填写不完整","");
            return false;
        }
        $.ajax( {
            type : "POST",
            url : commonJs.root() +"/modifyPassword?tt="+new Date().getTime(),
            contentType: "application/json; charset=utf-8",
            data : JSON.stringify(param),
            dataType:'json',
            success : function(data) {
                // $('#modal-form').modal('hide');
                saveEdit(data.msg,"");
            },
            error : function(){
                layer.msg("连接服务器失败");
            }
        });
    },

    //清空修改密码输入框
    cleanInput:function () {
        $("input[name='password']").val('');
        $("input[name='newPassword']").val('');
        $("input[name='repeatPassword']").val('');
    }
}