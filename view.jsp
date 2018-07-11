<%@ include file="init.jsp" %>
<input type="hidden" id="hiddenCategoryId" name="hiddenCategoryId" value=""/>
<input type="hidden" id="hiddenOrder" name="hiddenOrder" value=""/>
<%-- <input type="hidden" id="categoryIdFromDetail" name="categoryIdFromDetail" value="${categoryId}"/> --%>
<input type="hidden" id="categoryIdFromDetail" name="categoryIdFromDetail" value=""/>

<c:choose>
	<c:when test="${structureName eq 'Omnidesk_DigitalChannel_Structure' }">
		<div class="channels dotted-bg">	
	</c:when>
	<c:otherwise>
		<div class="wikis dotted-bg">
	</c:otherwise>
</c:choose>

	<div class="container ">
	     
	 <!-- Filter -->
     <div class="row filter-cont">
         <div class="col-md-8 col-sm-8 col-xs-12 filters">
             <div class="filter-btn">
                 <a href=""><i class="icon-filter"></i>
                     <i class="icon-chevron-down filter-status hide"></i>
                     <i class="icon-chevron-up filter-status"></i>
                 </a>
             </div>

			<select name="filterCategory" id="filterCategory" label="" class="filter-sort" onchange="getListofArticles(null, null, 1);">
				<option value="modifiedDate">Modified Date</option>
				<option value="createDate">Create Date</option>
			    <option value="title">Title Assending</option>
			    <option value="title">Title Desending</option>
			 </select>
         </div>

         <div class="col-md-4 col-sm-4 col-xs-12 search custom_search">
             <aui:input type="text" name="searchText" label="" placeholder="Search"/>
             <div class="search-btn">
                 <a href="javascript:void(0);" onblur="getListofArticles(null, null, 1);" class="tablet-btn"><i class="icon-search"></i></a>
             </div>
         </div>
     </div>

	<!-- Categories -->
          <div class="row">
              <div class="col-md-12 col-sm-12 col-xs-12 categories active">
                  <div class="row">
                  <c:forEach var="category" items="${assetCategories}">
	                  <div class="col-md-3 col-sm-4 col-xs-4 col-xxs-6 col-xxxs-12">
	                      <h5 onclick="getListofArticles('desc', '${category.categoryId}', 1);" style="cursor: pointer;">${category.categoryName}</h5>
	                      <ul class="list-unstyled">
	                      	  <c:forEach var="subCategory" items="${category.subCategoryList}">
		                          <li><a href="javascript:void(0);"  onclick="getListofArticles('desc', '${subCategory.categoryId}', 1);">${subCategory.name}</a></li>
	                      	  </c:forEach>	
	                      </ul>
	                  </div>
				 </c:forEach> 
				 </div>
              </div>
          </div>         
          
          <div id="external"></div>
          
	</div>
</div>        

<%-- <div class="row">
	   	<!-- <div class="col-lg-1">
	    	<a href="javascript:void(0);" onclick="getListofArticles('asc', null, 1);" id="Asc">Ascending</a>
	    	<a href="javascript:void(0);" onclick="getListofArticles('desc', null, 1);" id="Dsc">Descending</a>
	   	</div> -->
		<div class="col-lg-2">
			<aui:select name="filterCategory" label="">
				<aui:option value="title">Title</aui:option>
				<aui:option value="modifiedDate">Modified Date</aui:option>
				<aui:option value="createDate">Publish Date</aui:option>
			</aui:select>
		</div>
		<div class="col-lg-2">
			<aui:input type="text" name="searchText" label="" placeholder="Search" onblur="getListofArticles(null, null, 1);" />
		</div>
</div> 
<div class="row">
	<c:forEach var="category" items="${assetCategories}">
		<div class="col-lg-3" style="background: #eeeeee;padding:15px;">
			<b style="display: block;margin-bottom: 10px;font-size: 16px;"><aui:a style="color: #8b8b8b" href="javascript:void(0);" onclick="getListofArticles('desc', '${category.categoryId}', 1);">${category.categoryName}</aui:a></b>
			<c:forEach var="subCategory" items="${category.subCategoryList}">
				<p><aui:a style="color: #a7a8aa" href="javascript:void(0);" onclick="getListofArticles('desc', '${subCategory.categoryId}', 1);">${subCategory.name}</aui:a></p>
			</c:forEach>
		</div>
	</c:forEach>
</div>
	--%>
	
<div style="padding:30px 0;">	
	<div class="row">
		<div id="external"></div>
	</div>
</div>

<%@ include file="view_js.jsp" %>