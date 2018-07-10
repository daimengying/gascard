$(function(){
    _Login.init();
})

var _Login= {
    init: function () {
        /**
         * 极验证  验证码
         */
        $.ajax({
            url :commonJs.root() +"/startCaptcha?tt="+new Date().getTime(),
            type :'get',
            dataType : 'json',
            success :function(data){
                initGeetest({
                    gt: data.gt,
                    challenge: data.challenge,
                    new_captcha: data.new_captcha,
                    product: "float",
                    offline: !data.success
                }, _Login.handlerEmbed);
            },
            error : function(){
            }
        })
    },
    errorTip : function(emsg){
        layer.msg(emsg, {offset: 70, shift: 0});
    },
    getFormData : function(){
        return {
            userName : $("input[name='userName']").val(),
            passWord : $.md5($("input[name='passWord']").val()),
            redirectURL : $("#redirectURL").val()
        }
    },
    handlerEmbed : function (captchaObj) {
        $("#login").click(function() {
            var validate = captchaObj.getValidate();
            if (!validate) {
                _Login.errorTip('请先完成验证');
            }else {
                _Login.login(validate);
            }
        })

        // 将验证码加到id为captcha的元素里
        // 验证码将会在下面指定的元素中显示出来
        captchaObj.appendTo("#embed-captcha");
        captchaObj.onReady(function () {
            $("#wait").removeClass('show').addClass('hide');
        });
        $(document).ajaxStart(function () {
            loading = layer.load(2);
            $("#embed-submit").prop('disabled', true);
        }).ajaxStop(function () {
            $("#embed-submit").prop('disabled', false);
            layer.close(loading);
        });
    },
    loginVerify : function(data){
        if(data.userName.trim().length==0){
            _Login.errorTip('请输入登录账号');
            return false;
        }else if(data.passWord.trim().length==0){
            _Login.errorTip('请输入密码');
            return false;
        }else{
            return true;
        }
    },
    login : function(validate){
        var data = _Login.getFormData();
        if(_Login.loginVerify(data)){
            var param={
                'username': $("input[name='userName']").val(),
                'password':$.md5($("input[name='passWord']").val()),
                'geetest_challenge': validate.geetest_challenge,
                'geetest_validate': validate.geetest_validate,
                'geetest_seccode': validate.geetest_seccode
            };
            $.ajax({
                type: "POST",
                url: commonJs.root() +"/login?tt="+new Date().getTime(),
                contentType: "application/json; charset=utf-8",
                data:JSON.stringify(param),
                dataType:'json',
                success: function(msg){
                    if(msg.resultCode=="0"){
                        (data.redirectURL == null || data.redirectURL == '') ? window.location.href = commonJs.root() : window.location.href =  commonJs.root() + data.redirectURL;
                    }else{
                        _Login.errorTip(msg.resultMsg);
                    }
                }
            });
        }
    }


}