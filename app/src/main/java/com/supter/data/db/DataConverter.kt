package com.supter.data.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.supter.data.response.purchase.QuestionsItem
import java.lang.reflect.Type


class DataConverter {

    @TypeConverter
    fun fromCountryLangList(countryLang: List<QuestionsItem?>?): String? {
        if (countryLang == null) {
            return null
        }
        val gson = Gson()
        val type: Type = object : TypeToken<List<QuestionsItem?>?>() {}.type
        return gson.toJson(countryLang, type)
    }

    @TypeConverter
    fun toCountryLangList(countryLangString: String?): List<QuestionsItem>? {
        if (countryLangString == null) {
            return null
        }
        val gson = Gson()
        val type: Type = object : TypeToken<List<QuestionsItem?>?>() {}.type
        return gson.fromJson<List<QuestionsItem>>(countryLangString, type)
    }

}