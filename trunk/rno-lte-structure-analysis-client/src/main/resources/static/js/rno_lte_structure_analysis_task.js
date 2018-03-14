$(document).ready(function () {
    // 切换区域
    initAreaCascade();

    // 绑定时间控件
    datepickerBind();

    // 绑定提交事件
    $("#submitTask").click(function () {
        submitTask();
    });
});

/**
 * 初始化区域
 */
function initAreaCascade() {
    $("#provinceId").change(function () {
        getSubAreas("provinceId", "cityId", "市");
    });
}

/**
 * 绑定时间事件
 */
function datepickerBind() {
    $.datepicker.setDefaults({
        dateFormat: "yy-mm-dd",
        changeYear: true,
        changeMonth: true,
        showOtherMonths: true,
        selectOtherMonths: true,
        maxDate: "Today"
    });

    var begTime = $("#begTime");
    var endTime = $("#endTime");

    begTime.datepicker({
        // 缺省值，5天前
        defaultDate: "-5d",
        onClose: function (selectedDate) {
            // 调整结束时间最小值
            endTime.datepicker("option", "minDate", selectedDate);
        }
    });
    // 缺省值填入input
    begTime.datepicker("setDate", begTime.datepicker("option", "defaultDate"));

    endTime.datepicker({
        // 缺省值今天
        defaultDate: "Today",
        // 初始化最大最小值,5天前到今天
        minDate: "-5d",
        onClose: function (selectedDate) {
            // 调整开始时间最大值
            begTime.datepicker("option", "maxDate", selectedDate);
        }
    });
    // 缺省值填入input
    endTime.datepicker("setDate", endTime.datepicker("option", "defaultDate"));
}

// 提交结构分析任务
function submitTask() {
    var isMeet = checkTaskInfoSubmit();
    if (!isMeet) {
        return;
    }

    var cityId = $("#cityId").find("option:selected").val();
    var cityName = $("#cityId").find("option:selected").text().trim();
    var taskName = $("#taskName").val();
    var taskDescription = $("#taskDescription").val();
    var begTime = $("#begTime").val();
    var endTime = $("#endTime").val();

    $.ajax({
        url: 'submitLteStrucAnlsTask',
        data: {
            'cityId': cityId,
            'cityName': cityName,
            'taskName': taskName,
            'taskDesc': taskDescription,
            'begMeaTime': begTime,
            'endMeaTime': endTime
        },
        type: 'post',
        dataType: 'json',
        beforeSend: function () {
            showOperTips("loadingDataDiv", "loadContentId", "正在提交LTE结构优化分析任务");
        },
        success: function (data) {
            try {
                if (data['flag'] == false) {
                    alert(data['result']);
                } else {
                    alert("任务提交成功，请等待分析完成！");
                }
            } catch (err) {
            }
        },
        complete: function () {
            hideOperTips("loadingDataDiv");
            returnTaskList();
        }
    });
}

/**
 * 对提交分析任务的信息作验证
 */
function checkTaskInfoSubmit() {
    var n = 0;
    var m = 0;
    var flagName = true;
    var flagDesc = true;
    var flagDate = true;

    var taskName = $.trim($("#taskName").val());
    var taskDescription = $.trim($("#taskDescription").val());
    var begTime = $.trim($("#begTime").val());
    var endTime = $.trim($("#endTime").val());

    $("span#nameErrorText").html("");
    $("span#descErrorText").html("");
    $("span#dateErrorText").html("");
    $("span#nameError").html("");
    $("span#descError").html("");
    $("span#dateError").html("");
    if (ifHasSpecChar(taskName)) {
        $("span#nameErrorText").html("（名称包含特殊字符）");
        $("span#nameError").html("※");
        flagName = false;
    }
    if (ifHasSpecChar(taskDescription)) {
        $("span#descErrorText").html("（描述包含特殊字符）");
        $("span#descError").html("※");
        flagDesc = false;
    }
    for (var i = 0; i < taskName.length; i++) {     // 应用for循环语句,获取表单提交用户名字符串的长度
        var leg = taskName.charCodeAt(i);     // 获取字符的ASCII码值
        if (leg > 255) {        // 判断如果长度大于255
            n += 2;           // 则表示是汉字为2个字节
        } else {
            n += 1;           // 否则表示是英文字符,为1个字节
        }
    }
    for (var i = 0; i < taskDescription.length; i++) {
        var leg = taskDescription.charCodeAt(i);
        if (leg > 255) {
            m += 2;
        } else {
            m += 1;
        }
    }
    // 验证任务名称
    if (n > 25) {
        $("span#nameErrorText").html("（不超过25个字符）");
        $("span#nameError").html("※");
        flagName = false;
    } else if (n == 0) {
        $("span#nameErrorText").html("（请输入任务名称）");
        $("span#nameError").html("※");
        flagName = false;
    }
    // 验证任务描述
    if (m > 255) {
        $("span#descErrorText").html("（不超过255个字符）");
        $("span#descError").html("※");
        flagDesc = false;
    }

    if (endTime == "" || begTime == "") {
        $("span#dateErrorText").html("请填写需要使用的测量数据的时间！");
        $("span#dateError").html("※");
        flagDate = false;
    }
    else if (exDateRange(endTime, begTime) > 10) {
        // 验证测试日期是否大于十天
        $("span#dateErrorText").html("（时间跨度请不要超过10天！）");
        $("span#dateError").html("※");
        flagDate = false;
    }

    return flagName && flagDesc && flagDate;
}

/**
 * 返回任务详情页面
 */
function returnTaskList() {
    var cityId = $("#cityId").val();
    $("#hiddenCityId").val(cityId);
    $("#returnTaskListForm").submit();
}

// 计算两个日期差值
function exDateRange(sDate1, sDate2) {
    var iDateRange;
    if (sDate1 != "" && sDate2 != "") {
        var startDate = sDate1.replace(/-/g, "/");
        var endDate = sDate2.replace(/-/g, "/");
        var S_Date = new Date(Date.parse(startDate));
        var E_Date = new Date(Date.parse(endDate));
        iDateRange = (S_Date - E_Date) / 86400000;
        // alert(iDateRange);
    }
    return iDateRange;
}
