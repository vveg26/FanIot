
let websocket = null;


function webSocketService() {
    if ('WebSocket' in window) {
        websocket = new WebSocket('ws://localhost:8080/websocket');
        websocket.onopen = () => {
            Toastr('success','WebSocket连接成功');
            console.log('WebSocket连接成功');
            websocket.send('{"GetStatusCmd":1}');
        }
        websocket.onclose = () => {
            Toastr('warning','WebSocket连接关闭');
            console.log('WebSocket连接关闭');
        }
        websocket.onmessage = (event) => {
            console.log('【WebSocket服务端消息】： ' + event.data);
            let obj;
            if (event.data != null) {
                obj = JSON.parse(event.data);
            }
            if ("dht11Tem" in obj && "dht11Hum" in obj) {
                $("#relTimeTem").text("实时温度：" + obj.dht11Tem + " ºC");
                $("#relTimeHum").text("实时湿度：" + obj.dht11Hum + "%");
                addDeviceData(1, obj.dht11Tem + "," + obj.dht11Hum);
            }
            checkDeviceStatus(event.data);
        }
        websocket.onerror = () => {
            Toastr('error','WebSocket连接发生错误');
        }
    } else {
        Toastr('error','该浏览器不支持WebSocket');
    }
}


function webSocketClose() {
    if (websocket !== null) {
        websocket.close();
    }
}

function addDeviceData(deviceId, value) {
    $.ajax({
        url: URL_DEVICE_HEAD + "add_device_data_staff",
        type: "post",
        data: {"dataType": "measurement", "deviceId": deviceId, "value": value},
        headers: {
            "tokenValue": localStorage.getItem("tokenValue")
        },
        dataType: "json",
        success: function (result) {
            if (result.status == 0) {
                console.log("更新设备数据success", result.msg);
            } else {
                console.log("更新设备数据error", result.msg);
            }
        },
        error: function (xhr, textStatus, errorThrown) {
            showError("更新设备数据信息异常", xhr, textStatus, errorThrown);
        }
    });
}
