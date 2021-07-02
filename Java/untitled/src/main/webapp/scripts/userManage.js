const URL_USER_HEAD = "http://localhost:8080/user/"

function redirect2DevicePage() {
    if (localStorage.getItem("tokenValue")) {
        let role = localStorage.getItem("role");
        if (role === "developer") {
            window.location.href = "developdevicemanage.html";
        } else {
            window.location.href = "userdevicemanage.html";
        }
    }
}

function redirect2UserPage() {
    if (localStorage.getItem("tokenValue")) {
        let role = localStorage.getItem("role");
        if (role === "admin") {
            window.location.href = "admincenter.html";
        } else {
            window.location.href = "usercenter.html";
        }
    }
}

function redirect2HomePage() {
    window.location.href = "homepage.html";
}

function getUserInfoLoad() {
    if (localStorage.getItem("tokenValue")) {
        let name = localStorage.getItem("name");
        let role = localStorage.getItem("role");
        let info = role + "：" + name + "，退出登录";
        // 页面右上角登录信息
        $("#u46_text").text(info); // usercenter.html
        $("#u70_text").text(info); // admincenter.html
        $("#u6_text").text(info); // homepage.html
        $("#u137_text").text(info); // developdevicemanage.html
        $("#u103_text").text(info); // userdevicemanage.html

        if (role !== "admin") {
            let user = findSelfUser(localStorage.getItem("id"));
            // usercenter.html基本信息
            $("#u63_input").val(user.name);
            $("#u52_input").val(user.email);
            $("#u54_input").val(user.age);
            $("#u61_input").val(user.role);
        }
    }
}

function onUserManageLoad() {
    findAllUser();
}

function logoutClear() {
    if (localStorage.getItem("tokenValue")) {
        localStorage.clear();
        $("#46_text").text("登录"); // usercenter.html
        $("#u70_text").text("登录"); // admincenter.html
        $("#u6_text").text("登录"); // homepage.html
    }
}

function onLogout() {
    logoutClear();
    window.location.href = "login.html";
}

function onForgetPassword() {
    const email = $("#u38_input").val();
    $.ajax({
        url: URL_USER_HEAD + "forget_password",
        type: "get",
        data: {"email": email},
        dataType: "json",
        success: function (result) {
            if (result.status === 0) {
                Toastr("success", result.msg);
            } else {
                Toastr("error", result.msg);
            }
        },
        error: function (xhr, textStatus, errorThrown) {
            showError("发送失败", xhr, textStatus, errorThrown);
        }
    });
}

function onResetPasswordLoad() {
    $("#resetpassword_name").val(GetURLParameter("name"));
}

function onResetPasswordClick() {
    const tokenValue = GetURLParameter("tokenValue");
    const id = tokenValue.split("_")[0];
    const password = $("#resetpassword_pwd").val();
    if (password == null || password === "") {
        Toastr("warning", "请保证密码不为空");
        return;
    }
    $.ajax({
        url: URL_USER_HEAD + "reset_password",
        type: "put",
        data: {"id": id, "password": password},
        headers: {
            "tokenValue": tokenValue
        },
        dataType: "json",
        success: function (result) {
            if (result.status == 0) {
                Toastr("success", result.msg + "");
                setTimeout(function () {
                    window.location.href = "login.html";
                }, 500)
            } else {
                Toastr("error", result.msg);
            }
            findAllUser();
        },
        error: function (xhr, textStatus, errorThrown) {
            showError("重置密码异常", xhr, textStatus, errorThrown);
        }
    });
}

function GetURLParameter(sParam) {
    var sPageURL = window.location.search.substring(1);
    var sURLVariables = sPageURL.split('&');
    for (var i = 0; i < sURLVariables.length; i++) {
        var sParameterName = sURLVariables[i].split('=');
        if (sParameterName[0] == sParam) {
            return sParameterName[1];
        }
    }
}

function onLoginUser() {
    logoutClear();
    const userName = $("#u10_input").val();
    const password = $("#u11_input").val();
    $.ajax({
        url: URL_USER_HEAD + "login",
        type: "get",
        data: {"id": 0, "name": userName, "password": password},
        dataType: "json",
        success: function (result) {
            console.log(result)
            if (result.status === 0) {
                Toastr("success", result.msg)
                localStorage.setItem("tokenValue", result.data);
                let role = result.data.split("_")[1];
                let id = result.data.split("_")[0];
                localStorage.setItem("id", id);
                localStorage.setItem("role", role);
                localStorage.setItem("name", userName);
                setTimeout(function () {
                    if (role === "admin") {
                        window.location.href = "admincenter.html";
                    } else {
                        window.location.href = "usercenter.html";
                    }
                }, 500)
            } else {
                Toastr("error", result.msg)
            }
        },
        error: function (xhr, textStatus, errorThrown) {
            showError("登录失败", xhr, textStatus, errorThrown);
        }
    });
}

function onSignupUser() {
    const userName = $("#u21_input").val();
    const password = $("#u22_input").val();
    const passwordRepeat = $("#u25_input").val();
    const email = $("#u27_input").val();
    const role = $("#u36_input").val();
    if (password !== passwordRepeat) {
        Toastr("warning", "两次密码不一致，请重新输入");
        return;
    }
    $.ajax({
        url: URL_USER_HEAD + "signup",
        type: "post",
        data: {"name": userName, "password": password, "email": email, "role": role},
        dataType: "json",
        success: function (result) {
            if (result.status === 0) {
                Toastr("success", result.msg + "，请登录")
                setTimeout(function () {
                    window.location.href = "login.html";
                }, 500)
            } else {
                Toastr("error", result.msg)
            }
            findAllUser();
        },
        error: function (xhr, textStatus, errorThrown) {
            showError("注册用户异常", xhr, textStatus, errorThrown);
        }
    });
}

function onAddUser() {
    const userName = $("#u90_input").val();
    const email = $("#u91_input").val();
    const role = $("#u93_input").val();
    const password = $("#u94_input").val();
    const age = Number($("#u92_input").val());
    if (userName == null || password == null || email == null || age == null || role == null ||
        userName === "" || password === "" || email === "" || age == "" || role === "") {
        Toastr("warning", "请保证更新参数不为空");
        return;
    }
    $.ajax({
        url: URL_USER_HEAD + "add",
        type: "post",
        data: {"name": userName, "password": password, "email": email, "age": age, "role": role},
        headers: {
            "tokenValue": localStorage.getItem("tokenValue")
        },
        dataType: "json",
        success: function (result) {
            if (result.status == 0) {
                Toastr("success", result.msg);
            } else {
                Toastr("error", result.msg);
            }
            findAllUser();
        },
        error: function (xhr, textStatus, errorThrown) {
            showError("新增用户异常", xhr, textStatus, errorThrown);
        }
    });
}

function onFindUser() {
    const id = Number($("#txtFindId").val());
    const userName = $("#txtFindUserName").val();
    const email = $("#txtFindEmail").val();
    const age = Number($("#txtFindAge").val());
    findUser(id, userName, email, age);
}

function findSelfUser(id) {
    let user;
    $.ajax({
        url: URL_USER_HEAD + "find_self",
        type: "get",
        data: {"id": id},
        headers: {
            "tokenValue": localStorage.getItem("tokenValue")
        },
        dataType: "json",
        async: false,
        success: function (result) {
            user = result.data[0];
        },
        error: function (xhr, textStatus, errorThrown) {
            showError("查询用户异常", xhr, textStatus, errorThrown);
        }
    });
    return user;
}

function findUser(id, userName, email, age) {
    let users;
    $.ajax({
        url: URL_USER_HEAD + "find",
        type: "get",
        data: {"id": id, "name": userName, "email": email, "age": age},
        headers: {
            "tokenValue": localStorage.getItem("tokenValue")
        },
        dataType: "json",
        async: false,
        success: function (result) {
            clearUserList();
            generateUserTableList(result.data);
            users = result.data;
        },
        error: function (xhr, textStatus, errorThrown) {
            showError("查询用户异常", xhr, textStatus, errorThrown);
        }
    });
    return users;
}

function findAllUser() {
    findUser(0, null, null, 0);
}

function generateUserTableList(users) {
    clearUserList();
    var tableBuilder = new UserTableBuilder();
    for (let i = 0; i < users.length; i++) {
        let user = users[i];
        tableBuilder.addUserRow(i + 1, user.id, user.name, user.email, user.age, user.role, user.password);
    }
    tableBuilder.build();
}

function clearUserList() {
    $("#u73-data").remove();
    $("#u90_input").val("");
    $("#u91_input").val("");
    $("#u92_input").val("");
    $("#u94_input").val("");
}

function onModifyPasswordClick() {
    let password;
    password = prompt('请输入原密码:');
    let id = localStorage.getItem("id");
    if (password !== findSelfUser(id).password) {
        alert("密码错误！");
    } else {
        password = prompt('请输入新密码:');
        if (password == null || password == "") {
            alert("请保证密码不为空");
        } else {
            updatePassword(id, password);
            alert("密码修改成功，请重新登录");
            onLogout();
        }
    }

}

function updatePassword(id, password) {
    $.ajax({
        url: URL_USER_HEAD + "update_self",
        type: "put",
        data: {"id": id, "password": password},
        headers: {
            "tokenValue": localStorage.getItem("tokenValue")
        },
        dataType: "json",
        success: function (result) {
            if (result.status == 0) {
                Toastr("success", result.msg);
            } else {
                Toastr("error", result.msg);
            }
            findAllUser();
        },
        error: function (xhr, textStatus, errorThrown) {
            showError("更新用户信息异常", xhr, textStatus, errorThrown);
        }
    });
}

function onModifyUserClick(index) {
    const id = Number($("#rUserId-" + index).text());
    const userName = $("#txtUserName-" + index).val();
    const password = $("#txtPassword-" + index).val();
    const email = $("#txtEmail-" + index).val();
    const age = Number($("#txtAge-" + index).val());
    const role = $("#selRole-" + index).val();
    if (userName == null || password == null || email == null || age == null ||
        userName === "" || password === "" || email === "" || age == "") {
        Toastr("warning", "请保证更新参数不为空");
        return;
    }
    $.ajax({
        url: URL_USER_HEAD + "update",
        type: "put",
        data: {"id": id, "name": userName, "password": password, "email": email, "age": age, "role": role},
        headers: {
            "tokenValue": localStorage.getItem("tokenValue")
        },
        dataType: "json",
        success: function (result) {
            if (result.status == 0) {
                Toastr("success", result.msg);
            } else {
                Toastr("error", result.msg);
            }
            findAllUser();
        },
        error: function (xhr, textStatus, errorThrown) {
            showError("更新用户信息异常", xhr, textStatus, errorThrown);
        }
    });
}

function onModifySelfUserClick(index) {
    const id = localStorage.getItem("id");
    // const userName = $("#u63_input").val();
    const email = $("#u52_input").val();
    const age = Number($("#u54_input").val());
    // const role = $("#u61_input").val();

    if (email == null || age == null ||
        email === "" || age == "") {
        Toastr("warning", "请保证更新参数不为空");
        return;
    }
    $.ajax({
        url: URL_USER_HEAD + "update_self",
        type: "put",
        data: {"id": id, "email": email, "age": age},
        headers: {
            "tokenValue": localStorage.getItem("tokenValue")
        },
        dataType: "json",
        success: function (result) {
            if (result.status == 0) {
                Toastr("success", result.msg);
            } else {
                Toastr("error", result.msg);
            }
            findAllUser();
        },
        error: function (xhr, textStatus, errorThrown) {
            showError("更新用户信息异常", xhr, textStatus, errorThrown);
        }
    });
}

function onDeleteUserClick(index) {
    const id = Number($("#rUserId-" + index).text());
    $.ajax({
        url: URL_USER_HEAD + "delete",
        type: "delete",
        data: {"id": id},
        headers: {
            "tokenValue": localStorage.getItem("tokenValue")
        },
        dataType: "json",
        success: function (result) {
            if (result.status == 0) {
                Toastr("success", result.msg);
            } else {
                Toastr("error", result.msg);
            }
            findAllUser();
        },
        error: function (xhr, textStatus, errorThrown) {
            showError("删除用户信息异常", xhr, textStatus, errorThrown);
        }
    });
    findAllUser();

}


function UserTableBuilder() {
    this.builder = new StringBuilder();
    this.interval = 25;
    this.builder.append('<div id="u73-data">');
}

UserTableBuilder.prototype.build = function () {
    this.builder.append('</div>');
    var value = this.builder.toString();
    //console.log(value);
    $("#u73").append(value);
}

UserTableBuilder.prototype.addUserRow = function (index, id, name, email, age, role, password) {
    //alert("index: " + index);
    var rUserIdId = "rUserId-" + index;
    var txtNameId = "txtUserName-" + index;
    var txtEmailId = "txtEmail-" + index;
    var txtAgeId = "txtAge-" + index;
    var selRoleId = "selRole-" + index;
    var txtPasswordId = "txtPassword-" + index;
    var btnModifyUserId = "btnModifyUser-" + index;
    var btnDeleteUserId = "btnDeleteUser-" + index;
    var rowTop = this.interval * (index - 1);

    //新的用户信息行
    this.builder.append('<div class="preeval" style="width: 646px; height: 25px;">');
    //用户ID
    this.builder.append('<div class="ax_default box_1 u81" data-label="rId" ');
    this.builder.append('style="width: 44px; height: 25px; left: 0px; top: ');
    this.builder.append(rowTop);
    this.builder.append('px;visibility: inherit">');
    this.builder.append('<div class="u81_div" style="width: 44px; height: 25px;visibility: inherit"></div>');
    this.builder.append('<div class="text u81_text" style="visibility: inherit">');
    this.builder.append('<div id="');
    this.builder.append(rUserIdId);
    this.builder.append('">');
    this.builder.append(id);
    this.builder.append('</div>');
    this.builder.append('</div>');
    this.builder.append('</div>');
    //用户名
    this.builder.append('<div class="ax_default text_field u74" data-label="txtName" ');
    this.builder.append('style="width: 100px; height: 25px; left: 44px; top: ');
    this.builder.append(rowTop);
    this.builder.append('px;visibility: inherit">');
    this.builder.append('<div class="u74_div" style="width: 100px; height: 25px;visibility: inherit"></div>');
    this.builder.append('<input id="');
    this.builder.append(txtNameId);
    this.builder.append('" type="text" value="');
    this.builder.append(name);
    this.builder.append('" class="u74_input"/>');
    this.builder.append('</div>');
    //邮箱
    this.builder.append('<div class="ax_default text_field u75" data-label="txtEmail" ');
    this.builder.append('style="width: 155px; height: 25px; left: 144px; top: ');
    this.builder.append(rowTop);
    this.builder.append('px;visibility: inherit">');
    this.builder.append('<div class="u75_div" style="width: 155px; height: 25px;visibility: inherit"></div>');
    this.builder.append('<input id="');
    this.builder.append(txtEmailId);
    this.builder.append('" type="text" value="');
    this.builder.append(email);
    this.builder.append('" class="u75_input"/>');
    this.builder.append('</div>');
    //年龄
    this.builder.append('<div class="ax_default text_field u76" data-label="txtAge" ');
    this.builder.append('style="width: 67px; height: 25px; left: 299px; top: ');
    this.builder.append(rowTop);
    this.builder.append('px;visibility: inherit">');
    this.builder.append('<div class="u76_div" style="width: 67px; height: 25px;visibility: inherit"></div>');
    this.builder.append('<input id="');
    this.builder.append(txtAgeId);
    this.builder.append('" type="text" value="');
    this.builder.append(age);
    this.builder.append('" class="u76_input"/>');
    this.builder.append('</div>');
    //角色
    this.builder.append('<div class="ax_default droplist u77" data-label="selRole" ');
    this.builder.append('style="width: 100px; height: 25px; left: 366px; top: ');
    this.builder.append(rowTop);
    this.builder.append('px;visibility: inherit">');
    this.builder.append('<div class="u77_div" style="width: 100px; height: 25px;visibility: inherit"></div>');
    this.builder.append('<select id="');
    this.builder.append(selRoleId);
    this.builder.append('" class="u77_input">');
    this.builder.append('<option class="u77_input_option" ');
    if (role === "staff") {
        this.builder.append('selected ');
    }
    this.builder.append('value="staff">普通用户</option>');
    this.builder.append('<option class="u77_input_option" ');
    if (role === "admin") {
        this.builder.append('selected ');
    }
    this.builder.append('value="admin">管理员</option>');
    this.builder.append('<option class="u77_input_option" ');
    if (role === "developer") {
        this.builder.append('selected ');
    }
    this.builder.append('value="developer">开发者</option>');
    this.builder.append('</select>');
    this.builder.append('</div>');
    //密码
    this.builder.append('<div class="ax_default text_field u78" data-label="txtPassword" ');
    this.builder.append('style="width: 100px; height: 25px; left: 466px; top: ');
    this.builder.append(rowTop);
    this.builder.append('px;visibility: inherit">');
    this.builder.append('<div class="u78_div" style="width: 100px; height: 25px;visibility: inherit"></div>');
    this.builder.append('<input id="');
    this.builder.append(txtPasswordId);
    this.builder.append('" type="text" value="');
    this.builder.append(password);
    this.builder.append('" class="u78_input"/>');
    this.builder.append('</div>');
    //修改按钮
    this.builder.append('<div id="');
    this.builder.append(btnModifyUserId);
    this.builder.append('" class="ax_default button u79" data-label="btnModifyUser" ');
    this.builder.append('style="width: 40px; height: 25px; left: 566px; top: ');
    this.builder.append(rowTop);
    this.builder.append('px;visibility: inherit" onclick="onModifyUserClick(');
    this.builder.append(index);
    this.builder.append(')">');
    this.builder.append('<div class="u79_div" style="width: 40px; height: 25px;visibility: inherit"></div>');
    this.builder.append('<div class="text u79_text" style="visibility: inherit">');
    this.builder.append('<p><span style="text-decoration:none;">修改</span></p>');
    this.builder.append('</div>');
    this.builder.append('</div>');
    //删除按钮
    this.builder.append('<div id="');
    this.builder.append(btnDeleteUserId);
    this.builder.append('" class="ax_default button u80" data-label="btnDeleteUser" ');
    this.builder.append('style="width: 40px; height: 25px; left: 606px; top: ');
    this.builder.append(rowTop);
    this.builder.append('px;visibility: inherit" onclick="onDeleteUserClick(');
    this.builder.append(index);
    this.builder.append(')">');
    this.builder.append('<div class="u80_div" style="width: 40px; height: 25px;visibility: inherit"></div>');
    this.builder.append('<div class="text u80_text" style="visibility: inherit">');
    this.builder.append('<p><span style="text-decoration:none;">删除</span></p>');
    this.builder.append('</div>');
    this.builder.append('</div>');

    this.builder.append('</div>');
}
