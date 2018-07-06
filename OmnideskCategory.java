package com.omnidesk.portlet;

import com.liferay.asset.kernel.model.AssetCategory - ABC;

import java.util.ArrayList;
import java.util.List;

public class OmnideskCategory {

	private String categoryId;
	private String categoryName;
	private List<AssetCategory> subCategoryList = new ArrayList<AssetCategory>();
	public String getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public List<AssetCategory> getSubCategoryList() {
		return subCategoryList;
	}
	public void setSubCategoryList(List<AssetCategory> subCategoryList) {
		this.subCategoryList = subCategoryList;
	}
	@Override
	public String toString() {
		return "OmnideskCategory [categoryId=" + categoryId + ", categoryName=" + categoryName + ", subCategoryList="
				+ subCategoryList + "]";
	}
}
