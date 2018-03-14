var center;
var baseLayerGroup;
var tiled;
var clickCellOverlay;
var queryCellOverlay;
var interCellOverlay;
var thisCellOverlay;
var overlaysGroup;
var layerSwitcher;
var popup;
var blackstyle;
var redstyle;
var greenstyle;
var map;
var zoom;
var allfeatures;
var allfeatureNum;
var thiscoordinate;
var contextmenu;
var contextmenu_items;
var wfsUrl = "http://rnodev-ci.iscreate.com:18080/geoserver/rnodev/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=rnodev:gis_lte_cell_layer&maxFeatures=50&outputFormat=text%2Fjavascript";

//var matrixObj;//干扰矩阵数据
var topNcellNum = 6; //最强邻区个数
var currentBandType;//当前选择的频段

$(document).ready(function() {

	
	//绑定事件
	bindEvent();

	// 允许拖动（JQuery UI 的功能）
	$('.draggable').draggable();

	// 禁止显示浏览器HTML页面缺省的右键菜单，不影响 Openlayers3 自定义的右键菜单
	$("#map").bind("contextmenu", function() {
		return false;
	});

	var lon = $("#queryCellAreaId").find("option:selected").attr("data-lon");
	var lat = $("#queryCellAreaId").find("option:selected").attr("data-lat");

	// center = ol.proj.transform([ parseFloat(lon), parseFloat(lat) ], 'EPSG:4326', 'EPSG:3857');// [113.33, 23.13]
	center = [ parseFloat(lon), parseFloat(lat) ];//[113.27147, 23.1481];//[ parseFloat(lon), parseFloat(lat) ];

	baseLayerGroup = new ol.layer.Group({
		'title' : '基础地图',
		layers : [
		// 添加一个使用离线瓦片地图的层
		new ol.layer.Tile({
			title : '本地地图',
			type : 'base',
			visible : true,
			zIndex : 1,
			source : new ol.source.XYZ({
				url : 'http://192.168.6.71/tiles/osm/{z}/{x}/{y}.png'
			})
		}), new ol.layer.Tile({
			title : '在线地图',
			type : 'base',
			visible : false,
			zIndex : 2,
			source : new ol.source.OSM()
		})]
	});
	
	clickCellOverlay = new ol.layer.Vector({
		zIndex : 5,
		source : new ol.source.Vector(),
		map : map
	});
	
	queryCellOverlay = new ol.layer.Vector({
		zIndex : 6,
		source : new ol.source.Vector(),
		map : map
	});
	
	interCellOverlay = new ol.layer.Vector({
		zIndex : 7,
		source : new ol.source.Vector(),
		map : map
	});
	
	thisCellOverlay = new ol.layer.Vector({
		zIndex : 7,
		source : new ol.source.Vector(),
		map : map
	});
	
	overlaysGroup = new ol.layer.Group({
		layers : [clickCellOverlay, queryCellOverlay, interCellOverlay, thisCellOverlay]
	});

	map = new ol.Map({
		view : new ol.View({
			projection : 'EPSG:4326',
			center : center,
			zoom : 16
		}),
		layers : [ baseLayerGroup , overlaysGroup],
		target : 'map'
	});
	
	layerSwitcher = new ol.control.LayerSwitcher({
		tipLabel : 'RNO-GIS' // Optional label for button
	});
	map.addControl(layerSwitcher);

	//tooltip,显示小区名
	var tooltip = document.getElementById('tooltip');
	var overlay = new ol.Overlay({
		element : tooltip,
		offset : [ 10, 0 ],
		positioning : 'bottom-left'
	});
	map.addOverlay(overlay);

	map.on('pointermove', function(e) {
		if (e.dragging)
			return;

		var pixel = e.pixel;

		var feature = map.forEachFeatureAtPixel(pixel, function(feature) {
			return feature;
		});

		map.getTargetElement().style.cursor = feature ? 'pointer' : '';

		tooltip.style.display = feature ? '' : 'none';

		if (feature) {
			overlay.setPosition(e.coordinate);
			tooltip.innerHTML = feature.get('cell_name');
		}
	});

	// Popup showing the position the user clicked
	popup = new ol.Overlay({
		element : document.getElementById('popup')
	});
	map.addOverlay(popup);

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
			color : '#008000',
			opacity :1.0
		})
	});
	
	clearstyle = new ol.style.Style({
		stroke : new ol.style.Stroke({
			// 设置线条颜色
			color : 'yellow',
			size : 5
		}),
	});
	
	map.on("moveend", function() {
		zoom = map.getView().getZoom();
		// console.log(zoom);
	});

	map.on('singleclick', function(evt) {

		if (zoom < 15) {
			return;
		}

		var element = popup.getElement();
		$(element).popover('destroy');

		var view = map.getView();
		var url = tiled.getSource().getGetFeatureInfoUrl(evt.coordinate, view.getResolution(), view.getProjection(), {
			'INFO_FORMAT' : 'text/javascript',
			'FEATURE_COUNT' : 50
		});

		if (url) {
			var parser = new ol.format.GeoJSON();
			$.ajax({
				url : url,
				dataType : 'jsonp',
				jsonpCallback : 'parseResponse'
			}).then(function(response) {
				allfeatures = parser.readFeatures(response);
				allfeatureNum = allfeatures.length;

				if (allfeatureNum) {
					// 高亮 Features
					clickCellOverlay.getSource().clear();
					clickCellOverlay.getSource().addFeatures(allfeatures);
					
					for (var i = 0; i < allfeatures.length; i++) {
						// 设置 feature 的样式
						allfeatures[i].setStyle(clearstyle);
					}

				} else {
					console.log('No result');
				}
			});
		}
	});

		
	 contextmenu_items = [
		{
            text: '显示in干扰',
            data: 1,
            callback: showInMatrix
        },
        {
            text: '显示out干扰',
            data: 2,
            callback: showOutMatrix
        },
        {
            text: 'PCI智能优化',
            data: 3,
            callback: showPci
        }
	 ];

    contextmenu = new ContextMenu({
		width : 120,
		items : contextmenu_items
	});
	map.addControl(contextmenu);
	
	 // 右键菜单打开之前，判断是否在 feature 上，如果不是则禁止右键菜单
    contextmenu.on('beforeopen', function (e) {
        var feature = map.forEachFeatureAtPixel(e.pixel, function (feature) {
            return feature;
        });

        if (feature && zoom > 14) {
            contextmenu.enable();
        } else {
            contextmenu.disable();
        }
    });

    // 打开右键菜单
    contextmenu.on('open', function (e) {
        var feature = map.forEachFeatureAtPixel(e.pixel, function (feature) {
            return feature;
        });
       // console.log(feature);
        if (feature) {
            contextmenu.clear();
            contextmenu.extend(contextmenu_items);
        }
    });
    
    tiled = new ol.layer.Tile({
		zIndex : 3,
		source : new ol.source.TileWMS({
			url : 'http://rnodev-ci.iscreate.com:18080/geoserver/rnodev/wms',
			params : {
				'FORMAT' : 'image/png',
				'VERSION' : '1.1.1',
				tiled : true,
				STYLES : '',
				LAYERS : 'rnodev:gis_lte_cell_layer',
			}
		}),
		visible : false,
		opacity : 0.5
	});
	map.addLayer(tiled);

});

function bindEvent() {
	
	// 获取最近十次干扰矩阵计算
	getLatelyLteMatrix();
	
	$(".draggable").draggable();
	// $(".resource_list_box").css("display", "none");
	$(".resource_list_icon").animate({
		right : '286px'
	}, 'fast');

	$(".switch").click(function() {
		$(this).hide();
		$(".switch_hidden").show();
		$(".resource_list_icon").animate({
			right : '0px'
		}, 'fast');
		$(".resource_list_box").hide("fast");
	});
	$(".switch_hidden").click(function() {
		$(this).hide();
		$(".switch").show();
		$(".resource_list_icon").animate({
			right : '286px'
		}, 'fast');
		$(".resource_list_box").show("fast");
	});

	// 区域联动事件
	$("#provinceId").change(function() {
		getSubAreas("provinceId", "cityId", "市");
	});
	$("#cityId").change(function() {
		getSubAreas("cityId", "queryCellAreaId", "区/县");
	});
	$("#queryCellAreaId").change(function() {
		var lon = $("#queryCellAreaId").find("option:selected").attr("data-lon");
		var lat = $("#queryCellAreaId").find("option:selected").attr("data-lat");
		var pan = ol.animation.pan({
			duration : 2000,
			source : /* @type {ol.Coordinate} */
			(map.getView().getCenter())
		});
		map.beforeRender(pan);
		map.getView().setCenter([ parseFloat(lon), parseFloat(lat) ]);
	});

	// 小区查询(从空间数据库取数据)
	$("#queryCell").click(
			function() {
				var queryType = $("#queryType").val();
				var cellId = $("#cellId").val();
				var cellArr = cellId.split(",");
				var cellstr = "";
				var filter;
				for (var n = 0; n < cellArr.length; n++) {
					if (cellArr[n].trim() != "") {
						if (ifHasSpecChar(cellArr[n].trim())) {
							alert("查询内容不能包含特殊字符和中文标点符号!");
							return;
						}
						if (queryType == 'cellId') {
							//console.log(cellArr[n].trim());
							if (!isOnlyNumberAndComma(cellArr[n].trim())) {
								alert("小区ID只能输入数字,用半角逗号隔开!");
								return;
							}
							cellstr += "'" + cellArr[n].trim() + "',";
						}else {
							cellstr += cellArr[n].trim() + ",";
						}
						
					}
				}

				filter = queryType == 'cellId' ? encodeURIComponent("cell_id in ("
						+ cellstr.substring(0, cellstr.length - 1) + ")") : encodeURIComponent("cell_name like '%"
						+ cellstr.substring(0, cellstr.length - 1) + "%'");
			    //console.log(filter);

				var url = wfsUrl + "&CQL_FILTER=" + filter;
			    //console.log(url);
				var parser = new ol.format.GeoJSON();
				$.ajax({
					url : url,
					dataType : 'jsonp',
					jsonpCallback : 'parseResponse'
				}).then(function(response) {
					var features = parser.readFeatures(response);
					if (features.length) {
						// 高亮 Features
						queryCellOverlay.getSource().clear();
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
						map.getView().setCenter([ parseFloat(lon), parseFloat(lat) ]);
					}
				});
			});

	
	// 在地图上显示相应频段的小区
    $("#loadCellToMap").click(function () {
    	
    	var btn = $("#loadCellToMap");
    	btn.button('loading');
    	
    	var band_type;
    	var bt = $("#bandType").val();
    	if(bt=='all'){
    		band_type = "band_type in('D','E','F')"
    		currentBandType = "all";
    	}else{
    		band_type = "band_type='" + bt + "'";
    		currentBandType = bt;
    	}
    	
    	map.removeLayer(tiled);
    	tiled = new ol.layer.Tile({
    		zIndex : 3,
    		source : new ol.source.TileWMS({
    			url : 'http://rnodev-ci.iscreate.com:18080/geoserver/rnodev/wms',
    			params : {
    				'FORMAT' : 'image/png',
    				'VERSION' : '1.1.1',
    				tiled : true,
    				STYLES : '',
    				LAYERS : 'rnodev:gis_lte_cell_layer',
    				CQL_FILTER : band_type
    			}
    		}),
    		opacity : 0.5
    	});
    	map.addLayer(tiled);
    	setTimeout(function () { btn.button('reset'); },1000);
    });
    
    $("#pciMatrix").change(function(){
    	$("#matrixTip").text("当前使用（"+$("#pciMatrix").find("option:selected").text()+"）的干扰矩阵。");
    })
    
}

/**
 * 根据父控件的值，获取指定类型的子数据，并绑定到childDomId上
 * 
 * @param parentDomId
 * @param childDomId
 * @param childType
 */
function getSubAreas(parentDomId, childDomId, childType) {
	var parentId = $("#" + parentDomId).val();
	$.ajax({
		url : 'getSubAreaByParentAreaForAjaxAction',
		data : {
			'parentAreaId' : parentId,
			'subAreaLevel' : childType
		},
		dataType : 'json',
		type : 'post',
		async : false,
		success : function(data) {
			if (!data) {
				console.error("返回数据有错误");
				return;
			}
			try {
				areaData = data;
				var one;
				var htmlStr = "";
				for (var i = 0; i < data.length; i++) {
					one = data[i];
					if (one['AREA_ID']) {
						htmlStr += "<option value='" + one['AREA_ID'] + "' data-lon='" + one['LONGITUDE'] + "' data-lat='"
								+ one['LATITUDE'] + "'>" + $.trim(one['NAME']) + "</option>";
					}
				}
				$("#" + childDomId).empty();
				$("#" + childDomId).append(htmlStr);
			} catch (err) {
				console.log(err);
			}
		},
		complete : function() {
			$("#" + childDomId).trigger("change");
		}

	});
}

/**
 * 展现LTE小区详情
 */
function displayLteCellDetail(lteCell) {

	$(".switch_hidden").trigger("click");

	$("#showCellLabelId").html(getValidValue(lteCell.cell));
	$("#showCellNameId").html(getValidValue(lteCell.chineseName));
	$("#showCellPciId").html(getValidValue(lteCell.pci));

	$("#showCellLngId").html(getValidValue(lteCell.lng, "", 5));
	$("#showCellLatId").html(getValidValue(lteCell.lat, "", 5));

	$("#showCellGroundHeightId").html(getValidValue(lteCell.groundHeight));
	$("#showCellAntennaTypeId").html(getValidValue(lteCell.antennaType));
	$("#showCellIntegratedId").html(getValidValue(lteCell.integrated));
	$("#showCellAzimuthId").html(getValidValue(lteCell.azimuth, "", 2));
	$("#showCellRruverId").html(getValidValue(lteCell.rruver));
	$("#showCellRrunumId").html(getValidValue(lteCell.rrunum));
	$("#showCellRspowerId").html(getValidValue(lteCell.rspower));

	$("#showCellBandTypeId").html(getValidValue(lteCell.bandType));
	$("#showCellBandId").html(getValidValue(lteCell.band));
	$("#showCellEarfcnId").html(getValidValue(lteCell.earfcn));

	$("#showCellCoverTypeId").html(getValidValue(lteCell.coverType));
	$("#showCellCoverRangeId").html(getValidValue(lteCell.coverRange));

}

function getValidValue(v, defaultValue, precision) {
	if (v == null || v == undefined || v == "null" || v == "NULL" || v == "undefined" || v == "UNDEFINED") {
		if (defaultValue != null && defaultValue != undefined)
			return defaultValue;
		else
			return "";
	}

	if (typeof v == "number") {
		try {
			v = new Number(v).toFixed(precision);
		} catch (err) {
			// console.error("v=" + v + "," + err);
		}
	}
	return v;
}

/**
 * 测试是否包含有以下特殊字符 ~'!@#$%^&*()+_=:;?，。￥；？
 */
function ifHasSpecChar(str) {
	var pattern = new RegExp("[~'!@#$%^&*()+_=:;?，。￥；？]");
	return pattern.test(str);
}

/**
 * 只包括数字和逗号
 */
function isOnlyNumberAndComma(str) {
	//var reg = /^[0-9,]+$/;
	var reg = /^(\d{5,8}-\d{1,3})+$/;
	return reg.test(str);
}

/**
 * 获取最近十次干扰矩阵，显示在页面列表
 */
function getLatelyLteMatrix() {
	
	//$("#matrixTip").text("正在加载干扰矩阵列表...");
	//清除缓存干扰矩阵信息
	matrixObj = null;
	
	$("#pciMatrix").html("");
	areaid=$("#cityId").find("option:selected").val();
	
	$.ajax({
		url : 'getLatelyLteMatrixByCityIdForAjaxAction',
		data : {
			'cityId' : areaid
		},
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
				$("#pciMatrix").append(optHtml);
				$("#matrixTip").text("当前使用（"+$("#pciMatrix").find("option:selected").text()+"）的干扰矩阵。");
			} else {
				$("#matrixTip").text("无相应的干扰矩阵,请先进行干扰矩阵计算!");
			}
		}
	});
}

//显示in干扰列表
var showInMatrix = function showInMatrix(evt) {
	
	var element = popup.getElement();
	$(element).popover('destroy');
	
	var view = map.getView();
	var url = tiled.getSource().getGetFeatureInfoUrl(evt.coordinate, view.getResolution(), view.getProjection(), {
		'INFO_FORMAT' : 'text/javascript',
		'FEATURE_COUNT' : 50
	});

	if (url) {
		var parser = new ol.format.GeoJSON();
		$.ajax({
			url : url,
			dataType : 'jsonp',
			jsonpCallback : 'parseResponse'
		}).then(function(response) {
			allfeatures = parser.readFeatures(response);
			allfeatureNum = allfeatures.length;
			
			if (allfeatureNum) {
				
				//interCellOverlay.getSource().addFeatures(allfeatures);

				var content = '<table id="intable" class="table table-striped">';
				content += '<thead><th>小区ID</th><th>小区名称</th><th>PCI</th></thead>';
				content += '<tbody>';
				// 获取多个重叠 feature
				for (var i = 0; i < allfeatureNum; i++) {
					var feature = allfeatures[i];
					content += '<tr>';
					content += '<td>' + feature.get('cell_id') + '</td>';
					content += '<td>' + feature.get('cell_name') + '</td>';
					content += '<td>' + feature.get('pci') + '</td>';
					content += '</tr>';
				}
				content += '</tbody></table>';

				popup.setPosition(evt.coordinate);
				thiscoordinate = evt.coordinate;
				// the keys are quoted to prevent renaming in ADVANCED mode.
				$(element).popover({
					'placement' : 'auto',
					'animation' : false,
					'html' : true,
					'content' : content
				});
				$(element).popover('show');

				$('#intable tbody tr').click(function() {
					showOperTips("loadingDataDiv", "loadContentId", "正在加载数据");
					var matrixObj = null;
					
					var cellId = $(this).find('td:first').text();
					//console.log(cellId);
					
					$.ajax({
						url : "IN",
						data :{
							'cellId' : cellId,
							'jobId' : $("#pciMatrix").val()
						},
						type : 'post',
						dataType : 'json',
						success : function(data) {
						//console.log(data.length);
						//console.log("matrixObj="+matrixObj.length);
						if(data.length==0){
							hideOperTips("loadingDataDiv");
							$(element).popover('destroy');
							alert("没有找到相关数据!");
							return;
						}
						
						// 高亮 Features
						clickCellOverlay.getSource().clear();
						queryCellOverlay.getSource().clear();
						interCellOverlay.getSource().clear();
						thisCellOverlay.getSource().clear();
						
						var ncells = new Array();
						var ncellstr = "'" + cellId + "',";
						for ( var k = 0; k < data.length; k++) {
								ncells.push([data[k]['ncell'],data[k]['rela_val']]);
								ncellstr += "'" + data[k]['ncell'] + "',";
						}
						ncells.sort(function(a,b){return b[1]-a[1]});
						//console.log("ncellstr="+ncellstr);
						//当前小区重绘黑色，干扰小区重绘绿色
						paintInterCell(cellId,ncellstr.substring(0, ncellstr.length-1));
						
				    	$(element).popover('destroy');
						
				    	showTopInterList(cellId, ncells, "IN");
						$("#myTab a[href='#interDataLi']").tab('show');
						},
						error : function(err) {
							alert("获取干扰数据失败！");
							//console.log("fail");
						}
					});
					
				});
			} else {
				console.log('No result');
			}
		});
	}
}

//显示out干扰列表
var showOutMatrix = function showOutMatrix(evt) {
	
	var element = popup.getElement();
	$(element).popover('destroy');
	
	var view = map.getView();
	var url = tiled.getSource().getGetFeatureInfoUrl(evt.coordinate, view.getResolution(), view.getProjection(), {
		'INFO_FORMAT' : 'text/javascript',
		'FEATURE_COUNT' : 50
	});

	if (url) {
		var parser = new ol.format.GeoJSON();
		$.ajax({
			url : url,
			dataType : 'jsonp',
			jsonpCallback : 'parseResponse'
		}).then(function(response) {
			allfeatures = parser.readFeatures(response);
			allfeatureNum = allfeatures.length;
			
			if (allfeatureNum) {
				
				//interCellOverlay.getSource().addFeatures(allfeatures);

				var content = '<table id="outtable" class="table table-striped">';
				content += '<thead><th>小区ID</th><th>小区名称</th><th>PCI</th></thead>';
				content += '<tbody>';
				// 获取多个重叠 feature
				for (var i = 0; i < allfeatureNum; i++) {
					var feature = allfeatures[i];
					content += '<tr>';
					content += '<td>' + feature.get('cell_id') + '</td>';
					content += '<td>' + feature.get('cell_name') + '</td>';
					content += '<td>' + feature.get('pci') + '</td>';
					content += '</tr>';
				}
				content += '</tbody></table>';

				popup.setPosition(evt.coordinate);
				thiscoordinate = evt.coordinate;
				// the keys are quoted to prevent renaming in ADVANCED mode.
				$(element).popover({
					'placement' : 'auto',
					'animation' : false,
					'html' : true,
					'content' : content
				});
				$(element).popover('show');

				$('#outtable tbody tr').click(function() {
					// row was clicked
					showOperTips("loadingDataDiv", "loadContentId", "正在加载数据");
                    var matrixObj = null;
					
					var cellId = $(this).find('td:first').text();
					//console.log(cellId);
					
						$.ajax({
							url : "OUT",
							data :{
								'ncellId' : cellId,
								'jobId' : $("#pciMatrix").val()
							},
							type : "post",
							dataType : "json",
							success : function(data) {
								if(data.length==0){
									hideOperTips("loadingDataDiv");
									$(element).popover('destroy');
									alert("没有找到相关数据!");
									return;
								}
								
								// 高亮 Features
								clickCellOverlay.getSource().clear();
								queryCellOverlay.getSource().clear();
								interCellOverlay.getSource().clear();
								thisCellOverlay.getSource().clear();
								
								var ncells = new Array();
								var ncellstr = cellId + ",";
								for ( var i = 0; i < data.length; i++) {
										ncells.push([data[i]['cell'],data[i]['rela_val']]);
										ncellstr += data[i]['cell'] + ",";
								}
								ncells.sort(function(a,b){return b[1]-a[1]});
								
								//当前小区和受干扰小区重绘
								paintInterCell(cellId,ncellstr.substring(0, ncellstr.length-1));
								
						    	$(element).popover('destroy');
								
						    	showTopInterList(cellId, ncells, "OUT");
								$("#myTab a[href='#interDataLi']").tab('show');
							},
							error : function(err) {
								alert("获取干扰数据失败！");
								//console.log("fail");
							}
						});
						
					
				});
			} else {
				console.log('No result');
			}
		});
	}
}

/**
 * 显示TOP干扰列表
 * @param celllabel 主小区ID
 * @param ncells 邻小区
 * @param type 干扰类型 IN OR OUT
 */
function showTopInterList(celllabel,ncells,type) {
	var topList = new Array();
	if(ncells.length<topNcellNum){
		for(var i=0;i<ncells.length;i++){
			topList.push(ncells[i]);
		}
	}else{
		for(var i=0;i<topNcellNum;i++){
			topList.push(ncells[i]);
		}
	}
	$("#interDataLi").empty();
	//console.log("问题小区列表长度="+problemCell.length);
	var html="";
	html+="<h5>【"+celllabel+"】的【"+type+"】干扰TOP列表</h5>";
	if(topList.length>0){
		html+="<table class='table table-hover' >";
		html+="<thead><th>小区ID</th><th>关联度</th></thead>";
		$.each(topList,function(i){
			var ncelllabel = topList[i][0];
			var interVal = Number(topList[i][1]).toFixed(10);
			//console.log(problemCell[i][0]+","+problemCell[i][1].chineseName);
			html+="<tr>";
			html+="<td>"+ncelllabel+"</td>";
			html+="<td>"+interVal+"</td>";
			html+="</tr>";
		});
		html+="</table>";
	}else{
		html="<h5>没有找到相关数据</h5>";
	}
	$("#interDataLi").append(html);
}

//显示PCI智能优化的结果列表
var showPci = function pciAnalysis(evt){
	
	
	var element = popup.getElement();
	$(element).popover('destroy');
	
	var view = map.getView();
	var url = tiled.getSource().getGetFeatureInfoUrl(evt.coordinate, view.getResolution(), view.getProjection(), {
		'INFO_FORMAT' : 'text/javascript',
		'FEATURE_COUNT' : 50
	});

	if (url) {
		var parser = new ol.format.GeoJSON();
		$.ajax({
			url : url,
			dataType : 'jsonp',
			jsonpCallback : 'parseResponse'
		}).then(function(response) {
			allfeatures = parser.readFeatures(response);
			allfeatureNum = allfeatures.length;
			
			if (allfeatureNum) {
				
				//thisCellOverlay.getSource().addFeatures(allfeatures);

				var content = '<table id="pcitable" class="table table-striped">';
				content += '<thead><th>小区ID</th><th>小区名称</th><th>PCI</th></thead>';
				content += '<tbody>';
				// 获取多个重叠 feature
				for (var i = 0; i < allfeatureNum; i++) {
					var feature = allfeatures[i];
					content += '<tr>';
					content += '<td>' + feature.get('cell_id') + '</td>';
					content += '<td>' + feature.get('cell_name') + '</td>';
					content += '<td>' + feature.get('pci') + '</td>';
					content += '</tr>';

					// 设置 feature 的样式
					//feature.setStyle(blackstyle);
				}
				content += '</tbody></table>';
				
				$("#interDataLi").empty();

				popup.setPosition(evt.coordinate);
				thiscoordinate = evt.coordinate;
				// the keys are quoted to prevent renaming in ADVANCED mode.
				$(element).popover({
					'placement' : 'auto',
					'animation' : false,
					'html' : true,
					'content' : content
				});
				$(element).popover('show');

				$('#pcitable tbody tr').click(function() {
					// row was clicked
					showOperTips("loadingDataDiv", "loadContentId", "正在加载数据");
                    var matrixObj = null;
					
                    var celllabel = $(this).find('td:first').text();
					//console.log(cellId);
					
						$.ajax({
							url : "PCI",
							data :{
								'cellId' : celllabel,
								'jobId' : $("#pciMatrix").val()
							},
							type : "post",
							dataType : "json",
							success : function(json) {
								if(json.length==0){
									hideOperTips("loadingDataDiv");
									$(element).popover('destroy');
									alert("没有找到相关数据!");
									return;
								}
								
								// 高亮 Features
								clickCellOverlay.getSource().clear();
								queryCellOverlay.getSource().clear();
								interCellOverlay.getSource().clear();
								thisCellOverlay.getSource().clear();
								
								for (var i = 0; i < allfeatureNum; i++) {
									var feature = allfeatures[i];
									if(feature.get('cell_id')==celllabel){
										//设置 feature 的样式
										feature.setStyle(blackstyle);
										thisCellOverlay.getSource().addFeature(feature);
									}
								}		
								
								//过滤同站小区PCI重复的情况
								var samePciArr = new Array();
								$("#pciAnalysisTable").empty();
								//alert("小区名："+celllabel+"，获取同站小区和pci，计算干扰值，并用窗口显示列表，提供修改");
								//showOperTips("loadingDataDiv", "loadContentId", "正在分析计算");
								$.ajax({
									url : 'getSameStationCellsByLteCellIdForAjaxAction',
									data : {
										'lteCell' : "'" + celllabel + "'"
									},
									dataType : 'json',
									type : 'post',
									success : function(data) {
										if(data[0]['CELL'] != celllabel) {
											alert("加载数据出错！");
											return;  
										}
										var cell = data[0]['CELL'];
										var pci = data[0]['PCI'];
										if(pci%3==0){
											samePciArr[0]=pci+1;
											samePciArr[0]=pci+2;
										}else if (pci%3==1) {
											samePciArr[0]=pci-1;
											samePciArr[0]=pci+1;
										}else {
											samePciArr[0]=pci-1;
											samePciArr[0]=pci-2;
										}
										$("#cellname").html(cell);
										
										var tableHtml = "<thead><th>优化方案</th><th>in干扰值</th><th>out干扰值</th><th>总干扰值</th><th>是否修改</th></thead><tbody>" +
												"<tr><td>原方案："+pci+"</td>";
										var totInVal = 0;
										var totOutVal = 0;
										var totVal = 0;
										var mod = 0;
										var relaVal = 0;
										for ( var j = 0; j < json.length; j++) {
											if(cell == json[j]['cell']) {
												//关联度*mod值
												mod = getModValByPci(pci,json[j]['ncell_pci']);
												relaVal = Number(json[j]['rela_val']);
												totInVal += relaVal*mod;
											}
											if(cell == json[j]['ncell']) {
												//关联度*mod值
												mod = getModValByPci(pci,json[j]['cell_pci']);
												relaVal = Number(json[j]['rela_val']);
												totOutVal += relaVal*mod;
											}
										}
										totInVal = Number(totInVal).toFixed(5);
										totOutVal = Number(totOutVal).toFixed(5);
										totVal = (Number(totInVal) + Number(totOutVal)).toFixed(5);
										tableHtml += "<td>"+totInVal+"</td><td>"+totOutVal+"</td><td>"+totVal+"</td>";
										tableHtml += "<td></td></tr>";

										
										for ( var i = 1; i < data.length; i++) {
											tableHtml += "<tr><td>"+pci+"<->"+data[i]['PCI']+"〖"+data[i]['CELL']+"〗</td>";
											var cellInRelaVal = 0;
											var cellOutRelaVal = 0;
											var mod = 0;
											var relaVal = 0;
											for ( var j = 0; j < json.length; j++) {
												if(cell == json[j]['cell']) {
													//关联度*mod值
													mod = getModValByPci(data[i]['PCI'],json[j]['ncell_pci']);
													relaVal = Number(json[j]['rela_val']);
													cellInRelaVal += relaVal*mod;
												}
												if(cell == json[j]['ncell']) {
													//关联度*mod值
													mod = getModValByPci(data[i]['PCI'],json[j]['cell_pci']);
													relaVal = Number(json[j]['rela_val']);
													cellOutRelaVal += relaVal*mod;
												}
											}
											cellInRelaVal = Number(cellInRelaVal).toFixed(5);
											cellOutRelaVal = Number(cellOutRelaVal).toFixed(5);
											var cellTotRelaVal = (Number(cellInRelaVal) + Number(cellOutRelaVal)).toFixed(5);
											tableHtml += "<td>"+cellInRelaVal+"</td><td>"+cellOutRelaVal+"</td><td>"+cellTotRelaVal+"</td>";
											tableHtml += "<td><a onclick='changeLteCellPci(\""+cell+"\",\""+data[i]['PCI']+"\",\""+data[i]['CELL']+"\",\""+pci+"\")'>修改</a></td>";
											tableHtml += "</tr></tbody>";
										}
										$("#pciAnalysisTable").append(tableHtml);
										//显示pci分析
										$("#modalBtn").trigger("click");
							 		},
									complete : function() {
										hideOperTips("loadingDataDiv");
									}
								});
							},
							error : function(err) {
								alert("获取干扰数据失败！");
								//console.log("fail");
							}
						});
						
					
				});
			} else {
				console.log('No result');
			}
		});
	}
	
}

/**
 * 更新cell1的pci为pci1，cell2的pci为pci2
 * @param cell
 * @param pci
 * @param cell2
 * @param pci2
 */
function changeLteCellPci(cell1,pci1,cell2,pci2) {
	//console.log("cell1="+cell1+",pci1="+pci1+"; cell2="+cell2+",pci2="+pci2);
	//showOperTips("loadingDataDiv", "loadContentId", "正在更新pci");
	$.ajax({
		url : 'changeLteCellPciForAjaxAction',
		data : {
			'cell1' : cell1,
			'pci1' : pci1,
			'cell2' : cell2,
			'pci2' : pci2
		},
		dataType : 'json',
		type : 'post',
		success : function(data) {
			var flag = data['flag'];
			if(flag) {
				alert("更新成功！");
				//显示pci分析
				$('#interference_dialogId').hide();
			} else {
				alert("更新失败！");
				
			}
 		},
		complete : function() {
			//hideOperTips("loadingDataDiv");
		}
	});

}

/**
 * 通过pci求mod值
 * @param cellPci
 * @param ncellPci
 * @returns {Number}
 */
function getModValByPci(cellPci,ncellPci) {
	var result = 0;
	cellPci = Number(cellPci);
	ncellPci = Number(ncellPci);
	if(cellPci%3==(ncellPci%3)) {
		result += 1;
	}
	if(cellPci%6==(ncellPci%6)) {
		result += 0.8;
	}
	if(cellPci%30==(ncellPci%30)) {
		result += 0.1;
	}
	return result;
}

/**
 * 获取干扰小区并绘制干扰小区
 * @param cells
 */
function paintInterCell(cellId,cells) {
	var filter = encodeURIComponent("cell_id in (" + cells + ")");
	//console.log(filter);

	var url = wfsUrl + "&CQL_FILTER=" + filter;
    //console.log(url);
	var parser = new ol.format.GeoJSON();
	$.ajax({
		url : url,
		dataType : 'jsonp',
		jsonpCallback : 'parseResponse'
	}).then(function(response) {
		var features = parser.readFeatures(response);
		//console.log(features.length);
		if (features.length) {
			for(var m = 0; m < features.length; m++) {
				var onefeature = features[m];
				if(onefeature.get('cell_id')==cellId){
					onefeature.setStyle(blackstyle);
					thisCellOverlay.getSource().addFeature(onefeature);
				}else{
					onefeature.setStyle(greenstyle);
					interCellOverlay.getSource().addFeature(onefeature);
				}
			}
		}
		hideOperTips("loadingDataDiv");
	});
	
}

function showOperTips(outerId, tipId, tips) {
	try {
		$("#" + outerId).css("display", "");
		$("#" + outerId).find("#" + tipId).html(tips);
	} catch (err) {
	}
}

function hideOperTips(outerId) {
	try {
		$("#" + outerId).css("display", "none");
	} catch (err) {
	}
}