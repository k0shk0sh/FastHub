package com.fastaccess.data.model

/**
 * Created by Kosh on 23.01.19.
 */
data class FirebaseTrendingConfigModel(
    var pathUrl: String = "https://github.com/trending/",
    var description: String = ".Box-row > p",
    var forks: String = ".f6 > a[href*=/network]",
    var language: String = ".f6 span[itemprop=programmingLanguage]",
    var languageFallback: String = ".f6 span[itemprop=programmingLanguage]",
    var listName: String = ".Box",
    var listNameSublistTag: String = "article",
    var stars: String = ".f6 > a[href*=/stargazers]",
    var title: String = ".Box-row > h1 > a",
    var todayStars: String = ".f6 > span.float-sm-right",
    var todayStarsFallback: String = ".f6 > span.float-sm-right"
) {

    companion object {
        fun map(map: HashMap<String, String>?): FirebaseTrendingConfigModel {
            val trendingModel = FirebaseTrendingConfigModel()
            map?.let {
                trendingModel.description = it.getOrElse("description") { trendingModel.description }
                trendingModel.forks = it.getOrElse("forks") { trendingModel.forks }
                trendingModel.language = it.getOrElse("language") { trendingModel.language }
                trendingModel.languageFallback = it.getOrElse("language_fallback") { trendingModel.languageFallback }
                trendingModel.listName = it.getOrElse("list_name") { trendingModel.listName }
                trendingModel.listNameSublistTag = it.getOrElse("list_name_sublist_tag") { trendingModel.listNameSublistTag }
                trendingModel.stars = it.getOrElse("stars") { trendingModel.stars }
                trendingModel.title = it.getOrElse("title") { trendingModel.title }
                trendingModel.todayStars = it.getOrElse("today_stars") { trendingModel.title }
                trendingModel.todayStarsFallback = it.getOrElse("today_stars_fallback") { trendingModel.title }
                trendingModel.pathUrl = it.getOrElse("path_url") { trendingModel.pathUrl }
            }
            return trendingModel
        }
    }
}