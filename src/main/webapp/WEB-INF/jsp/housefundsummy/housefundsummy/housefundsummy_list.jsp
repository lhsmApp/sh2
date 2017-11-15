<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html>
<html lang="en">
<head>
		<base href="<%=basePath%>">
		<!-- 下拉框 -->
		<link rel="stylesheet" href="static/ace/css/chosen.css" />
		<!-- jsp文件头和头部 ，其中包含旧版本（Ace）Jqgrid Css-->
		<%@ include file="../../system/index/topWithJqgrid.jsp"%>
		<!-- 日期框 -->
		<link rel="stylesheet" href="static/ace/css/datepicker.css" />
		
		<!-- 最新版的Jqgrid Css，如果旧版本（Ace）某些方法不好用，尝试用此版本Css，替换旧版本Css -->
		<!-- <link rel="stylesheet" type="text/css" media="screen" href="static/ace/css/ui.jqgrid-bootstrap.css" /> -->
		<script type="text/javascript" src="static/js/jquery-1.7.2.js"></script>
		<!-- 树形下拉框start -->
		<script type="text/javascript" src="plugins/selectZtree/selectTree.js"></script>
		<script type="text/javascript" src="plugins/selectZtree/framework.js"></script>
		<link rel="stylesheet" type="text/css"
			href="plugins/selectZtree/import_fh.css" />
		<script type="text/javascript" src="plugins/selectZtree/ztree/ztree.js"></script>
		<link type="text/css" rel="stylesheet"
			href="plugins/selectZtree/ztree/ztree.css"></link>
		<!-- 树形下拉框end -->
		<!-- 标准页面统一样式 -->
		<link rel="stylesheet" href="static/css/normal.css" />
	    <style>
			.page-header{
				padding-top: 9px;
				padding-bottom: 9px;
				margin: 0 0 8px;
			}
		</style>
	</head>
	<body class="no-skin">
		<div class="main-container" id="main-container">
			<div class="main-content">
				<div class="main-content-inner">
					<div class="page-content">
						<!-- /section:settings.box -->
						<div class="page-header">
									    <span class="label label-xlg label-success arrowed-right">人工成本</span>
										<!-- arrowed-in-right --> 
										<span class="label label-xlg label-yellow arrowed-in arrowed-right"
										    id="subTitle" style="margin-left: 2px;">公积金汇总</span> 
	                                    <span style="border-left: 1px solid #e2e2e2; margin: 0px 10px;">&nbsp;</span>
									
										<button id="btnQuery" class="btn btn-white btn-info btn-sm"
											onclick="showQueryCondi($('#jqGrid'),null,true)">
											<i class="ace-icon fa fa-chevron-down bigger-120 blue"></i> <span>隐藏查询</span>
										</button>
								
						            <div class="pull-right">
									    <span class="label label-xlg label-blue arrowed-left"
									        id = "showDur" style="background:#428bca; margin-right: 2px;"></span>
								    </div>
						</div><!-- /.page-header -->
				
							<div class="row">
							<div class="col-xs-12">
								<div class="widget-box" >
									<div class="widget-body">
										<div class="widget-main">
											<form class="form-inline">
											    <span class="pull-left" style="margin-right: 5px;">
												  <select class="chosen-select form-control"
													name="SelectedCustCol7" id="SelectedCustCol7"
													data-placeholder="请选择帐套"
													style="vertical-align: top; height:32px;width: 150px;">
													<option value="">请选择帐套</option>
													<c:forEach items="${FMISACC}" var="each">
														<option value="${each.DICT_CODE}">${each.NAME}</option>
													</c:forEach>
												  </select>
											    </span>
											<span class="pull-left" id="spanSelectTree" style="margin-right: 5px;" <c:if test="${pd.departTreeSource=='0'}">hidden</c:if>>
												<div class="selectTree" id="selectTree1" multiMode="true"
												    allSelectable="false" noGroup="false"></div>
											    <input type="text" id="SelectedDepartCode" hidden></input>
											</span>
											<span class="pull-left" style="margin-right: 5px;" hidden>
												<select class="chosen-select form-control"
													name="SelectedUserGrop" id="SelectedUserGrop"
													data-placeholder="请选择员工组"
													style="vertical-align: top; height:32px;width: 150px;">
													<option value="">请选择员工组</option>
													<c:forEach items="${EMPLGRP}" var="each">
														<option value="${each.DICT_CODE}">${each.NAME}</option>
													</c:forEach>
												</select>
											</span>
											<span class="pull-left" style="margin-right: 5px;" hidden>
												<select class="chosen-select form-control"
													name="SelectedUserCatg" id="SelectedUserCatg"
													data-placeholder="请选择企业特定员工分类"
													style="vertical-align: top; height:32px;width: 150px;">
													<option value="">请选择企业特定员工分类</option>
													<c:forEach items="${PARTUSERTYPE}" var="each">
														<option value="${each.DICT_CODE}">${each.NAME}</option>
													</c:forEach>
												</select>
											</span>
											<span class="pull-left" style="margin-right: 5px;" hidden>
												<select class="chosen-select form-control"
													name="SelectedOrgUnit" id="SelectedOrgUnit"
													data-placeholder="请选择组织单元文本"
													style="vertical-align: top; height:32px;width: 150px;">
													<option value="">请选择组织单元文本</option>
													<c:forEach items="${ORGUNIT}" var="each">
														<option value="${each.DICT_CODE}">${each.NAME}</option>
													</c:forEach>
												</select>
											</span>
											<!-- <span class="pull-left" style="margin-right: 5px;">
												<div class="selectTree" id="selectTree2" multiMode="true"
												    allSelectable="false" noGroup="false"></div>
											    <input type="text" id="SelectedUnitsCode" hidden></input>
											</span> -->
												<button type="button" class="btn btn-info btn-sm" onclick="btnSummyClick();">
													<i class="ace-icon fa fa-pencil-square-o bigger-120 blue"></i> <span>汇总</span>
												</button>
												<button type="button" class="btn btn-info btn-sm" onclick="tosearch();">
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
		
			<!-- 返回顶部 -->
			<a href="#" id="btn-scroll-up" class="btn-scroll-up btn btn-sm btn-inverse">
				<i class="ace-icon fa fa-angle-double-up icon-only bigger-110"></i>
			</a>
		</div>
	</body>
		
		
		<!-- basic scripts -->
		<!-- 页面底部js¨ -->
		<%@ include file="../../system/index/foot.jsp"%>
		
		<!-- 最新版的Jqgrid Js，如果旧版本（Ace）某些方法不好用，尝试用此版本Js，替换旧版本JS -->
		<!-- <script src="static/ace/js/jquery.jqGrid.min.js" type="text/javascript"></script>
		<script src="static/ace/js/grid.locale-cn.js" type="text/javascript"></script> -->
		
		<!-- 旧版本（Ace）Jqgrid Js -->
		<script src="static/ace/js/jqGrid/jquery.jqGrid.src.js"></script>
		<script src="static/ace/js/jqGrid/i18n/grid.locale-cn.js"></script>
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
		<!-- 上传控件 -->
		<script src="static/ace/js/ace/elements.fileinput.js"></script>
		
		<script type="text/javascript"> 
	    var gridBase_selector = "#jqGrid";  
	    var pagerBase_selector = "#jqGridPager";  
	    
		$(document).ready(function () {
			$(top.hangge());//关闭加载状态
		    
			//当前期间,取自tb_system_config的SystemDateTime字段
		    var SystemDateTime = '${SystemDateTime}';
			//当前登录人所在二级单位
		    var DepartName = '${DepartName}';
		    $("#showDur").text('当前期间：' + SystemDateTime + ' 登录人责任中心：' + DepartName); 
		    
			//前端数据表格界面字段,动态取自tb_tmpl_config_detail，根据当前单位编码及表名获取字段配置信息
		    var jqGridColModel = eval("(${jqGridColModel})");//此处记得用eval()行数将string转为array
		    //分组字段
		    var jqGridGroupField = ${jqGridGroupField};
		    //分组字段是否显示在表中
		    var jqGridGroupColumnShow = ${jqGridGroupColumnShow};
		    
			//resize to fit page size
			$(window).on('resize.jqGrid', function () {
				$(gridBase_selector).jqGrid( 'setGridWidth', $(".page-content").width());
				//$(gridBase_selector).jqGrid( 'setGridHeight', $(window).height() - 240);
				resizeGridHeight($(gridBase_selector),null,true);
		    });
			
			$(gridBase_selector).jqGrid({
				url: '<%=basePath%>housefundsummy/getPageList.do?'
		            +'SelectedCustCol7='+$("#SelectedCustCol7").val()
		            +'&SelectedDepartCode='+$("#SelectedDepartCode").val()
		            +'&SelectedUserGrop='+$("#SelectedUserGrop").val()
	                +'&SelectedUserCatg='+$("#SelectedUserCatg").val()
	                //+'&SelectedUnitsCode='+$("#SelectedUnitsCode").val()
	                +'&SelectedOrgUnit='+$("#SelectedOrgUnit").val(),
				datatype: "json",
				colModel: jqGridColModel,
				viewrecords: true, 
				shrinkToFit: false,
				rowNum: 0,
				//rowList: [100,200,500],
	            multiselect: true,
	            multiboxonly: true,
				altRows: true, //斑马条纹
				
				pager: pagerBase_selector,
				pgbuttons: false, // 分页按钮是否显示 
				pginput: false, // 是否允许输入分页页数 
				footerrow: true,
				userDataOnFooter: true,

	            sortable: true,
	            sortname: 'DEPT_CODE',
				sortorder: 'asc',

				grouping: true,
				groupingView: {
					groupField: [jqGridGroupField],
					groupColumnShow: [jqGridGroupColumnShow],
					groupText: ['<b>{0}</b>'],
					groupSummary: [true],
					groupSummaryPos: ['footer'], //header
					groupCollapse: false,
	                plusicon : 'fa fa-chevron-down bigger-110',
					minusicon : 'fa fa-chevron-up bigger-110'
				},
				
				subGrid: true,
				subGridOptions: {
					plusicon : "ace-icon fa fa-plus center bigger-110 blue",
					minusicon  : "ace-icon fa fa-minus center bigger-110 blue",
					openicon : "ace-icon fa fa-chevron-right center orange"
	            },
	            subGridRowExpanded: showChildGrid,

				scroll: 1,
                //scrollPopUp:true,
				//scrollLeftOffset: "83%",

				cellEdit: true,
				gridComplete:function(){
					$("[role='checkbox']").on('click',function(e){
						var target = $(this);
						var curRow=target.closest('tr');
						var curRowId=curRow.attr('id');
						var curRowData=$(gridBase_selector).getRowData(curRowId);
						var ids = $(gridBase_selector).jqGrid('getDataIDs');
						
						$(gridBase_selector).setSelection(curRowId,true);
	                    for (i = 0; i < ids.length; i++) { 
	                    	var item=$(gridBase_selector).getRowData(ids[i]);
	                    	if(item.DEPT_CODE==curRowData.DEPT_CODE
	                    			&& item.CUST_COL7==curRowData.CUST_COL7)
	                    		$(gridBase_selector).setSelection(ids[i],true);
	                    }
					});
			    },
	            
				loadComplete : function(data) {
					var table = this;
					setTimeout(function(){
						styleCheckbox(table);
						updateActionIcons(table);
						updatePagerIcons(table);
						enableTooltips(table);
					}, 0);
				},
			});
		    
			$(window).triggerHandler('resize.jqGrid');//trigger window resize to make the grid get the correct size

			$(gridBase_selector).navGrid(pagerBase_selector, 
					{
			            //navbar options
				        edit: false,
			            editicon : 'ace-icon fa fa-pencil blue',
			            add: false,
			            addicon : 'ace-icon fa fa-plus-circle purple',
			            del: false,
			            delicon : 'ace-icon fa fa-trash-o red',
			            search: true,
			            searchicon : 'ace-icon fa fa-search orange',
			            refresh: true,
			            refreshicon : 'ace-icon fa fa-refresh green',
			            view: false,
			            viewicon : 'ace-icon fa fa-search-plus grey',
		        }, {}, {}, {},
		        {
					//search form
					recreateForm: true,
					afterShowSearch: beforeSearchCallback,
					afterRedraw: function(){
						style_search_filters($(this));
					},
					multipleSearch: true,
					//multipleGroup:true,
					showQuery: false
		        },
		        {},{});
			
			$(gridBase_selector).navSeparatorAdd(pagerBase_selector, {
				sepclass : "ui-separator",
				sepcontent: ""
			});
	        //$(gridBase_selector).navButtonAdd(pagerBase_selector, {
			//	id : "batchEdit",
	        //    title: "汇总",
	        //    caption: "汇总",
	        //	buttonicon: "ace-icon fa fa-pencil-square-o purple",
	        //    position: "last",
	        //    onClickButton: summary,
	        //    cursor : "pointer"
	        //});
			$(gridBase_selector).navButtonAdd(pagerBase_selector, {
				id : "report",
	             caption : "上报",
	             buttonicon : "ace-icon fa fa-check-square-o green",
	             onClickButton : report,
	             position : "last",
	             title : "上报",
	             cursor : "pointer"
	         });

				//汇总
				function summary(e) {
			    	//获得选中的行ids的方法
			    	var ids = $(gridBase_selector).getGridParam("selarrrow");  
			    	
					if(!(ids!=null && ids.length>0)){
						bootbox.dialog({
							message: "<span class='bigger-110'>您没有选择任何内容!</span>",
							buttons: 			
							{ "button":{ "label":"确定", "className":"btn-sm btn-success"}}
						});
					}else{
		                var msg = '确定要汇总吗??';
		                bootbox.confirm(msg, function(result) {
		    				if(result) {
		    					var listData =new Array();
		    					//遍历访问这个集合  
		    					$(ids).each(function (index, id){  
		    			            var rowData = $(gridBase_selector).getRowData(id);
		    			            listData.push(rowData);
		    					});
		    					top.jzts();
		    					$.ajax({
		    						type: "POST",
		    						url: '<%=basePath%>housefundsummy/summaryDepartString.do?'
		    				            +'SelectedCustCol7='+$("#SelectedCustCol7").val()
		    				            +'&SelectedDepartCode='+$("#SelectedDepartCode").val()
		    				            +'&SelectedUserGrop='+$("#SelectedUserGrop").val()
		    			                +'&SelectedUserCatg='+$("#SelectedUserCatg").val()
		    			                //+'&SelectedUnitsCode='+$("#SelectedUnitsCode").val()
		    			                +'&SelectedOrgUnit='+$("#SelectedOrgUnit").val(),
			    				    data: {DataRowSummy:JSON.stringify(listData)},
		    						dataType:'json',
		    						cache: false,
		    						success: function(response){
		    							if(response.code==0){
		    								$(gridBase_selector).trigger("reloadGrid");  
		    								$(top.hangge());//关闭加载状态
		    								$("#subTitle").tips({
		    									side:3,
		    						            msg:'汇总成功',
		    						            bg:'#009933',
		    						            time:3
		    						        });
		    							}else{
		    								$(top.hangge());//关闭加载状态
		    								$("#subTitle").tips({
		    									side:3,
		    						            msg:'汇总失败,'+response.message,
		    						            bg:'#cc0033',
		    						            time:3
		    						        });
		    							}
		    						},
		    				    	error: function(response) {
		    							$(top.hangge());//关闭加载状态
	    								$("#subTitle").tips({
	    									side:3,
	    						            msg:'汇总出错:'+response.responseJSON.message,
	    						            bg:'#cc0033',
	    						            time:3
	    						        });
		    				    	}
		    					});
		    				}
		                });
					}
				}

			/**
			 * 上报
			 */
			function report(){
		    	//获得选中的行ids的方法
		    	var ids = $(gridBase_selector).getGridParam("selarrrow");  
		    	
				if(!(ids!=null && ids.length>0)){
					bootbox.dialog({
						message: "<span class='bigger-110'>您没有选择任何内容!</span>",
						buttons: 			
						{ "button":{ "label":"确定", "className":"btn-sm btn-success"}}
					});
				}else{
	                var msg = '确定要上报吗??';
	                bootbox.confirm(msg, function(result) {
	    				if(result) {
	    					var listData =new Array();
	    					
	    					//遍历访问这个集合  
	    					$(ids).each(function (index, id){  
	    			            var rowData = $(gridBase_selector).getRowData(id);
	    			            listData.push(rowData);
	    					});
							top.jzts();
							$.ajax({
								type: "POST",
								url: '<%=basePath%>housefundsummy/report.do?'
						            +'SelectedCustCol7='+$("#SelectedCustCol7").val()
						            +'&SelectedDepartCode='+$("#SelectedDepartCode").val()
						            +'&SelectedUserGrop='+$("#SelectedUserGrop").val()
					                +'&SelectedUserCatg='+$("#SelectedUserCatg").val()
					                //+'&SelectedUnitsCode='+$("#SelectedUnitsCode").val()
					                +'&SelectedOrgUnit='+$("#SelectedOrgUnit").val(),
	    				    	data: {DataRowsReport:JSON.stringify(listData)},
	    						dataType:'json',
	    						cache: false,
								success: function(response){
									if(response.code==0){
	    								$(gridBase_selector).trigger("reloadGrid");  
										$(top.hangge());//关闭加载状态
										$("#subTitle").tips({
											side:3,
								            msg:'上报成功',
								            bg:'#009933',
								            time:3
								        });
									}else{
										$(top.hangge());//关闭加载状态
										$("#subTitle").tips({
											side:3,
								            msg:'上报失败,'+response.message,
								            bg:'#cc0033',
								            time:3
								        });
									}
								},
						    	error: function(response) {
									$(top.hangge());//关闭加载状态
									$("#subTitle").tips({
										side:3,
							            msg:'上报出错:'+response.responseJSON.message,
							            bg:'#cc0033',
							            time:3
							        });
						    	}
							});
	    				}
	                });
				}
			}
		});

        // the event handler on expanding parent row receives two parameters
        // the ID of the grid tow  and the primary key of the row
        function showChildGrid(parentRowID, parentRowKey) {
        	console.log(parentRowID+"  "+parentRowKey);
			var rowData = $(gridBase_selector).getRowData(parentRowKey);
        	var BILL_CODE = rowData.BILL_CODE__;
        	console.log(BILL_CODE);
        	var DEPT_CODE = rowData.DEPT_CODE__;
        	console.log(DEPT_CODE);
        	
            var detailColModel = "[]";
			$.ajax({
				type: "GET",
				url: '<%=basePath%>housefundsummy/getDetailColModel.do?'
		            +'SelectedCustCol7='+$("#SelectedCustCol7").val()
		            +'&SelectedDepartCode='+$("#SelectedDepartCode").val()
		            +'&SelectedUserGrop='+$("#SelectedUserGrop").val()
	                +'&SelectedUserCatg='+$("#SelectedUserCatg").val()
	                //+'&SelectedUnitsCode='+$("#SelectedUnitsCode").val()
	                +'&SelectedOrgUnit='+$("#SelectedOrgUnit").val(),
		    	data: {DataDeptCode:DEPT_CODE},
				dataType:'json',
				cache: false,
				success: function(response){
					if(response.code==0){
						$(top.hangge());//关闭加载状态
						detailColModel = response.message;

			            detailColModel = eval(detailColModel);
			            var childGridID = parentRowID + "_table";
			            var childGridPagerID = parentRowID + "_pager";
			            // send the parent row primary key to the server so that we know which grid to show
			            var childGridURL = '<%=basePath%>housefundsummy/getDetailList.do?'
				            +'SelectedCustCol7='+$("#SelectedCustCol7").val()
				            +'&SelectedDepartCode='+$("#SelectedDepartCode").val()
				            +'&SelectedUserGrop='+$("#SelectedUserGrop").val()
			                +'&SelectedUserCatg='+$("#SelectedUserCatg").val()
			                //+'&SelectedUnitsCode='+$("#SelectedUnitsCode").val()
			                +'&SelectedOrgUnit='+$("#SelectedOrgUnit").val()
			                +'&DetailListBillCode='+BILL_CODE+'';
			            //childGridURL = childGridURL + "&parentRowID=" + encodeURIComponent(parentRowKey)

			            // add a table and pager HTML elements to the parent grid row - we will render the child grid here
			            $('#' + parentRowID).append('<table id=' + childGridID + '></table><div id=' + childGridPagerID + ' class=scroll></div>');

			            $("#" + childGridID).jqGrid({
			                url: childGridURL,
			                mtype: "GET",
			                datatype: "json",
			                colModel: detailColModel,
			                page: 1,
			                width: '100%',
			                //height: '100%',
			                rowNum: 0,	
			                pager: "#" + childGridPagerID,
							pgbuttons: false, // 分页按钮是否显示 
							pginput: false, // 是否允许输入分页页数 
			                viewrecords: true,
			                recordpos: "left", // 记录数显示位置 
			                
			    			loadComplete : function() {
			    				var table = this;
			    				setTimeout(function(){
			    					styleCheckbox(table);
			    					updateActionIcons(table);
			    					updatePagerIcons(table);
			    					enableTooltips(table);
			    				}, 0);
			    			},
			            });
					}else{
						$(top.hangge());//关闭加载状态
						$("#subTitle").tips({
							side:3,
				            msg:'获取结构失败：'+response.message,
				            bg:'#cc0033',
				            time:3
				        });
					}
				},
		    	error: function(response) {
					$(top.hangge());//关闭加载状态
					$("#subTitle").tips({
						side:3,
			            msg:'获取结构出错:'+response.responseJSON.message,
			            bg:'#cc0033',
			            time:3
			        });
		    	}
			});
        };
		
		//加载单位树
		function initComplete(){
			//下拉树
			var defaultNodes1 = {"treeNodes":${zTreeNodes1}};
			var defaultNodes2 = {"treeNodes":${zTreeNodes2}};
			//绑定change事件
			$("#selectTree1").bind("change",function(){
				$("#SelectedDepartCode").val("");
				if($(this).attr("relValue")){
					$("#SelectedDepartCode").val($(this).attr("relValue"));
					console.log($(this).attr("relValue"));
			    }
			});
			//$("#selectTree2").bind("change",function(){
			//	$("#SelectedUnitsCode").val("");
			//	if($(this).attr("relValue")){
			//		$("#SelectedUnitsCode").val($(this).attr("relValue"));
			//		console.log($(this).attr("relValue"));
			//    }
			//});
			//赋给data属性
			$("#selectTree1").data("data",defaultNodes1);  
			$("#selectTree1").render();
			//$("#selectTree2").data("data",defaultNodes2);  
			//$("#selectTree2").render();
			$("#selectTree2_input").val("请选择单位");
		}
		
		//汇总
		function btnSummyClick(){
			var transferCustCol7 = $("#SelectedCustCol7").val();
			var transferDepartCode = $("#SelectedDepartCode").val();
			console.log($("#spanSelectTree").is(":hidden"));
			
			if(!$("#spanSelectTree").is(":hidden") && !(transferDepartCode!=null && transferDepartCode.trim()!="")){
			    bootbox.dialog({
				    message: "<span class='bigger-110'>您没有选择任何单位!</span>",
				    buttons: 			
				    { "button":{ "label":"确定", "className":"btn-sm btn-success"}}
			    }); 
			} else if (!(transferCustCol7!=null && transferCustCol7.trim()!="")){
			    bootbox.dialog({
				    message: "<span class='bigger-110'>您没有选择账套信息!</span>",
				    buttons: 			
				    { "button":{ "label":"确定", "className":"btn-sm btn-success"}}
			    }); 
			} else {
                var msg = '确定要汇总吗??';
                bootbox.confirm(msg, function(result) {
    				if(result) {
    					top.jzts();
    					$.ajax({
    						type: "POST",
    						url: '<%=basePath%>housefundsummy/summaryDepartString.do?'
    				            +'SelectedCustCol7='+$("#SelectedCustCol7").val()
    				            +'&SelectedDepartCode='+$("#SelectedDepartCode").val()
    				            +'&SelectedUserGrop='+$("#SelectedUserGrop").val()
    			                +'&SelectedUserCatg='+$("#SelectedUserCatg").val()
    			                //+'&SelectedUnitsCode='+$("#SelectedUnitsCode").val()
    			                +'&SelectedOrgUnit='+$("#SelectedOrgUnit").val()
    			                +'&DataRowSummy='+'',
    						dataType:'json',
    						cache: false,
    						success: function(response){
    							if(response.code==0){
    								$(gridBase_selector).trigger("reloadGrid");  
    								$(top.hangge());//关闭加载状态
    								$("#subTitle").tips({
    									side:3,
    						            msg:'汇总成功',
    						            bg:'#009933',
    						            time:3
    						        });
    							}else{
    								$(top.hangge());//关闭加载状态
    								$("#subTitle").tips({
    									side:3,
    						            msg:'汇总失败,'+response.message,
    						            bg:'#cc0033',
    						            time:3
    						        });
    							}
    						},
    				    	error: function(response) {
    							$(top.hangge());//关闭加载状态
								$("#subTitle").tips({
									side:3,
						            msg:'汇总出错:'+response.responseJSON.message,
						            bg:'#cc0033',
						            time:3
						        });
    				    	}
    					});
    				}
                });
			}
		}
		
		//检索
		function tosearch() {
			console.log($("#SelectedDepartCode").val());
			var SelectedDepartCode = $("#SelectedDepartCode").val();
			$(gridBase_selector).jqGrid('setGridParam',{  // 重新加载数据
				url:'<%=basePath%>housefundsummy/getPageList.do?'
		            +'SelectedCustCol7='+$("#SelectedCustCol7").val()
		            +'&SelectedDepartCode='+$("#SelectedDepartCode").val()
		            +'&SelectedUserGrop='+$("#SelectedUserGrop").val()
	                +'&SelectedUserCatg='+$("#SelectedUserCatg").val()
	                //+'&SelectedUnitsCode='+$("#SelectedUnitsCode").val()
	                +'&SelectedOrgUnit='+$("#SelectedOrgUnit").val(),  
				datatype:'json',
			      page:1
			}).trigger("reloadGrid");
		}  

	 	</script>
	</html>