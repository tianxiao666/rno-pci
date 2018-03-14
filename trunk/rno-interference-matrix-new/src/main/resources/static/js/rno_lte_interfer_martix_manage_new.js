$(document).ready(function() {

	datepicker();
	// 切换区域
	// initAreaCascade();
	// 默认加载干扰矩阵
	getInterferMartix();

	$("#searchInterMartix").click(function() {
		getInterferMartix();
	});

	// 重定向至新增4G干扰矩阵页面
	$("#addInterMartix").click(function() {
		var sessionId = $("#sessionId").text();
		location.href = 'initNewLteInterferMartixAdd?account='+sessionId;
	});
});

/**
 * 按条件查询干扰矩阵
 */
function getInterferMartix() {
	// 重置分页条件
	initFormPage('interferMartixForm');
	// 提交表单
	sumbitInterferMartixForm();
}

/**
 * 提交表单
 */
function sumbitInterferMartixForm() {
	showOperTips("loadingDataDiv", "loadContentId", "正在加载");

	$("#interferMartixForm").ajaxSubmit({
		url : 'queryNewLteInterferMartixByPage',
		dataType : 'text',
		success : function(raw) {
			showInterMartix(raw);
		},
		complete : function() {
			hideOperTips("loadingDataDiv");
		}
	});
}

/**
 * 列表显示干扰矩阵
 */
function showInterMartix(raw) {

	var data = eval("(" + raw + ")");
	var table = $("#interferMartixTable");

	// 清空干扰矩阵详情列表
	$("#interferMartixTable tr:gt(0)").remove();

	if (data == null || data == undefined) {
		return;
	}

	var list = data['data'];
	var html = "";
	var tr = "";
	var one = "";

	var startStr;
	var endStr;
	var startDate;
	var endDate;
	var sy, sm, sd;// 年月日
	var ey, em, ed;
	var jobId;
	var workStatus;
	var paramStr;
	var dataDescStr;
	var tmpStr;
	var typeStr;
	var checkTaskReportStr;
	var optionStr;
	for (var i = 0; i < list.length; i++) {
		one = list[i];
		tr = "<tr>";
		tr += "<td>" + getValidValue(one['CITY_NAME'], '') + "</td>";
		tr += "<td>" + getValidValue(one['TASK_NAME'], '') + "</td>";
		tr += "<td>创建：" + getValidValue(one['CREATE_DATE'], '') + "<br/>开始：" + getValidValue(one['LAUNCH_TIME'], '')
				+ "<br/>结束：" + getValidValue(one['COMPLETE_TIME'], '') + "</td>";
		// 获取开始结束日期转为页面描述信息
		startStr = getValidValue(one['START_MEA_DATE'], '');
		endStr = getValidValue(one['END_MEA_DATE'], '');
		startStr = startStr.replace(/-/g, "/");
		endStr = endStr.replace(/-/g, "/");
		startDate = new Date(startStr);
		sy = startDate.getYear() + 1900;
		sm = startDate.getMonth() + 1;
		sd = startDate.getDate();
		endDate = new Date(endStr);
		ey = endDate.getYear() + 1900;
		em = endDate.getMonth() + 1;
		ed = endDate.getDate();
		jobId = getValidValue(one['JOB_ID'], '');
		// workStatus=getValidValue(one['WORK_STATUS'],'');
		if (one['JOB_RUNNING_STATUS'] == "Waiting") {
			workStatus = "<td>排队中</td>";
		} else if (one['JOB_RUNNING_STATUS'] == "Launched" || one['JOB_RUNNING_STATUS'] == "Running") {
			workStatus = "<td>运行中</td>";
		} else if (one['JOB_RUNNING_STATUS'] == "Stopping") {
			workStatus = "<td>停止中</td>";
		} else if (one['JOB_RUNNING_STATUS'] == "Stopped") {
			workStatus = "<td>已停止</td>";
		} else if (one['JOB_RUNNING_STATUS'] == "Fail") {
			workStatus = "<td style='color:red;'>异常终止</td>";
		} else if (one['JOB_RUNNING_STATUS'] == "Succeded") {
			workStatus = "<td>正常完成</td>";
		} else {
			workStatus = "<td></td>";
		}
		dataDescStr = getValidValue(one['DATA_DESCRIPTION'], '');
		tmpStr = dataDescStr.split(";");
		dataDescStr = getValidValue(tmpStr[0], '') + "<br/>" + getValidValue(tmpStr[1], '') + "<br/>"
				+ getValidValue(tmpStr[2], '');
		typeStr = getValidValue(one['TYPE'], '');
		if (typeStr == "ALL") {
			typeStr = "全部";
		}
		paramStr = "MR关联度分子：" + getValidValue(one['RELA_NUM_TYPE'], '') + "<br/>同频小区相关系数权值Kss ："
				+ getValidValue(one['SAMEFREQCELLCOEFWEIGHT'], '', 2) + "<br/>切换比例权值Kho ："
				+ getValidValue(one['SWITCHRATIOWEIGHT'], '', 2);

		// 排队中
		if (one['JOB_RUNNING_STATUS'] == "Waiting") {
			optionStr = "<td></td>";/*"<td><input type='button' value='停止' onclick='stopTask(\"" + one['JOB_ID'] + "\",\""
					+ one['MR_JOB_ID'] + "\")'/></td>"*/
		}
		// 运行中
		else if (one['JOB_RUNNING_STATUS'] == "Launched" || one['JOB_RUNNING_STATUS'] == "Running") {
			optionStr = "<td>"/*<input type='button' value='停止' onclick='stopTask(\"" + one['JOB_ID'] + "\",\""
					+ one['MR_JOB_ID'] + "\")'/> <br/> "*/
					+ "<input type='button' value='查看运行报告' onclick='checkTaskReport(\"" + one['JOB_ID'] + "\")'/></td>";
		}
		// 异常终止
		else if (one['JOB_RUNNING_STATUS'] == "Fail") {
			optionStr = "<td><input type='button' value='查看运行报告' onclick='checkTaskReport(\"" + one['JOB_ID']
					+ "\")'/></td>";
		}
		// 正常完成
		else if (one['JOB_RUNNING_STATUS'] == "Succeded") {
			optionStr = "<td><input type='button' value='下载结果文件' onclick='download4GMatrixFile(\"" + one['JOB_ID']
					+ "\",\"" + one['MR_JOB_ID'] + "\")'/> <br/> "
					+ "<input type='button' value='查看运行报告' onclick='checkTaskReport(\"" + one['JOB_ID'] + "\")'/></td>";
		}
		// 停止中
		else if (one['JOB_RUNNING_STATUS'] == "Stopping") {
			optionStr = "<td><input type='button' value='查看运行报告' onclick='checkTaskReport(\"" + one['JOB_ID']
					+ "\")'/></td>";
		}
		// 其他（，已停止）
		else {
			optionStr = "<td><input type='button' value='下载结果文件' onclick='download4GMatrixFile(\"" + one['JOB_ID']
					+ "\",\"" + one['MR_JOB_ID'] + "\")'/> <br/>"
					+ "<input type='button' value='查看运行报告' onclick='checkTaskReport(\"" + one['JOB_ID'] + "\")'/></td>";
		}

		tr += "<td>" + sy + "年" + sm + "月" + getMonthWeek(sy, sm, sd) + "周<br/>" + sy + "-" + sm + "-" + sd + " ~ "
				+ ey + "-" + em + "-" + ed + "</td>";
		tr += "<td>" + getValidValue(typeStr, '') + "</td>";
		tr += "<td>" + getValidValue(one['RECORD_NUM'], '') + "</td>";
		tr += "<td>" + getValidValue(dataDescStr, '') + "</td>";
		tr += "<td>" + getValidValue(paramStr, '') + "</td>";
		// tr += "<td>"+(workStatus!='计算完成'?workStatus:workStatus+downStr)+"</td>";
		tr += workStatus;
		tr += optionStr;
		tr += "</tr>";
		html += tr;
	}
	table.append(html);

	// 设置隐藏的page信息
	setFormPageInfo("interferMartixForm", data['page']);

	// 设置分页面板
	setPageView(data['page'], "interMartixPageDiv");
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

	form.find("#hiddenPageSize").val(page.pageSize);
	form.find("#hiddenCurrentPage").val(new Number(page.currentPage));
	form.find("#hiddenTotalPageCnt").val(page.totalPageCnt);
	form.find("#hiddenTotalCnt").val(page.totalCnt);
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
	// alert(form.find("#hiddenPageSize").val());
	var pageSize = new Number(form.find("#hiddenPageSize").val());
	var currentPage = new Number(form.find("#hiddenCurrentPage").val());
	var totalPageCnt = new Number(form.find("#hiddenTotalPageCnt").val());
	var totalCnt = new Number(form.find("#hiddenTotalCnt").val());

	// console.log("pagesize="+pageSize+",currentPage="+currentPage+",totalPageCnt="+totalPageCnt+",totalCnt="+totalCnt);
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
		var userinput = $(div).find("#showCurrentPage").val();
		if (isNaN(userinput)) {
			alert("请输入数字！")
			return;
		}
		if (userinput > totalPageCnt || userinput < 1) {
			alert("输入页面范围不在范围内！");
			return;
		}
		var reg = /^[0-9]+$/;
		if(!reg.test(userinput)){
			alert("请输入正确的页码！");
			return;
		}
		$(form).find("#hiddenCurrentPage").val(userinput);
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

/*
 * function initAreaCascade() {
 * 
 * $("#provinceId2").change(function() { getSubAreas("provinceId2", "cityId2", "市"); });
 * 
 * $("#cityId2").change(function() { getSubAreas("cityId2", "areaId2", "区/县",function(){ $("#areaId2").append("<option
 * selected='true' value=''>全部</option>"); }); });
 * 
 * //判断服务器是否指定了默认的cityId var cityIdFromRes = $("#cityIdFromRes").val(); if(cityIdFromRes != "" && cityIdFromRes != "0" &&
 * cityIdFromRes != null) { $("#cityId2").val(cityIdFromRes); //console.log(cityIdFromRes); } }
 */

var getMonthWeek = function(a, b, c) {
	/*
	 * a = d = 当前日期 b = 6 - w = 当前周的还有几天过完（不算今天） a + b 的和在除以7 就是当天是当前月份的第几周
	 */
	var date = new Date(a, parseInt(b) - 1, c), w = date.getDay(), d = date.getDate();
	return Math.ceil((d + 6 - w) / 7);
};
// 下载4g矩阵结果文件
function download4GMatrixFile(jobId, mrJobId) {
	var form = $("#download4GMatrixFileForm");
	form.find("input#jobId").val(jobId);
	form.submit();
	
}
// 停止任务
function stopTask(jobId, mrJobId, runType) {
	var flag = confirm("是否停止该任务计算？");
	if (flag == false) {
		return;
	}
	showOperTips("loadingDataDiv", "loadContentId", "正在停止任务");
	var sessionId = $("#sessionId").text();
	$.ajax({
		url : 'stopNewPciJobByJobIdAndMrJobId?account='+sessionId,
		data : {
			'jobId' : jobId,
			'mrJobId' : mrJobId,
			'runType' : runType
		},
		dataType : 'text',
		type : 'post',
		success : function(raw) {
			var data;
			try {
				data = eval("(" + raw + ")");
				var flag = data['flag'];
				if (flag == 'isSubmit') {
					alert("停止操作已提交，请稍后查看停止结果");
					// 刷新列表
					sumbitInterferMartixForm();
					// window.location.href='queryNewLteInterferMartixByPage';
					// sumbitStructureForm();
				} else if (flag == 'NotFound') {
					alert("获取任务信息失败，请稍后刷新页面再试！");
				} else {
					alert("任务停止失败！");
				}
			} catch (err) {
			}
		},
		complete : function() {
			hideOperTips("loadingDataDiv");
		}
	});
}

/**
 * 从报告的详情返回列表页面
 */
function returnToTaskList() {
	$("#taskReportDiv").css("display", "none");
	$("#taskProfileDiv").css("display", "block");
}

/**
 * 查看运行报告
 */
function checkTaskReport(jobId) {
	$("#viewReportForm").find("input#hiddenJobId").val(jobId);
	initFormPage("viewReportForm");
	$("#taskReportDiv").css("display", "block");
	$("#taskProfileDiv").css("display", "none");
	queryReportData();
}
/**
 * 查询指定job的报告
 */
function queryReportData() {
	$("#viewReportForm").ajaxSubmit({
		url : 'queryJobReport',
		type : 'post',
		dataType : 'text',
		success : function(raw) {
			var data = {};
			try {
				data = eval("(" + raw + ")");
			} catch (err) {
				console.log(err);
			}
			displayReportRec(data['data']);
			setFormPageInfo("viewReportForm", data['page']);
			setPageView(data['page'], "reportListPageDiv");
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
	//
	$("#reportListTab tr:not(:first)").each(function(i, ele) {
		$(ele).remove();
	});
	var html = "";
	for (var i = 0; i < data.length; i++) {
		one = data[i];
		html += "<tr>";
		html += "<td>" + getValidValue(one['STAGE'], '') + "</td>";
		html += "<td>" + getValidValue(one['BEG_TIME'], '') + "</td>";
		html += "<td>" + getValidValue(one['END_TIME'], '') + "</td>";
		console.log()
		if (getValidValue(one['STATE'], '').indexOf("失败") == -1) {
			html += "<td>" + getValidValue(one['STATE'], '') + "</td>";
		} else {
			html += "<td style='color: red;'>" + getValidValue(one['STATE'], '') + "</td>";
		}
		html += "<td>" + getValidValue(one['ATT_MSG'], '') + "</td>";
		html += "</tr>";
	}
	$("#reportListTab").append(html);
}

/**
 * 获取关联区域
 */
function getSubAreas(parentDom, cityDom) {
	var allAreas = $("#allAreas").val();
	var parentId = $("#" + parentDom).val();
	$("#" + cityDom).empty();
	var one, htmlStr = "";
	var data = eval("(" + allAreas + ")");
	for (var i = 0; i < data.length; i++) {
		one = data[i];
		if (one['parentId'] == parentId) {
			htmlStr += "<option value='" + one['areaId'] + "'>" + $.trim(one['name']) + "</option>";
		}
	}
	$("#" + cityDom).append(htmlStr);
}

/**
 * 时间选择器
 */
function datepicker() {
	var now = new Date();
	$("#begTime").datepicker({
		lang : 'ch',// 中文化
		dateFormat : "yy-mm-dd",
		defaultDate : "-2",
		changeYear : true,
		changeMonth : true,
		showOtherMonths : true,
		selectOtherMonths : true,
		numberOfMonths : 1,
		onClose : function(selectedDate) {
			$("#latestAllowedTime").datepicker("option", "minDate", selectedDate);
		}
	});
	$("#begTime").datepicker("setDate",
			addDays(new DateTime(now.getFullYear(), now.getMonth() + 1, now.getDate()), -1));// 减去2天

	$("#latestAllowedTime").datepicker({
		lang : 'ch',// 中文化
		dateFormat : "yy-mm-dd",
		defaultDate : "+1w",
		changeYear : true,
		changeMonth : true,
		showOtherMonths : true,
		selectOtherMonths : true,
		numberOfMonths : 1,
		onClose : function(selectedDate) {
			$("#begTime").datepicker("option", "maxDate", selectedDate);
		}
	});
	$("#latestAllowedTime").datepicker("setDate", addDays(new Date(), 0));
}

/*
 * * 在某个基准日期的基础上，对时间进行加减 正数为加，负数为减去
 * 
 * @param baseDate @param day @returns {Date}
 */
function addDays(baseDate, day) {
	var tinoneday = 24 * 60 * 60 * 1000;
	var nt = baseDate.getTime();
	var cht = day * tinoneday;
	var newD = new Date();
	newD.setTime(nt + cht);
	return newD;
}

function showOperTips(outerId, tipId, tips) {
	try {
		$("#" + outerId).css("display", "");
		$("#" + outerId).find("#" + tipId).html(tips);
	} catch (err) {
	}
}

function hideOperTips() {
	$("#loadingDataDiv").css("display", "none");
}

function DateTime(year, month, day, hour, min, sec) {
	var d = new Date();

	if (year || year == 0) {
		d.setFullYear(year);
	}
	if (month || month == 0) {
		d.setMonth(month - 1);
	}
	if (day || day == 0) {
		d.setDate(day);
	}
	if (hour || hour == 0) {
		d.setHours(hour);
	}
	if (min || min == 0) {
		d.setMinutes(min);
	}
	if (sec || sec == 0) {
		d.setSeconds(sec);
	}
	return d;
}

function getValidValue(v, defaultValue) {
	if (v == null || v == undefined || v == "null" || v == "NULL" || v == "undefined" || v == "UNDEFINED") {
		if (defaultValue != null && defaultValue != undefined)
			return defaultValue;
		else
			return "";
	}
	return v;
}
