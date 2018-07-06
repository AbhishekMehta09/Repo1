<%@ include file="init.jsp" %>

<portlet:resourceURL var="wikiListAjaxURL" id="wikiListAjaxURL" >
</portlet:resourceURL>

<script type="text/javascript">

$(document).ready(function(){
	$("#Dsc").hide();
	
	getListofArticles(null, null, 1);
	$('body').on('click' , '.video-node' , function(){
		$(this).toggleClass('active');
		if($(this).hasClass('active')){
			$(this).get(0).play();
		}else{
			$(this).get(0).pause();
		}
	});
});


function getListofArticles(order, categoryId, pageIndex){
	var searchText = $("#<portlet:namespace />searchText").val();
	var filterCategory = $('#filterCategory').find(":selected").text();
	var filterCategoryId = $("#hiddenCategoryId").val();
	var hiddenCategoryIdFromDetail = $("#categoryIdFromDetail").val();
	
	if(hiddenCategoryIdFromDetail != null){
		filterCategoryId = document.getElementById("categoryIdFromDetail").value;	
	}
	
	if(categoryId != null){
		document.getElementById("hiddenCategoryId").value = categoryId;
		filterCategoryId = document.getElementById("hiddenCategoryId").value;
	}
	
	if(categoryId == null){
		filterCategoryId = document.getElementById("hiddenCategoryId").value;
	}
	
	var orderOfList = $("#hiddenOrder").val();
	if(order != null){
		document.getElementById("hiddenOrder").value = order;
		orderOfList = document.getElementById("hiddenOrder").value;
	}
	
	$.ajax({
	    url :"<%=wikiListAjaxURL%>",
	    data: {  
	           <portlet:namespace />searchText: searchText,
			   <portlet:namespace />order : orderOfList,
	           <portlet:namespace />selectedCategoryId : filterCategoryId,
	           <portlet:namespace />filterCategory : filterCategory,
	           <portlet:namespace />seq : pageIndex
	         },	
	    success: function(html){
	    	$("#external").empty();
			$("#external").append(html);
			
			if(order != null){
				if(order == "asc"){
					  $("#Asc").hide();
					  $("#Dsc").show();
				 }else{
					  $("#Dsc").hide();
					  $("#Asc").show();
				 }
			}
		}
	});
}

$("#<portlet:namespace />searchText").keyup(function(event){
    if(event.keyCode == 13){
    	getListofArticles(null, null, 1);
    }
});


</script>