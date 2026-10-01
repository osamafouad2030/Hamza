package com.example.network

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiClient {
    private const val MODEL = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun askGemini(
        systemInstruction: String,
        prompt: String,
        lessonContext: String
    ): Pair<String, Boolean> = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            // Return null indicator so caller uses intelligent local knowledge engine
            return@withContext Pair("", false)
        }

        try {
            val url = "$BASE_URL/$MODEL:generateContent?key=$apiKey"

            val jsonBody = JSONObject().apply {
                val fullSystemInstruction = """
                    $systemInstruction
                    
                    السياق المعتمد من الكتاب الدراسي (تحريرات الإمام حمزة من طيبة النشر):
                    $lessonContext
                    
                    تعليمات هامة وأساسية:
                    1. اعتمد أولاً وحصراً على الكتاب المدرسي أعلاه ولا تخترع معلومات غير موجودة فيه.
                    2. إذا كانت إجابتك تتجاوز محتوى الكتاب أو تضيف معلومات خارجية، يجب عليك أن تذكر صراحة للطالب في بداية الإجابة أو نهايتها: [تنبيه: هذه المعلومة إضافية وليست واردة في الكتاب المدرسي].
                    3. خاطب الطالب بأسلوب المعلم المربي الماهر، المشجع والميسر لعلم القراءات القرآنية.
                """.trimIndent()

                put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply { put("text", fullSystemInstruction) })
                    })
                })

                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply { put("text", prompt) })
                        })
                    })
                })

                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.4)
                    put("topP", 0.9)
                })
            }

            val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext Pair("", false)
            }

            val resString = response.body?.string() ?: return@withContext Pair("", false)
            val resJson = JSONObject(resString)
            val candidates = resJson.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val contentObj = candidates.getJSONObject(0).optJSONObject("content")
                val parts = contentObj?.optJSONArray("parts")
                if (parts != null && parts.length() > 0) {
                    val reply = parts.getJSONObject(0).optString("text", "")
                    val isExtra = reply.contains("معلومة إضافية") || reply.contains("ليست واردة في الكتاب")
                    return@withContext Pair(reply, isExtra)
                }
            }
            Pair("", false)
        } catch (e: Exception) {
            Pair("", false)
        }
    }
}
