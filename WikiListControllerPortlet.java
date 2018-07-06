package com.omnidesk.portlet;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetCategoryLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.asset.kernel.service.persistence.AssetEntryQuery;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.document.library.kernel.util.DLUtil;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.OrderFactoryUtil;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.SortFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.DocumentException;
import com.liferay.portal.kernel.xml.Node;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.ratings.kernel.service.RatingsStatsLocalServiceUtil;
import com.omnidesk.configuration.WikiListConfiguration;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

@Component(
	 configurationPid =
	    "com.omnidesk.portlet.WikiListConfiguration",
	immediate = true,
	property = {
		"com.liferay.portlet.display-category=Omnidesk",
		"com.liferay.portlet.instanceable=true",
		"javax.portlet.display-name=Omnidesk-Wiki-Listing Portlet",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user"
	},
	service = Portlet.class
)
public class WikiListControllerPortlet extends MVCPortlet {
	private Log _log = LogFactoryUtil.getLog(WikiListControllerPortlet.class.getName());
	private static String WIKI_STRUCTURE = "Omnidesk_Wiki_Structure";
	private static String DIGITALCHANNEL_STRUCTURE = "Omnidesk_DigitalChannel_Structure";
	private static String MARKET_PLACE_STRUCTURE = "Omnidesk_MarketPlace_Structure";
	private static String OMNIDESK_STRUCTURE_NAME = "omnideskStrctureName";
	private static String DISPLAY_RECORD_LENGTH = "displayRecordLength";
	private static String DEFAULT_RECORD_LENGTH = "12";
	
	@Override
	public void render(RenderRequest renderRequest, RenderResponse renderResponse) {

		PortletPreferences portletPreferences = renderRequest.getPreferences();
	    String omnStrctureName = portletPreferences.getValue(OMNIDESK_STRUCTURE_NAME, WIKI_STRUCTURE);

		if(!omnStrctureName.equalsIgnoreCase(MARKET_PLACE_STRUCTURE)){
			/** Get asset categories **/
			List<AssetCategory> assetCategories = new ArrayList<AssetCategory>();
			List<AssetCategory> subCategories = new ArrayList<AssetCategory>();
			List<OmnideskCategory> omnideskCategoriesList = new ArrayList<OmnideskCategory>();
			OmnideskCategory omnCategory = new OmnideskCategory();

			assetCategories = AssetCategoryLocalServiceUtil.getChildCategories(0);
			for (AssetCategory assetCategoriesObj : assetCategories) {

				omnCategory = new OmnideskCategory();
				omnCategory.setCategoryId(String.valueOf(assetCategoriesObj.getCategoryId()));
				omnCategory.setCategoryName(assetCategoriesObj.getName());
				subCategories = AssetCategoryLocalServiceUtil.getChildCategories(assetCategoriesObj.getCategoryId());
				omnCategory.setSubCategoryList(subCategories);
				omnideskCategoriesList.add(omnCategory);
			}
			renderRequest.setAttribute("assetCategories", omnideskCategoriesList);
			/** End Get asset categories **/

		    HttpServletRequest httpReq = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(renderRequest));
		    String categoryId = ParamUtil.getString(httpReq, "categoryId");
		    
		    if(Validator.isNotNull(categoryId)){
		    	renderRequest.setAttribute("categoryId", categoryId);
		    }
		    renderRequest.setAttribute("structureName", omnStrctureName);
			try {
				super.render(renderRequest, renderResponse);
			} catch (IOException | PortletException e) {
				_log.error("Error in rendering data of wiki list : " + e.getMessage());
			}
		}else{
			try {
				getPortletContext().getRequestDispatcher("/marketPlace_view.jsp").include(renderRequest, renderResponse);
			} catch (PortletException | IOException e) {
				_log.error("Error in rendering data of wiki list : " + e.getMessage());
			}
		}
	}
	
	private static int searchCount(long structureId,long categoryId, String searchText){
		AssetEntryQuery assetEntryQuery = new AssetEntryQuery();
		assetEntryQuery.setClassTypeIds(new long[]{structureId});
		if(categoryId > 0){
			assetEntryQuery.setAllCategoryIds(new long[]{categoryId});
		}
		assetEntryQuery.setKeywords(searchText);
		assetEntryQuery.setClassName(JournalArticle.class.getName());
		List<AssetEntry> assetEntryList = AssetEntryLocalServiceUtil.getEntries(assetEntryQuery);
		return assetEntryList.size();
	}
	
	private static List<AssetEntry> search(long structureId, long categoryId, String searchText, String sortOrder, String orderBy, int start, int end, ThemeDisplay themeDisplay, PortletRequest request) {

		/*** Start code by AssetQuery ****/
		
		/*AssetEntryQuery assetEntryQuery = new AssetEntryQuery();
		if(categoryId > 0){
			assetEntryQuery.setAllCategoryIds(new long[]{categoryId});
		}
		
		assetEntryQuery.setClassTypeIds(new long[]{structureId});
		assetEntryQuery.setKeywords(searchText);
		assetEntryQuery.setClassName(JournalArticle.class.getName());
		assetEntryQuery.setStart(start);
		assetEntryQuery.setEnd(end);
		
		if(Validator.isNotNull(orderBy) && Validator.isNotNull(sortOrder)){
			if(orderBy.equalsIgnoreCase("title")){
				assetEntryQuery.setOrderByCol1(Field.TITLE);
			}else if(orderBy.equalsIgnoreCase("modifiedDate")){
				assetEntryQuery.setOrderByCol1(Field.MODIFIED_DATE);
			}else if(orderBy.equalsIgnoreCase("createDate")){
				assetEntryQuery.setOrderByCol1(Field.CREATE_DATE);
			}
			assetEntryQuery.setOrderByType1(sortOrder);	
		}
		
		List<AssetEntry> assetEntryList = AssetEntryLocalServiceUtil.getEntries(assetEntryQuery);*/
		/*** End code by AssetQuery ****/
		
		/** Code to get detail by Hits ***/
		
		// Step 1 : List of Journal articles from keyword
		
		List<com.liferay.portal.kernel.search.Document> documents = null;
		DDMStructure ddmStructure = null;
		ddmStructure = getStructureDetail(structureId);
		Sort sorts = SortFactoryUtil.getSort(JournalArticle.class, Field.TITLE, "asc");
		Hits hits = JournalArticleLocalServiceUtil.search(themeDisplay.getCompanyId(), themeDisplay.getScopeGroupId(), Collections.EMPTY_LIST, 0, ddmStructure.getStructureKey(), null, searchText, null, -1, -1, sorts);
		documents = hits.toList();
		List<Long> searchTextIds = new ArrayList<Long>();
		if (documents != null && !documents.isEmpty()) {
			 for(com.liferay.portal.kernel.search.Document doc : documents) { 
				 Long classPK = Long.parseLong(doc.get(Field.ENTRY_CLASS_PK));
				 JournalArticle article = null;
				 try {
					article = JournalArticleLocalServiceUtil.getLatestArticle(classPK);
					List<AssetCategory> categories = AssetCategoryLocalServiceUtil.getCategories(JournalArticle.class.getName(), article.getResourcePrimKey());
				 } catch (PortalException e) {
					e.printStackTrace();
				}
				 searchTextIds.add(classPK);
			 }
		}
		
		// Step 2 : List of CategoryId
		List<AssetEntry> assetEntryListForCategory= new ArrayList<AssetEntry>();
		List<Long> assetEntryCategoriesList = new ArrayList<Long>();
		if(categoryId > 0){
			AssetEntryQuery assetEntryQueryCategory = new AssetEntryQuery();
			assetEntryQueryCategory.setAllCategoryIds(new long[]{categoryId});
			assetEntryQueryCategory.setClassTypeIds(new long[]{structureId});
			assetEntryQueryCategory.setClassName(JournalArticle.class.getName());
			assetEntryListForCategory = AssetEntryLocalServiceUtil.getEntries(assetEntryQueryCategory);
		}
		
		if(assetEntryListForCategory.size()>0){
			for (AssetEntry assetEntry : assetEntryListForCategory) {
				assetEntryCategoriesList.add(assetEntry.getEntryId());
			}
		}
		
		
		// Step 3 : List of Asset Entry
		DynamicQuery dynamicQueryAsset = DynamicQueryFactoryUtil.forClass(AssetEntry.class,PortalClassLoaderUtil.getClassLoader());
		dynamicQueryAsset.add(PropertyFactoryUtil.forName("classPK").in(searchTextIds));
		if(categoryId>0){
			dynamicQueryAsset.add(PropertyFactoryUtil.forName("entryId").in(assetEntryCategoriesList));
		}
		
		if(orderBy.equalsIgnoreCase("Create Date")){
			dynamicQueryAsset.addOrder(OrderFactoryUtil.desc(Field.CREATE_DATE));
		}else if(orderBy.equalsIgnoreCase("Modified Date")){
			dynamicQueryAsset.addOrder(OrderFactoryUtil.desc("modifiedDate"));
		}else if(orderBy.equalsIgnoreCase("Title Assending")){
			dynamicQueryAsset.addOrder(OrderFactoryUtil.asc(Field.TITLE));
		}else if(orderBy.equalsIgnoreCase("Title Desending")){
			dynamicQueryAsset.addOrder(OrderFactoryUtil.desc(Field.TITLE));
		}
		List<AssetEntry> assetEntries = AssetEntryLocalServiceUtil.dynamicQuery(dynamicQueryAsset, start, end);
		/** End Code to get detail by Hits ***/
//		return assetEntryList;
		return assetEntries;
	}
	
	private static long getStructureId(String structureName){
		List<DDMStructure> ddmStructure = new ArrayList<DDMStructure>();
		ddmStructure = DDMStructureLocalServiceUtil.getStructures();
		
		long structureId=0;
		for (DDMStructure ddmStructure2 : ddmStructure) {
			if(ddmStructure2.getName(Locale.ENGLISH).equalsIgnoreCase(structureName)){
				structureId = ddmStructure2.getStructureId();
			}
		}
		return structureId;
	}
	
	private static List<Map<String, Object>> wikiArticleList(List<AssetEntry> assetEntryList, ThemeDisplay themeDisplay){
    	List<Map<String, Object>> articleList = new ArrayList<Map<String, Object>>();
    	try {
			Document document = null;
			String title = null;
			String imageURL=null;
			String desc=null;
			String videoURL=null;
			String youtubeLink=null;
			String appImage = null;
			Map<String, Object> data= null ;
			
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
			
			for (AssetEntry article : assetEntryList) {
				Node imgNode=null ,videoNode=null,youtubeLinkNode=null,appImageNode=null;
				JournalArticle journalArticle = JournalArticleLocalServiceUtil.getLatestArticle(article.getClassPK());

				try {
					data = new HashMap<String, Object>();
					document = SAXReaderUtil.read(journalArticle.getContentByLocale("en_US"));
					if(Validator.isNotNull(document)){
						title = journalArticle.getTitle(Locale.ENGLISH);
						imgNode = document.selectSingleNode("/root/dynamic-element[@name='" + "WikiImage" + "']/dynamic-content");
						desc = journalArticle.getDescription(Locale.ENGLISH);
						videoNode = document.selectSingleNode("/root/dynamic-element[@name='" + "digitalChannelVideo" + "']/dynamic-content");
						youtubeLinkNode = document.selectSingleNode("/root/dynamic-element[@name='" + "youtubeLink" + "']/dynamic-content");
						appImageNode = document.selectSingleNode("/root/dynamic-element[@name='" + "AppImage" + "']/dynamic-content");
						double ratingsStats =  RatingsStatsLocalServiceUtil.getStats(JournalArticle.class.getName(),journalArticle.getResourcePrimKey()).getTotalEntries();

						if(Validator.isNotNull(imgNode)){
							imageURL = imgNode.getText();
						}
						if(Validator.isNotNull(videoNode)){
					        videoURL = videoNode.getText();
					    }
						if(Validator.isNotNull(youtubeLinkNode)){
							youtubeLink = youtubeLinkNode.getText();
				            if(Validator.isNotNull(youtubeLink)){
								String[] split = youtubeLink.split("v=");
					            data.put("youtubeVideoThumbnail","http://img.youtube.com/vi/"+split[1]+"/0.jpg");
					            data.put("youtubeFragment", split[1]);
				            }
						}

						if(Validator.isNotNull(appImageNode)){
							appImage = appImageNode.getText();
						}
						
						data.put("titleNode", title);
						data.put("urlTitle", journalArticle.getUrlTitle());
						data.put("imageNode", imageURL);
						data.put("videoNode",videoURL);
						data.put("descriptionNode", desc);
						data.put("ratingsStats",(int)ratingsStats);
						data.put("createdDate",simpleDateFormat.format(article.getCreateDate()));
						data.put("youtubeLink", youtubeLink);
						data.put("smallImageId",journalArticle.getSmallImageId());
						data.put("smallImage",journalArticle.getSmallImage());
						data.put("appImage",appImage);
					}
				} catch (DocumentException e) {
					e.printStackTrace();
				}
				articleList.add(data);
			}
		} catch (PortalException e) {
			//e.printStackTrace();
		}
    	return articleList;
    }

	private static int setPaginationCounter(int listSize, int displayRecordLength){
		int j = 0;
		int paginationCounter=0;
		j = listSize % displayRecordLength;
		paginationCounter = listSize/displayRecordLength;
		if(j != 0){
			paginationCounter+=1;
		}
		return paginationCounter;
		
	}
	
	@Override
	public void serveResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse) throws IOException,PortletException {
		ThemeDisplay themeDisplay = (ThemeDisplay) resourceRequest.getAttribute(WebKeys.THEME_DISPLAY);
	    
		PortletPreferences portletPreferences = resourceRequest.getPreferences();
	    String structureName = portletPreferences.getValue(OMNIDESK_STRUCTURE_NAME, WIKI_STRUCTURE);
	    String displayRecordLengthStr = portletPreferences.getValue(DISPLAY_RECORD_LENGTH, DEFAULT_RECORD_LENGTH);
	    int displayRecordLength = Integer.parseInt(displayRecordLengthStr);
	    
		String categoryId = ParamUtil.getString(resourceRequest, "selectedCategoryId");
		String searchText = ParamUtil.getString(resourceRequest, "searchText");
		String sortOrder =  ParamUtil.getString(resourceRequest, "order");
		String filterCategory = ParamUtil.getString(resourceRequest, "filterCategory");
		String pageNumberStr = ParamUtil.getString(resourceRequest, "seq");

		int pageNumber = Validator.isNull(pageNumberStr) ? 1 : Integer.parseInt(pageNumberStr);
		pageNumber = pageNumber-1;
		int start = (pageNumber*displayRecordLength);
		int end = (pageNumber*displayRecordLength)+displayRecordLength;
		
		List<AssetEntry> assetEntryList = new ArrayList<AssetEntry>();
		assetEntryList = search(getStructureId(structureName), Validator.isNull(categoryId) ? 0 : Long.parseLong(categoryId), searchText, sortOrder, filterCategory, start, end,themeDisplay, resourceRequest);
		List<Map<String, Object>> wikiArticleList = new ArrayList<Map<String, Object>>();
		wikiArticleList = wikiArticleList(assetEntryList, themeDisplay);
		resourceRequest.setAttribute("articleList", wikiArticleList);

		int listSize = searchCount(getStructureId(structureName), Validator.isNull(categoryId) ? 0 : Long.parseLong(categoryId), searchText);
		int paginationCounter = setPaginationCounter(listSize, displayRecordLength);
		resourceRequest.setAttribute("total", wikiArticleList.size());
		resourceRequest.setAttribute("sequence", ++pageNumber);
		resourceRequest.setAttribute("paginationCounter",paginationCounter);

		if(!structureName.equalsIgnoreCase(MARKET_PLACE_STRUCTURE)){
			getPortletContext().getRequestDispatcher("/wikiListResult.jsp").include(resourceRequest, resourceResponse);
		}else{
			getPortletContext().getRequestDispatcher("/marketPlaceResult.jsp").include(resourceRequest, resourceResponse);
		}

		super.serveResource(resourceRequest, resourceResponse);
	}
	
	@Activate
    @Modified
    protected void activate(Map<Object, Object> properties) {
    	_wikiListConfiguration = ConfigurableUtil.createConfigurable(WikiListConfiguration.class, properties);
    }
	
	private static DDMStructure getStructureDetail(long structureId){
		DDMStructure ddmStructure = null;
		try {
			ddmStructure = DDMStructureLocalServiceUtil.getStructure(structureId);
		} catch (PortalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ddmStructure;
	}
	
    private volatile WikiListConfiguration _wikiListConfiguration;
}