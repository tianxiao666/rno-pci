$(document).ready(function () {
    // 切换区域
    initAreaCascade();

    // 绑定时间控件
    datepicker();

    // 绑定查询事件
    $("#queryTasks").click(function () {
        var nameErrorText = $("span#nameErrorText");
        nameErrorText.html("");
        var taskName = $.trim($("#taskName").val());
        if (ifHasSpecChar(taskName)) {
            nameErrorText.html("含有特殊字符");
            return;
        }
        getTaskList();
    });

    // 重定向至新增结构分析任务
    $("#addOneTask").click(function () {
        var cityId = $("#cityId").val();
        $("#hiddenCityId").val(cityId);
        $("#createTaskForm").submit();
    });

    // 默认加载结构分析
    getTaskList();
});


// 初始化区域
function initAreaCascade() {

    $("#provinceId").change(function () {
        getSubAreas("provinceId", "cityId", "市");
    });
}

function datepicker() {
    $.datepicker.setDefaults({
        dateFormat: "yy-mm-dd",
        changeYear: true,
        changeMonth: true,
        showOtherMonths: true,
        selectOtherMonths: true,
        maxDate: "Today"
    });

    var meaTime = $("#meaTime");

    meaTime.datepicker({
        defaultDate: "-2"
    });

    // 允许使用backspace和delete键清楚数据，不影响readonly特性
    meaTime.keyup(function(e) {
        if(e.keyCode == 8 || e.keyCode == 46) {
            $.datepicker._clearDate(this);
        }
    });

    var startSubmitTime = $("#startSubmitTime");
    var endSubmitTime = $("#endSubmitTime");

    startSubmitTime.datetimepicker({
        timeFormat: "HH:mm:ss",
        // 缺省值，5天前
        defaultDate: "-5d",
        onClose: function (selectedDate) {
            // 调整结束时间最小值
            endSubmitTime.datetimepicker("option", "minDate", selectedDate);
            // 展开结束时间
            // endSubmitTime.datetimepicker("show");
        }
    });

    startSubmitTime.keyup(function(e) {
        if(e.keyCode == 8 || e.keyCode == 46) {
            $.datepicker._clearDate(this);
        }
    });

    endSubmitTime.datetimepicker({
        timeFormat: "HH:mm:ss",
        // 缺省值今天
        defaultDate: "Today",
        // 初始化最大最小值,5天前到今天
        minDate: "-5d",
        onClose: function (selectedDate) {
            // 调整开始时间最大值
            startSubmitTime.datetimepicker("option", "maxDate", selectedDate);
        }
    });

    endSubmitTime.keyup(function(e) {
        if(e.keyCode == 8 || e.keyCode == 46) {
            $.datepicker._clearDate(this);
        }
    });
}

/**
 * 按条件查询结构分析任务
 */
function getTaskList() {
    // 重置分页条件
    initFormPage('taskListForm');
    // 提交表单
    submitTaskListForm();
}

/**
 * 提交表单
 */
function submitTaskListForm() {

    $("#taskListForm").ajaxSubmit({
        url: 'queryLteAzimuthEvaluationTaskByPage',
        dataType: 'json',
        beforeSend: function () {
            showOperTips("loadingDataDiv", "loadContentId", "正在查询");
        },
        success: function (data) {
            showTaskList(data);
        },
        complete: function () {
            hideOperTips("loadingDataDiv");
        }
    });
}

/**
 * 列表显示结构分析任务
 */
function showTaskList(data) {
    if (data == null || data == undefined) {
        return;
    }

    var table = $("#taskListTab");
    // 清空结构分析任务详情列表
    table.find("tr:gt(0)").remove();

    var list = data['data'];
    var tr = "";
    var one = "";

    for (var i = 0; i < list.length; i++) {
        one = list[i];
        tr += "<tr>";
        tr += "<td>" + getValidValue(one['jobName'], '') + "</td>";
        if (one['jobRunningStatus'] == "Waiting") {
            tr += "<td>排队中</td>";
        } else if (one['jobRunningStatus'] == "Launched"
            || one['jobRunningStatus'] == "Running") {
            tr += "<td>运行中</td>";
        } else if (one['jobRunningStatus'] == "Stopping") {
            tr += "<td>停止中</td>";
        } else if (one['jobRunningStatus'] == "Stopped") {
            tr += "<td>已停止</td>";
        } else if (one['jobRunningStatus'] == "Fail") {
            tr += "<td style='color:red;'>异常终止</td>";
        } else if (one['jobRunningStatus'] == "Succeeded") {
            tr += "<td>正常完成</td>";
        } else {
            tr += "<td></td>";
        }
        tr += "<td>" + getValidValue(one['cityName'], '') + "</td>";
        tr += "<td>--</td>";
        tr += "<td>" + getValidValue(one['begMeaTime'].substr(0, 10), '') + "<br/>至<br/>"
            + getValidValue(one['endMeaTime'].substr(0, 10), '') + "</td>";
        tr += "<td>" + getValidValue(one['launchTime'], '') + "</td>";
        tr += "<td>" + getValidValue(one['completeTime'], '') + "</td>";
        // 排队中
        if (one['jobRunningStatus'] == "Waiting") {
            tr += "<td><input type='button' value='停止' onclick='stopOneTask(\"" + one['jobId'] + "\")'/></td>";
        }
        // 运行中
        else if (one['jobRunningStatus'] == "Launched"
            || one['jobRunningStatus'] == "Running") {
            tr += "<td><input type='button' value='停止' onclick='stopOneTask(\"" + one['jobId'] + "\")'/> "
                + "<input type='button' value='查看运行报告' onclick='checkTaskReport(\"" + one['jobId'] + "\")'/></td>";
        }
        // 异常终止
        else if (one['jobRunningStatus'] == "Fail") {
            tr += "<td><input type='button' value='查看运行报告' onclick='checkTaskReport(\"" + one['jobId'] + "\")'/></td>";
        }
        // 正常完成
        else if (one['jobRunningStatus'] == "Succeeded") {
            tr += "<td><input type='button' value='下载结果文件' onclick='downloadResultFile(\"" + one['jobId'] + "\")'/> "
                + "<input type='button' value='查看运行报告' onclick='checkTaskReport(\"" + one['jobId'] + "\")'/> ";
        }
        // 其他（停止中，已停止）
        else {
            tr += "<td><input type='button' value='查看运行报告' onclick='checkTaskReport(\"" + one['jobId'] + "\")'/></td>";
        }
        tr += "</tr>";
    }
    table.append(tr);

    // 设置隐藏的page信息
    setFormPageInfo("taskListForm", data['page']);

    // 设置分页面板
    setPageView(data['page'], "taskListPageBar");
}

// 查看运行报告
function checkTaskReport(jobId) {
    $("#reportListForm").find("input#hiddenJobId").val(jobId);
    initFormPage("reportListForm");
    $("#reportListDiv").css("display", "block");
    $("#taskListDiv").css("display", "none");
    queryReportData();
}

// 停止任务
function stopOneTask(jobId) {
    var flag = confirm("是否停止该任务计算？");
    if (flag == false) {
        return;
    }

    $.ajax({
        url: 'stopJobByJobId',
        data: {
            'jobId': jobId
        },
        type: 'post',
        dataType: 'json',
        beforeSend: function () {
            showOperTips("loadingDataDiv", "loadContentId", "正在停止任务");
        },
        success: function (data) {
            try {
                if (data['flag']) {
                    alert("停止操作已提交，请稍后查看停止结果");
                    // 刷新列表
                    submitTaskListForm();
                } else {
                    alert("任务停止失败！");
                }
            } catch (err) {
            }
        },
        complete: function () {
            hideOperTips("loadingDataDiv");
        }
    });
}

// 下载结果文件
function downloadResultFile(jobId) {
    var form = $("#downloadResultFileForm");
    form.find("input#jobId").val(jobId);
    form.submit();
}

/**
 * 查询指定job的报告
 */
function queryReportData() {
    $("#reportListForm").ajaxSubmit({
        url: 'queryJobReportByPage',
        type: 'post',
        dataType: 'json',
        success: function (data) {
            displayReportRec(data['data']);
            setFormPageInfo("reportListForm", data['page']);
            setPageView(data['page'], "reportListPageBar");
        }
    });
}

/**
 * 显示报告
 */
function displayReportRec(data) {
    if (data == null || data == undefined) {
        return;
    }
    var table = $("#reportListTab");
    // 清空结构分析任务详情列表
    table.find("tr:gt(0)").remove();
    var html = "";
    var one;

    var date = new Date();
    var dateFormat = "yyyy-MM-dd HH:mm:ss";

    for (var i = 0; i < data.length; i++) {
        one = data[i];
        html += "<tr>";
        html += "<td>" + getValidValue(one['stage'], '') + "</td>";

        date.setTime(getValidValue(one['begTime'], ''));
        html += "<td>" + date.format(dateFormat) + "</td>";

        date.setTime(getValidValue(one['endTime'], ''));
        html += "<td>" + date.format(dateFormat) + "</td>";

        if (one['finishState'].indexOf("Fail") == -1) {
            html += "<td>" + getValidValue(one['finishState'], '') + "</td>";
        } else {
            html += "<td style='color: red;'>" + getValidValue(one['finishState'], '') + "</td>";
        }
        html += "<td>" + getValidValue(one['attMsg'], '') + "</td>";
        html += "</tr>";
    }
    table.append(html);
}

/**
 * 从报告的详情返回列表页面
 */
function returnToTaskList() {
    $("#reportListDiv").css("display", "none");
    $("#taskListDiv").css("display", "block");
}

// 设置隐藏的page信息
function setFormPageInfo(formId, page) {
    if (formId == null || formId == undefined || page == null || page == undefined) {
        return;
    }

    var form = $("#" + formId);
    if (!form) {
        return;
    }

    form.find("#hiddenPageSize").val(page['pageSize']);
    form.find("#hiddenCurrentPage").val(page['currentPage']);
    form.find("#hiddenTotalPageCnt").val(page['totalPageCnt']);
    form.find("#hiddenTotalCnt").val(page['totalCnt']);
}


/**
 * 设置分页面板
 *
 * @param page
 *            分页信息
 * @param divId
 *            分页面板id
 */
function setPageView(page, divId) {
    if (page == null || page == undefined) {
        return;
    }

    var div = $("#" + divId);
    if (!div) {
        return;
    }

    var pageSize = page['pageSize'] ? page['pageSize'] : 0;
    var currentPage = page['currentPage'] ? page['currentPage'] : 1;
    var totalPageCnt = page['totalPageCnt'] ? page['totalPageCnt'] : 0;
    var totalCnt = page['totalCnt'] ? page['totalCnt'] : 0;

    // 设置到面板上
    $(div).find("#emTotalCnt").html(totalCnt);
    $(div).find("#showCurrentPage").val(currentPage);
    $(div).find("#emTotalPageCnt").html(totalPageCnt);
}

/**
 * 分页跳转的响应
 *
 * @param dir
 * @param action（方法名）
 * @param formId
 * @param divId
 */
function showListViewByPage(dir, action, formId, divId) {

    var form = $("#" + formId);
    var div = $("#" + divId);

    var currentPage = parseInt(form.find("#hiddenCurrentPage").val());
    var totalPageCnt = parseInt(form.find("#hiddenTotalPageCnt").val());

    if (dir === "first") {
        if (currentPage <= 1) {
            return;
        } else {
            $(form).find("#hiddenCurrentPage").val("1");
        }
    } else if (dir === "last") {
        if (currentPage >= totalPageCnt) {
            return;
        } else {
            $(form).find("#hiddenCurrentPage").val(totalPageCnt);
        }
    } else if (dir === "back") {
        if (currentPage <= 1) {
            return;
        } else {
            $(form).find("#hiddenCurrentPage").val(currentPage - 1);
        }
    } else if (dir === "next") {
        if (currentPage >= totalPageCnt) {
            return;
        } else {
            $(form).find("#hiddenCurrentPage").val(currentPage + 1);
        }
    } else if (dir === "num") {
        var userInput = $(div).find("#showCurrentPage").val();
        if (isNaN(userInput)) {
            alert("请输入数字！");
            return;
        }
        if (userInput > totalPageCnt || userInput < 1) {
            alert("输入页面范围不在范围内！");
            return;
        }
        $(form).find("#hiddenCurrentPage").val(userInput);
    } else {
        return;
    }
    // 获取资源
    if (typeof action == "function") {
        action();
    }
}

// 初始化form下的page信息
function initFormPage(formId) {
    var form = $("#" + formId);
    if (!form) {
        return;
    }
    form.find("#hiddenPageSize").val(25);
    form.find("#hiddenCurrentPage").val(1);
    form.find("#hiddenTotalPageCnt").val(-1);
    form.find("#hiddenTotalCnt").val(-1);
}

/**
 * 时间格式解释器
 */
Date.prototype.format = function (format) {
    var o = {
        "M+": this.getMonth() + 1, //month
        "d+": this.getDate(),    //day
        "H+": this.getHours(),   //hour
        "m+": this.getMinutes(), //minute
        "s+": this.getSeconds(), //second
        "q+": Math.floor((this.getMonth() + 3) / 3),  //quarter
        "S": this.getMilliseconds() //millisecond
    };
    if (/(y+)/.test(format)) format = format.replace(RegExp.$1,
        (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o) if (new RegExp("(" + k + ")").test(format))
        format = format.replace(RegExp.$1,
            RegExp.$1.length == 1 ? o[k] :
                ("00" + o[k]).substr(("" + o[k]).length));
    return format;
};

function getValidValue(v, defaultValue, precision) {
    if (v == null || v == undefined || v == "null" || v == "NULL"
        || v == "undefined" || v == "UNDEFINED") {
        if (defaultValue != null && defaultValue != undefined)
            return defaultValue;
        else
            return "";
    }

    if (typeof v == "number") {
        try {
            v = Number(v).toFixed(precision);
        } catch (err) {
        }
    }
    return v;
}
