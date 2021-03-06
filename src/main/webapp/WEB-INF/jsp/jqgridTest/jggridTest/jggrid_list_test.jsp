﻿<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html>
<html lang="en">
<head>
<base href="<%=basePath%>">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
<meta charset="utf-8" />
<title>${pd.SYSNAME}</title>
<meta name="description" content="" />
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, maximum-scale=1.0" />
<!-- bootstrap & fontawesome -->
<link rel="stylesheet" href="static/ace/css/bootstrap.css" />
<link rel="stylesheet" href="static/ace/css/font-awesome.css" />
<!-- page specific plugin styles -->

<link rel="stylesheet" href="static/ace/css/jquery-ui.css" />
<link rel="stylesheet" href="static/ace/css/ui.jqgrid.css" />
<!-- <link rel="stylesheet" href="static/ace/css/jqgrid3.6/ui.jqgrid.css" /> -->

<!-- text fonts -->
<link rel="stylesheet" href="static/ace/css/ace-fonts.css" />
<!-- ace styles -->
<link rel="stylesheet" href="static/ace/css/ace.css"
	class="ace-main-stylesheet" id="main-ace-style" />
<script src="static/ace/js/ace-extra.js"></script>
<!-- 下拉框 -->
<link rel="stylesheet" href="static/ace/css/chosen.css" />
<!-- 日期框 -->
<link rel="stylesheet" href="static/ace/css/datepicker.css" />

<!-- 最新版的Jqgrid Css，如果旧版本（Ace）某些方法不好用，尝试用此版本Css，替换旧版本Css -->
<!-- <link rel="stylesheet" type="text/css" media="screen" href="static/ace/css/jqgrid3.6/ui.jqgrid-bootstrap.css" /> -->
<!-- 标准页面统一样式 -->
<link rel="stylesheet" href="static/css/normal.css" />
</head>
<body class="no-skin">
	<div class="main-container" id="main-container">
		<div class="main-content">
			<div class="main-content-inner">
				<div class="page-content">
					<!-- /section:settings.box -->
					<div class="page-header">
						<span class="label label-xlg label-success arrowed-right">东部管道</span>
						<!-- arrowed-in-right -->
						<span
							class="label label-xlg label-yellow arrowed-in arrowed-right"
							id="subTitle" style="margin-left: 2px;">成本核算</span> <span
							style="border-left: 1px solid #e2e2e2; margin: 0px 10px;">&nbsp;</span>
						<button id="btnQuery" class="btn btn-white btn-info btn-sm"
							onclick="showQueryCondi($('#jqGrid'),null,true)">
							<i class="ace-icon fa fa-chevron-down bigger-120 blue"></i> <span>显示查询</span>
						</button>
					</div>
					<!-- /.page-header -->

					<div class="row">
						<div class="col-xs-12">
							<div class="widget-box" style="display: none;">
								<div class="widget-body">
									<div class="widget-main">
										<!-- <p class="alert alert-info">Nunc aliquam enim ut arcu.</p> -->
										<form class="form-inline">
											<span class="input-icon"> <input
												id="form-field-icon-1" type="text" placeholder="这里输入关键词">
												<i class="ace-icon fa fa-leaf blue"></i>
											</span>
											<!-- class="input-small" -->
											<input type="text" placeholder="Username" /> <span> <select
												class="chosen-select form-control" name="BELONG_AREA"
												id="belong_area" data-placeholder="请选择所属区域"
												style="vertical-align: top; height: 32px; width: 150px;">
													<option value="" selected>请选择国家</option>
													<option value="">USA</option>
													<c:forEach items="${areaList}" var="area">
														<option value="${area.BIANMA }"
															<c:if test="${pd.BELONG_AREA==area.BIANMA}">selected</c:if>>${area.NAME }</option>
													</c:forEach>
											</select>
											</span>
											<button type="button" class="btn btn-info btn-sm">
												<i class="ace-icon fa fa-search bigger-110"></i>
											</button>
										</form>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-xs-12">
							<table id="jqGrid"></table>
							<div id="jqGridPager"></div>
						</div>
					</div>
				</div>
			</div>
		</div>

		<a href="#" id="btn-scroll-up"
			class="btn-scroll-up btn btn-sm btn-inverse"> <i
			class="ace-icon fa fa-angle-double-up icon-only bigger-110"></i>
		</a>
	</div>


	<!-- basic scripts -->
	<!-- 页面底部js¨ -->
	<%@ include file="../../system/index/foot.jsp"%>

	<!-- 最新版的Jqgrid Js，如果旧版本（Ace）某些方法不好用，尝试用此版本Js，替换旧版本JS -->
	<script src="static/ace/js/jqgrid3.6/jquery.jqGrid.js" type="text/javascript"></script>
	<script src="static/ace/js/jqgrid3.6/grid.locale-cn.js" type="text/javascript"></script>
	<!-- Load pdfmake, jszip lib files -->
	<!-- <script type="text/javascript" language="javascript" src="//cdn.rawgit.com/bpampuch/pdfmake/0.1.20/build/pdfmake.min.js">	</script>
	<script type="text/javascript" language="javascript" src="//cdn.rawgit.com/bpampuch/pdfmake/0.1.20/build/vfs_fonts.js"></script> -->
	<script type="text/javascript" language="javascript" src="//cdnjs.cloudflare.com/ajax/libs/jszip/2.5.0/jszip.min.js"></script>
	
	<!-- 旧版本（Ace）Jqgrid Js -->
	<!-- <script src="static/ace/js/jqGrid/jquery.jqGrid.src.js"></script>
	<script src="static/ace/js/jqGrid/i18n/grid.locale-cn.js"></script> -->
	<!-- 删除时确认窗口 -->
	<script src="static/ace/js/bootbox.js"></script>
	<!-- ace scripts -->
	<script src="static/ace/js/ace/ace.js"></script>
	<!-- 下拉框 -->
	<script src="static/ace/js/chosen.jquery.js"></script>
	<!-- 日期框 -->
	<script src="static/ace/js/date-time/bootstrap-datepicker.js"></script>
	<!--提示框-->
	<script type="text/javascript" src="static/js/jquery.tips.js"></script>
	<!-- JqGrid统一样式统一操作 -->
	<script type="text/javascript" src="static/js/common/jqgrid_style.js"></script>
	<script type="text/javascript"
		src="static/js/common/cusElement_style.js"></script>
	<script type="text/javascript" src="static/js/util/toolkit.js"></script>
	<script src="static/ace/js/ace/ace.widget-box.js"></script>
	<script type="text/javascript"> 
	//var gridHeight=192;
	$(document).ready(function () {
		/* $.jgrid.defaults.width = 780;*/
		//$.jgrid.defaults.styleUI = 'Bootstrap'; 
		$(top.hangge());//关闭加载状态
		//dropDownStyle();
		
		//resize to fit page size
		$(window).on('resize.jqGrid', function () {
			$("#jqGrid").jqGrid( 'setGridWidth', $(".page-content").width());
			//console.log("ccc"+$("iframe").height());
			//$("#jqGrid").jqGrid( 'setGridHeight', $(window).height() - gridHeight);
			resizeGridHeight($("#jqGrid"),null,true);
	    })
		
		$("#jqGrid").jqGrid({
			<%-- url: '<%=basePath%>static/data/data.json', --%>
			url: '<%=basePath%>jqgridJia/getPageList.do',
			datatype: "json",
			 colModel: [
				/* {label: ' ',name:'myac',index:'', width:80, fixed:true, sortable:false, resize:false,
					formatter:'actions', 
					formatoptions:{ 
                        onSuccess: function(response) {
							if(response.responseJSON.code==0){
								return [true];
							}else{
								return [false, response.responseJSON.message];
							}                
                        },
                        onError :function(rowid, res, stat, err) {
                        	if(err!=null)
                        		console.log(err);
                        },
                        
                        afterSave:function(rowid, res){
                        	$("#jqGrid").trigger("reloadGrid");
                        },
						keys:true,
						delOptions:{
							recreateForm: true, 
							beforeShowForm:beforeDeleteCallback,
							afterSubmit: function(response, postData) {
								if(response.responseJSON.code==0){
									console.log("sss");
									return [true];
								}else{
									console.log("rsdf");
									return [false, response.responseJSON.message];
								}
                            },
						}
					}
				}, */
				{label: 'id',name:'ID',index:'',key: true, width:30, sorttype:"int", editable: false},
				{ label: 'Category Name', name: 'CATEGORYNAME', width: 75,editable: true,editoptions:{size:"20",maxlength:"30"} },
				{ label: 'Product Name', name: 'PRODUCTNAME', width: 90,editable: true,editoptions:{size:"20",maxlength:"30"} },
				{ label: 'Country', name: 'COUNTRY', width: 100,editable: true,edittype:"select",editoptions:{value:"FE:FedEx;IN:InTime;TN:TNT;AR:ARAMEX"}},
				{ label: 'Price', name: 'PRICE', width: 80, formatter: 'number',sorttype: 'number',summaryTpl: "sum: {0}", summaryType: "sum",editable: true,align:'right'},
				// sorttype is used only if the data is loaded locally or loadonce is set to true
				{ label: 'Quantity', name: 'QUANTITY', width: 80, sorttype: 'integer',editable: true,align:'right' ,edittype:'custom', editoptions:{custom_element: myelem, custom_value:myvalue} }                   
			],
			reloadAfterSubmit: true, 
			//caption: "jqGrid with inline editing",
			//autowidth:true,
			//shrinkToFit:true,
			viewrecords: true, // show the current page, data rang and total records on the toolbar
			rowNum: 10,
			rowList:[10,20,30,100000],
			//loadonce: true, // this is just for the demo
			//height: '100%', 
			loadonce:true,
			sortname: 'PRODUCTNAME',
			
			footerrow: true,
			userDataOnFooter: true, // the calculated sums and/or strings from server are put at footer row.
			grouping: true,
			groupingView: {
                groupField: ["CATEGORYNAME"],
                groupColumnShow: [true],
                groupText: ["<b>{0}</b>"],
                groupOrder: ["desc"],
                groupDataSorted : false,
                groupSummary: [true],
                groupCollapse: false,
                plusicon : 'fa fa-chevron-down bigger-110',
				minusicon : 'fa fa-chevron-up bigger-110'
            },
            
            /* groupingView : { 
				 groupField : ['name'],
				 groupDataSorted : true,
				 plusicon : 'fa fa-chevron-down bigger-110',
				 minusicon : 'fa fa-chevron-up bigger-110'
			}, */
			
			pager: "#jqGridPager",
			loadComplete : function() {
				var table = this;
				setTimeout(function(){
					styleCheckbox(table);
					updateActionIcons(table);
					updatePagerIcons(table);
					enableTooltips(table);
				}, 0);
			},
			
			altRows: true,
			//toppager: true,
			rownumbers: true, // show row numbers
            rownumWidth: 35, // the width of the row numbers columns
			
			multiselect: true,
			//multikey: "ctrlKey",
	        multiboxonly: true,
	        editurl: "<%=basePath%>jqgridJia/edit.do",//nothing is saved
	        
	      //subgrid options
			subGrid : true,
			//subGridModel: [{ name : ['No','Item Name','Qty'], width : [55,200,80] }],
			//datatype: "xml",
			subGridOptions : {
				plusicon : "ace-icon fa fa-plus center bigger-110 blue",
				minusicon  : "ace-icon fa fa-minus center bigger-110 blue",
				openicon : "ace-icon fa fa-chevron-right center orange"
			},
			//for this example we are using local data
			subGridRowExpanded: showChildGrid,
		});
		
		$(window).triggerHandler('resize.jqGrid');//trigger window resize to make the grid get the correct size
	
		//navButtons
		jQuery("#jqGrid").jqGrid('navGrid',"#jqGridPager",
			{ 	//navbar options
				edit: false,
				editicon : 'ace-icon fa fa-pencil blue',
				add: true,
				addicon : 'ace-icon fa fa-plus-circle purple',
				del: true,
				delicon : 'ace-icon fa fa-trash-o red',
				search: true,
				searchicon : 'ace-icon fa fa-search orange',
				refresh: true,
				refreshicon : 'ace-icon fa fa-refresh green',
				view: false,
				viewicon : 'ace-icon fa fa-search-plus grey',
			},
			{
				//edit record form
				//closeAfterEdit: true,
				//width: 700,
				recreateForm: true,
				beforeShowForm :beforeEditOrAddCallback
			},
			{
				//new record form
				//width: 700,
				closeAfterAdd: true,
				recreateForm: true,
				viewPagerButtons: false,
				//reloadAfterSubmit: true,
				beforeShowForm : beforeEditOrAddCallback,
			    onclickSubmit: function(params, posdata) {
					console.log("onclickSubmit");
                    //console.log(posdata	);
                } , 
                afterSubmit: fn_addSubmit
			},
			{
				//delete record form
				recreateForm: true,
				beforeShowForm : beforeDeleteCallback,
				onClick : function(e) {
					
				}
			},
			{
				//search form
				recreateForm: true,
				afterShowSearch: beforeSearchCallback,
				afterRedraw: function(){
					style_search_filters($(this));
				},
				multipleSearch: true,
				//multipleGroup:true,
				showQuery: true
			}
		);
	
		
		// 批量编辑
        $('#jqGrid').navButtonAdd('#jqGridPager',
        {
            buttonicon: "ace-icon fa fa-pencil-square-o purple",
            title: "导出Excel",
            caption: "",
            position: "last",
            onClickButton: exportExcel
        });
		
     	// 批量编辑
        $('#jqGrid').navButtonAdd('#jqGridPager',
        {
            buttonicon: "ace-icon fa fa-undo",
            title: "导出PDF",
            caption: "",
            position: "last",
            onClickButton: exportPdf
        });

       //批量保存
       $('#jqGrid').navButtonAdd('#jqGridPager',
       {
    	   /* bigger-150 */
           buttonicon: "ace-icon fa fa-save green",
           title: "分组导出",
           caption: "",
           position: "last",
           onClickButton: exportGroup
       });
	});
	
	function exportGroup(){
		$('#jqGrid').jqGrid('exportToExcel');
		//$('#jqGrid').jqGrid('exportToCsv');
	}
	
	function exportPdf(){
		$('#jqGrid').jqGrid('exportToPdf');
	}
	
	function exportExcel(){
	 	$("#jqGrid").jqGrid("exportToExcel",{
			includeLabels : true,
			includeGroupHeader : true,
			includeFooter: true,
			fileName : "jqGridExport.xlsx",
			maxlength : 40 // maxlength for visible string data 
		});
	}


	//显示明细信息
	// the event handler on expanding parent row receives two parameters
    // the ID of the grid tow  and the primary key of the row
    function showChildGrid(parentRowID, parentRowKey) {
    	console.log(parentRowID+"  "+parentRowKey);
        var childGridID = parentRowID + "_table";
        var childGridPagerID = parentRowID + "_pager";
     // send the parent row primary key to the server so that we know which grid to show
        var childGridURL = '<%=basePath%>jqGridExtend/getDetailList.do?PARENTID='+parentRowKey+'';
        //childGridURL = childGridURL + "&parentRowID=" + encodeURIComponent(parentRowKey)

        // add a table and pager HTML elements to the parent grid row - we will render the child grid here
        $('#' + parentRowID).append('<table id=' + childGridID + '></table><div id=' + childGridPagerID + ' class=scroll></div>');
        $("#" + childGridID).jqGrid({
            url: childGridURL,
            mtype: "GET",
            datatype: "json",
            page: 1,
            colModel: [
			           {lable: 'ParentID', name: 'ParentID', width: 80},
			           {lable: 'ID', name: 'ID', width: 80},
			           {lable: 'Name', name: 'Name'},
			           {lable: 'Qty', name: 'Qty', width: 100},
			           {lable: 'SaleDate', name: 'SaleDate', width: 150,sorttype:'date',formatter: formateDate,unformat:unformateDate
			           }
            ],
            //width: '100%',
            height: '100%',
            //autowidth:true,
            //pager: "#" + childGridPagerID,
            loadComplete : function() {
				var table = this;
				setTimeout(function(){
					styleCheckbox(table);
					updateActionIcons(table);
					updatePagerIcons(table);
					enableTooltips(table);
				}, 0);
			},
			gridComplete:function(){

			    //$("#" + childGridID).parents(".ui-jqgrid-bdiv").css("overflow-x","hidden");
				$(".ui-jqgrid-btable").removeAttr("style");
			}
        });
	}

	
	//日期格式化
    function formateDate(value, row, index) {
        var formateNewDate=toolkit.dateFormat(new Date(value),"yyyy-MM-dd")
        return formateNewDate;
    }
    
  	//日期反格式化  
	function unformateDate(cellValue, options, rowObject){  
	    var updateDate = new Date(cellValue);  
	    return updateDate;  
	}
  	
	//创建一个input输入框
	function myelem (value, options) {
		var el = document.createElement("input");
		el.type="number";
		el.value = value;
		return el;
		/* $(el).ace_spinner({value:0,min:0,max:200,step:10, btn_up_class:'btn-info' , btn_down_class:'btn-info'});
			return el; */
	}
/* 	 */
	//获取值
	function myvalue(elem) {
		return $(elem).val();
	}
 	</script>
</body>
</html>