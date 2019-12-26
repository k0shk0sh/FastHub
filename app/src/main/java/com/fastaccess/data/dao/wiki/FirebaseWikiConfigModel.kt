package com.fastaccess.data.dao.wiki

data class FirebaseWikiConfigModel(
    var sideBarListTitle: String = "a",
    var sideBarUl: String = ".js-wiki-sidebar-toggle-display > ul",
    var sideBarList: String = "li",
    var wikiWrapper: String = "#wiki-wrapper",
    var wikiHeader: String = ".gh-header > h1.gh-header-title",
    var sideBarListLink: String = "href",
    var wikiBody: String = "#wiki-body",
    var wikiSubHeader: String = ".gh-header-meta",
    var wikiContent: String = "#wiki-content"
) {
    companion object {
        fun map(map: HashMap<String, String>?): FirebaseWikiConfigModel {
            val model = FirebaseWikiConfigModel()
            map?.let {
                model.sideBarListTitle = it.getOrElse("sideBarListTitle") { model.sideBarListTitle }
                model.sideBarUl = it.getOrElse("sideBarUl") { model.sideBarUl }
                model.sideBarList = it.getOrElse("sideBarList") { model.sideBarList }
                model.wikiWrapper = it.getOrElse("wikiWrapper") { model.wikiWrapper }
                model.wikiHeader = it.getOrElse("wikiHeader") { model.wikiHeader }
                model.sideBarListLink = it.getOrElse("sideBarListLink") { model.sideBarListLink }
                model.wikiBody = it.getOrElse("wikiBody") { model.wikiBody }
                model.wikiSubHeader = it.getOrElse("wikiSubHeader") { model.wikiSubHeader }
                model.wikiContent = it.getOrElse("wikiContent") { model.wikiContent }
            }
            return model
        }
    }
}