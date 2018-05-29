<%@ page language="java" contentType="text/html; charset=UTF-8"
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
<!-- 下拉框 -->
<link rel="stylesheet" href="static/ace/css/chosen.css" />
<!-- jsp文件头和头部 ，其中包含旧版本（Ace）Jqgrid Css-->
<%@ include file="../../system/index/topWithJqgrid.jsp"%>
<!-- 日期框 -->
<link rel="stylesheet" href="static/ace/css/datepicker.css" />

<!-- 最新版的Jqgrid Css，如果旧版本（Ace）某些方法不好用，尝试用此版本Css，替换旧版本Css -->
<!-- <link rel="stylesheet" type="text/css" media="screen" href="static/ace/css/ui.jqgrid-bootstrap.css" /> -->
<script type="text/javascript" src="static/js/jquery-1.7.2.js"></script>
<!-- <script type="text/javascript" src="static/ace/js/jquery.js"></script> -->
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
							id="subTitle" style="margin-left: 2px;">组织机构分线关系 </span> <span
							style="border-left: 1px solid #e2e2e2; margin: 0px 10px;">&nbsp;</span>
						<button id="btnQuery" class="btn btn-white btn-info btn-sm"
							onclick="showQueryCondi($('#jqGrid'),null,true)">
							<i class="ace-icon fa fa-chevron-down bigger-120 blue"></i> <span>显示查询</span>
						</button>
					</div>
					<!-- /.page-header -->

					<div class="row">
						<div class="col-xs-12">
							<div class="widget-box" hidden>
								<div class="widget-body">
									<div class="widget-main">
										<form class="form-inline">
											<span style="margin-right: 5px;">
												<select class="chosen-select form-control"
													name="SelectedCustCol7" id="SelectedCustCol7"
													data-placeholder="请选择帐套"
													style="vertical-align: top; height:32px;width: 150px;">
													<option value="">请选择帐套</option>
													<c:forEach items="${FMISACC}" var="each">
														<option value="${each.DICT_CODE}"
														    <c:if test="${pd.SelectedCustCol7==each.DICT_CODE}">selected</c:if>>${each.NAME}</option>
													</c:forEach>
												</select>
											</span>
											<span class="pull-left" style="margin-right: 5px;"> 
												<select class="chosen-select form-control" 
												    name="SelectedTypeCode" id="SelectedTypeCode" data-placeholder="请选择业务类型"
													style="vertical-align: top; height: 32px; width: 150px;">
														<option value="">请选择业务类型</option>
														<c:forEach items="${PZTYPE}" var="each">
															<option value="${each.DICT_CODE}"
																<c:if test="${pd.SelectedTypeCode==each.DICT_CODE}">selected</c:if>>${each.NAME}</option>
														</c:forEach>
												</select>
											</span>
											<button type="button" class="btn btn-info btn-sm"
												onclick="tosearch();">
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

	<script type="text/javascript"> 
	$(document).ready(function () { 
		$(top.hangge());//关闭加载状态
		 
		//resize to fit page size
		$(window).on('resize.jqGrid', function () {
			$("#jqGrid").jqGrid( 'setGridWidth', $(".page-content").width());
			resizeGridHeight($("#jqGrid"),null,true);
	    })
		
		$("#jqGrid").jqGrid({
			url: '<%=basePath%>certParmConfig/getPageList.do?SelectedCustCol7='+$("#SelectedCustCol7").val()
            + '&SelectedTypeCode=' + $("#SelectedTypeCode").val(),
			datatype: "json",
			 colModel: [
				{label: ' ',name:'myac',index:'', width:70, fixed:true, sortable:false, resize:false,
					formatter:'actions', 
					formatoptions:{ 
					 onEdit:function(rowid){
							 //var curRow= $("tr[id="+rowid+"]");
							 //var curCol=curRow.find("td[aria-describedby='jqGrid_STATE']");
							 //if(curCol.attr('title')=='停用'){
							 //	 var cur=$("#jSaveButton_"+rowid);
							 //	 cur.find("span").css('display','none');
							 //}
						},
                        onSuccess: function(response) {
                        	var code=JSON.parse(response.responseText);
							if(code.code==0){
								return [true];
							}else{
								$("#subTitle").tips({
									side : 3,
									msg : '保存失败,' + code.message,
									bg : '#cc0033',
									time : 3
								});
								return [false, code.message];
							}                
                        },
                        onError :function(rowid, res, stat, err) {
                        	if(err!=null)
                        		console.log(err);
                        },
                        afterSave:function(rowid, res){
                        	$(".tooltip").remove();
                        	/* $("#jqGrid").trigger("reloadGrid"); */
                        	
                        },
						keys:true,
					    delbutton: false,//disable delete button
					}
				},

				{ label: '账套',name:'BILL_OFF__', width:90,hidden : true,editable: true},
				{ label: '业务类型', name: 'TYPE_CODE__', width: 60,hidden : true,editable: true,},
				{ label: '单位', name: 'DEPT_CODE__', width: 60,hidden : true,editable: true,},
				{ label: '单位', name: 'DEPT_CODE', width: 60,hidden : true,editable: true,},

				{ label: '账套', name: 'BILL_OFF', width: 60,editable: true,edittype: 'select',formatter:'select',formatoptions:{value:"${billOffStrAll}"},editoptions:{value:"${billOffStrSelect}"},stype: 'select',searchoptions:{value:"${billOffStrAll}"}},
				{ label: '业务类型', name: 'TYPE_CODE', width: 60,editable: true,edittype: 'select',formatter:'select',formatoptions:{value:"${typeCodeStrAll}"},editoptions:{value:"${typeCodeStrSelect}"},stype: 'select',searchoptions:{value:"${typeCodeStrAll}"}},
				{ label: '明细汇总字段', name: 'GROUP_COND', width: 100, editable: true,edittype:'text', editoptions:{maxLength:'200'}},
				{ label: '总汇总字段', name: 'GROUP_COND1', width: 100, editable: true,edittype:'text', editoptions:{maxLength:'200'}}
			],
			reloadAfterSubmit: true, 
			viewrecords: true,
			rowNum: 100,
			rowList: [100,200,500],
            multiSort: true,
			sortname: 'BILL_OFF,TYPE_CODE',
			pager: "#jqGridPager",
			
			altRows: true,
			rownumbers: true, 
            rownumWidth: 35,		
			/* multiselect: true,
	        multiboxonly: true, */
	        editurl: '<%=basePath%>certParmConfig/save.do?SelectedCustCol7='+$("#SelectedCustCol7").val()
            + '&SelectedTypeCode=' + $("#SelectedTypeCode").val(),
	        
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
		
		$(window).triggerHandler('resize.jqGrid');//trigger window resize to make the grid get the correct size
	
		//navButtons
		jQuery("#jqGrid").jqGrid('navGrid',"#jqGridPager",
			{ 	//navbar options
				edit: false,
				editicon : 'ace-icon fa fa-pencil blue',
				add: true,
				addicon : 'ace-icon fa fa-plus-circle purple',
				del: false,
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
			    id: "edit",
				closeAfterEdit: true,
				recreateForm: true,
				beforeShowForm :beforeEditOrAddCallback,
	            afterSubmit: fn_addSubmit_extend
	        },
	        {
				//new record form
			    id: "add",
				closeAfterAdd: true,
				recreateForm: true,
				viewPagerButtons: false,
				//width: 700,
				//reloadAfterSubmit: true,
				beforeShowForm : beforeEditOrAddCallback,
			    onclickSubmit: function(params, posdata) {
					console.log("onclickSubmit");
	            } , 
	            afterSubmit: fn_addSubmit_extend
	        },
	        { },
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
	        {},{}
		);
		
		$(gridBase_selector).navSeparatorAdd(pagerBase_selector, {
			sepclass : "ui-separator",
			sepcontent: ""
		});
        $(gridBase_selector).navButtonAdd(pagerBase_selector, {
			        id : "batchEdit",
                    buttonicon: "ace-icon fa fa-pencil-square-o purple",
                    title: "批量编辑",
                    caption: "",
                    position: "last",
                    onClickButton: batchEdit,
                    cursor : "pointer"
                });
        $(gridBase_selector).navButtonAdd(pagerBase_selector, {
        	        id : "batchCancelEdit",
                    buttonicon: "ace-icon fa fa-undo",
                    title: "取消批量编辑",
                    caption: "",
                    position: "last",
                    onClickButton: batchCancelEdit,
                    cursor : "pointer"
                });
        $(gridBase_selector).navButtonAdd(pagerBase_selector, {
        			id : "batchSave",
                     caption : "",
                     buttonicon : "ace-icon fa fa-save green",
                     onClickButton : batchSave,
                     position : "last",
                     title : "批量保存",
                     cursor : "pointer"
                 });
        $(gridBase_selector).navButtonAdd(pagerBase_selector, {
        			id : "batchDelete",
                    caption : "",
                    buttonicon : "ace-icon fa fa-trash-o red",
                    onClickButton : batchDelete,
                    position : "last",
                    title : "删除",
                    cursor : "pointer"
                });
        $(gridBase_selector).navSeparatorAdd(pagerBase_selector, {
        			sepclass : "ui-separator",
        			sepcontent: ""
        		});
 	});
	
	 //批量编辑
	function batchEdit(e) {
		var grid = $("#jqGrid");
        var ids = grid.jqGrid('getDataIDs');
        for (var i = 0; i < ids.length; i++) {
            grid.jqGrid('editRow',ids[i]);
       	}
   	}
	
	//取消批量编辑
	function batchCancelEdit(e) {
		var grid = $("#jqGrid");
        var ids = grid.jqGrid('getDataIDs');
        for (var i = 0; i < ids.length; i++) {
            grid.jqGrid('restoreRow',ids[i]);
        }
    }
	
	//批量保存
	function batchSave(e) {
		var listData =new Array();
		var ids = $("#jqGrid").jqGrid('getDataIDs');
		console.log(ids);
		//遍历访问这个集合  
		var rowData;
		$(ids).each(function (index, id){  
            $("#jqGrid").saveRow(id, false, 'clientArray');
             rowData = $("#jqGrid").getRowData(id);
            listData.push(rowData);
		});
		top.jzts();
		$.ajax({
			type: "POST",
			url: '<%=basePath%>certParmConfig/updateAll.do?SelectedCustCol7='+$("#SelectedCustCol7").val()
            + '&SelectedTypeCode=' + $("#SelectedTypeCode").val(),
				data:{DataRows : JSON.stringify(listData)},
				dataType : 'json',
				cache : false,
				success : function(response) {
					if (response.code == 0) {
						$("#jqGrid").trigger("reloadGrid");
						$(top.hangge());//关闭加载状态
						$("#subTitle").tips({
							side : 3,
							msg : '保存成功',
							bg : '#009933',
							time : 3
						});
					} else {
						$(top.hangge());//关闭加载状态
						$("#subTitle").tips({
							side : 3,
							msg : '保存失败,' + response.message,
							bg : '#cc0033',
							time : 3
						});
					}
				},
				error : function(e) {
					$(top.hangge());//关闭加载状态
				}
			});
		}

    /**
     * 批量删除
     */
    function batchDelete(){
    	//获得选中的行ids的方法
        var ids = $(gridBase_selector).getGridParam("selarrrow");  
 		
 		if(!(ids!=null&&ids.length>0)){
			bootbox.dialog({
				message: "<span class='bigger-110'>您没有选择任何内容!</span>",
				buttons: 			
				{ "button":{ "label":"确定", "className":"btn-sm btn-success"}}
			});
		}else{
            var msg = '确定要删除选中的数据吗??';
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
						url: '<%=basePath%>certParmConfig/deleteAll.do?',
				    	data: {DataRows:JSON.stringify(listData)},
						dataType:'json',
						cache: false,
						success: function(response){
							if(response.code==0){
								$(gridBase_selector).trigger("reloadGrid");  
								$(top.hangge());//关闭加载状态
								$("#subTitle").tips({
									side:3,
						            msg:'删除成功',
						            bg:'#009933',
						            time:3
						        });
							}else{
								$(top.hangge());//关闭加载状态
								$("#subTitle").tips({
									side:3,
						            msg:'删除失败,'+response.message,
						            bg:'#cc0033',
						            time:3
						        });
							}
						},
				    	error: function(response) {
							$(top.hangge());//关闭加载状态
							$("#subTitle").tips({
								side:3,
					            msg:'删除出错:'+response.responseJSON.message,
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
     * 增加成功
     * 
     * @param response
     * @param postdata
     * @returns
     */
    function fn_addSubmit_extend(response, postdata) {
        var responseJSON = JSON.parse(response.responseText);
    	if (responseJSON.code == 0) {
    		// console.log("Add Success");
    		$("#subTitle").tips({
    			side : 3,
    			msg : '保存成功',
    			bg : '#009933',
    			time : 3
    		});
    		return [ true ];
    	} else {
    		// console.log("Add Failed"+response.responseJSON.message);
    		$("#subTitle").tips({
    			side : 3,
    			msg : '保存失败,' + responseJSON.message,
    			bg : '#cc0033',
    			time : 3
    		});
    		return [ false, responseJSON.message ];
    	}
    }
	
		//检索
		function tosearch() {
			$("#jqGrid").jqGrid('setGridParam',{  // 重新加载数据
				url:'<%=basePath%>certParmConfig/getPageList.do?SelectedCustCol7='+$("#SelectedCustCol7").val()
	            + '&SelectedTypeCode=' + $("#SelectedTypeCode").val(),
								datatype : 'json'
							}).trigger("reloadGrid");
		}
	</script>
</body>
</html>