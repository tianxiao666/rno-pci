var map;
var tiled;
var baseMapUrl = 'http://192.168.6.71/tiles/osm';
var geoUrl='http://rnodev-ci.iscreate.com:18080/geoserver/rnodev';
var wfsUrl = "http://rnodev-ci.iscreate.com:18080/geoserver/rnodev/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=rnodev:gis_lte_cell_layer&maxFeatures=50&outputFormat=text%2Fjavascript";
var celllabel = "";
var highlightOverlay;
var lineOverlay;
var queryCellOverlay;
var bezierOverlay;
var popup;
var contextmenu_items;
var lineFeatureArr = new Array();

var ncellsOfCell = new Array();// 缓存获取到的小区的邻区，存储的是CellToNcell对象

var targetCellColor = "#B40431";// 暗红色
var targetNcellColor = "#2EFE2E";// 耀眼的绿色
var ncellsOfCell = new Array();// 缓存获取到的小区的邻区，存储的是CellToNcell对象


var dynaPolygon_12,dynaPolygon_3; //动态覆盖图形
var dynaPolyline_12,dynaPolyline_3; //矢量线
var dynaArrow_12,dynaArrow_3;  //矢量线的箭头
var dynaCellColor = "#1C1C1C"; //当前动态覆盖图所属小区的颜色
var dynaPolygonColor_12 = "blue";
var dynaPolygonColor_3 = "red";
var dynaPolylineColor_12 = "#2EFE2E"; 
var dynaPolylineColor_3 = "red";

var blackstyle;
var redstyle;
var greenstyle;
var $ = jQuery.noConflict();
$(document).ready(
		function() {
			//tab选项卡
			tab("div_tab", "li", "onclick");//项目服务范围类别切换
			$(".draggable").draggable();
			$("#trigger").css("display","none");
			// 禁止显示地图页面缺省的右键菜单，不影响 Openlayers3 自定义的右键菜单，将该div下的内容屏蔽缺省右键菜单
			document.getElementById('map').oncontextmenu = function () {
				return false;
			};
			var lng = $("#hiddenLng").val();
			var lat = $("#hiddenLat").val();
			//以城市为单位创建区域网格
			var cityName = $("#cityId").find("option:selected").text().trim();
			var areaId = $("#cityId").val();
			bindNormalEvent();
			//区域联动
			initAreaCascade();
			var baseLayerGroup = new ol.layer.Group({
				'title': '基础地图',
				layers: [
					// 添加一个使用离线瓦片地图的层
					new ol.layer.Tile({
						title: '本地地图',
						type: 'base',
						visible: true,
						source: new ol.source.XYZ({
							url: baseMapUrl+'/{z}/{x}/{y}.png'
						})
					}),
					new ol.layer.Tile({
						title: '在线地图',
						type: 'base',
						visible: false,
						source: new ol.source.OSM()
					}),
					new ol.layer.Group({})]
			});
			 tiled = new ol.layer.Tile({
				title: '广州LTE小区',
				source: new ol.source.TileWMS({
					url: geoUrl+'/wms',
					params: {
						'FORMAT': 'image/png',
						'VERSION': '1.1.1',
						tiled: true,
						STYLES: '',
						LAYERS: 'rnodev:gis_lte_cell_layer'
					}
				}),
				opacity: 0.5
			});
			 highlightOverlay = new ol.layer.Vector({
				title: '被选小区高亮',
				source: new ol.source.Vector(),
				map: map
			});
			lineOverlay = new ol.layer.Vector({
				title: '小区间连线',
				source: new ol.source.Vector(),
				map: map
			});
			queryCellOverlay = new ol.layer.Vector({
				source : new ol.source.Vector(),
				map : map
			});
			bezierOverlay = new ol.layer.Vector({
				source : new ol.source.Vector(),
				map : map
			});

			var overlaysGroup = new ol.layer.Group({
				title: 'Overlays',
				layers: [tiled, highlightOverlay,lineOverlay,queryCellOverlay,bezierOverlay]
			});

			map = new ol.Map({
				view: new ol.View({
					projection: 'EPSG:4326',
					center: [parseFloat(lng), parseFloat(lat)],
					zoom: 16
				}),

				layers: [baseLayerGroup, overlaysGroup],

				target: 'map'
			});

			var layerSwitcher = new ol.control.LayerSwitcher({
				tipLabel: 'RNO-GIS' // Optional label for button
			});
			map.addControl(layerSwitcher);

			map.on("moveend", function () {
				var zoom = map.getView().getZoom();
				var zoomInfo = 'Zoom level = ' + zoom;
				// console.log(zoomInfo);
			});

			// Popup showing the position the user clicked
			popup = new ol.Overlay({
				element: document.getElementById('popup')
			});
			map.addOverlay(popup);
			//单击事件弹出小区信息窗口
			map.on('singleclick', function (evt) {
				// console.log("singleclick===="+evt.coordinate[0]);
				var element = popup.getElement();
				$(element).popover('destroy');
				var view = map.getView();
				// console.log("view==========="+view);
				var url = tiled.getSource().getGetFeatureInfoUrl(
					evt.coordinate, view.getResolution(), view.getProjection(),
					{'INFO_FORMAT': 'text/javascript', 'FEATURE_COUNT': 50});
				if (url) {
					var parser = new ol.format.GeoJSON();
					$.ajax({
						url: url,
						dataType: 'jsonp',
						jsonpCallback: 'parseResponse'
					}).then(function (response) {
						var features = parser.readFeatures(response);
						if (features.length) {
							// 高亮 Features
							highlightOverlay.getSource().clear();
							queryCellOverlay.getSource().clear();
							highlightOverlay.getSource().addFeatures(features);
							var content = '<table class="table table-hover">';
							content += '<thead><th>小区ID</th><th>小区名称</th><th>PCI</th></thead>';
							content += '<tbody>';
							// 获取多个重叠 feature
							for (var i = 0; i < features.length; i++) {
								var feature = features[i];
								content += '<tr>';
								content += '<td>' + feature.get('cell_id') + '</td>';
								content += '<td>' + feature.get('cell_name') + '</td>';
								content += '<td>' + feature.get('pci') + '</td>';
								content += '</tr>';

								// 设置 feature 的样式
								feature.setStyle(redstyle);
							}
							content += '</tbody></table>';
							popup.setPosition(evt.coordinate);
							// the keys are quoted to prevent renaming in ADVANCED mode.
							$(element).popover({
								'placement': 'top',
								'animation': false,
								'html': true,
								'content': content
							});

							try {
								$(element).popover('show');
							}catch (e){
								console.log(e);
							}
							$('.table > tbody > tr').click(function () {
								// row was clicked
								var cellId = $(this).find('td:first').text();
								// console.log("通过小区ID查工参详情 : " + cellId);
							});
						} else {
							console.log('No result');
						}
					});
				}
			});
			//右键菜单项
			contextmenu_items = [
				/*{
					text: '动态覆盖图1',
					data: 7,
					callback:showDynaCoverageCallBack
				},*/
				{
					text: '动态覆盖图2',
					data: 7,
					callback: showDynaCoverage2CallBack
				},
				{
					text: 'IN干扰小区连线',
					data: 7,
					callback: InInterCellLineCallBack
				},
				{
					text: 'OUT干扰小区连线',
					data: 7,
					callback: OutInterCellLineCallBack
				}
				];
			var contextmenu = new ContextMenu({
				width: 120,
				items: contextmenu_items
			});
			//将上下文菜单项加入地图控件
			map.addControl(contextmenu);
			// 右键菜单打开之前，判断是否在 feature 上，如果不是则禁止右键菜单
			// 先将该处feature重绘然后再打开feature;
			contextmenu.on('beforeopen', function (e) {
				// console.log("e.coordinate====="+e.coordinate);
				//清理弹出小区信息覆盖物
				var element = popup.getElement();
				$(element).popover('destroy');
				var view = map.getView();
				// console.log("view==========="+view);
                contextmenu_items[contextmenu_items.length-1] = e.coordinate;
				var url = tiled.getSource().getGetFeatureInfoUrl(
					e.coordinate, view.getResolution(), view.getProjection(),
					{'INFO_FORMAT': 'text/javascript', 'FEATURE_COUNT': 50});
				if (url) {
					var parser = new ol.format.GeoJSON();
					$.ajax({
						url: url,
						dataType: 'jsonp',
						jsonpCallback: 'parseResponse'
					}).then(function (response) {
					var	features = parser.readFeatures(response);
						if (features.length) {
							// 高亮 Features
							//先清除覆盖物
							clearAll();
							highlightOverlay.getSource().addFeatures(features);
							// 获取多个重叠 feature
							for (var i = 0; i < features.length; i++) {
								var feature = features[i];
								// 设置 feature 的样式
								feature.setStyle(redstyle);
							}
							contextmenu.enable();
						} else {
							//highlightOverlay.getSource().clear();
							console.log('No result');
							contextmenu.disable();
						}
					});
				}
			});
			// 打开右键菜单
			contextmenu.on('open', function (e) {
				/*var feature = map.forEachFeatureAtPixel(e.pixel, function (feature) {
					return feature;
				});
				// console.log(feature);
				if (feature) {
					contextmenu.clear();
					contextmenu.extend(contextmenu_items);
				}*/
			});
			//样式设置
			redstyle = new ol.style.Style({
				stroke : new ol.style.Stroke({
					// 设置线条颜色
					color : 'yellow',
					size : 5
				}),
				fill : new ol.style.Fill({
					// 设置填充颜色与不透明度
					color : 'rgba(255, 0, 0, 1.0)'
				})
			});

			blackstyle = new ol.style.Style({
				stroke : new ol.style.Stroke({
					// 设置线条颜色
					color : 'yellow',
					size : 5
				}),
				fill : new ol.style.Fill({
					// 设置填充颜色与不透明度
					color : 'black',//rgba(0, 0, 0, 1.0),
					opacity :1.0
				})
			});

			greenstyle = new ol.style.Style({
				stroke : new ol.style.Stroke({
					// 设置线条颜色
					color : 'yellow',
					size : 5
				}),
				fill : new ol.style.Fill({
					// 设置填充颜色与不透明度
					color : 'rgba(0, 255, 0, 1.0)'
				})
			});

}, false);
function sleep(d){
	for(var t = Date.now();Date.now() - t <= d;);
}
var showDynaCoverageCallBack = function　dynaCoverage1(){
	var features = highlightOverlay.getSource().getFeatures();
	var element = popup.getElement();
	var coordinate = contextmenu_items[contextmenu_items.length-1];
	if (features.length) {
		// 高亮 Features
		var content = '<table class="table table-hover">';
		content += '<thead><th>小区ID</th><th>小区名称</th><th>PCI</th></thead>';
		content += '<tbody>';
		// 获取多个重叠 feature
		for (var i = 0; i < features.length; i++) {
			var feature = features[i];
			var geo = feature.get('geometry')['flatCoordinates'];

			content += '<tr><input name="" type="hidden" value='+ geo[2]+","+geo[3] + '>';
			content += '<td>' + feature.get('cell_id') + '</td>';
			content += '<td>' + feature.get('cell_name') + '</td>';
			content += '<td>' + feature.get('pci') + '</td>';

			content += '</tr>';
			// 设置 feature 的样式
			feature.setStyle(redstyle);
		}
		content += '</tbody></table>';
		popup.setPosition(coordinate);
		// the keys are quoted to prevent renaming in ADVANCED mode.
		$(element).popover({
			'placement': 'top',
			'animation': false,
			'html': true,
			'content': content
		});
		try {
			$(element).popover('show');
		}catch (e){
			console.log(e);
		}
		$('.table > tbody > tr').click(function () {
			// row was clicked
			var lnglat = $(this).find('input').val();
			var cellId = $(this).find('td:first').text();
			var lnglatarr = lnglat.split(",");
			showDynaCoverage(cellId,lnglatarr[0],lnglatarr[1]);
			console.log("通过小区ID查工参详情 : " + cellId+"-----经纬度详情="+lnglat);
		});
	} else {
		console.log('No result');
	}
}
var showDynaCoverage2CallBack = function dynaCoverage2(){
	var features = highlightOverlay.getSource().getFeatures();
	var element = popup.getElement();
	var coordinate = contextmenu_items[contextmenu_items.length-1];
	if (features.length) {
		// 高亮 Features

		var content = '<table class="table table-hover">';
		content += '<thead><th>小区ID</th><th>小区名称</th><th>PCI</th></thead>';
		content += '<tbody>';
		// 获取多个重叠 feature
		for (var i = 0; i < features.length; i++) {
			var feature = features[i];
			//var geo = feature.get('geometry')['flatCoordinates'];
			var geo = feature.getProperties();
			// console.log("geo===="+geo)
			content += '<tr><input name="" type="hidden" value='+ geo['longitude']+","+geo['latitude'] + '>';
			content += '<td>' + feature.get('cell_id') + '</td>';
			content += '<td>' + feature.get('cell_name') + '</td>';
			content += '<td>' + feature.get('pci') + '</td>';

			content += '</tr>';

			// 设置 feature 的样式
			feature.setStyle(redstyle);
		}
		content += '</tbody></table>';
		popup.setPosition(coordinate);
		// the keys are quoted to prevent renaming in ADVANCED mode.
		$(element).popover({
			'placement': 'top',
			'animation': false,
			'html': true,
			'content': content
		});
		try {
			$(element).popover('show');
		}catch (e){
			console.log(e);
		}
		$('.table > tbody > tr').click(function () {
			// row was clicked
			var lnglat = $(this).find('input').val();
			var cellId = $(this).find('td:first').text();
			var lnglatarr = lnglat.split(",");
			showDynaCoverage2(cellId,lnglatarr[0],lnglatarr[1]);
			// console.log("通过小区ID查工参详情 : " + cellId+"-----经纬度详情="+lnglat);
		});
	} else {
		console.log('No result');
	}
}
var InInterCellLineCallBack = function InCellLine(){
	var features = highlightOverlay.getSource().getFeatures();
	var element = popup.getElement();
	var coordinate = contextmenu_items[contextmenu_items.length-1];
	if (features.length) {
		// 高亮 Features

		var content = '<table class="table table-hover">';
		content += '<thead><th>小区ID</th><th>小区名称</th><th>PCI</th></thead>';
		content += '<tbody>';
		// 获取多个重叠 feature
		for (var i = 0; i < features.length; i++) {
			var feature = features[i];
			var geo = feature.get('geometry')['flatCoordinates'];

			content += '<tr><input name="" type="hidden" value='+ geo[2]+","+geo[3] + '>';
			content += '<td>' + feature.get('cell_id') + '</td>';
			content += '<td>' + feature.get('cell_name') + '</td>';
			content += '<td>' + feature.get('pci') + '</td>';

			content += '</tr>';

			// 设置 feature 的样式
			feature.setStyle(redstyle);
		}
		content += '</tbody></table>';
		popup.setPosition(coordinate);
		// the keys are quoted to prevent renaming in ADVANCED mode.
		$(element).popover({
			'placement': 'top',
			'animation': false,
			'html': true,
			'content': content
		});
		try {
			$(element).popover('show');
		}catch (e){
			console.log(e);
		}
		$('.table > tbody > tr').click(function () {
			// row was clicked
			var lnglat = $(this).find('input').val();
			var cellId = $(this).find('td:first').text();
			var lnglatarr = lnglat.split(",");
			InInterCellLine(cellId,lnglatarr[0],lnglatarr[1]);
			// console.log("通过小区ID查工参详情 : " + cellId+"-----经纬度详情="+lnglat);
		});
	} else {
		console.log('No result');
	}
}
var OutInterCellLineCallBack = function OutCellLine(){
	var features = highlightOverlay.getSource().getFeatures();
	var element = popup.getElement();
	var coordinate = contextmenu_items[contextmenu_items.length-1];
	if (features.length) {
		// 高亮 Features

		var content = '<table class="table table-hover">';
		content += '<thead><th>小区ID</th><th>小区名称</th><th>PCI</th></thead>';
		content += '<tbody>';
		// 获取多个重叠 feature
		for (var i = 0; i < features.length; i++) {
			var feature = features[i];
			var geo = feature.get('geometry')['flatCoordinates'];

			content += '<tr><input name="" type="hidden" value='+ geo[2]+","+geo[3] + '>';
			content += '<td>' + feature.get('cell_id') + '</td>';
			content += '<td>' + feature.get('cell_name') + '</td>';
			content += '<td>' + feature.get('pci') + '</td>';

			content += '</tr>';

			// 设置 feature 的样式
			feature.setStyle(redstyle);
		}
		content += '</tbody></table>';
		popup.setPosition(coordinate);
		// the keys are quoted to prevent renaming in ADVANCED mode.
		$(element).popover({
			'placement': 'top',
			'animation': false,
			'html': true,
			'content': content
		});
		try {
			$(element).popover('show');
		}catch (e){
			console.log(e);
		}
		$('.table > tbody > tr').click(function () {
			// row was clicked
			var lnglat = $(this).find('input').val();
			var cellId = $(this).find('td:first').text();
			var lnglatarr = lnglat.split(",");
			OutInterCellLine(cellId,lnglatarr[0],lnglatarr[1]);
			console.log("通过小区ID查工参详情 : " + cellId+"-----经纬度详情="+lnglat);
		});
	} else {
		console.log('No result');
	}
}
/*
改变小区样式
 */
function changeCellPolygonOptions(cellLng,cellLat,fillOption){
	var view = map.getView();
	var url = tiled.getSource().getGetFeatureInfoUrl(
		[cellLng,cellLat], view.getResolution(), view.getProjection(),
		{'INFO_FORMAT': 'text/javascript', 'FEATURE_COUNT': 50});
	var style = new ol.style.Style({
		stroke: new ol.style.Stroke({
			// 设置线条颜色
			color: 'yellow',
			size: 5
		}),
		fill: new ol.style.Fill({
			// 设置填充颜色与不透明度
			color: fillOption.fillColor
		})
	});
	if (url) {
		console.log("url==="+url);
		var parser = new ol.format.GeoJSON();
		$.ajax({
			url: url,
			dataType: 'jsonp',
			jsonpCallback: 'parseResponse'
		}).then(function (response) {
			var features = parser.readFeatures(response);

			if (features.length) {
				// 高亮 Features
				// highlightOverlay.getSource().clear();
				highlightOverlay.getSource().addFeatures(features);
				// 获取多个重叠 feature
				for (var i = 0; i < features.length; i++) {
					var feature = features[i];

					// 设置 feature 的样式
					feature.setStyle(style);
				}

			} else {
				console.log('No result');
			}
		});
	}
}
/**
 * 画出两点坐标的连线
 */
function drawLineBetweenPoints(cellLon, cellLat,ncellLon,ncellLat,option) {
	var line =new ol.geom.LineString(ol.proj.transform([[cellLon,cellLat],[ncellLon,ncellLat]], 'EPSG:4326', 'EPSG:4326'));
	var lineFeature = new ol.Feature(line);
	lineOverlay.getSource().addFeature(lineFeature);
	//lineFeatureArr.push(lineFeature);

	lineFeature.setStyle(new ol.style.Style({
		fill: new ol.style.Fill({
			color: option.strokeColor
		}),
		stroke: new ol.style.Stroke({
			width: option.strokeWeight,
			color: option.strokeColor
		})
	}));
	/*// Source
	var vectorSource = new ol.source.Vector({
		projection: 'EPSG:4326'
	});
	vectorSource.addFeatures([lineFeature]);

	// Vector layer
	vectorLayer = new ol.layer.Vector({
		source: vectorSource,
	});

	// Add Vector layer to map
	map.addLayer(vectorLayer);*/
	return lineFeature;
}
function bindNormalEvent() {

	//打开查找小区窗口
	$(".queryButton").click(function() {
		//$("#searchDiv").slideToggle();
		$("#searchDiv").toggle();
	});
	// 根据条件搜索小区
	$("#searchCellBtn").click(function() {
		$("span#errorDiv").html("");
		var cell = $("#conditionValue").val();
		 /*var strExp=/^[\u4e00-\u9fa5A-Za-z0-9\s_-]+$/;
		  if(!strExp.test(cell)){
			   	$("span#errorDiv").html("含有非法字符！");
			   	return false;
		  }else if(!(cell.length<40)){
				$("span#errorDiv").html("输入信息过长！");
			    return false;
		  }*/
		searchCell();
	});
	
	//清除地图上的动态覆盖图
	$("#clearCoverPolygon").click(function() {
		//清除动态覆盖图，方向线，箭头
		clearAll();
	});
}


function initPageParam() {
	$("#hiddenPageSize").val(100);
	$("#hiddenCurrentPage").val(1);
	$("#hiddenTotalPageCnt").val(0);
	$("#hiddenTotalCnt").val(0);
	$("#hiddenForcedStartIndex").val(-1);
}


/**
 * 区域联动事件
 */
function initAreaCascade() {
	// 区域联动事件
	$("#provinceId").change(function() {
		getSubAreas("provinceId", "cityId", "市");
	});
	$("#clearSearchResultBtn").click(function(){
		clearAll();
	});
	$("#cityId").change(
			function() {
				
				// stsResult = getRnoTrafficRenderer('pdinterference');
				getSubAreas("cityId", "areaId", "区/县", function(data) {
					$("#hiddenAreaLngLatDiv").html("");
					$("#hiddenLng").val("");
					$("#hiddenLat").val("");
					// console.log("data===" + data.toSource());
					if (data) {
						var html = "";
						for ( var i = 0; i < data.length; i++) {
							var one = data[i];
							html += "<input type=\"hidden\" id=\"areaid_"
									+ one['areaId'] + "\" value=\""
									+ one["lon"] + "," + one["lat"]
									+ "\" />";
							// console.log("lng="+one["longitude"]);
							// console.log("lat="+one["latitude"]);
							if (i == 0) {
								/*console.log("lng="+one["lon"]);
								console.log("lat="+one["lat"]);*/
								$("#hiddenLng").val(one["lon"]);
								$("#hiddenLat").val(one["lat"]);
							}
						}
						$("#hiddenAreaLngLatDiv").append(html);
					}
					// $("#areaId").trigger("change");
					$("#areaId").append("<option value=\"-1\">全部</option>");
				});
			});

	$("#areaId").change(function() {
		var lnglat = $("#areaid_" + $("#areaId").val()).val();
		// console.log("lnglat===="+lnglat);
		if (lnglat) {
			// 地图中心点
			var lls = lnglat.split(",");
			if (lls[0] == 0 || lls[1] == 0) {
				// console.warn("未设置该区域的中心点经纬度。");
			} else {
				// 地图移动
				// 应用内置的动画，实现平移动画
				var pan = ol.animation.pan({
					duration : 2000,
					source : (map.getView().getCenter())
				});
				// console.log("lls[0]="+lls[0]+"-----lls[0]="+lls[1]);
				map.beforeRender(pan);
				map.getView().setCenter([ parseFloat(lls[0]), parseFloat(lls[1]) ]);
			}
		}

		//以城市为单位创建区域网格
		clearAll(); //清除缓存
		var cityName = $("#cityId").find("option:selected").text().trim();
	});

}

// 重置查询表单的值
function resetQueryCondition() {
	$("#hiddenCurrentPage").val(1);
	$("#hiddenTotalPageCnt").val(0);
	$("#hiddenTotalCnt").val(0);
	$("#hiddenForcedStartIndex").val(-1);
	$("#hiddenPageSize").val(100);

}
/**
 * 查看小区动态覆盖图(曲线)
 */
function showDynaCoverage(cell, lng, lat) {

	if (!cell||!lng||!lat) {
		return;
	}
	var lnglatarr = [lng,lat];
	$("#interDetailTab tr:gt(0)").remove(); 
	//$("#interDetailTab tr").not("tr:first").remove();
	//清空界面数据
	//clearAll();
	//获取城市id
	var cityId = $("#cityId").val();
	//获取日期范围
	var sDate = $("#sDate").val();
	var eDate = $("#eDate").val();

	//获取图形大小系数
	var imgSizeCoeff = $("#imgSizeCoeff").val();
	//console.log(Number(imgSizeCoeff));
	var valiNumber = /^[+]?[0-9]+(\.[0-9]+)?$/;   //验证数字
	if(!valiNumber.test(Number(imgSizeCoeff))) {
		alert("折线图形大小系数请输入数字且值大于0.001小于1000！");
		return;
	}
	if(Number(imgSizeCoeff) <= 0.001) {
		alert("折线图形大小系数请输入数字且值大于0.001小于10000！");
		return;
	}
	if(Number(imgSizeCoeff) >= 10000) {
		alert("折线图形大小系数请输入数字且值大于0.001小于10000！");
		return;
	}
	var celllng,celllat;
	if(lng) {
		celllng = lng;
	}
	if(lat) {
		celllat = lat;
	}
	
	showOperTips("loadingDataDiv", "loadContentId", "正在生成动态覆盖图");

	$.ajax({
		url : 'get4GDynaCoverageDataForAction',
		data : {
			'cityId' : cityId,
			'lteCellId' : cell,
			'startDate' : sDate,
			'endDate' : eDate,
			'imgSizeCoeff':imgSizeCoeff
			//'imgCoeff' : imgCoeff
		},
		dataType : 'json',
		type : 'post',
		success : function(data) {
			
			if(data != null) {
				
				var curvePoints_12 = data['curvePoints_12'];
				var curvePoints_3 = data['curvePoints_3'];
				var resInterDetail = data['resInterDetail'];
				
				if(curvePoints_12 != null && curvePoints_3 != null) {
					var vectorPoints_12 = data['vectorPoint_12'];
					var vecLng_12 = vectorPoints_12[0]['lng'];
					var vecLat_12 = vectorPoints_12[0]['lat'];
					
					var vectorPoints_3 = data['vectorPoint_3'];
					var vecLng_3 = vectorPoints_3[0]['lng'];
					var vecLat_3 = vectorPoints_3[0]['lat'];
					
					//console.log(points);
					var pointArray_12 = new Array();
					/*for ( var i = 0; i < curvePoints_12.length; i++) {
						var lng = curvePoints_12[i]["lng"];
						var lat = curvePoints_12[i]["lat"];
						//pointArray_12.push(new BMap.Point(lng, lat));
                        //new ol.geom.Point(ol.Coordinate(lnglatarr),ol.geom.GeometryLayout);
                        // var point = new ol.geom.Point([lng, lat]).transform("EPSG:4326", "EPSG:4326");
						pointArray_12.push([lng, lat]);
					}*/
					for ( var key in curvePoints_12[0]) {
						var lng = curvePoints_12[0][key]["baiduLng"];
						var lat = curvePoints_12[0][key]["baiduLat"];
						//pointArray_12.push(new BMap.Point(lng, lat));
						//new ol.geom.Point(ol.Coordinate(lnglatarr),ol.geom.GeometryLayout);
						// var point = new ol.geom.Point([lng, lat]).transform("EPSG:4326", "EPSG:4326");
						pointArray_12.push([lng, lat]);
					}
					var pointArray_3 = new Array();
					/*for ( var i = 0; i < curvePoints_3.length; i++) {
						var lng = curvePoints_3[i]["lng"];
						var lat = curvePoints_3[i]["lat"];
						// pointArray_3.push(new BMap.Point(lng, lat));
						pointArray_3.push([lng, lat]);
					}*/
					for ( var key in curvePoints_3[0]) {
						var lng = curvePoints_3[0][key]["baiduLng"];
						var lat = curvePoints_3[0][key]["baiduLat"];
						// pointArray_3.push(new BMap.Point(lng, lat));
						pointArray_3.push([lng, lat]);
					}
					for ( var i = 0; i < resInterDetail.length; i++) {
						var cellId = resInterDetail[i]["CELL_ID"];
						var ncellId = resInterDetail[i]["NCELL_ID"];
						var cosi1 = resInterDetail[i]["VAL1"];
						var cosi2 = resInterDetail[i]["VAL2"];
						var rsr0 = resInterDetail[i]["RSRPTIMES0"];
						var rsr1 = resInterDetail[i]["RSRPTIMES1"];
						var dis = resInterDetail[i]["DISTANCE"];
						$("#interDetailTab").append(
								"<tr><td>" + ncellId + "</td><td>" + cellId
										+ "</td><td>" + toConverVal(cosi1)
										+ "</td><td>" + toConverVal(cosi2)
										+ "</td><td>" + rsr0 + "</td><td>"
										+ rsr1 + "</td><td>" + toConverVal(dis)
										+ "</td></tr>");
					}
					new TableSorter("interDetailTab"); //为table加上点击排序

					drawBezier(pointArray_12);
					drawBezier(pointArray_3);
			        //添加方向线
                    var dynaPolyline_12 =new ol.geom.LineString([[celllng,celllat],[vecLng_12,vecLat_12]]);
                    var lineFeature = new ol.Feature(dynaPolyline_12);
                    // Source
                    /*var vectorSource = new ol.source.Vector({
                        projection: 'EPSG:4326'
                    });*/
					var vectorSource = bezierOverlay.getSource();
						lineFeature.setStyle(new ol.style.Style({
                        fill: new ol.style.Fill({
                            color: dynaPolylineColor_12
                        }),
                        stroke: new ol.style.Stroke({
                            width: 2,
                            color: dynaPolylineColor_12
                        })
                    }));
                    // vectorSource.addFeatures([dynaPolygon_12Feature,dynaPolygon_3Feature,lineFeature]);
					vectorSource.addFeatures([lineFeature]);
                    // Vector layer
                    /*var vectorLayer = new ol.layer.Vector({
                        source: vectorSource,
                    });*/

                    // Add Vector layer to map
                    // map.addLayer(vectorLayer);
					//添加方向线箭头
					addArrow(dynaPolyline_12,20,Math.PI/7,"1");
//					addArrow(dynaPolyline_3,20,Math.PI/7,"2");

				} else {
					hideOperTips("loadingDataDiv");
					alert("该小区在搜索的时间段内没有数据！");
				}
			} else {
				hideOperTips("loadingDataDiv");
				alert("该小区在搜索的时间段内没有数据！");
			}
		},
		error : function(xhr, textstatus, e) {

		},
		complete : function() {
			hideOperTips("loadingDataDiv");
		}
	});
}

/**
 * 查看小区动态覆盖图(折线)
 */
function showDynaCoverage2( cell, lng, lat) {

    if (!cell||!lng||!lat) {
        return;
    }
    var lnglatarr = [lng,lat];
	$("#interDetailTab tr:gt(0)").remove(); 
	//清空界面数据
	// clearAll();
	//获取城市id
	var cityId = $("#cityId").val();
	//获取日期范围
	var sDate = $("#sDate").val();
	var eDate = $("#eDate").val();
	
	//获取图形大小系数
	var imgCoeff = $("#imgCoeff").val();
	//console.log(Number(imgCoeff));
	var valiNumber = /^[+]?[0-9]+(\.[0-9]+)?$/;   //验证数字
	if(!valiNumber.test(Number(imgCoeff))) {
		alert("折线图系数请输入数字且值大于0小于0.5！");
		return;
	}
	if(Number(imgCoeff) <= 0) {
		alert("折线图系数请输入数字且值大于0小于0.5！");
		return;
	}
	if(Number(imgCoeff) >= 0.5) {
		alert("折线图系数请输入数字且值大于0小于0.5！");
		return;
	}
	
	//获取图形大小系数
	var imgSizeCoeff = $("#imgSizeCoeff").val();
	//console.log(Number(imgSizeCoeff));
	var valiNumber = /^[+]?[0-9]+(\.[0-9]+)?$/;   //验证数字
	if(!valiNumber.test(Number(imgSizeCoeff))) {
		alert("折线图形大小系数请输入数字且值大于0.001小于10000！");
		return;
	}
	if(Number(imgSizeCoeff) <= 0.001) {
		alert("折线图形大小系数请输入数字且值大于0.001小于10000！");
		return;
	}
	if(Number(imgSizeCoeff) >= 10000) {
		alert("折线图形大小系数请输入数字且值大于0.001小于10000！");
		return;
	}
	var celllng,celllat;
	if(lng) {
		celllng = lng;
	}
	if(lat) {
		celllat = lat;
	}
	
	showOperTips("loadingDataDiv", "loadContentId", "正在生成动态覆盖图");

	$.ajax({
		url : 'get4GDynaCoverageData2ForAction',
		data : {
			'cityId' : cityId,
			'lteCellId' : cell,
			'startDate' : sDate,
			'endDate' : eDate,
			'imgCoeff' : imgCoeff,
			'imgSizeCoeff':imgSizeCoeff
		},
		dataType : 'json',
		type : 'post',
		success : function(data) {
			
			if(data != null) {
				
				var curvePoints_12 = data['curvePoints_12'];
				var curvePoints_12_ = data['curvePoints_12_'];
				var curvePoints_3 = data['curvePoints_3'];
				var resInterDetail = data['resInterDetail'];

				if(curvePoints_12 != null/* && curvePoints_3 != null*/) {
					var vectorPoints_12 = data['vectorPoint_12'];
					var vecLng_12 = vectorPoints_12[0]['lng'];
					var vecLat_12 = vectorPoints_12[0]['lat'];
					
					/*var vectorPoints_3 = data['vectorPoint_3'];
					var vecLng_3 = vectorPoints_3[0]['lng'];
					var vecLat_3 = vectorPoints_3[0]['lat'];*/
					
					//console.log(points);
					var pointArray_12 = new Array();
					var pointArray_12_ = new Array();
					/*for ( var i = 0; i < curvePoints_12.length; i++) {
						var lng = curvePoints_12[i]["lng"];
						var lat = curvePoints_12[i]["lat"];
						// pointArray_12.push(new BMap.Point(lng, lat));
						pointArray_12.push([lng, lat]);
					}*/
					for ( var key in curvePoints_12[0]) {
						var lng = curvePoints_12[0][key]["baiduLng"];
						var lat = curvePoints_12[0][key]["baiduLat"];
						//pointArray_12.push(new BMap.Point(lng, lat));
						//new ol.geom.Point(ol.Coordinate(lnglatarr),ol.geom.GeometryLayout);
						// var point = new ol.geom.Point([lng, lat]).transform("EPSG:4326", "EPSG:4326");
						pointArray_12.push([lng, lat]);
					}
					/*for ( var key in curvePoints_12_[0]) {
						var lng = curvePoints_12_[0][key]["baiduLng"];
						var lat = curvePoints_12_[0][key]["baiduLat"];
						//pointArray_12.push(new BMap.Point(lng, lat));
						//new ol.geom.Point(ol.Coordinate(lnglatarr),ol.geom.GeometryLayout);
						// var point = new ol.geom.Point([lng, lat]).transform("EPSG:4326", "EPSG:4326");
						pointArray_12_.push([lng, lat]);
					}*/
					var pointArray_3 = new Array();
					/*for ( var i = 0; i < curvePoints_3.length; i++) {
						var lng = curvePoints_3[i]["lng"];
						var lat = curvePoints_3[i]["lat"];
						// pointArray_3.push(new BMap.Point(lng, lat));
						pointArray_3.push([lng, lat]);
					}*/
					/*for ( var key in curvePoints_3[0]) {
						var lng = curvePoints_3[0][key]["baiduLng"];
						var lat = curvePoints_3[0][key]["baiduLat"];
						// pointArray_3.push(new BMap.Point(lng, lat));
						pointArray_3.push([lng, lat]);
					}*/
					for ( var i = 0; i < resInterDetail.length; i++) {
						var cellId = resInterDetail[i]["CELL_ID"];
						var ncellId = resInterDetail[i]["NCELL_ID"];
						var cosi1 = resInterDetail[i]["VAL1"];
						var cosi2 = resInterDetail[i]["VAL2"];
						var rsr0 = resInterDetail[i]["RSRPTIMES0"];
						var rsr1 = resInterDetail[i]["RSRPTIMES2"];
						var dis = resInterDetail[i]["DISTANCE"];
						$("#interDetailTab").append(
								"<tr><td>" + ncellId + "</td><td>" + cellId
										+ "</td><td>" + toConverVal(cosi1)
										+ "</td><td>" + toConverVal(cosi2)
										+ "</td><td>" + rsr0 + "</td><td>"
										+ rsr1 + "</td><td>" + toConverVal(dis)
										+ "</td></tr>");
					}
					new TableSorter("interDetailTab"); //为table加上点击排序
					//暂不填充颜色
					//添加动态覆盖图

					drawBezier(pointArray_12);
					//drawBezier(pointArray_3);
					/*var ran;
					for (var i = 0;i < pointArray_12.length;i++){
						ran = Math.random()/100;
						drawArrow(celllng,celllat,pointArray_12[i][0],pointArray_12[i][1],ran,"red");
					}
					for (var i = 0;i < pointArray_12_.length;i++){
						ran = Math.random()/100;
						drawArrow(celllng,celllat,pointArray_12_[i][0],pointArray_12_[i][1],ran,"blue");
					}*/

			        //添加方向线
					drawArrow(celllng,celllat,vecLng_12,vecLat_12,0.002,dynaPolylineColor_12);
//					addArrow(dynaPolyline_3,20,Math.PI/7,"2");
				} else {
					hideOperTips("loadingDataDiv");
					animateInAndOut("operInfo", 1000, 1000, 1000, "operTip", "该小区在搜索的时间段内没有数据！");
					alert("该小区在搜索的时间段内没有数据！");
				}
			} else {
				hideOperTips("loadingDataDiv");
				animateInAndOut("operInfo", 1000, 1000, 1000, "operTip", "该小区在搜索的时间段内没有数据！");
				alert("该小区在搜索的时间段内没有数据！");
			}
		},
		error : function(xhr, textstatus, e) {

		},
		complete : function() {
			hideOperTips("loadingDataDiv");
		}
	});
}


/**
 * 清除全部覆盖数据数据
 */
function clearAll() {
	//清除动态覆盖图，方向线，箭头
	highlightOverlay.getSource().clear();
	lineOverlay.getSource().clear();
	bezierOverlay.getSource().clear();
	queryCellOverlay.getSource().clear();
	var element = popup.getElement();
	$(element).popover('destroy');
}

//初始化form下的page信息
function initFormPage(formId){
	var form=$("#"+formId);
	if(!form){
		return;
	}
	form.find("#hiddenPageSize").val(25);
	form.find("#hiddenCurrentPage").val(1);
	form.find("#hiddenTotalPageCnt").val(-1);
	form.find("#hiddenTotalCnt").val(-1);
}

/**
 * 画出百度地图线的箭头
 * @param polyline  需要画箭头的线
 * @param length  长度
 * @param angleValue  箭头角度
 */
function addArrow(polyline,length,angleValue,type,color){ //绘制箭头的函数
    var linePoint=polyline.getCoordinates();//线的坐标串
    var arrowCount=linePoint.length;
    for(var i =1;i<arrowCount;i++){ //在拐点处绘制箭头
        var linepoint1 = linePoint[i-1];
        var linepoint2 = linePoint[i];

        var pixelStart = map.getPixelFromCoordinate(linepoint1);
        var pixelEnd=map.getPixelFromCoordinate(linepoint2);
        var angle=angleValue;//箭头和主线的夹角
        var r=length; // r/Math.sin(angle)代表箭头长度
        var delta=0; //主线斜率，垂直时无斜率
        var param=0; //代码简洁考虑
        var pixelTemX,pixelTemY;//临时点坐标
        var pixelX,pixelY,pixelX1,pixelY1;//箭头两个点
        if(pixelEnd[0]-pixelStart[0]==0){ //斜率不存在是时
            pixelTemX=pixelEnd[0];
            if(pixelEnd[1]>pixelStart[1]){
                pixelTemY=pixelEnd[1]-r;
            } else {
                pixelTemY=pixelEnd[1]+r;
            }
            //已知直角三角形两个点坐标及其中一个角，求另外一个点坐标算法
            pixelX=pixelTemX-r*Math.tan(angle);
            pixelX1=pixelTemX+r*Math.tan(angle);
            pixelY=pixelY1=pixelTemY;
        }
        //斜率存在时
        else {
            delta=(pixelEnd[1]-pixelStart[1])/(pixelEnd[0]-pixelStart[0]);
            param=Math.sqrt(delta*delta+1);

            //第二、三象限
            if((pixelEnd[0]-pixelStart[0])<0) {
                pixelTemX=pixelEnd[0]+ r/param;
                pixelTemY=pixelEnd[1]+delta*r/param;
            }
            //第一、四象限
            else {
                pixelTemX=pixelEnd[0]- r/param;
                pixelTemY=pixelEnd[1]-delta*r/param;
            }
            //已知直角三角形两个点坐标及其中一个角，求另外一个点坐标算法
            pixelX=pixelTemX+ Math.tan(angle)*r*delta/param;
            pixelY=pixelTemY-Math.tan(angle)*r/param;

            pixelX1=pixelTemX- Math.tan(angle)*r*delta/param;
            pixelY1=pixelTemY+Math.tan(angle)*r/param;
        }
        var pointArrow = map.getCoordinateFromPixel([pixelX,pixelY])
        var pointArrow1 = map.getCoordinateFromPixel([pixelX1,pixelY1])
        if(type=="1") {
            var line =new ol.geom.LineString(ol.proj.transform([pointArrow,linePoint[i],pointArrow1], 'EPSG:4326', 'EPSG:4326'));
            var lineFeature = new ol.Feature(line);
			var vectorSource = bezierOverlay.getSource();
            lineFeature.setStyle(new ol.style.Style({
                fill: new ol.style.Fill({
                    color: color
                }),
                stroke: new ol.style.Stroke({
                    width: 2,
                    color: color
                })
            }));
            vectorSource.addFeatures([lineFeature]);
        }
    }
}


/***************** 查找地图小区 start ********************/
// 按条件搜索小区
function searchCell() {
	//gisCellDisplayLib.clearOnlyExtraOverlay();//清除额外覆盖物
	showOperTips("loadingDataDiv", "loadContentId", "正在查找小区");

	// 获取输入的值
	var inputValue = $.trim($("#conditionValue").val());
	var conditionType = $("#conditionType").val();

	//console.log("conditionType="+conditionType+",inputValue="+inputValue);
	if ($.trim(inputValue) == "") {
		alert("请输入搜索条件");
		hideOperTips("loadingDataDiv");
		return;
	}

	// 将特别渲染的polygon恢复默认
	//clearSpecRendPolygons();
	var queryType = conditionType;
	var cellId = inputValue;
	var cellArr = cellId.split(",");
	var cellstr = "";
	var filter;
	for (var n = 0; n < cellArr.length; n++) {
		if (cellArr[n].trim() != "") {
			if (ifHasSpecChar(cellArr[n].trim())) {
				alert("查询内容不能包含特殊字符和中文标点符号!");
				hideOperTips("loadingDataDiv");
				return;
			}
			if (queryType == 'cell') {
				if (!isOnlyNumberAndComma(cellArr[n].trim())) {
					alert("小区ID只能输入数字和半角-,用半角逗号隔开!");
					hideOperTips("loadingDataDiv");
					return;
				}
			}
			cellstr += "'"+cellArr[n].trim() + "',";
		}
	}
	filter = queryType == 'cell' ? encodeURIComponent("cell_id in ("
		+ cellstr.substring(0, cellstr.length - 1) + ")") : encodeURIComponent("cell_name in ("
		+ cellstr.substring(0, cellstr.length - 1) + ")");
	// console.log(filter);

	var url = wfsUrl + "&CQL_FILTER=" + filter;
	// console.log("url======"+url);
	var parser = new ol.format.GeoJSON();
	$.ajax({
		url : url,
		dataType : 'jsonp',
		jsonpCallback : 'parseResponse'
	}).then(function(response) {
		var features = parser.readFeatures(response);
		if (features.length) {
			// 高亮 Features
			clearAll();
			queryCellOverlay.getSource().addFeatures(features);
			for (var i = 0; i < features.length; i++) {
				// 设置 feature 的样式
				features[i].setStyle(redstyle);
			}

			// 取第一个小区扇形的顶点为新的地图中心点
			var lon = features[0].getGeometry().getFlatCoordinates()[0];
			var lat = features[0].getGeometry().getFlatCoordinates()[1];

			// 应用内置的动画，实现平移动画
			var pan = ol.animation.pan({
				duration : 1000,
				source : map.getView().getCenter()
			});
			map.beforeRender(pan);
			map.getView().setCenter([ lon, lat ]);
		}else {
			animateInAndOut("operInfo", 1000, 1000, 1000, "operTip", "不存在该空间数据");
		}
	});
		hideOperTips("loadingDataDiv");

}

/**
 * 
 * @title I N干扰小区连线：主小区被所有小区检测的连线。
 * @param polygon
 * @param cell
 * @param lng
 * @param lat
 * @author chao.xj
 * @date 2015-6-12上午11:28:15
 * @company 怡创科技
 * @version 1.2
 */
function InInterCellLine( cell, lng, lat) {

    if (!cell||!lng||!lat) {
        return;
    }
    var lnglatarr = [lng,lat];
	$("#interDetailTab tr:gt(0)").remove(); 
	//清空界面数据
	//clearAll();

	//获取城市id
	var cityId = $("#cityId").val();
	//获取日期范围
	var sDate = $("#sDate").val();
	var eDate = $("#eDate").val();
	
	//获取图形大小系数
	var imgCoeff = $("#imgCoeff").val();
	//console.log(Number(imgCoeff));
	var valiNumber = /^[+]?[0-9]+(\.[0-9]+)?$/;   //验证数字
	
	var celllng,celllat;
	if(lng) {
		celllng = lng;
	}
	if(lat) {
		celllat = lat;
	}
	
	showOperTips("loadingDataDiv", "loadContentId", "正在获取IN干扰数据");

	$.ajax({
		url : 'get4GDynaCoverageInInferDataForAction',
		data : {
			'cityId' : cityId,
			'lteCellId' : cell,
			'startDate' : sDate,
			'endDate' : eDate
		},
		dataType : 'json',
		type : 'post',
		success : function(data) {
//			console.log("data========>>>>>>>>>>>"+data);
			var ncellarr = [];
			var linefeatures = [];
			if(data != null) {
				for ( var i = 0; i < data.length; i++) {
					var cellId=data[i]['CELL_ID'];
					var cellLat=data[i]['CELL_LAT'];
					var cellLon=data[i]['CELL_LON'];
					var ncellId=data[i]['NCELL_ID'];
					var ncellLat=data[i]['NCELL_LAT'];
					var ncellLon=data[i]['NCELL_LON'];
//					console.log(cellId+" "+cellLon+" "+cellLat+" "+ncellId+" "+ncellLon+" "+ncellLat);
					ncellarr.push(ncellId);
					//连线
					var feature = drawLineBetweenPoints(lng,lat,ncellLon,ncellLat,{'strokeColor':'red',"strokeWeight":1});
				}
					searchNcell(ncellarr);
				} else {
					hideOperTips("loadingDataDiv");
					alert("该小区在搜索的时间段内没有数据！");
				}
			
		},
		error : function(xhr, textstatus, e) {

		},
		complete : function() {
			hideOperTips("loadingDataDiv");
		}
	});
}
/**
 * 
 * @title OUT干扰小区连线：主小区检测到所有邻小区连线。
 * @param polygon
 * @param cell
 * @param lng
 * @param lat
 * @author chao.xj
 * @date 2015-6-12上午11:28:15
 * @company 怡创科技
 * @version 1.2
 */
function OutInterCellLine(cell, lng, lat) {

	if (!cell||!lng||!lat) {
		return;
	}
	var lnglatarr = [lng,lat];
	$("#interDetailTab tr:gt(0)").remove(); 
	//清空界面数据
	//clearAll();
	//获取城市id
	var cityId = $("#cityId").val();
	//获取日期范围
	var sDate = $("#sDate").val();
	var eDate = $("#eDate").val();
	
	
	var celllng,celllat;
	if(lng) {
		celllng = lng;
	}
	if(lat) {
		celllat = lat;
	}
	
	showOperTips("loadingDataDiv", "loadContentId", "正在获取OUT干扰数据");

	$.ajax({
		url : 'get4GDynaCoverageOutInferDataForAction',
		data : {
			'cityId' : cityId,
			'lteCellId' : cell,
			'startDate' : sDate,
			'endDate' : eDate
		},
		dataType : 'json',
		type : 'post',
		success : function(data) {
			var ncellarr = [];
			if(data != null) {
				for ( var i = 0; i < data.length; i++) {
					var cellId=data[i]['CELL_ID'];
					var cellLat=data[i]['CELL_LAT'];
					var cellLon=data[i]['CELL_LON'];
					var ncellId=data[i]['NCELL_ID'];
					var ncellLat=data[i]['NCELL_LAT'];
					var ncellLon=data[i]['NCELL_LON'];
//					console.log(cellId+" "+cellLon+" "+cellLat+" "+ncellId+" "+ncellLon+" "+ncellLat);
					ncellarr.push(cellId);
					//连线
					drawLineBetweenPoints(lng,lat,cellLon,cellLat,{'strokeColor':'red',"strokeWeight":1});
				}
				searchNcell(ncellarr);
				
			} else {
				hideOperTips("loadingDataDiv");
				alert("该小区在搜索的时间段内没有数据！");
			}
		},
		error : function(xhr, textstatus, e) {

		},
		complete : function() {
			hideOperTips("loadingDataDiv");
		}
	});
}
/**
 *  渲染邻小区
 * @param ncellIds
 */
function searchNcell(ncellIds) {
	var cellstr = "";
	var filter;
	for (var n = 0; n < ncellIds.length; n++) {
			cellstr += "'"+ncellIds[n].trim() + "',";
	}
	// console.log(filter);
	filter = encodeURIComponent("cell_id in ("
		+ cellstr.substring(0, cellstr.length - 1) + ")");
	var url = wfsUrl + "&CQL_FILTER=" + filter;
	var parser = new ol.format.GeoJSON();
	$.ajax({
		url : url,
		dataType : 'jsonp',
		jsonpCallback : 'parseResponse'
	}).then(function(response) {
		var features = parser.readFeatures(response);
		if (features.length) {
			// 高亮 Features
			queryCellOverlay.getSource().addFeatures(features);
			for (var i = 0; i < features.length; i++) {
				// 设置 feature 的样式
				features[i].setStyle(redstyle);
			}
			// 取第一个小区扇形的顶点为新的地图中心点
			var lon = features[0].getGeometry().getFlatCoordinates()[0];
			var lat = features[0].getGeometry().getFlatCoordinates()[1];
		}
	});
}
function getCellInfo(cellid) {
	var cellstr = "";
	var filter;
	filter = encodeURIComponent("cell_id in ('"
		+ cellid + "')");
	var url = wfsUrl + "&CQL_FILTER=" + filter;
	// console.log("url==="+url);
	var parser = new ol.format.GeoJSON();
	$.ajax({
		url : url,
		dataType : 'jsonp',
		jsonpCallback : 'parseResponse'
	}).then(function(response) {
		var features = parser.readFeatures(response);
		console.log("features==="+features);
		if (features.length) {

			// 取第一个小区扇形的顶点为新的地图中心点
			/*var lon = features[0].getGeometry().getFlatCoordinates()[0];
			var lat = features[0].getGeometry().getFlatCoordinates()[1];*/
			return features[0].getProperties();
			/*console.log(lon+"---"+lat);
			console.log(features[0].getProperties()['longitude']);*/
		}
	});
}
/**
 * 添加带有箭头方向的箭线
 */
function drawArrow(celllng,celllat,vecLng,vecLat,ratio,color){
	var difflng = vecLng-celllng;
	var difflat = vecLat-celllat;
	var r = Math.sqrt(difflng*difflng+difflat*difflat);
	var conV = difflng/r;
	var sinV = difflat/r;
	//console.log("celllng,celllat=="+celllng+","+celllat+"-------"+(Number(celllng)+Number(conV*0.001))+","+(Number(celllat)+Number(sinV*0.001)))
	// var dynaPolyline_12 =new ol.geom.LineString([[celllng,celllat],[vecLng_12,vecLat_12]]);
	var dynaPolyline_12 =new ol.geom.LineString([[celllng,celllat],[Number(celllng)+Number(conV*ratio),Number(celllat)+Number(sinV*ratio)]]);
	var lineFeature = new ol.Feature(dynaPolyline_12);
	var vectorSource = bezierOverlay.getSource();
	lineFeature.setStyle(new ol.style.Style({
		fill: new ol.style.Fill({
			color: color
		}),
		stroke: new ol.style.Stroke({
			width: 2,
			color: color
		})
	}));
	vectorSource.addFeatures([lineFeature]);
	//添加方向线箭头
	addArrow(dynaPolyline_12,20,Math.PI/7,"1",color);
}
/**
 * 只包括数字和逗号
 */
function isOnlyNumberAndComma(str) {
	var reg = /^[0-9,-]+$/;
	return reg.test(str);
}
function toConverVal(val) {
	var num = Number(val);
	if (num != 0) {
		return num.toFixed(4);
	} else {
		return num;
	}
}
(function($){   
	$.chromatable = {   
	defaults: {   
	width: "900px",   
	height: "300px",   
	scrolling: "yes"   
	}   
	}; 
$.fn.chromatable = function(options){   
	var options = $.extend({}, $.chromatable.defaults, options);   
	return this.each(function(){   
	var $this = $(this);   
	var $uniqueID = $(this).attr("ID") + ("wrapper");   
	$(this).css('width', options.width).addClass("_scrolling");   
	$(this).wrap('<div class="scrolling_outer"><div id="'+$uniqueID+'" class="scrolling_inner"></div></div>');   
	$(".scrolling_outer").css({'position':'relative'});   
	$("#"+$uniqueID).css(   
	{'border':'1px solid #CCCCCC',   
	'overflow-x':'hidden',   
	'overflow-y':'auto',   
	'padding-right':'17px'   
	});   
	$("#"+$uniqueID).css('height', options.height);   
	$("#"+$uniqueID).css('width', options.width);   
	$(this).before($(this).clone().attr("id", "").addClass("_thead").css(   
	{'width' : 'auto',   
	'display' : 'block',   
	'position':'absolute',   
	'border':'none',   
	'border-bottom':'1px solid #CCC',   
	'top':'1px'   
	}));   
	$('._thead').children('tbody').remove();   
	$(this).each(function( $this ){   
	if (options.width == "100%" || options.width == "auto") {   
	$("#"+$uniqueID).css({'padding-right':'0px'});   
	}   
	if (options.scrolling == "no") {   
	$("#"+$uniqueID).before('<a href="#" class="expander" style="width:100%;">Expand table</a>');   
	$("#"+$uniqueID).css({'padding-right':'0px'});   
	$(".expander").each(   
	function(int){   
	$(this).attr("ID", int);   
	$( this ).bind ("click",function(){   
	$("#"+$uniqueID).css({'height':'auto'});   
	$("#"+$uniqueID+" ._thead").remove();   
	$(this).remove();   
	});   
	});   
	$("#"+$uniqueID).resizable({ handles: 's' }).css("overflow-y", "hidden");   
	}   
	});   
	$curr = $this.prev();   
	$("thead:eq(0)>tr th",this).each( function (i) {   
	$("thead:eq(0)>tr th:eq("+i+")", $curr).width( $(this).width());   
	});   
	if (options.width == "100%" || "auto"){   
	$(window).resize(function(){   
	resizer($this);   
	});   
	}   
	});   
	};   
	function resizer($this) {   
	$curr = $this.prev();   
	$("thead:eq(0)>tr th", $this).each( function (i) {   
	$("thead:eq(0)>tr th:eq("+i+")", $curr).width( $(this).width());   
	});   
	};   
	})(jQuery);   


jQuery.fn.CloneTableHeader = function(tableId, tableParentDivId) {

    var obj = document.getElementById("tableHeaderDiv" + tableId);

    if (obj) {

        jQuery(obj).remove();

    }

    var browserName = navigator.appName;

    var ver = navigator.appVersion;

    var browserVersion = parseFloat(ver.substring(ver.indexOf("MSIE") + 5, ver.lastIndexOf("Windows")));

    var content = document.getElementById(tableParentDivId);

    var scrollWidth = content.offsetWidth - content.clientWidth;

    var tableOrg = jQuery("#" + tableId)

    var table = tableOrg.clone();

    table.attr("id", "cloneTable");

    var tableClone = jQuery(tableOrg).find("tr").each(function() {

    });

    var tableHeader = jQuery(tableOrg).find("thead");

    var tableHeaderHeight = tableHeader.height();

    tableHeader.hide();

    var colsWidths = jQuery(tableOrg).find("tbody tr:first td").map(function() {

        return jQuery(this).width();

    });

    var tableCloneCols = jQuery(table).find("thead tr:first td")

    if (colsWidths.size() > 0) {

        for (i = 0; i < tableCloneCols.size(); i++) {

            if (i == tableCloneCols.size() - 1) {

                if (browserVersion == 8.0)

                    tableCloneCols.eq(i).width(colsWidths[i] + scrollWidth);

                else

                    tableCloneCols.eq(i).width(colsWidths[i]);

            } else {

                tableCloneCols.eq(i).width(colsWidths[i]);

            }

        }

    }

    var headerDiv = document.createElement("div");

    headerDiv.appendChild(table[0]);

    jQuery(headerDiv).css("height", tableHeaderHeight);

    jQuery(headerDiv).css("overflow", "hidden");

    jQuery(headerDiv).css("z-index", "20");

    jQuery(headerDiv).css("width", "100%");

    jQuery(headerDiv).attr("id", "tableHeaderDiv" + tableId);

    jQuery(headerDiv).insertBefore(tableOrg.parent());

};
/**
 * 贝塞尔曲线
 * @param coords 经纬度二维数组
 */
function drawBezier(coords){
	// console.log("coords==="+coords);
	var line = {
		"type": "Feature",
		"properties": {
			"stroke": 'rgba(255, 0, 0, 0.2)'
		},
		"geometry": {
			"type": "LineString",
			"coordinates": coords
		}
	};
	coords.push(coords[0]);
	var curved = turf.bezier(line, 10000, 0.85);
	var polygon = turf.polygon([coords]);
	polygon.properties = {stroke: "rgba(255, 0, 0, 0.5)",fill:"rgba(255, 0, 0, 0.2)"};

	//curved.properties = {stroke: "#f00"};
	var geojsonObject = {
		"type": "FeatureCollection",
		"features": [line, curved,polygon]
	};
	var bezierSource = bezierOverlay.getSource();
	var features = (new ol.format.GeoJSON()).readFeatures(geojsonObject, {
		dataProjection: 'EPSG:4326',
		featureProjection: 'EPSG:4326'
	});
	var styles = {};
	var styleFunction = function (feature) {
		var featureColor = feature.get('stroke');
		var fillColor = feature.get('fill');
		styles[featureColor] = new ol.style.Style({
			stroke: new ol.style.Stroke({
				color: featureColor,
				width: 4
			}),
			fill : new ol.style.Fill({
				// 设置填充颜色与不透明度
				color : 'rgba(255, 0, 0, 0.2)'
			})
		})
		return styles[featureColor];
	};
	bezierOverlay.setStyle(styleFunction);
	bezierSource.addFeatures(features);
}