package com.omnidesk.portlet;

import com.liferay.asset.kernel.model.AssetCategory;

import java.util.ArrayList;
import java.util.List;

public class OmnideskVocabulary {

	private String vobularyId;
	private String vocabularyName;
	private List<AssetCategory> categoryList = new ArrayList<AssetCategory>();
	public String getVobularyId() {
		return vobularyId;
	}
	public void setVobularyId(String vobularyId) {
		this.vobularyId = vobularyId;
	}
	public String getVocabularyName() {
		return vocabularyName;
	}
	public void setVocabularyName(String vocabularyName) {
		this.vocabularyName = vocabularyName;
	}
	public List<AssetCategory> getCategoryList() {
		return categoryList;
	}
	public void setCategoryList(List<AssetCategory> categoryList) {
		this.categoryList = categoryList;
	}
	@Override
	public String toString() {
		return "OmnideskVocabulary [vobularyId=" + vobularyId + ", vocabularyName=" + vocabularyName + ", categoryList="
				+ categoryList + "]";
	}
}
