//获取url参数
function getUrlPara(variable)
{
    var query = window.location.search.substring(1);
    var vars = query.split("&");
    for (var i=0;i<vars.length;i++) {
        var pair = vars[i].split("=");
        if(pair[0] == variable){return pair[1];}
    }
    return false;
}

// type: success info warning error
function Toastr(type, msg) {
    toastr.options = {
        "closeButton": false,
        "debug": false,
        "newestOnTop": false,
        "progressBar": false,
        "positionClass": "toast-top-center",
        "preventDuplicates": false,
        "onclick": null,
        "showDuration": "800",
        "hideDuration": "500",
        "timeOut": "1000",
        "extendedTimeOut": "200",
        "showEasing": "swing",
        "hideEasing": "linear",
        "showMethod": "fadeIn",
        "hideMethod": "fadeOut"
    }
    toastr[type](msg)
}

function showError(label, xhr, textStatus, errorThrown) {
    Toastr("error", "操作失败");
    // Toastr("error", "label:\n"
    //     + "状态码：" + xhr.status + "\n"
    //     + "状态：" + xhr.readyState + "\n" /*0-未初始化，1-正在载入，2-已经载入，3-数据进行交互，4-完成*/
    //     + "错误信息：" + xhr.statusText + "\n"
    //     + "返回相应信息：" + xhr.responseText + "\n"
    //     + "请求状态：" + textStatus + "\n"
    //     + "完整异常：" + errorThrown)
}


function StringBuilder() {
    this.strings = new Array("");
    this.strings.length = 0;
}

// Appends the given value to the end of this instance.
StringBuilder.prototype.append = function (value) {
    if (value) {
        this.strings.push(value);
    }
}
// Clears the string buffer
StringBuilder.prototype.clear = function () {
    this.strings.length = 0;
}
// Converts this instance to a String.
StringBuilder.prototype.toString = function () {
    return this.strings.join("");
}

StringBuilder.prototype.size = function () {
    return this.strings.length;
}
