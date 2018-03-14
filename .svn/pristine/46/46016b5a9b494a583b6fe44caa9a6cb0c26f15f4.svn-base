//点击td转换可编辑
var editHTML;
var editText;
var maxDateInterval = 300; // 允许最大时间跨度。
var oneDay = 86400000; // 一天的毫秒数
// 上传文件
var stopQueryProgress = false;// /停止查询进度

$(document).ready(function() {
	// 绑定事件
	bindEvent();
	// 切换区域
	// initAreaCascade();
	// 默认加载结构分析
	//getStructureTask();
});

/**
 * 绑定事件
 * 
 * @returns
 */
function bindEvent() {
	$("#searchStructureTask").click(function() {
		$("span#nameErrorText").html("");
		var taskName = $.trim($("#taskName").val());
		if (ifHasSpecChar(taskName)) {
			$("span#nameErrorText").html("含有特殊字符");
			return;
		} else if (taskName.length > 40) {
			$("span#nameErrorText").html("信息过长");
			return;
		}
		getStructureTask();
	});

	datepicker();
	
	//获取干扰矩阵方式
	$("#getmatrix").change(function(){
		$("#matrix").css("display","none");
		$("#useflow").css("display","none");
		$("#usesf").css("display","none");
		$("#meatime").css('display','none');
		$("#ksrow").css('display','none');
		$("#freqajust").css('display','none');
		$("#d1d2plan").css('display','none');
		$("#d1d2range").css('display','none');
		$("#isUseFlow").prop("checked",false);
		$("#isUseSf").prop("checked",false);
		$("#isFreqAdj").prop("checked",false);
		if($("#getmatrix").val()==0){
			getLatelyLteMatrix();
			$("#matrix").css("display","");
		}else if ($("#getmatrix").val()==1){
			$("#meatime").css('display','');
			$("#useflow").css("display","");
			$("#usesf").css("display","");
		}
	});

	// 设置为不可选
	//$(".forfilecheck").attr("disabled", "disabled");
	//$(".freqAdjType").attr("disabled", "disabled");
	//$(".freqinput").attr("disabled", "disabled");
	//$("#isFreqAdj").attr("disabled", "disabled");
	//流量文件选择
	$("#isUseFlow").change(function() {
		if ($("#isUseFlow").prop("checked")) {
			//$(".forfilecheck").removeAttr("disabled");
			//$("#isFreqAdj").removeAttr("disabled");
			$("#ksrow").css('display','');
		} else {
			//$(".forfilecheck").attr("disabled", "disabled");
			//$("#isFreqAdj").attr("disabled", "disabled");
			$("#ksrow").css('display','none');
		}
	});
	// 扫频文件框选择
	$("#isUseSf").change(function() {
		if ($("#isUseSf").prop("checked")) {
			//$(".forfilecheck").removeAttr("disabled");
			//$("#isFreqAdj").removeAttr("disabled");
			$("#freqajust").css('display','');
			$("#meatime").css('display','');
		} else {
			//$(".forfilecheck").attr("disabled", "disabled");
			//$("#isFreqAdj").attr("disabled", "disabled");
			$("#freqajust").css('display','none');
			$("#meatime").css('display','none');
		}
	});
	// 频率调整框选择事件
	$("#isFreqAdj").change(function() {
		if ($("#isFreqAdj").prop("checked")) {
			//$(".freqAdjType").removeAttr("disabled");
			//$(".freqinput").removeAttr("disabled");
			$("#d1d2plan").css('display','');
			$("#d1d2range").css('display','');
		} else {
			//$(".freqAdjType").attr("disabled", "disabled");
			//$(".freqinput").attr("disabled", "disabled");
			$("#d1d2plan").css('display','none');
			$("#d1d2range").css('display','none');
		}
	});
	
	/**
	 * 新建任务
	 */
	$("#addTask").click(function(){
		location.href = "addTask_" + $("#citymenu").val();
	});
	
	/**
	 * 任务消息页面的下一步点击事件
	 */
	/*$("#taskInfoNextStep").click(function() {
		storageTaskInfoForSession();
	});*/
	/**
	 * 参数配置页面的上一步点击事件
	 */
	/*$("#paramInfoPreStep").click(function() {
		fromParamInfoToTaskInfoPage();
	});
	*//**
	 * 参数配置页面的下一步点击事件
	 *//*
	$("#paramInfoNextStep").click(function() {
		fromParamInfoToImportFlowFilePage();
	});*/
	/**
	 * 导入流量文件上一步点击事件
	 */
	$("#importFlowFilePreStep").click(function() {
		$("#flowIsSubmit").val("");
		fromImportFlowFileToParamInfoPage();
	});
	/**
	 * 导入流量文件下一步点击事件
	 */
	$("#importFlowFileNextStep").click(function() {
		fromImportFlowFileToSweepPage();
	});
	/**
	 * 导入扫频文件上一步点击事件
	 */
	$("#sweepPreStep").click(function() {
		fromSweepToImportFlowFilePage();
	});
	/**
	 * 导入扫频文件下一步点击事件
	 */
	$("#sweepNextStep").click(function() {
		fromSweepToOverViewInfoPage();
	});
	/**
	 * overview消息页面上一步点击事件
	 */
	/*$("#overviewInfoPreStep").click(function() {
		// 主要跳转至mrr消息页面
		fromOverviewInfoToSweepPage();
	});*/

	/**
	 * 提交任务分析
	 */
	$("#submitTask").click(function() {
		var filenames = getFileName();
		if($("#usesf").val()=='YES'){
			if (typeof (filenames) == "undefined" || filenames.length == 0) {
				alert("您还没有选择扫频文件");
				return;
			}
		}
		if (pciCellValCheck()) {
			var calcType = $("#impmatrix").val();
			if (calcType == "NO") {
				// 触发任务提交action
				pciPlanAnalysis(filenames);
			} else {
				var filename = $("#fileid").val();
				$("#err").remove();
				if ($("#fileid").val() == "") {
					$("#fileid").parent().append("<font id='err' color='red'>请选择干扰矩阵文件</font>");
					return;
				}
				$("span#fileDiv").html("");
				var filename = fileid.value;
				if (!(filename.toUpperCase().endsWith(".CSV"))) {
					$("#fileDiv").html("不支持该类型文件！");
					return false;
				}

				$("#cells").val($("#pciCell").val());
				$('#submitTask').attr("disabled", "true");
				showOperTips("loadingDataDiv", "loadContentId", "正在提交结构分析计算任务");
				var sessionId = $("#sessionId").text();
				$("#formImportPciPlanFile").ajaxSubmit({
					url : 'submitNewPciPlanAnalysisTaskWithMatrix',
					type : 'post',
					data : {
						'account' : sessionId,
						'filenames' : filenames
					},
					dataType : 'json',
					success : function(data) {
					},
					complete : function() {
						hideOperTips("loadingDataDiv");
						location.href = "/" + sessionId;
					}
				});
			}
		}
	});
	/**
	 * 取消任务
	 */
	$("#cancleTask").click(function() {
		cancleLteInterferCalcTask();
	});
	/**
	 * 导入干扰矩阵时自动选中
	 */
	$("#fileid").click(function() {
		$("#pciPlanImport").attr("checked", "checked");
	});
	// 绑定td点击事件
	$(".editbox").each(function() { // 取得所有class为editbox的对像
		$(this).bind("click", function() { // 给其绑定单击事件
			var objId = $(this).attr("id");
			$("span#" + objId).html("");
			editText = $(this).html().trim(); // 取得表格单元格的文本
			setEditHTML(editText); // 初始化控件
			$(this).data("oldtxt", editText) // 将单元格原文本保存在其缓存中，便修改失败或取消时用
			.html(editHTML) // 改变单元格内容为编辑状态
			.unbind("click"); // 删除单元格单击事件，避免多次单击
			$("#editTd").focus();

		});
	});
}

Date.prototype.Format = function(fmt) {
	var o = {
		"M+" : this.getMonth() + 1, // 月份
		"d+" : this.getDate(), // 日
	};
	if (/(y+)/.test(fmt)) {
		fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
	}
	for ( var k in o) {
		if (new RegExp("(" + k + ")").test(fmt)) {
			fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
		}
	}
	return fmt;
}
Date.prototype.Format1 = function(fmt) {
	var o = {
		"M+" : this.getMonth() + 1, // 月份
		"d+" : this.getDate() - 2, // 日
	};
	if (/(y+)/.test(fmt)) {
		fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
	}
	for ( var k in o) {
		if (new RegExp("(" + k + ")").test(fmt)) {
			fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
		}
	}
	return fmt;
}

// 参数配置页面的上一步点击事件
function fromParamInfoToTaskInfoPage() {

	var SAMEFREQCELLCOEFWEIGHT = $("#SAMEFREQCELLCOEFWEIGHT").length>0?$("#SAMEFREQCELLCOEFWEIGHT").html().trim():"";
	var SWITCHRATIOWEIGHT = $("#SWITCHRATIOWEIGHT").length>0?$("#SWITCHRATIOWEIGHT").html().trim():"";
	var CELLM3RINTERFERCOEF = $("#CELLM3RINTERFERCOEF").html().trim();
	var CELLM6RINTERFERCOEF = $("#CELLM6RINTERFERCOEF").html().trim();
	var CELLM30RINTERFERCOEF = $("#CELLM30RINTERFERCOEF").html().trim();
	var BEFORENSTRONGCELLTAB = $("#BEFORENSTRONGCELLTAB").html().trim();
	var TOPNCELLLIST = $("#TOPNCELLLIST").html().trim();
	var INCREASETOPNCELLLIST = $("#INCREASETOPNCELLLIST").html().trim();
	var CONVERMETHOD1TARGETVAL = $("#CONVERMETHOD1TARGETVAL").html().trim();
	var CONVERMETHOD2TARGETVAL = $("#CONVERMETHOD2TARGETVAL").html().trim();
	var CONVERMETHOD2SCOREN = $("#CONVERMETHOD2SCOREN").html().trim();
	var MINCORRELATION = $("#MINCORRELATION").length>0?$("#MINCORRELATION").html().trim():"";
	var MINMEASURESUM = $("#MINMEASURESUM").length>0?$("#MINMEASURESUM").html().trim():"";
	var DISLIMIT = $("#DISLIMIT")?$("#DISLIMIT").html().trim():"";

	// 验证数据
	if (!checkThreshold()) {
		return;
	}

	showOperTips("loadingDataDiv", "loadContentId", "正在跳转页面");
	$.ajax({
		url : 'storageParamInfoBack',
		type : 'post',
		data : {
			'SAMEFREQCELLCOEFWEIGHT' : SAMEFREQCELLCOEFWEIGHT,
			'SWITCHRATIOWEIGHT' : SWITCHRATIOWEIGHT,
			'CELLM3RINTERFERCOEF' : CELLM3RINTERFERCOEF,
			'CELLM6RINTERFERCOEF' : CELLM6RINTERFERCOEF,
			'CELLM30RINTERFERCOEF' : CELLM30RINTERFERCOEF,
			'BEFORENSTRONGCELLTAB' : BEFORENSTRONGCELLTAB,
			'TOPNCELLLIST' : TOPNCELLLIST,
			'INCREASETOPNCELLLIST' : INCREASETOPNCELLLIST,
			'CONVERMETHOD1TARGETVAL' : CONVERMETHOD1TARGETVAL,
			'CONVERMETHOD2TARGETVAL' : CONVERMETHOD2TARGETVAL,
			'CONVERMETHOD2SCOREN' : CONVERMETHOD2SCOREN,
			'MINCORRELATION' : MINCORRELATION,
			'MINMEASURESUM' : MINMEASURESUM,
			'DISLIMIT' : DISLIMIT
		},
		dataType : 'json',
		success : function(data) {
			try {
				var state = data['state'];
			} catch (err) {
			}
		},
		complete : function() {
			// 跳转新的页面
			location.href = "paramInfoBack";
		}
	});

}

// 参数配置页面的下一步点击事件
function fromParamInfoToOverViewInfoPage() {

	var SAMEFREQCELLCOEFWEIGHT = $("#SAMEFREQCELLCOEFWEIGHT").length>0?$("#SAMEFREQCELLCOEFWEIGHT").html().trim():"";
	var SWITCHRATIOWEIGHT = $("#SWITCHRATIOWEIGHT").length>0?$("#SWITCHRATIOWEIGHT").html().trim():"";
	var CELLM3RINTERFERCOEF = $("#CELLM3RINTERFERCOEF").html().trim();
	var CELLM6RINTERFERCOEF = $("#CELLM6RINTERFERCOEF").html().trim();
	var CELLM30RINTERFERCOEF = $("#CELLM30RINTERFERCOEF").html().trim();
	var BEFORENSTRONGCELLTAB = $("#BEFORENSTRONGCELLTAB").html().trim();
	var TOPNCELLLIST = $("#TOPNCELLLIST").html().trim();
	var INCREASETOPNCELLLIST = $("#INCREASETOPNCELLLIST").html().trim();
	var CONVERMETHOD1TARGETVAL = $("#CONVERMETHOD1TARGETVAL").html().trim();
	var CONVERMETHOD2TARGETVAL = $("#CONVERMETHOD2TARGETVAL").html().trim();
	var CONVERMETHOD2SCOREN = $("#CONVERMETHOD2SCOREN").html().trim();
	var MINCORRELATION = $("#MINCORRELATION").length>0?$("#MINCORRELATION").html().trim():"";
	var MINMEASURESUM = $("#MINMEASURESUM").length>0?$("#MINMEASURESUM").html().trim():"";
	var DISLIMIT = $("#DISLIMIT")?$("#DISLIMIT").html().trim():"";

	// 验证数据
	if (!checkThreshold()) {
		return;
	}
	showOperTips("loadingDataDiv", "loadContentId", "正在统计LTE干扰计算任务信息");
	$.ajax({
		url : 'storageParamInfoForward',
		type : 'post',
		data : {
			'SAMEFREQCELLCOEFWEIGHT' : SAMEFREQCELLCOEFWEIGHT,
			'SWITCHRATIOWEIGHT' : SWITCHRATIOWEIGHT,
			'CELLM3RINTERFERCOEF' : CELLM3RINTERFERCOEF,
			'CELLM6RINTERFERCOEF' : CELLM6RINTERFERCOEF,
			'CELLM30RINTERFERCOEF' : CELLM30RINTERFERCOEF,
			'BEFORENSTRONGCELLTAB' : BEFORENSTRONGCELLTAB,
			'TOPNCELLLIST' : TOPNCELLLIST,
			'INCREASETOPNCELLLIST' : INCREASETOPNCELLLIST,
			'CONVERMETHOD1TARGETVAL' : CONVERMETHOD1TARGETVAL,
			'CONVERMETHOD2TARGETVAL' : CONVERMETHOD2TARGETVAL,
			'CONVERMETHOD2SCOREN' : CONVERMETHOD2SCOREN,
			'MINCORRELATION' : MINCORRELATION,
			'MINMEASURESUM' : MINMEASURESUM,
			'DISLIMIT' : DISLIMIT
		},
		dataType : 'json',
		success : function(data) {
			try {
				var state = data['state'];
			} catch (err) {
			}
		},
		complete : function() {
			var sessionId = $("#sessionId").text();
			// 跳转新的页面
			location.href = "paramInfoForward?account=" + sessionId;
		}
	});
}
// importflowfile消息页面上一步点击事件
function fromImportFlowFileToParamInfoPage() {
	showOperTips("loadingDataDiv", "loadContentId", "返回上一步");
	location.href = "importFlowFileBack";
}
// importflowfile消息页面下一步点击事件
function fromImportFlowFileToSweepPage() {
	var ks = $("#ks").val();
	if (ks.length > 25) {
		$("span#errorText").html("Ks值长度过长！");
		return;
	}
	if (ks == "" || ks == null) {
		$("span#errorText").html("Ks值不能为空！");
		return;
	}
	var caltype = file.value;
	var isWithFlow;
	var flowIsSubmit = $("#flowIsSubmit").val();
	if (caltype != "" && caltype != null && flowIsSubmit == "") {
		if (!confirm("选择的流量文件没有导入，将不会参与计算，是否继续？")) {
			return;
		}
	}
	showOperTips("loadingDataDiv", "loadContentId", "正在跳转页面");
	$.ajax({
		url : 'storageImportFlowFileForward',
		type : 'post',
		data : {
			'ks' : ks
		},
		dataType : 'json',
		success : function(data) {
			try {
				var state = data['state'];
			} catch (err) {
			}
		},
		complete : function() {
			// 跳转新的页面
			if (caltype != "" && caltype != null && flowIsSubmit != "") {
				location.href = "importFlowFileForward_y";
			} else {
				location.href = "importFlowFileForward_n";
			}
		}
	});

}
function fromSweepToImportFlowFilePage() {
	showOperTips("loadingDataDiv", "loadContentId", "返回上一步");
	location.href = "sweepBack";
}
// importflowfile消息页面下一步点击事件
function fromSweepToOverViewInfoPage() {
	var isUseSf = $("#isUseSf").prop("checked");
	var isFreqAdj = $("#isFreqAdj").prop("checked");
	var filenames = getFileName();
	var d1Freq = $("#d1Freq").val();
	var d2Freq = $("#d2Freq").val();
	var fats = $(".freqAdjType");
	var freqAdjType = "";
	for (var i = 0; i < fats.length; i++) {
		if (fats.eq(i).prop("checked")) {
			freqAdjType = fats.eq(i).val();
		}
	}
	if (isUseSf && isFreqAdj && freqAdjType.length == 0) {
		alert("请选择频率调整方案");
		return;
	}
	if (isUseSf && isFreqAdj
			&& (!isNumeric(d1Freq, /^[1-9]\d{4}$/) || !isNumeric(d2Freq, /^[1-9]\d{4}$/) || d1Freq.trim().length == 0 || d2Freq.trim().length == 0)) {
		alert("请输入有效的D1D2频");
		return;
	}
	if (isUseSf && (typeof (filenames) == "undefined" || filenames.length == 0)) {
		alert("您还没有选择扫频文件");
		return;
	}
	showOperTips("loadingDataDiv", "loadContentId", "正在跳转页面");
	$.ajax({
		url : 'storageSweepForward',
		type : 'post',
		data : {
			'filenames' : filenames,
			'd1Freq' : d1Freq,
			'd2Freq' : d2Freq,
			'freqAdjType' : freqAdjType
		},
		dataType : 'json',
		success : function(data) {
			try {
				var state = data['state'];
			} catch (err) {
			}
		},
		complete : function() {
			location.href = "sweepForward"
		}
	});
}
// overview消息页面上一步点击事件
function fromOverviewInfoToParamInfoPage() {

	var lteCells = $("#pciCell").val().trim();

	// 验证数据
	if (false) {
		return;
	}

	showOperTips("loadingDataDiv", "loadContentId", "返回上一步");
	$.ajax({
		url : 'storageOverViewBack',
		type : 'post',
		data : {
			'lteCells' : lteCells
		},
		dataType : 'json',
		success : function(data) {
			try {
				var state = data['state'];
			} catch (err) {
			}
		},
		complete : function() {
			// 跳转新的页面
			location.href = "overviewInfoBack";
		}
	});
}

/** 判断变PCI小区表是否符合要求 * */
function pciCellValCheck() {
	var lteCells = $("#pciCell").val().trim();
	if (lteCells == null || lteCells == "") {
		animateInAndOut("operInfo", 500, 500, 1000, "operTip", "变PCI小区表不能为空！");
		return false;
	} else if (ifHasSpecChar(lteCells)) {
		animateInAndOut("operInfo", 500, 500, 1000, "operTip", "包含有以下特殊字符:~'!@#$%^&*()-+_=:");
		// $('#submitTask').removeAttr("disabled");
		return false;
	} else if (!isNumCutByComma(lteCells)) {
		animateInAndOut("operInfo", 500, 500, 1000, "operTip", "变PCI小区表应该是小区ID以逗号分隔的形式");
		return false;
	}
	return true;
}

// 提交PCI分析任务
function pciPlanAnalysis(filenames) {

	var lteCells = $("#pciCell").val().trim();
	$('#submitTask').attr("disabled", "true");
	showOperTips("loadingDataDiv", "loadContentId", "正在提交结构分析计算任务");
	var sessionId = $("#sessionId").text();
	$.ajax({
		url : 'submitNewPciPlanAnalysisTaskWithDayData',
		type : 'post',
		data : {
			'account' : sessionId,
			'lteCells' : lteCells,
			'filenames' : filenames
		},
		dataType : 'json',
		success : function(data) {
		},
		complete : function() {
			hideOperTips("loadingDataDiv");
			location.href = "/" + sessionId;
		}
	});
}

// 取消结构分析任务
function cancleLteInterferCalcTask() {
	var sessionId = $("#sessionId").text();
	location.href = "/" + sessionId;
}

/**
 * 
 * @title 存储任务消息向session
 * @author chao.xj
 * @date 2014-7-15下午3:33:14
 * @company 怡创科技
 * @version 1.2
 */
function storageTaskInfoForSession() {
	var isMeet = checkTaskInfoSubmit();
	if (!isMeet) {
		return;
	}
	$("span#dataTypeErrorText").html("");
	// 存储数据
	var taskName = $("#taskName").val();
	var taskDescription = $("#taskDescription").val();
	var provinceId = $("#provincemenu").val();
	var cityId = $("#citymenu").val();
	var provinceName = $("#provincemenu  option:selected").text();
	var cityName = $("#citymenu  option:selected").text();
	var meaStartTime = $("#begTime").val();
	var meaEndTime = $("#endTime").val();

	var planTypeOne = $("#planType1").is(":checked");
	var planTypeTwo = $("#planType2").is(":checked");

	var planOne = $("#useEriData").is(":checked");
	var planTwo = $("#useHwData").is(":checked");
	var cosi = $("#cosi").val();
	var checkNCell = $("#isCheckNCell").is(":checked");
	var ExportAssoTable = $("#isExportAssoTable").is(":checked");
	var ExportMidPlan = $("#isExportMidPlan").is(":checked");
	var ExportNcCheckPlan = $("#isExportNcCheckPlan").is(":checked");
	var useFlow = $("#isUseFlow").is(":checked");
	var useSf = $("#isUseSf").is(":checked");
	var type = $("#getmatrix").val();
	var matrixJobId = $("#matrix").val();

	var i = 0;
	if (!planOne) {
		i++;
	}
	if (!planTwo) {
		i++;
	}
	if (i == 2) {
		$("span#dataTypeErrorText").html("不能均不选择，至少选择一类!");
		return;
	}
	var converType;
	if (planOne) {
		converType = "ONE";
	} else {
		converType = "TWO";
	}

	i = 0;
	if (!planTypeOne) {
		i++;
	}
	if (!planTypeTwo) {
		i++;
	}
	if (i == 2) {
		$("span#dataTypeErrorText").html("不能均不选择，至少选择一类!");
		return;
	}
	var planType;
	if (planTypeOne) {
		planType = "ONE";
	} else {
		planType = "TWO";
	}

	var isCheckNCell, isExportAssoTable, isExportMidPlan, isExportNcCheckPlan, isNewMatrix, isImportMatrix, isUseFlow, isUseSf;
	if (checkNCell) {
		isCheckNCell = "YES";
	} else {
		isCheckNCell = "NO";
	}
	if (ExportAssoTable) {
		isExportAssoTable = "YES";
	} else {
		isExportAssoTable = "NO";
	}
	if (ExportMidPlan) {
		isExportMidPlan = "YES";
	} else {
		isExportMidPlan = "NO";
	}
	if (ExportNcCheckPlan) {
		isExportNcCheckPlan = "YES";
	} else {
		isExportNcCheckPlan = "NO";
	}
	if (useFlow) {
		isUseFlow = "YES";
	} else {
		isUseFlow = "NO";
	}
	if (useSf) {
		isUseSf = "YES";
	} else {
		isUseSf = "NO";
	}
	
	var ks = "", freqAdjType = "", d1Freq  = "", d2Freq = "";
	if(type==1){
		//流量验证
		isNewMatrix = "YES";
		if(useFlow){
		     ks = $("#ks").val();
			if (ks.length > 25) {
				$("span#errorText").html("Ks值长度过长！");
				return;
			}
			if (ks == "" || ks == null) {
				$("span#errorText").html("Ks值不能为空！");
				return;
			}
		}
		//扫频验证
		if(useSf){
			var isFreqAdj = $("#isFreqAdj").prop("checked");
			 d1Freq = $("#d1Freq").val();
		     d2Freq = $("#d2Freq").val();
			var fats = $(".freqAdjType");
			
			for (var i = 0; i < fats.length; i++) {
				if (fats.eq(i).prop("checked")) {
					freqAdjType = fats.eq(i).val();
				}
			}
			if (isFreqAdj && freqAdjType.length == 0) {
				alert("请选择频率调整方案");
				return;
			}
			if (isFreqAdj
					&& (!isNumeric(d1Freq, /^[1-9]\d{4}$/) || !isNumeric(d2Freq, /^[1-9]\d{4}$/) || d1Freq.trim().length == 0 || d2Freq.trim().length == 0)) {
				alert("请输入有效的D1D2频");
				return;
			}
		}
	}else{
		isNewMatrix = "NO";
	}
	if(type==2){
		isImportMatrix = "YES";
	}else{
		isImportMatrix = "NO";
	}

	showOperTips("loadingDataDiv", "loadContentId", "正在跳转页面");

	$.ajax({
		url : 'storageTaskInfoForward',
		type : 'post',
		data : {
			'taskName' : taskName,
			'taskDescription' : taskDescription,
			'provinceId' : provinceId,
			'cityId' : cityId,
			'provinceName' : provinceName,
			'cityName' : cityName,
			'meaStartTime' : meaStartTime,
			'meaEndTime' : meaEndTime,
			'planType' : planType,
			'converType' : converType,
			'cosi' : cosi,
			'isCheckNCell' : isCheckNCell,
			'isExportAssoTable' : isExportAssoTable,
			'isExportMidPlan' : isExportMidPlan,
			'isExportNcCheckPlan' : isExportNcCheckPlan,
			'isImportMatrix' : isImportMatrix,
			'isUseFlow' : isUseFlow,
			'ks' : ks,
			'isUseSf' : isUseSf,
			'd1Freq' : d1Freq,
			'd2Freq' : d2Freq,
			'freqAdjType' : freqAdjType,
			'matrixJobId' : matrixJobId
		},
		dataType : 'json',
		success : function(data) {
			try {
				var state = data['state'];
			} catch (err) {
			}
		},
		complete : function() {
			// 跳转新的页面
			location.href = "taskInfoForward_" + isNewMatrix;
		}
	});
}

/**
 * 
 * @title 对提交分析任务的信息作验证:不涉及ncs及mrr
 * @returns
 * @author chao.xj
 * @date 2014-7-15上午11:58:25
 * @company 怡创科技
 * @version 1.2
 */
function checkTaskInfoSubmit() {
	var n = 0;
	var m = 0;
	var flagName = true;
	var flagDesc = true;
	var flagDate = true;

	var taskName = $.trim($("#taskName").val());
	var taskDescription = $.trim($("#taskDescription").val());
	var startTime = $.trim($("#begTime").val());
	var endTime = $.trim($("#endTime").val());

	$("span#nameErrorText").html("");
	$("span#descErrorText").html("");
	$("span#dateErrorText").html("");
	$("span#nameError").html("");
	$("span#descError").html("");
	$("span#dateError").html("");

	for (var i = 0; i < taskName.length; i++) { // 应用for循环语句,获取表单提交用户名字符串的长度
		var leg = taskName.charCodeAt(i); // 获取字符的ASCII码值
		if (leg > 255) { // 判断如果长度大于255
			n += 2; // 则表示是汉字为2个字节
		} else {
			n += 1; // 否则表示是英文字符,为1个字节
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
	if (ifHasSpecChar2(taskName)) {
		$("span#nameErrorText").html("（包含有以下特殊字符:<br>~'!@#$%^&*+=）");
		$("span#nameError").html("※");
		flagName = false;
	}
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
	/*
	 * if (ifHasSpecChar(taskDescription)) { $("span#descErrorText").html("（包含有以下特殊字符:<br>~'!@#$%^&*()-+_=:）");
	 * $("span#descError").html("※"); flagName = false; }
	 */
	if (m > 255) {
		$("span#descErrorText").html("（不超过255个字符）");
		$("span#descError").html("※");
		flagDesc = false;
	}

	/*if (endTime == "" || startTime == "") {
		$("span#dateErrorText").html("请填写需要使用的测量数据的时间！");
		$("span#dateError").html("※");
		flagDate = false;
	} else if (exDateRange(endTime, startTime) > maxDateInterval) {
		// 验证测试日期是否大于十天
		$("span#dateErrorText").html("（时间跨度请不要超过" + maxDateInterval + "天！）");
		$("span#dateError").html("※");
		flagDate = false;
	}*/

	if (flagName && flagDesc) {
		result = true;
	} else {
		result = false;
	}
	return result;
}

/**
 * 按条件查询结构分析任务
 */
function getStructureTask() {
	// 重置分页条件
	initFormPage('structureTaskForm');
	// 提交表单
	sumbitStructureForm();
}

/**
 * 提交表单
 */
function sumbitStructureForm() {
	showOperTips("loadingDataDiv", "loadContentId", "正在查询");
	var sessionId = $("#sessionId").text();
	var extraData = {'account' : sessionId};
	$.ajax({
		url : 'queryNewPciPlanAnalysisTaskByPage',
		type: 'POST',
		data : $("#structureTaskForm").serialize() + '&' + $.param(extraData),
		dataType : 'json',
		success : function(data) {
			showStructureTask(data);
		},
		complete : function() {
			hideOperTips("loadingDataDiv");
		}
	});
}

/**
 * 列表显示结构分析任务
 */
function showStructureTask(data) {
	if (data) {
		var table = $("#structureTaskTable");
		// 清空结构分析任务详情列表
		$("#structureTaskTable tr:gt(0)").remove();

		var list = data['data'];
		var tr = "";
		var one = "";

		for (var i = 0; i < list.length; i++) {
			one = list[i];
			tr += "<tr>";
			tr += "<td>" + getValidValue(one['JOB_NAME'], '') + "</td>";
			if (one['JOB_RUNNING_STATUS'] == "Waiting") {
				tr += "<td>排队中</td>";
			} else if (one['JOB_RUNNING_STATUS'] == "Launched" || one['JOB_RUNNING_STATUS'] == "Running") {
				tr += "<td>运行中</td>";
			} else if (one['JOB_RUNNING_STATUS'] == "Stopping") {
				tr += "<td>停止中</td>";
			} else if (one['JOB_RUNNING_STATUS'] == "Stopped") {
				tr += "<td>已停止</td>";
			} else if (one['JOB_RUNNING_STATUS'] == "Fail") {
				tr += "<td style='color:red;'>异常终止</td>";
			} else if (one['JOB_RUNNING_STATUS'] == "Succeded") {
				tr += "<td>正常完成</td>";
			} else {
				tr += "<td></td>";
			}
			tr += "<td>" + getValidValue(one['CITY_NAME'], '') + "</td>";
			tr += "<td>--</td>";
			tr += "<td>" + getValidValue(one['BEG_MEA_TIME'].substr(0, 10), '') + "<br/>至<br/>" + getValidValue(one['END_MEA_TIME'].substr(0, 10), '')
					+ "</td>";
			tr += "<td>" + getValidValue(one['LAUNCH_TIME'], '') + "</td>";
			tr += "<td>" + getValidValue(one['COMPLETE_TIME'], '') + "</td>";
			// 排队中
			if (one['JOB_RUNNING_STATUS'] == "Waiting") {
				tr += "<td><input type='button' value='停止' onclick='stopPciTask(\"" + one['JOB_ID'] + "\",\"" + one['MR_JOB_ID'] + "\")'/></td>";
			}
			// 运行中
			else if (one['JOB_RUNNING_STATUS'] == "Launched" || one['JOB_RUNNING_STATUS'] == "Running") {
				tr += "<td><input type='button' value='停止' onclick='stopPciTask(\"" + one['JOB_ID'] + "\",\"" + one['MR_JOB_ID'] + "\")'/> "
						+ "<input type='button' value='查看运行报告' onclick='checkStructureTaskReport(\"" + one['JOB_ID'] + "\")'/></td>";
			}
			// 异常终止
			else if (one['JOB_RUNNING_STATUS'] == "Fail") {
				tr += "<td><input type='button' value='查看运行报告' onclick='checkStructureTaskReport(\"" + one['JOB_ID'] + "\")'/></td>";
			}
			// 正常完成
			else if (one['JOB_RUNNING_STATUS'] == "Succeded") {
				tr += "<td><input type='button' value='下载结果文件' onclick='downloadPciFile(\"" + one['JOB_ID'] + "\",\"" + one['MR_JOB_ID'] + "\")'/> "
						+ "<input type='button' value='查看运行报告' onclick='checkStructureTaskReport(\"" + one['JOB_ID'] + "\")'/></td>";
			}
			// 停止中
			else if (one['JOB_RUNNING_STATUS'] == "Stopping") {
				tr += "<td><input type='button' value='查看运行报告' onclick='checkStructureTaskReport(\"" + one['JOB_ID'] + "\")'/></td>";
			}
			// 其他（，已停止）
			else {
				tr += "<td><input type='button' value='查看运行报告' onclick='checkStructureTaskReport(\"" + one['JOB_ID'] + "\")'/></td>";
			}
			tr += "</tr>";
		}
		table.append(tr);

		// 设置隐藏的page信息
		setFormPageInfo("structureTaskForm", data['page']);

		// 设置分页面板
		setPageView(data['page'], "structureTaskPageDiv");
	}
}

// 查看运行报告
function checkStructureTaskReport(jobId) {
	$("#viewReportForm").find("input#hiddenJobId").val(jobId);
	initFormPage("viewReportForm");
	$("#reportDiv").css("display", "block");
	$("#structureTaskDiv").css("display", "none");
	$("#renderImgDiv").css("display", "none");
	queryReportData();
}
// 停止任务
function stopPciTask(jobId, mrJobId) {
	var flag = confirm("是否停止该任务计算？");
	if (flag == false) {
		return;
	}
	showOperTips("loadingDataDiv", "loadContentId", "正在停止任务");
	var sessionId = $("#sessionId").text();
	$.ajax({
		url : 'stopNewPciJobByJobIdAndMrJobId',
		type : 'post',
		data : {
			'account' : sessionId,
			'jobId' : jobId,
			'mrJobId' : mrJobId
		},
		dataType : 'json',
		success : function(data) {
			try {
				var flag = data['flag'];
				if (flag) {
					alert("停止操作已提交，请稍后查看停止结果");
					// 刷新列表
					sumbitStructureForm();
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
// 下载结果文件
function downloadPciFile(jobId, mrJobId) {
	var form = $("#downloadPciFileForm");
	form.find("input#jobId").val(jobId);
	form.find("input#mrJobId").val(mrJobId);
	form.submit();
}
// 查看渲染图
function viewRenderImg(jobId) {
	// 保存jobId用于获取对应的渲染图
	$("#reportNcsTaskId").val(jobId);
	// 加载渲染图
	var flag = loadRenderImage();
	if (!flag) {
		return;
	}
	// 加载默认渲染规则
	showRendererRuleColor();

	$("#renderImgDiv").css("display", "block");
	$("#structureTaskDiv").css("display", "none");
	$("#reportDiv").css("display", "none");
}

/**
 * 查询指定job的报告
 */
function queryReportData() {
	$.ajax({
		url : 'queryJobReport',
		type : 'post',
		data : $("#viewReportForm").serialize(),
		dataType : 'json',
		success : function(data) {
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
		if (one['STATE'].indexOf("Fail") == -1) {
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
 * 从报告的详情返回列表页面
 */
function returnToTaskList() {
	$("#reportDiv").css("display", "none");
	$("#structureTaskDiv").css("display", "block");
	$("#renderImgDiv").css("display", "none");
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

// 初始化区域
function initAreaCascade() {

	$("#provinceId2").change(function() {
		getSubAreas("provinceId2", "cityId2", "市");
	});

	$("#cityId2").change(function() {

		// 以城市为单位创建区域网格
		var cityName = $("#cityId2").find("option:selected").text().trim();
		// 获取cityId赋值到表单隐藏域
		var cityId = $("#cityId2").val();
		$("#cityId").val(cityId);
	});
}

// 验证门限值是否符合要求
function checkThreshold() {

	clearTip();

	var SAMEFREQCELLCOEFWEIGHT = $("#SAMEFREQCELLCOEFWEIGHT").html().trim();
	var SWITCHRATIOWEIGHT = $("#SWITCHRATIOWEIGHT").html().trim();
	var CELLM3RINTERFERCOEF = $("#CELLM3RINTERFERCOEF").html().trim();
	var CELLM6RINTERFERCOEF = $("#CELLM6RINTERFERCOEF").html().trim();
	var CELLM30RINTERFERCOEF = $("#CELLM30RINTERFERCOEF").html().trim();
	var BEFORENSTRONGCELLTAB = $("#BEFORENSTRONGCELLTAB").html().trim();
	var TOPNCELLLIST = $("#TOPNCELLLIST").html().trim();
	var INCREASETOPNCELLLIST = $("#INCREASETOPNCELLLIST").html().trim();
	var CONVERMETHOD1TARGETVAL = $("#CONVERMETHOD1TARGETVAL").html().trim();
	var CONVERMETHOD2TARGETVAL = $("#CONVERMETHOD2TARGETVAL").html().trim();
	var CONVERMETHOD2SCOREN = $("#CONVERMETHOD2SCOREN").html().trim();
	var MINCORRELATION = $("#MINCORRELATION").html().trim();
	var MINMEASURESUM = $("#MINMEASURESUM").html().trim();
	var DISLIMIT = $("#DISLIMIT").html().trim();

	var reg = /^[-+]?[0-9]+(\.[0-9]+)?$/; // 验证数字
	var reg1 = /^[0-9]*[1-9][0-9]*$/; // 正整数
	var reg2 = /^(?:[1-9]?\d|100)$/; // 0-100的整数，适用于验证百分数
	var reg3 = /^([1-9]\d*|0)$/; // 非负整数
	var flag = true;

	if (!reg.test(SAMEFREQCELLCOEFWEIGHT)) {
		$("span#SAMEFREQCELLCOEFWEIGHT").html("※请输入数字※");
		flag = false;
	} else if (SAMEFREQCELLCOEFWEIGHT > 1 || SAMEFREQCELLCOEFWEIGHT < 0) {
		$("span#SAMEFREQCELLCOEFWEIGHT").html("※值需要大于等于0小于等于1※");
		flag = false;
	} else {
		$("span#SAMEFREQCELLCOEFWEIGHT").html("");
	}
	if (!reg.test(SWITCHRATIOWEIGHT)) {
		$("span#SWITCHRATIOWEIGHT").html("※请输入数字※");
		flag = false;
	} else if (SWITCHRATIOWEIGHT > 1 || SWITCHRATIOWEIGHT < 0) {
		$("span#SWITCHRATIOWEIGHT").html("※值需要大于等于0小于等于1※");
		flag = false;
	} else {
		$("span#SWITCHRATIOWEIGHT").html("");
	}
	if (!reg.test(CELLM3RINTERFERCOEF)) {
		$("span#CELLM3RINTERFERCOEF").html("※请输入数字※");
		flag = false;
	} else {
		$("span#CELLM3RINTERFERCOEF").html("");
	}

	if (!reg.test(CELLM6RINTERFERCOEF)) {
		$("span#CELLM6RINTERFERCOEF").html("※请输入数字※");
		flag = false;
	} else {
		$("span#CELLM6RINTERFERCOEF").html("");
	}

	if (!reg.test(CELLM30RINTERFERCOEF)) {
		$("span#CELLM30RINTERFERCOEF").html("※请输入数字※");
		flag = false;
	} else {
		$("span#CELLM30RINTERFERCOEF").html("");
	}

	if (!reg1.test(BEFORENSTRONGCELLTAB)) {
		$("span#BEFORENSTRONGCELLTAB").html("※请输入正整数※");
		flag = false;
	} else {
		$("span#BEFORENSTRONGCELLTAB").html("");
	}

	if (!reg2.test(TOPNCELLLIST)) {
		$("span#TOPNCELLLIST").html("※请输入0-100间的整数※");
		flag = false;
	} else {
		$("span#TOPNCELLLIST").html("");
	}

	if (!reg.test(INCREASETOPNCELLLIST)) {
		$("span#INCREASETOPNCELLLIST").html("※请输入数字※");
		flag = false;
	} else {
		$("span#INCREASETOPNCELLLIST").html("");
	}

	if (!reg2.test(CONVERMETHOD1TARGETVAL)) {
		$("span#CONVERMETHOD1TARGETVAL").html("※请输入0-100间的整数※");
		flag = false;
	} else {
		$("span#CONVERMETHOD1TARGETVAL").html("");
	}

	if (!reg2.test(CONVERMETHOD2TARGETVAL)) {
		$("span#CONVERMETHOD2TARGETVAL").html("※请输入0-100间的整数※");
		flag = false;
	} else {
		$("span#CONVERMETHOD2TARGETVAL").html("");
	}

	if (!reg.test(CONVERMETHOD2SCOREN)) {
		$("span#CONVERMETHOD2SCOREN").html("※请输入数字※");
		flag = false;
	} else {
		$("span#CONVERMETHOD2SCOREN").html("");
	}

	if (!reg2.test(MINCORRELATION)) {
		$("span#MINCORRELATION").html("※请输入0-100间的整数※");
		flag = false;
	} else {
		$("span#MINCORRELATION").html("");
	}
	if (!reg3.test(MINMEASURESUM)) {
		$("span#MINMEASURESUM").html("※请输入非负整数※");
		flag = false;
	} else {
		$("span#MINMEASURESUM").html("");
	}
	if (!reg3.test(DISLIMIT)) {
		$("span#DISLIMIT").html("※请输入非负整数※");
		flag = false;
	} else {
		$("span#DISLIMIT").html("");
	}
	return flag;
}

function clearTip() {

	$("span#SAMEFREQINTERTHRESHOLD").html("");
	$("span#OVERSHOOTINGIDEALDISMULTIPLE").html("");
	$("span#BETWEENCELLIDEALDISMULTIPLE").html("");
	$("span#CELLCHECKTIMESIDEALDISMULTIPLE").html("");
	$("span#CELLDETECTCITHRESHOLD").html("");
	$("span#CELLIDEALDISREFERENCECELLNUM").html("");
	$("span#GSM900CELLFREQNUM").html("");
	$("span#GSM1800CELLFREQNUM").html("");
	$("span#GSM900CELLIDEALCAPACITY").html("");
	$("span#GSM1800CELLIDEALCAPACITY").html("");
	$("span#DLCOVERMINIMUMSIGNALSTRENGTHTHRESHOLD").html("");
	$("span#ULCOVERMINIMUMSIGNALSTRENGTHTHRESHOLD").html("");
	$("span#INTERFACTORMOSTDISTANT").html("");
	$("span#INTERFACTORSAMEANDADJFREQMINIMUMTHRESHOLD").html("");
	$("span#RELATIONNCELLCITHRESHOLD").html("");

	$("span#TOTALSAMPLECNTSMALL").html("");
	// $("span#TOTALSAMPLECNTBIG").html("");
	$("span#TOTALSAMPLECNTTOOSMALL").html("");
	$("span#SAMEFREQINTERCOEFBIG").html("");
	$("span#SAMEFREQINTERCOEFSMALL").html("");
	$("span#OVERSHOOTINGCOEFRFFERDISTANT").html("");
	$("span#NONNCELLSAMEFREQINTERCOEF").html("");
}

// 计算两个日期差值
function exDateRange(sDate1, sDate2) {
	var iDateRange;
	if (sDate1 != "" && sDate2 != "") {
		var startDate = sDate1.replace(/-/g, "/");
		var endDate = sDate2.replace(/-/g, "/");
		var S_Date = new Date(Date.parse(startDate));
		var E_Date = new Date(Date.parse(endDate));
		iDateRange = (S_Date.getTime() - E_Date.getTime()) / oneDay;
		// alert(iDateRange);
	}
	return iDateRange;
}

function extendInt(i) { // 将1位数转换为0开头的2位数
	var j = "00";
	if (i < 10) {
		j = "0" + i.toString();
	} else {
		j = i.toString();
	}
	return j;
};
/**
 * 根据结束时间调整开始时间，使其保持在maxDateInterval天以内
 */
function getEndDate() {
	var begDateStr = $("#begTime").val().replace(/-/g, "\/");
	var endDateStr = $("#endTime").val().replace(/-/g, "\/");
	var begDate = new Date(begDateStr);
	var endDate = new Date(endDateStr);
	var begTimeStr = extendInt(begDate.getHours()) + ":" + extendInt(begDate.getMinutes()) + ":" + extendInt(begDate.getSeconds());
	if (exDateRange(endDateStr, begDateStr) > maxDateInterval) {
		endDate.setDate(endDate.getDate() - maxDateInterval);
		var sss = endDate.getFullYear() + "-" + (endDate.getMonth() + 1) + "-" + endDate.getDate() + " " + begTimeStr;
		if (exDateRange(endDateStr, sss.replace(/-/g, "\/")) > maxDateInterval) {
			endDate.setDate(endDate.getDate() + 1);
			sss = endDate.getFullYear() + "-" + (endDate.getMonth() + 1) + "-" + endDate.getDate() + " " + begTimeStr;
		}
		$("#begTime").val(sss);
		beginTime.focus();
	}
};
/**
 * 根据开始时间调整结束时间，使其保持在maxDateInterval天以内
 */
function getBegDate() {
	var begDateStr = $("#begTime").val().replace(/-/g, "\/");
	var endDateStr = $("#endTime").val().replace(/-/g, "\/");
	var begDate = new Date(begDateStr);
	var endDate = new Date(endDateStr);
	var endTimeStr = extendInt(endDate.getHours()) + ":" + extendInt(endDate.getMinutes()) + ":" + extendInt(endDate.getSeconds());
	if (exDateRange(endDateStr, begDateStr) > maxDateInterval) {
		begDate.setDate(begDate.getDate() + maxDateInterval);
		var sss = begDate.getFullYear() + "-" + (begDate.getMonth() + 1) + "-" + begDate.getDate() + " " + endTimeStr;
		if (exDateRange(sss.replace(/-/g, "\/"), begDateStr) > maxDateInterval) {
			begDate.setDate(begDate.getDate() - 1);
			sss = begDate.getFullYear() + "-" + (begDate.getMonth() + 1) + "-" + begDate.getDate() + " " + endTimeStr;
		}
		$("#endTime").val(sss);
		if (focusCnt > 0) {
			latestAllowedTime.focus();
		}
	}
};

// 点击td转换可编辑
function setEditHTML(value) {
	editHTML = '<input id="editTd" type="text" maxlength="7" onBlur="ok(this)" value="' + value + '" />';

}

// 修改
function ok(obtn) {
	var $obj = $(obtn).parent();
	var objId = $obj.attr("id");
	var value = $obj.find("input:text")[0].value; // 取得文本框的值，即新数据
	if (value === "") {
		value = "   ";
	}
	// alert("success");
	$obj.data("oldtxt", value); // 设置此单元格缓存为新数据
	$obj.html($obj.data("oldtxt"));
	$obj.bind("click", function() { // 重新绑定单元格单击事件
		editText = $(this).html().trim();
		setEditHTML(editText);
		$(this).data("oldtxt", editText).html(editHTML).unbind("click");
		$("#editTd").focus();

	});
	if (objId == "SAMEFREQCELLCOEFWEIGHT") {
		var samefreq = $obj.text();
		var r = new RegExp("^\\d+(\\.\\d+)?$");
		if (r.test(samefreq)) {
			$("#SAMEFREQCELLCOEFWEIGHT").text(Number(samefreq).toFixed(2));
			$("#SWITCHRATIOWEIGHT").text((1 - Number(samefreq)).toFixed(2));
		} else {
			$("#SAMEFREQCELLCOEFWEIGHT").text("");
			$("#SWITCHRATIOWEIGHT").text("");
		}
	}
	if (objId == "SWITCHRATIOWEIGHT") {
		var switchrat = $obj.text();
		var r = new RegExp("^\\d+(\\.\\d+)?$");
		if (r.test(switchrat)) {
			$("#SWITCHRATIOWEIGHT").text(Number(switchrat).toFixed(2));
			$("#SAMEFREQCELLCOEFWEIGHT").text((1 - Number(switchrat)).toFixed(2));
		} else {
			$("#SWITCHRATIOWEIGHT").text("");
			$("#SAMEFREQCELLCOEFWEIGHT").text("");
		}
	}
}
function getFileName() {
	var filenames = "";
	var files = $(".forfilecheck");
		for (var i = 0; i < files.length; i++) {
			if (files.eq(i).prop("checked")) {
				filenames += files.eq(i).parent().next().text() + ",";
			}
		}
		return filenames.substr(0, filenames.length - 1) + "";
}

function getSubAreas(parentDom, cityDom) {
	var parentId = $("#" + parentDom).val();
	$("#" + cityDom).empty();

	showOperTips("loadingDataDiv", "loadContentId", "获取区域信息");
	var sessionId = $("#sessionId").text();
	$.ajax({
		url : 'querySubAreasByParentId',
		type : 'post',
		data : {
			'account' : sessionId,
			'parentId' : parentId
		},
		dataType : 'json',
		success : function(data) {
			var state = data['state'];
			if (state) {
				var one, htmlStr = "";
				var subAreas = data['data'];
				$(subAreas).each(function(index, row) {
					htmlStr += "<option value='" + row['areaId'] + "'>" + $.trim(row['name']) + "</option>";
				});
				$("#" + cityDom).append(htmlStr);
			}
		},
		complete : function() {
			hideOperTips("loadingDataDiv");
		}
	});
}

/**
 * 在某个基准日期的基础上，对时间进行加减 正数为加，负数为减去
 * 
 * @param baseDate
 * @param day
 * @returns {Date}
 */
function addDays(baseDate, day) {
	var tinoneday = 24 * 60 * 60 * 1000;
	var nt = baseDate.getTime();
	var cht = day * tinoneday;
	var newD = new Date();
	newD.setTime(nt + cht);
	return newD;
}

function datepicker() {
	$("#beginTime").datepicker({
		"dateFormat" : "yy-mm-dd"
	});

	$("#startSubmitTime").datepicker({
		lang : 'ch',// 中文化
		dateFormat : "yy-mm-dd",
		defaultDate : "-2",
		changeYear : true,
		changeMonth : true,
		showOtherMonths : true,
		selectOtherMonths : true,
		numberOfMonths : 1,
		onClose : function(selectedDate) {
			$("#endSubmitTime").datepicker("option", "minDate", selectedDate);
			$("#endSubmitTime").show;
		}
	});

	$("#endSubmitTime").datepicker({
		lang : 'ch',// 中文化
		dateFormat : "yy-mm-dd",
		defaultDate : "+1w",
		changeYear : true,
		changeMonth : true,
		showOtherMonths : true,
		selectOtherMonths : true,
		numberOfMonths : 1,
		onClose : function(selectedDate) {
			$("#startSubmitTime").datepicker("option", "maxDate", selectedDate);
		}
	});

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
			$("#endTime").datepicker("option", "minDate", selectedDate);
			// $("#endTime").datetimepicker("show");
		}
	});
	$("#begTime").datepicker("setDate", addDays(new Date(), -5));// 减去2天

	$("#endTime").datepicker({
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
	$("#endTime").datepicker("setDate", addDays(new Date(), -1));
}

/**
 * 测试是否包含以下特殊字符 ~'!@#$%^&*+=
 * 
 * @param str
 * @returns
 */
function ifHasSpecChar2(str) {
	var pattern = new RegExp("[~'!@#$%^&*+=]");
	return pattern.test(str);
}

function showOperTips(outerId, tipId, tips) {
	try {
		$("#" + outerId).css("display", "");
		$("#" + outerId).find("#" + tipId).html(tips);
	} catch (err) {
	}
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

function hideOperTips() {
	$("#loadingDataDiv").css("display", "none");
}

/**
 * 测试是否包含有以下特殊字符 ~'!@#$%^&*()-+_=:
 * 
 * @param str
 * @returns
 */
function ifHasSpecChar(str) {
	var pattern = new RegExp("[~'!@#$%^&*()-+_=:]");
	return pattern.test(str);
}

/**
 * 使一个元素渐渐展现然后又渐渐隐去
 * 
 * @param objId
 * @param timeIn
 * @param timeOut
 * @param stayTime
 * 
 */
function animateInAndOut(objId, timeIn, timeOut, stayTime, tipId, tips) {
	if (objId == null || objId == undefined) {
		return;
	}
	if (tipId && tips) {
		try {
			$("#" + tipId).html(tips);
		} catch (err) {

		}
	}
	try {
		if (typeof timeIn == "number" && typeof timeOut == "number") {
			$("#" + objId).fadeIn(timeIn, function() {
				window.setTimeout(function() {
					$("#" + objId).fadeOut(timeOut);
				}, stayTime);
			});
		}
	} catch (err) {

	}
}

/**
 * 是否以逗号分割的数字，允许逗号结尾
 * 
 * @param lteCells
 * @returns {Boolean}
 */
function isNumCutByComma(lteCells) {
	var flag = true;
	var reg = /^(\d{5,8}-\d{1,3})(,\d{5,9}-\d{1,3})*,?$/;
	if (!reg.test(lteCells)) {
		flag = false;
	}
	return flag;
}

/**
 * 判断是不是数字
 * 
 * @param num
 *            要判断的内容
 * @param reg
 *            用于判断的正则表达式
 * @returns {Boolean}
 */
function isNumeric(num, reg) {
	var flag = true;
	if (num == null || num == undefined) {
		flag = false;
	}
	if (flag) {
		// 2016.4.7 cc 修改 reg.trim() 不能对正则表达式使用
		// if (reg == null || reg == undefined || reg.trim() == "") {
		if (reg == null || reg == undefined) {
			if (isNaN(num)) {
				flag = false;
			}
		} else if (typeof (reg) == 'object') { // 正则表达式的类型为object 但不安全
			if (!reg.test(num)) {
				flag = false;
			}
		} else {
			flag = false;
		}
	}
	return flag;
}

/**
 * 获取最近十次干扰矩阵，显示在页面列表
 */
function getLatelyLteMatrix() {
	
	$("#matrix").html("");
	areaid=$("#citymenu").find("option:selected").val();
	
	$.ajax({
		url : 'getLatelyLteMatrix/'+areaid,
		dataType : 'json',
		type : 'post',
		success : function(data) {
			//console.log(raw);
            //var data = eval("(" + raw + ")");
			if(data.length != 0) {
				var optHtml = "";
				for ( var i = 0; i < data.length; i++) {
					var one = data[i];
					optHtml += "<option value='"+one['JOB_ID']+"'>"+one['TASK_NAME']+"</option>";
				}
				//console.log(optHtml);
				$("#matrix").append(optHtml);
			} 
		}
	});
}