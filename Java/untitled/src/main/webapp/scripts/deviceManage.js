const URL_DEVICE_HEAD = "http://localhost:8080/device/"

function onControlDeviceClick() {
    let deviceName = $("#u108_input").val();
    window.location.href = "userdevicecontrol.html?deviceName=" + deviceName;
}

function onDeviceManageLoad() {
    var tableBuilder = new DeviceTableBuilder();
    findAllDevices();
    tableBuilder.build();
}

function clearDeviceList() {
    $("#u111-data").remove();
    $("#u125_input").val("");
    $("#u108_input").empty();
    $("#u128_input").val("");
}

function clearDeviceDataTable() {
    $("#my_device_data").empty();
}

function generateDeviceTableList(devices) {
    clearDeviceList();
    var tableBuilder = new DeviceTableBuilder();
    for (let i = 0; i < devices.length; i++) {
        let device = devices[i];
        tableBuilder.addDeviceRow(i + 1, device.id, device.name, device.latestUpdateTime, device.online, device.dataType);
    }
    tableBuilder.build();
}

function showDeviceData() {
    let deviceName = $("#u108_input").val();
    $.ajax({
        url: URL_DEVICE_HEAD + "find_device_all_data",
        type: "get",
        data: {"name": deviceName},
        headers: {
            "tokenValue": localStorage.getItem("tokenValue")
        },
        dataType: "json",
        async: false,
        success: function (result) {
            clearDeviceDataTable();
            DeviceDataTableBuilder(result.msg, result.data);
        },
        error: function (xhr, textStatus, errorThrown) {
            showError("查询设备数据异常", xhr, textStatus, errorThrown);
        }
    });
}

function findDevice(deviceName) {
    let device;
    $.ajax({
        url: URL_DEVICE_HEAD + "find_device",
        type: "get",
        data: {"name": deviceName},
        headers: {
            "tokenValue": localStorage.getItem("tokenValue")
        },
        dataType: "json",
        async: false,
        success: function (result) {
            device = result.data;
        },
        error: function (xhr, textStatus, errorThrown) {
            showError("查询设备异常", xhr, textStatus, errorThrown);
        }
    });
    return device;
}

function findAllDevices() {
    $("#u141_input").empty();
    $("#u155_input").empty();
    let devices;
    $.ajax({
        url: URL_DEVICE_HEAD + "find_all_devices",
        type: "get",
        // data: {"id": id, "name": userName, "email": email, "age": age},
        headers: {
            "tokenValue": localStorage.getItem("tokenValue")
        },
        dataType: "json",
        async: false,
        success: function (result) {
            devices = result.data;
            if (localStorage.getItem("role") === "staff") {
                clearDeviceList();
                generateDeviceTableList(result.data);
                for (const device of devices) {
                    $("#u108_input").append('<option class="u113_input_option" value="' + device.name + '">' + device.name + '</option>');
                }
            }
            if (localStorage.getItem("role") === "developer") {
                $("#u155_input").append('<option class="u113_input_option" value="" selected>请选择</option>');
                for (const device of devices) {
                    $("#u141_input").append('<option class="u113_input_option" value="' + device.name + '">' + device.name + '</option>');
                    $("#u155_input").append('<option class="u113_input_option" value="' + device.name + '">' + device.name + '</option>');
                }
            }
        },
        error: function (xhr, textStatus, errorThrown) {
            showError("查询设备异常", xhr, textStatus, errorThrown);
        }
    });
    return devices;
}

function onAddDevice() {
    const id = $("#u128_input").val();
    const deviceName = $("#u125_input").val();
    const online = $("#u130_input").val();
    const dataType = $("#u126_input").val();
    if (id == null || deviceName == null ||
        id === "" || deviceName === "") {
        Toastr("warning", "请保证更新参数不为空");
        return;
    }
    $.ajax({
        url: URL_DEVICE_HEAD + "add_device",
        type: "post",
        data: {"id": id, "name": deviceName, "online": online, "dataType": dataType},
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
            findAllDevices();
        },
        error: function (xhr, textStatus, errorThrown) {
            showError("新增设备异常", xhr, textStatus, errorThrown);
        }
    });
}

function onAddDeviceDataSelectChange() {
    let deviceName = $("#u141_input").val();
    let device = findDevice(deviceName);
    if (device.dataType === "waypoint") {
        $("#madd_div1").css("display", "none");
        $("#madd_div_waypoint").css("display", "block");
    } else {
        $("#madd_div1").css("display", "block");
        $("#madd_div_waypoint").css("display", "none");
    }
}

function onAddDeviceDataClick() {
    let deviceName = $("#u141_input").val();
    let device = findDevice(deviceName);
    const value = $("#u145_input").val();
    const longitude = $("#madd_div2_input1").val();
    const latitude = $("#madd_div2_input2").val();
    const elevation = $("#madd_div2_input3").val();
    if (device.dataType === "waypoint") {
        if (longitude == null || longitude === "" ||
            latitude == null || latitude === "" ||
            elevation == null || elevation === "") {
            Toastr("warning", "请保证更新参数不为空");
            return;
        }
    } else {
        if (value == null || value === "" ) {
            Toastr("warning", "请保证更新参数不为空");
            return;
        }
    }
    $.ajax({
        url: URL_DEVICE_HEAD + "add_device_data",
        type: "post",
        data: {"dataType": device.dataType, "deviceId": device.id, "value": value,
        "longitude": longitude, "latitude": latitude, "elevation": elevation},
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
            findAllDevices();
            $("#u145_input").val("");
            $("#madd_div2_input1").val("");
            $("#madd_div2_input2").val("");
            $("#madd_div2_input3").val("");
        },
        error: function (xhr, textStatus, errorThrown) {
            showError("更新设备数据信息异常", xhr, textStatus, errorThrown);
        }
    });
}

function onDelInputValueChange() {
    // 如果设备选择框为空，则禁止填写key和value，可以填写设备id
    if ($("#u155_input").val() === ""){
        $("#u157_input").val("");
        $("#u159_input").val("");
        $("#u157_input").prop('disabled', true); // 禁止key输入
        $("#u157_input").prop('placeholder', '禁止输入');
        $("#u159_input").prop('disabled', true); // 禁止value输入
        $("#u159_input").prop('placeholder', '禁止输入');
        $("#u153_input").prop('disabled', false); // 允许设备id输入
        $("#u153_input").prop('placeholder', '选择设备后将禁止输入id');
    } else { // 如果设备选择框已经选择，则允许填写key和value，禁止填写设备id
        $("#u157_input").prop('disabled', false); // 允许key输入
        $("#u157_input").prop('placeholder', '');
        $("#u159_input").prop('disabled', false); // 允许value输入
        $("#u159_input").prop('placeholder', '');
        $("#u153_input").prop('disabled', true); // 禁止设备id输入
        $("#u153_input").val("");
        $("#u153_input").prop('placeholder', '选择"请选择"后才能输入id');
    }
}

function onDeleteDeviceDataClick() {
    const deviceId = Number($("#u153_input").val());
    const deviceName = $("#u155_input").val();
    const key = $("#u157_input").val();
    const value = $("#u159_input").val();
    if ($("#u155_input").val() === "") {
        if ($("#u153_input").val() === ""){
            Toastr("warning", "请保证设备id不为空");
        }
    } else {
        if ($("#u157_input").val() === "" || $("#u159_input").val() === ""){
            Toastr("warning", "请保证key、value不为空");
        }
    }
        $.ajax({
        url: URL_DEVICE_HEAD + "delete_device_data",
        type: "delete",
        data: {"deviceId": deviceId, "deviceName": deviceName,
            "key": key, "value": value},
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
        },
        error: function (xhr, textStatus, errorThrown) {
            showError("删除设备异常", xhr, textStatus, errorThrown);
        }
    });
}

function onModifyDeviceClick(index) {
    const id = Number($("#rId-" + index).text());
    const deviceName = $("#txtName-" + index).val();
    const online = Number($("#selOnline-" + index).val());
    const dataType = $("#selDataType-" + index).val();
    if (deviceName == null || deviceName === "") {
        Toastr("warning", "请保证更新参数不为空");
        return;
    }
    $.ajax({
        url: URL_DEVICE_HEAD + "update_device",
        type: "put",
        data: {"id": id, "name": deviceName, "online": online, "dataType": dataType},
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
            findAllDevices();
        },
        error: function (xhr, textStatus, errorThrown) {
            showError("更新设备信息异常", xhr, textStatus, errorThrown);
        }
    });
}

function onDeleteDeviceClick(index) {
    const id = Number($("#rId-" + index).text());
    $.ajax({
        url: URL_DEVICE_HEAD + "delete_device",
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
            findAllDevices();
        },
        error: function (xhr, textStatus, errorThrown) {
            showError("删除设备异常", xhr, textStatus, errorThrown);
        }
    });
    findAllDevices();
}

function DeviceDataTableBuilder(dataType, data) {
    if (dataType === "dataType:waypoint") {
        $("#my_device_data").append('<tr><th>设备ID</th><th style="width:300px">时间戳</th><th style="width:100px">经度</th><th style="width:100px">纬度</th><th style="width:100px">海拔</th></tr>');
        for (const d of data) {
            $("#my_device_data").append("<tr>"
                + "<td>" + d.deviceId + "</td>"
                + "<td>" + d.timing + "</td>"
                + "<td>" + d.longitude + "</td>"
                + "<td>" + d.latitude + "</td>"
                + "<td>" + d.elevation + "</td>" +
                "</tr>");
        }
    } else if (dataType === "dataType:alert") {
        $("#my_device_data").append('<tr><th>设备ID</th><th style="width:300px">时间戳</th><th style="width:100px">数值</th></tr>');
        for (const d of data) {
            $("#my_device_data").append("<tr>"
                + "<td>" + d.deviceId + "</td>"
                + "<td>" + d.timing + "</td>"
                + "<td>" + d.news + "</td>" +
                "</tr>");
        }
    } else {
        $("#my_device_data").append('<tr><th>设备ID</th><th style="width:300px">时间戳</th><th style="width:100px">数值</th></tr>');
        for (const d of data) {
            $("#my_device_data").append("<tr>"
                + "<td>" + d.deviceId + "</td>"
                + "<td>" + d.timing + "</td>"
                + "<td>" + d.value + "</td>" +
                "</tr>");
        }
    }
}

/**
 * 该类实现设备表格动态添加行
 */
function DeviceTableBuilder() {
    this.builder = new StringBuilder();
    this.interval = 25;

    $("#u111-data").remove();
    this.builder.append('<div id="u111-data">');
}

DeviceTableBuilder.prototype.addDeviceRow = function (index, id, name, timestamp, online, dataType) {
    var rIdId = "rId-" + index;
    var txtNameId = "txtName-" + index;
    var rLatestTimestampId = "rLatestTimestamp-" + index;
    var selOnlineId = "selOnline-" + index;
    var selDataTypeId = "selDataType-" + index;
    var btnModifyDeviceId = "btnModifyDevice-" + index;
    var btnDeleteDeviceId = "btnDeleteDevice-" + index;
    var rowTop = this.interval * (index - 1);

    //新的设备信息行
    this.builder.append('<div class="preeval" style="width: 561px; height: 25px;">');
    //设备ID
    this.builder.append('<div class="ax_default box_1 u116" data-label="rId" ');
    this.builder.append('style="width: 44px; height: 25px; left: 0px; top: ');
    this.builder.append(rowTop);
    this.builder.append('px;visibility: inherit">');
    this.builder.append('<div class="u116_div" style="width: 44px; height: 25px;visibility: inherit"></div>');
    this.builder.append('<div class="text u116_text" style="visibility: inherit">');
    this.builder.append('<p id="');
    this.builder.append(rIdId);
    this.builder.append('"><span style="text-decoration:none;">');
    this.builder.append(id);
    this.builder.append('</span></p>');
    this.builder.append('</div>');
    this.builder.append('</div>');
    //设备名称
    this.builder.append('<div class="ax_default text_field u112" data-label="txtName"');
    this.builder.append(' style="width: 100px; height: 25px; left: 44px; top: ');
    this.builder.append(rowTop);
    this.builder.append('px;visibility: inherit">');
    this.builder.append('<div class="u112_div" style="width: 100px; height: 25px;visibility: inherit"></div>');
    this.builder.append('<input id="');
    this.builder.append(txtNameId);
    this.builder.append('" type="text" value="');
    this.builder.append(name);
    this.builder.append('" class="u112_input"/>');
    this.builder.append('</div>');
    //更新时间
    this.builder.append('<div class="ax_default box_1 u117" data-label="rLatestTimestamp" ');
    this.builder.append('style="width: 155px; height: 25px; left: 144px; top: ');
    this.builder.append(rowTop);
    this.builder.append('px;visibility: inherit">');
    this.builder.append('<div class="u117_div" style="width: 155px; height: 25px;visibility: inherit"></div>');
    this.builder.append('<div class="text u117_text" style="visibility: inherit">');
    this.builder.append('<p id="');
    this.builder.append(rLatestTimestampId);
    this.builder.append('"><span style="text-decoration:none;">');
    this.builder.append(timestamp);
    this.builder.append('</span></p>');
    this.builder.append('</div>');
    this.builder.append('</div>');
    //是否在线
    this.builder.append('<div class="ax_default droplist u118" data-label="selOnline" ');
    this.builder.append('style="width: 67px; height: 25px; left: 299px; top: ');
    this.builder.append(rowTop);
    this.builder.append('px;visibility: inherit">');
    this.builder.append('<div class="u118_div" style="width: 67px; height: 25px;visibility: inherit"></div>');
    this.builder.append('<select id="');
    this.builder.append(selOnlineId);
    this.builder.append('" class="u118_input">');
    this.builder.append('<option class="u118_input_option" ');
    if (online === 0) {
        this.builder.append('selected ');
    }
    this.builder.append('value=0>否</option>');
    this.builder.append('<option class="u118_input_option" ');
    if (online === 1) {
        this.builder.append('selected ');
    }
    this.builder.append('value=1>是</option>');
    this.builder.append('</select>');
    this.builder.append('</div>');
    //数值类型
    this.builder.append('<div class="ax_default droplist u113" data-label="selDeviceType" ');
    this.builder.append('style="width: 115px; height: 25px; left: 366px; top: ');
    this.builder.append(rowTop);
    this.builder.append('px;visibility: inherit">');
    this.builder.append('<div class="u113_div" style="width: 115px; height: 25px;visibility: inherit"></div>');
    this.builder.append('<select id="');
    this.builder.append(selDataTypeId);
    this.builder.append('" class="u113_input">');
    this.builder.append('<option class="u113_input_option" ');
    if (dataType === "measurement") {
        this.builder.append('selected ');
    }
    this.builder.append('value="measurement">数值测量值型</option>');
    this.builder.append('<option class="u113_input_option" ');
    if (dataType === "status") {
        this.builder.append('selected ');
    }
    this.builder.append('value="status">开关状态型</option>');
    this.builder.append('<option class="u113_input_option" ');
    if (dataType === "waypoint") {
        this.builder.append('selected ');
    }
    this.builder.append('value="waypoint">地理位置定位型</option>');
    this.builder.append('<option class="u113_input_option" ');
    if (dataType === "alert") {
        this.builder.append('selected ');
    }
    this.builder.append('value="alert">文本预警消息型</option>"');
    this.builder.append('</select>');
    this.builder.append('</div>');
    //修改按钮
    this.builder.append('<div id="');
    this.builder.append(btnModifyDeviceId);
    this.builder.append('" class="ax_default button u114" data-label="btnModifyDevice" ');
    this.builder.append('style="width: 40px; height: 25px; left: 481px; top: ');
    this.builder.append(rowTop);
    this.builder.append('px;visibility: inherit" onclick="onModifyDeviceClick(');
    this.builder.append(index);
    this.builder.append(')">');
    this.builder.append('<div class="u114_div" style="width: 40px; height: 25px;visibility: inherit"></div>');
    this.builder.append('<div class="text u114_text" style="visibility: inherit">');
    this.builder.append('<p><span style="text-decoration:none;">修改</span></p>');
    this.builder.append('</div>');
    this.builder.append('</div>');
    //删除按钮
    this.builder.append('<div id="');
    this.builder.append(btnDeleteDeviceId);
    this.builder.append('" class="ax_default button u115" data-label="btnDeleteDevice" ');
    this.builder.append('style="width: 40px; height: 25px; left: 521px; top: ');
    this.builder.append(rowTop);
    this.builder.append('px;visibility: inherit" onclick="onDeleteDeviceClick(');
    this.builder.append(index);
    this.builder.append(')">');
    this.builder.append('<div class="u115_div" style="width: 40px; height: 25px;visibility: inherit"></div>');
    this.builder.append('<div class="text u115_text" style="visibility: inherit">');
    this.builder.append('<p><span style="text-decoration:none;">删除</span></p>');
    this.builder.append('</div>');
    this.builder.append('</div>');
    //设备行收尾
    this.builder.append('</div>');
}

DeviceTableBuilder.prototype.build = function () {
    this.builder.append('</div>');
    var value = this.builder.toString();
    //console.log(value);
    $("#u111").append(value);
}
