// const labels = [
//     'January',
//     'February',
//     'March',
//     'April',
//     'May',
//     'June',
// ];
// const data = {
//     labels: labels,
//     datasets: [{
//         label: 'My First dataset',
//         backgroundColor: 'rgb(255, 99, 132)',
//         borderColor: 'rgb(255, 99, 132)',
//         data: [0, 10, 5, 2, 20, 30, 45],
//     }]
// };
// const config = {
//     type: 'line',
//     data,
//     options: {}
// };


//实际开发板的LED灯是否亮、风扇是否开启、工作模式是否为自动
let isLedOn, isFanOn, isWorkModeAuto;
function checkDeviceStatus(payload) {
    //存放mqtt获取的数据
    let ledStatus = -1, fanStatus = -1, workModeStatus = -1, temLimit=-1, interval=-1;
    if (payload != null) {
        obj = JSON.parse(payload);
    }
    if ("FanStatus" in obj) fanStatus = obj.FanStatus;
    if ("LedStatus" in obj) ledStatus = obj.LedStatus;
    if ("WorkModeStatus" in obj) workModeStatus = obj.WorkModeStatus;
    if ("TemLimit" in obj) {
        $("#temSlider").val(obj.TemLimit);
        $("#temLimitVal-div").text(obj.TemLimit + "ºC");
    }
    if ("Interval" in obj){
        $("#intervalTime").attr("value", parseFloat(obj.Interval/1000) + "");
    }


    if (ledStatus === 1) {
        isLedOn = true;
        Toastr("success", "开灯")
        $("#light").attr("src", "resources/images/lighton.png");
    }
    if (ledStatus === 0) {
        isLedOn = false;
        Toastr("success", "关灯")
        $("#light").attr("src", "resources/images/lightoff.png");
    }
    if (fanStatus === 1) {
        isFanOn = true;
        Toastr("success", "开风扇")
        $('#btn').prop("checked", true); //开风扇
    }
    if (fanStatus === 0) {
        isFanOn = false;
        Toastr("success", "关风扇")
        $('#btn').prop("checked", false); //关风扇
    }
    if (workModeStatus === 1) {
        isWorkModeAuto = true;
        Toastr("success", "自动模式")
        $('#btn').attr("disabled", true); //禁止点击风扇开关
        $('#btn-auto').css('height', '5px'); //自动模式
        $("#temSlider").prop("disabled", false); //允许设置温度阀值
    }
    if (workModeStatus === 0) {
        isWorkModeAuto = false;
        Toastr("success", "手动模式")
        $('#btn').attr("disabled", false); //允许点击风扇开关
        $('#btn-auto').css('height', '12px'); //手动模式
        $("#temSlider").prop("disabled", true); //禁止设置温度阀值
    }
    // console.log("实际开发板状态：isLedOn=" + isLedOn + "，isFanOn=" + isFanOn + "，isWorkModeAuto=" + isWorkModeAuto
    //     + ", temLimit=" + temLimit + ", interval=" + interval);

}

function onFanWorkModeClick() {
    if (!isWorkModeAuto) {
       //$('#btn-auto').css('height', '10px'); //自动模式
       $('#btn-auto').css('background-color', 'blue'); //自动模式
        $('#btn').prop("checked", false); //关风扇
        websocket.send('{"WorkModeCmd":1}');
    }
    if (isWorkModeAuto) {
        //$('#btn-auto').css('height', '40px'); //手动模式
       $('#btn-auto').css('background-color', 'red'); //自动模式
        $('#btn').prop("checked", false); //关风扇
        websocket.send('{"WorkModeCmd":0}');
    }
}

function onFanSwitchClick() {
    if (isWorkModeAuto){
        Toastr("warning", "当前为自动模式，禁止控制风扇开关");
    }
    if (!isWorkModeAuto) {
        if (!isFanOn) {
            // $('#btn').css('height', '5px'); //按下风扇开按钮
            //$('#btn').css('background-color', 'blue');
            // $('#btn').prop("checked", true); //开风扇
            websocket.send('{"FanCmd":1}');

        }
        if (isFanOn) {
            // $('#btn').css('height', '12px'); //按下风扇关按钮
            // $('#btn').prop("checked", false); //关风扇
           // $('#btn').css('background-color', 'green');
            websocket.send('{"FanCmd":0}');
        }
    }
}

function onLightClick() {
    if (!isLedOn) {
        websocket.send('{"LedCmd":1}');
    } else if (isLedOn) {
        websocket.send('{"LedCmd":0}');
    }
}

function onTemLimitChange() {
    let temLimitVal = parseInt($("#temSlider").val());
    $("#temLimitVal-div").text(temLimitVal + "ºC");
    websocket.send('{"TemLimit":' + temLimitVal + '}');
    Toastr("success", "温度阀值为" + temLimitVal + "ºC");
}

function onIntervalTimeChange() {
    let intervalTimeVal = $("#intervalTime").val();
    if (intervalTimeVal < 1.5){
        Toastr("error", "时间间隔不得低于1.5秒");
    } else {
        websocket.send('{"Interval":' + intervalTimeVal * 1000 + '}');
        Toastr("success", "时间间隔设置为" + intervalTimeVal + "秒");
    }
}

